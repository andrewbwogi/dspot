package eu.stamp_project.dspot;

import eu.stamp_project.Main;
import eu.stamp_project.automaticbuilder.AutomaticBuilder;
import eu.stamp_project.dspot.assertiongenerator.AssertionGenerator;
import eu.stamp_project.dspot.common.testTuple;
import eu.stamp_project.dspot.configuration.Configuration;
import eu.stamp_project.dspot.input_ampl_distributor.InputAmplDistributor;
import eu.stamp_project.dspot.selector.TestSelector;
import eu.stamp_project.utils.compilation.DSpotCompiler;
import eu.stamp_project.utils.compilation.TestCompiler;
import eu.stamp_project.utils.program.InputConfiguration;
import eu.stamp_project.utils.report.error.Error;
import eu.stamp_project.utils.report.output.Output;
import eu.stamp_project.utils.test_finder.TestFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static eu.stamp_project.utils.report.error.ErrorEnum.*;
import static eu.stamp_project.utils.report.error.ErrorEnum.ERROR_ASSERT_AMPLIFICATION;
import static eu.stamp_project.utils.report.error.ErrorEnum.ERROR_SELECTION;

/**
 * User: Simon
 * Date: 08/06/15
 * Time: 17:36
 */
public class DSpot {

    public static boolean verbose;

    Configuration configuration;

    public DSpot(InputConfiguration inputConfiguration){
        configuration = new Configuration(inputConfiguration);
        configuration.run();
    }

    private TestCompiler testCompiler;

    public DSpot(double delta,
                 TestFinder testFinder,
                 DSpotCompiler compiler,
                 TestSelector testSelector,
                 InputAmplDistributor inputAmplDistributor,
                 Output output,
                 int numberOfIterations,
                 boolean shouldGenerateAmplifiedTestClass,
                 AutomaticBuilder automaticBuilder,
                 TestCompiler testCompiler) {
        Configuration.getInputConfiguration().setDelta(delta);
        Configuration.setTestSelector(testSelector);
        Configuration.setInputAmplDistributor(inputAmplDistributor);
        Configuration.getInputConfiguration().setNbIteration(numberOfIterations);
        Configuration.setTestFinder(testFinder);
        Configuration.setCompiler(compiler);
        Configuration.setOutput(output);
        Configuration.getInputConfiguration().setGenerateAmplifiedTestClass(shouldGenerateAmplifiedTestClass);
        Configuration.setAutomaticBuilder(automaticBuilder);
        Configuration.setTestCompiler(testCompiler);
    }

    public void run() {

        // starting amplification
        final List<CtType<?>> amplifiedTestClasses = amplify(Configuration.getTestClassesToBeAmplified(), Configuration.getTestMethodsToBeAmplifiedNames());
        configuration.report(amplifiedTestClasses);
    }

    public CtType<?> amplify(CtType<?> testClassToBeAmplified, List<String> testMethodsToBeAmplifiedAsString) {
        return this.amplify(Collections.singletonList(testClassToBeAmplified), testMethodsToBeAmplifiedAsString).get(0);
    }

    public List<CtType<?>> amplify(List<CtType<?>> testClassesToBeAmplified, List<String> testMethodsToBeAmplifiedAsString) {
        for (CtType<?> testClassToBeAmplified : testClassesToBeAmplified) {
            testTuple tuple = configuration.preAmplification(testClassToBeAmplified,testMethodsToBeAmplifiedAsString);

            // Amplification of the given test methods of the given test class
            final List<CtMethod<?>> amplifiedTestMethods = amplification(tuple.testClassToBeAmplified,tuple.testMethodsToBeAmplified);
            configuration.postAmplification(testClassToBeAmplified,amplifiedTestMethods);
        }
        return configuration.getAmplifiedTestClasses();
    }

    private int globalNumberOfSelectedAmplification = 0;

    // todo use configuration logger?
    private static final Logger LOGGER = LoggerFactory.getLogger(DSpot.class);

    // todo use testTuple everywhere
    public List<CtMethod<?>>  amplification(CtType<?> testClassToBeAmplified, List<CtMethod<?>> testMethodsToBeAmplified) {
        final List<CtMethod<?>> amplifiedTestMethodsToKeep = onlyAssertionGeneration(testClassToBeAmplified,testMethodsToBeAmplified);

        // in case there is no amplifier, we can leave
        if (configuration.getInputAmplDistributor().shouldBeRun()) {
            amplifyAllMethods(testClassToBeAmplified,testMethodsToBeAmplified,amplifiedTestMethodsToKeep);
        }
        return amplifiedTestMethodsToKeep;
    }

    public List<CtMethod<?>> onlyAssertionGeneration(CtType<?> testClassToBeAmplified, List<CtMethod<?>> testMethodsToBeAmplified){
        final List<CtMethod<?>> selectedToBeAmplified;
        final List<CtMethod<?>> amplifiedTestMethodsToKeep;
        try {
            selectedToBeAmplified = configuration.firstSelectorSetup(testClassToBeAmplified,testMethodsToBeAmplified);

            // generate tests with additional assertions
            final List<CtMethod<?>> assertionAmplifiedTestMethods = this.assertionsAmplification(testClassToBeAmplified, selectedToBeAmplified);
            amplifiedTestMethodsToKeep = selection(assertionAmplifiedTestMethods);
        } catch (Exception e) {
            return Collections.emptyList();
        }
        return amplifiedTestMethodsToKeep;
    }

