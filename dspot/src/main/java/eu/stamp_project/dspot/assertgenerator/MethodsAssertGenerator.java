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
import spoon.support.reflect.reference.CtArrayTypeReferenceImpl;

import java.util.*;
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
     * @param testCases Passing and input amplified test methods
     * @return New tests with new assertions generated from observation points values
     */
    public List<CtMethod<?>> addAssertions(CtType<?> testClass, List<CtMethod<?>> testCases) {
        CtType clone = testClass.clone();
        testClass.getPackage().addType(clone);
        LOGGER.info("Add observations points in passing tests.");
        LOGGER.info("Instrumentation...");

        // add logs in tests to observe state of tested program
        final List<CtMethod<?>> testCasesWithLogs = testCases.stream()
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
            return testCasesWithLogs;
        }

        // clone and set up tests with added logs
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

        // compile and run tests with added logs
        LOGGER.info("Run instrumented tests. ({})", testsToRun.size());
        TestFramework.get().generateAfterClassToSaveObservations(clone, testsToRun);
        try {
            // todo debug
            System.out.println("/////////////////////////// printing clone");
            System.out.println(clone);
            final TestResult result = TestCompiler.compileAndRun(clone,
                    this.compiler,
                    testsToRun,
                    this.configuration
            );
            if (!result.getFailingTests().isEmpty()) {
                LOGGER.warn("Some instrumented test failed!");
            }
        } catch (AmplificationException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }

        // add assertions with values retrieved from logs in tests
        Map<String, Observation> observations = ObjectLog.getObservations();
        LOGGER.info("Generating assertions...");
        //todo debug
        //printMap(observations);
        System.out.println("ppppppppppppppppppp observations");
        for(String s : observations.keySet()){
            System.out.println("outer observation");
            System.out.println(s);
            for(String s2 : observations.get(s).getObservationValues().keySet()){
                System.out.println("inner observation: " + s2);
            //System.out.println(((int[]) observations.get(s).getObservationValues().get(s2))[1]);
            }
        }
        //System.out.println(((int[]) observations.get())[1]);
        return testCases.stream()
                .map(ctMethod -> this.buildTestWithAssert(ctMethod, observations))
                .collect(Collectors.toList());
    }
    public static void printMap(Map mp) {
        Iterator it = mp.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println(pair.getKey() + " = " + pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
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
            // todo debug
            System.out.println("--------------------new observation, new assertion");
            if (!id.split("__")[0].equals(testWithAssert.getSimpleName())) {
                continue;
            }
            final List<CtStatement> assertStatements = AssertBuilder.buildAssert(
                    test,
                    observations.get(id).getNotDeterministValues(),
                    observations.get(id).getObservationValues(),
                    Double.parseDouble(configuration.getDelta())
            );

            // skip the current observation if it produces an assertion that has already been added to the test method
            if (assertStatements.stream()
                    .map(Object::toString)
                    .map("// AssertGenerator add assertion\n"::concat)
                    .anyMatch(testWithAssert.getBody().getLastStatement().toString()::equals)) {
                continue;
            }
            int line = Integer.parseInt(id.split("__")[1]);
            CtStatement lastStmt = null;

            // for every assertStatement, prepare and put the assertStatement in the test method
            for (CtStatement assertStatement : assertStatements) {
                DSpotUtils.addComment(assertStatement, "AssertGenerator add assertion", CtComment.CommentType.INLINE);
                System.out.println("cccccc assertStatement: " + assertStatement);
                System.out.println("end assertStatement");
                try {
                    CtStatement statementToBeAsserted = statements.get(line);
                    if (lastStmt == null) {
                        lastStmt = statementToBeAsserted;
                    }
                    if (statementToBeAsserted instanceof CtBlock) {
                        break;
                    }
                    System.out.println("bbbbbb: " + statementToBeAsserted);
                    System.out.println("end");
                    // if statement to be asserted is an array, create a local variable
                    System.out.println("content of invocation: " + ((CtInvocation) assertStatement).getArguments().get(0).getClass());
                    System.out.println("class of statementToBeAsserted: " + statementToBeAsserted);
                    if (((CtInvocation) assertStatement).getArguments().get(1) instanceof CtArrayTypeReferenceImpl &&
                            statementToBeAsserted.getParent() instanceof CtBlock) {
                        System.out.println("aaaaaaaaaaaaaaaa we have an array");

                    }
                    // if statement to be asserted is a method or constructor call, create a local variable
                    else if (statementToBeAsserted instanceof CtInvocation &&
                            !AssertGeneratorHelper.isVoidReturn((CtInvocation) statementToBeAsserted) &&
                            statementToBeAsserted.getParent() instanceof CtBlock) {
                        // todo debug
                        System.out.println(statementToBeAsserted);
                        // replace the invocation with a local variable
                        CtInvocation invocationToBeReplaced = (CtInvocation) statementToBeAsserted.clone();
                        final CtLocalVariable localVariable = factory.createLocalVariable(
                                AssertGeneratorHelper.getCorrectTypeOfInvocation(invocationToBeReplaced),
                                "o_" + id.split("___")[0],
                                invocationToBeReplaced
                        );

                        // place the local variable and the assertion in the test method
                        statementToBeAsserted.replace(localVariable);
                        DSpotUtils.addComment(localVariable, "AssertGenerator create local variable with return value of invocation", CtComment.CommentType.INLINE);
                        localVariable.setParent(statementToBeAsserted.getParent());
                        addAtCorrectPlace(id, localVariable, assertStatement, statementToBeAsserted);
                        statements.remove(line);
                        statements.add(line, localVariable);

                    // no creation of local variable is needed, just place the assertion in the test method
                    } else {
                        // todo debug
                        System.out.println("ooooooooooooooo assignment");
                        System.out.println(statementToBeAsserted);
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
            // todo debug
            System.out.println("!!!!!!!!!!!!!!!!!!!! TEST WITH ASSERT");
            System.out.println(testWithAssert);
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
