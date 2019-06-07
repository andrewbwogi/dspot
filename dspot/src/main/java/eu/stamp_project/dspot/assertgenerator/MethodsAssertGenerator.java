package eu.stamp_project.dspot.assertgenerator;

import eu.stamp_project.compare.ObjectLog;
import eu.stamp_project.compare.Observation;
import eu.stamp_project.dspot.AmplificationException;
import eu.stamp_project.test_framework.TestFramework;
import eu.stamp_project.testrunner.listener.TestResult;
import eu.stamp_project.utils.AmplificationHelper;
import eu.stamp_project.utils.CloneHelper;
import eu.stamp_project.utils.Counter;
import eu.stamp_project.utils.DSpotUtils;
import eu.stamp_project.utils.compilation.DSpotCompiler;
import eu.stamp_project.utils.compilation.TestCompiler;
import eu.stamp_project.utils.program.InputConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Benjamin DANGLOT
 * benjamin.danglot@inria.fr
 * on 3/3/17
 */
public class MethodsAssertGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodsAssertGenerator.class);

    private CtType originalClass;

    private Factory factory;

    private InputConfiguration configuration;

    private DSpotCompiler compiler;

    private Map<CtMethod<?>, List<CtLocalVariable<?>>> variableReadsAsserted;

    public MethodsAssertGenerator(CtType originalClass,
                                  InputConfiguration configuration,
                                  DSpotCompiler compiler,
                                  Map<CtMethod<?>, List<CtLocalVariable<?>>> variableReadsAsserted) {
        this.originalClass = originalClass;
        this.configuration = configuration;
        this.compiler = compiler;
        this.factory = configuration.getFactory();
        this.variableReadsAsserted = variableReadsAsserted;
    }

    /**
     * Adds new assertions in multiple tests.
     * <p>
     * <p>Instruments the tests to have observation points.
     * Details in {@link AssertGeneratorHelper#createTestWithLog(CtMethod, String, List)}.
     * <p>
     * <p>Details of the assertion generation in {@link #buildTestWithAssert(CtMethod, Map)}.
     *
     * @param testClass Test class
     * @param testCases Passing test methods
     * @return New tests with new assertions generated from observation points values
     */
    public List<CtMethod<?>> addAssertions(CtType<?> testClass, List<CtMethod<?>> testCases) {
        CtType clone = testClass.clone();
        testClass.getPackage().addType(clone);
        LOGGER.info("Add observations points in passing tests.");
        LOGGER.info("Instrumentation...");
        final List<CtMethod<?>> testCasesWithLogs;
        try {
            testCasesWithLogs = addLogs(testCases);
            final List<CtMethod<?>> testsToRun = setupTests(testCasesWithLogs,clone);
            Map<String, Observation> observations = compileRunTests(clone,testsToRun);
            return addAssertions(testCases,observations);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.EMPTY_LIST;
        }
    }

    private List<CtMethod<?>> addAssertions(List<CtMethod<?>> testCases,Map<String, Observation> observations) {
        LOGGER.info("Generating assertions...");
        return testCases.stream()
                .map(ctMethod -> this.buildTestWithAssert(ctMethod, observations))
                .collect(Collectors.toList());
    }

    private Map<String, Observation> compileRunTests(CtType clone, final List<CtMethod<?>> testsToRun) throws Exception{
            final TestResult result = TestCompiler.compileAndRun(
                    clone,
                    this.compiler,
                    testsToRun,
                    this.configuration
            );
            if (!result.getFailingTests().isEmpty()) {
                LOGGER.warn("Some instrumented test failed!");
            }
            return ObjectLog.getObservations();

    }

    private List<CtMethod<?>> addLogs(List<CtMethod<?>> testCases) throws Exception{
        List<CtMethod<?>> testCasesWithLogs = testCases.stream()
                .map(ctMethod -> {
                            DSpotUtils.printProgress(testCases.indexOf(ctMethod), testCases.size());
                            return AssertGeneratorHelper.createTestWithLog(
                                    ctMethod,
                                    this.originalClass.getPackage().getQualifiedName(),
                                    this.variableReadsAsserted.get(ctMethod)
                            );
                        }
                ).filter(ctMethod -> !ctMethod.getBody().getStatements().isEmpty())
                .collect(Collectors.toList());
        if (testCasesWithLogs.isEmpty()) {
            LOGGER.warn("Could not continue the assertion amplification since all the instrumented test have an empty body.");
            throw new Exception();
        }
        return testCasesWithLogs;
    }

    private List<CtMethod<?>> setupTests(List<CtMethod<?>> testCasesWithLogs, CtType clone){
        final List<CtMethod<?>> testsToRun = new ArrayList<>();
        IntStream.range(0, 3).forEach(i -> testsToRun.addAll(
                testCasesWithLogs.stream()

                        //Optimization: Tracking cloned test methods using AmplificationHelper as candidates
                        //for caching their associated Test Framework
                        .map(CloneHelper::cloneMethod)
                        .peek(ctMethod -> ctMethod.setSimpleName(ctMethod.getSimpleName() + i))
                        .peek(clone::addMethod)
                        .collect(Collectors.toList())
        ));
        ObjectLog.reset();
        LOGGER.info("Run instrumented tests. ({})", testsToRun.size());
        TestFramework.get().generateAfterClassToSaveObservations(clone, testsToRun);
        return testsToRun;
    }

    /**
     * Adds new assertions to a test from observation points.
     *
     * @param test         Test method
     * @param observations Observation points of the test suite
     * @return Test with new assertions
     */
    @SuppressWarnings("unchecked")
    private CtMethod<?> buildTestWithAssert(CtMethod test, Map<String, Observation> observations) {
        CtMethod testWithAssert = CloneHelper.cloneTestMethodForAmp(test, "");
        int numberOfAddedAssertion = 0;
        List<CtStatement> statements = Query.getElements(testWithAssert, new TypeFilter(CtStatement.class));

        // for every observation, create an assertion
        for (String id : observations.keySet()) {
            if (!id.split("__")[0].equals(testWithAssert.getSimpleName())) {
                continue;
            }
            final List<CtStatement> assertStatements = AssertBuilder.buildAssert(
                    test,
                    observations.get(id).getNotDeterministValues(),
                    observations.get(id).getObservationValues(),
                    Double.parseDouble(configuration.getDelta())
            );

            // skip the current observation if it leads to an assertion identical to the last assertion put into the test method
            if (assertStatements.stream()
                    .map(Object::toString)
                    .map("// AssertGenerator add assertion\n"::concat)
                    .anyMatch(testWithAssert.getBody().getLastStatement().toString()::equals)) {
                continue;
            }
            int line = Integer.parseInt(id.split("__")[1]);
            CtStatement lastStmt = null;
            for (CtStatement assertStatement : assertStatements) {
                DSpotUtils.addComment(assertStatement, "AssertGenerator add assertion", CtComment.CommentType.INLINE);
                try {
                    CtStatement statementToBeAsserted = statements.get(line);
                    if (lastStmt == null) {
                        lastStmt = statementToBeAsserted;
                    }
                    if (statementToBeAsserted instanceof CtBlock) {
                        break;
                    }

                    // if the statement to be asserted is a method or constructor call, replace that invocation in the
                    // assertion with an equivalent local variable
                    if (statementToBeAsserted instanceof CtInvocation &&
                            !AssertGeneratorHelper.isVoidReturn((CtInvocation) statementToBeAsserted) &&
                            statementToBeAsserted.getParent() instanceof CtBlock) {

                        // create a new local variable and assign the invocation to it
                        CtInvocation invocationToBeReplaced = (CtInvocation) statementToBeAsserted.clone();
                        final CtLocalVariable localVariable = factory.createLocalVariable(
                                AssertGeneratorHelper.getCorrectTypeOfInvocation(invocationToBeReplaced),
                                "o_" + id.split("___")[0],
                                invocationToBeReplaced
                        );

                        // put the new local variable into the assertion and the assertion into the test method
                        statementToBeAsserted.replace(localVariable);
                        DSpotUtils.addComment(localVariable, "AssertGenerator create local variable with return value of invocation", CtComment.CommentType.INLINE);
                        localVariable.setParent(statementToBeAsserted.getParent());
                        addAtCorrectPlace(id, localVariable, assertStatement, statementToBeAsserted);
                        statements.remove(line);
                        statements.add(line, localVariable);

                        // no creation of local variable is needed, just put the assertion into the test method
                    } else {
                        addAtCorrectPlace(id, lastStmt, assertStatement, statementToBeAsserted);
                    }
                    lastStmt = assertStatement;
                    numberOfAddedAssertion++;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        Counter.updateAssertionOf(testWithAssert, numberOfAddedAssertion);
        if (!testWithAssert.equals(test)) {
            return testWithAssert;
        } else {
            AmplificationHelper.removeAmpTestParent(testWithAssert);
            return null;
        }
    }

    private void addAtCorrectPlace(String id,
                                   CtStatement lastStmt,
                                   CtStatement assertStatement,
                                   CtStatement statementToBeAsserted) {
        if (id.endsWith("end")) {
            statementToBeAsserted.getParent(CtBlock.class).insertEnd(assertStatement);
        } else {
            lastStmt.insertAfter(assertStatement);
        }
    }
}