    private void amplifyAllMethods(CtType<?> testClassToBeAmplified,List<CtMethod<?>> testMethodsToBeAmplified,List<CtMethod<?>> amplifiedTestMethodsToKeep){
        LOGGER.info("Applying Input-amplification and Assertion-amplification test by test.");
        for (int i = 0; i < testMethodsToBeAmplified.size(); i++) {
            CtMethod test = testMethodsToBeAmplified.get(i);
            LOGGER.info("Amplification of {}, ({}/{})", test.getSimpleName(), i + 1, testMethodsToBeAmplified.size());

            // tmp list for current test methods to be amplified
            // this list must be a implementation that support remove / clear methods
            List<CtMethod<?>> currentTestList = new ArrayList<>();
            currentTestList.add(test);
            final List<CtMethod<?>> amplifiedTests = new ArrayList<>();
            for (int j = 0; j < configuration.getInputConfiguration().getNbIteration() ; j++) {
                LOGGER.info("iteration {} / {}", j, configuration.getInputConfiguration().getNbIteration());

                // full amplification
                // generate tests with input modification and associated new assertions
                currentTestList = this.amplification(testClassToBeAmplified, currentTestList, amplifiedTests, j);
            }
            amplifiedTestMethodsToKeep.addAll(amplifiedTests);
            this.globalNumberOfSelectedAmplification += amplifiedTestMethodsToKeep.size();
            LOGGER.info("{} amplified test methods has been selected to be kept. (global: {})", amplifiedTests.size(), this.globalNumberOfSelectedAmplification);
        }
    }

    /**
     * Amplification of test methods
     *
     * DSpot combines the different kinds of I-Amplification iteratively: at each iteration all kinds of
     * I-Amplification are applied, resulting in new tests. From one iteration to another, DSpot reuses the
     * previously amplified tests, and further applies I-Amplification.
     *
     * @param testClassToBeAmplified        Test class
     * @param currentTestListToBeAmplified  Methods to amplify
     * @return Valid amplified tests
     */
    public List<CtMethod<?>> amplification(CtType<?> testClassToBeAmplified,
                                           List<CtMethod<?>> currentTestListToBeAmplified,
                                           List<CtMethod<?>> amplifiedTests,
                                           int currentIteration) {
        final List<CtMethod<?>> selectedToBeAmplified;
        final List<CtMethod<?>> inputAmplifiedTests;
        final List<CtMethod<?>> currentTestList;
        try {
            selectedToBeAmplified = configuration.fullSelectorSetup(testClassToBeAmplified,currentTestListToBeAmplified);

            // amplify tests and shrink amplified set with inputAmplDistributor
            inputAmplifiedTests = configuration.getInputAmplDistributor().inputAmplify(selectedToBeAmplified, currentIteration);

            // add assertions to input modified tests and return them
            // new amplified tests will be the basis for further amplification
            currentTestList = this.assertionsAmplification(testClassToBeAmplified, inputAmplifiedTests);

            // keep tests that improve the test suite
            selection2(currentTestList,amplifiedTests);
        } catch (AmplificationException e) {
            return Collections.emptyList();
        } catch (Exception | java.lang.Error e) {
            Main.GLOBAL_REPORT.addError(new Error(ERROR_INPUT_AMPLIFICATION, e));
            return Collections.emptyList();
        }
        return currentTestList;
    }

    private List<CtMethod<?>>  selection(List<CtMethod<?>> assertionAmplifiedTestMethods) throws Exception {
        final List<CtMethod<?>> amplifiedTestMethodsToKeep;
        try {
            // keep tests that improve the test suite
            amplifiedTestMethodsToKeep = configuration.getTestSelector().selectToKeep(assertionAmplifiedTestMethods);
        } catch (Exception | java.lang.Error e) {
            Main.GLOBAL_REPORT.addError(new Error(ERROR_SELECTION, e));
            //return Collections.emptyList();
            throw new Exception();
        }
        this.globalNumberOfSelectedAmplification += amplifiedTestMethodsToKeep.size();
        LOGGER.info("{} amplified test methods has been selected to be kept. (global: {})", amplifiedTestMethodsToKeep.size(), this.globalNumberOfSelectedAmplification);

        return amplifiedTestMethodsToKeep;
    }

    private void selection2(List<CtMethod<?>> currentTestList,List<CtMethod<?>> amplifiedTests) throws AmplificationException {
        final List<CtMethod<?>> amplifiedTestMethodsToKeep;
        try {
            amplifiedTestMethodsToKeep = configuration.getTestSelector().selectToKeep(currentTestList);
        } catch (Exception | java.lang.Error e) {
            Main.GLOBAL_REPORT.addError(new Error(ERROR_SELECTION, e));
            throw new AmplificationException("");
        }
        LOGGER.info("{} amplified test methods has been selected to be kept.", amplifiedTestMethodsToKeep.size());
        amplifiedTests.addAll(amplifiedTestMethodsToKeep);
    }

    public List<CtMethod<?>> assertionsAmplification(CtType<?> classTest, List<CtMethod<?>> testMethods) {
        final List<CtMethod<?>> testsWithAssertions;
        try {
            testsWithAssertions = configuration.getAssertionGenerator().assertionAmplification(classTest, testMethods);
        } catch (Exception | java.lang.Error e) {
            Main.GLOBAL_REPORT.addError(new Error(ERROR_ASSERT_AMPLIFICATION, e));
            return Collections.emptyList();
        }
        if (testsWithAssertions.isEmpty()) {
            return testsWithAssertions;
        }
        // final check on A-amplified test, see if they all pass.
        // If they don't, we just discard them.
        final List<CtMethod<?>> amplifiedPassingTests =
                configuration.getTestCompiler().compileRunAndDiscardUncompilableAndFailingTestMethods(
                        classTest,
                        testsWithAssertions,
                        configuration.getCompiler()
                );
        LOGGER.info("Assertion amplification: {} test method(s) has been successfully amplified.", amplifiedPassingTests.size());
        return amplifiedPassingTests;
    }
}
