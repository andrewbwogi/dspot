package eu.stamp_project.dspot.configuration;

import eu.stamp_project.automaticbuilder.maven.DSpotPOMCreator;
import eu.stamp_project.dspot.AmplificationException;
import eu.stamp_project.dspot.common.testTuple;
import eu.stamp_project.dspot.selector.TestSelector;
import eu.stamp_project.test_framework.TestFramework;
import eu.stamp_project.testrunner.EntryPoint;
import eu.stamp_project.utils.AmplificationHelper;
import eu.stamp_project.utils.Counter;
import eu.stamp_project.utils.compilation.DSpotCompiler;
import eu.stamp_project.utils.compilation.TestCompiler;
import eu.stamp_project.utils.report.GlobalReport;
import eu.stamp_project.utils.report.error.Error;
import eu.stamp_project.utils.report.output.Output;
import eu.stamp_project.utils.report.output.selector.TestSelectorElementReport;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static eu.stamp_project.utils.report.error.ErrorEnum.ERROR_EXEC_TEST_BEFORE_AMPLIFICATION;
import static eu.stamp_project.utils.report.error.ErrorEnum.ERROR_PRE_SELECTION;

/**
 * Created by Andrew Bwogi
 * abwogi@kth.se
 * on 31/10/19
 */
public class AmplificationSetup {

    public  final GlobalReport GLOBAL_REPORT;
    private final Logger LOGGER;
    final List<CtType<?>> amplifiedTestClasses = new ArrayList<>();
    private DSpotConfiguration configuration;
    private DSpotCompiler compiler;
    private TestCompiler testCompiler;
    private TestSelector testSelector;
    private Output output;
    private long time;

    public AmplificationSetup(DSpotConfiguration configuration){
        this.configuration = configuration;
        compiler = configuration.getCompiler();
        testCompiler = configuration.getTestCompiler();
        testSelector = configuration.getTestSelector();
        output = configuration.getOutput();
        LOGGER = configuration.getLogger();
        GLOBAL_REPORT = configuration.getGlobalReport();
    }

    public testTuple preAmplification(CtType<?> testClassToBeAmplified, List<String> testMethodsToBeAmplifiedAsString){
        configuration.getInputAmplDistributor().resetAmplifiers(testClassToBeAmplified);
        configuration.clearData();
        final List<CtMethod<?>> testMethodsToBeAmplified =
                configuration.getTestFinder().findTestMethods(testClassToBeAmplified, testMethodsToBeAmplifiedAsString);

        // here, we base the execution mode to the first test method given.
        // the user should provide whether JUnit3/4 OR JUnit5 but not both at the same time.
        // TODO DSpot could be able to switch from one to another version of JUnit, but I believe that the ROI is not worth it.
        final boolean jUnit5 = TestFramework.isJUnit5(testMethodsToBeAmplified.get(0));
        EntryPoint.jUnit5Mode = jUnit5;
        DSpotPOMCreator.isCurrentlyJUnit5 = jUnit5;
        Counter.reset();
        if (configuration.getInputConfiguration().shouldGenerateAmplifiedTestClass()) {
            testClassToBeAmplified = AmplificationHelper.renameTestClassUnderAmplification(testClassToBeAmplified);
        }
        time = System.currentTimeMillis();
        testTuple tuple = new testTuple(testClassToBeAmplified,testMethodsToBeAmplified);
        return tuple;
    }

    public void postAmplification(CtType<?> testClassToBeAmplified,List<CtMethod<?>> amplifiedTestMethods){
        final long elapsedTime = System.currentTimeMillis() - time;
        LOGGER.info("elapsedTime {}", elapsedTime);
        this.output.addClassTimeJSON(testClassToBeAmplified.getQualifiedName(), elapsedTime);
        LOGGER.debug("OPTIMIZATION: GC invoked");
        System.gc(); //Optimization: cleaning up heap before printing the amplified class
        configuration.getAutomaticBuilder().reset();
        try {
            final TestSelectorElementReport report = configuration.getTestSelector().report();
            this.output.reportSelectorInformation(report.getReportForCollector());
            GLOBAL_REPORT.addTestSelectorReportForTestClass(testClassToBeAmplified, report);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Something bad happened during the report fot test-criterion.");
            LOGGER.error("Dspot might not have output correctly!");
        }
        final CtType<?> amplifiedTestClass = this.output.output(testClassToBeAmplified, amplifiedTestMethods);
        amplifiedTestClasses.add(amplifiedTestClass);
        cleanAfterAmplificationOfOneTestClass(configuration.getCompiler(), testClassToBeAmplified);
    }

    public List<CtType<?>> getAmplifiedTestClasses(){
        return amplifiedTestClasses;
    }

    private void cleanAfterAmplificationOfOneTestClass(DSpotCompiler compiler, CtType<?> testClassToBeAmplified) {
        // Cleaning modified source directory by DSpot
        try {
            FileUtils.cleanDirectory(compiler.getSourceOutputDirectory());
        } catch (Exception exception) {
            exception.printStackTrace();
            LOGGER.warn("Something went wrong when trying to cleaning temporary sources directory: {}",
                    compiler.getSourceOutputDirectory());
        }

        // Cleaning binary generated by Dspot
        try {
            String pathToDotClass = compiler.getBinaryOutputDirectory().getAbsolutePath() + "/" +
                    testClassToBeAmplified.getQualifiedName().replaceAll("\\.", "/") + ".class";
            FileUtils.forceDelete(new File(pathToDotClass));
        } catch (IOException ignored) {
            //ignored
        }
    }

    public List<CtMethod<?>> firstSelectorSetup(CtType<?> testClassToBeAmplified, List<CtMethod<?>> testMethodsToBeAmplified)
            throws Exception {
        if(testMethodsToBeAmplified.isEmpty()) {
            LOGGER.warn("No test provided for amplification in class {}", testClassToBeAmplified.getQualifiedName());
            throw new Exception();
        }
        LOGGER.info("Amplification of {} ({} test(s))", testClassToBeAmplified.getQualifiedName(), testMethodsToBeAmplified.size());
        LOGGER.info("Assertion amplification of {} ({} test(s))", testClassToBeAmplified.getQualifiedName(), testMethodsToBeAmplified.size());
        if (!testSelector.init()) {
            throw new Exception();
        }
        final List<CtMethod<?>> passingTests;
        try {
            passingTests = testCompiler.compileRunAndDiscardUncompilableAndFailingTestMethods(
                            testClassToBeAmplified,
                            testMethodsToBeAmplified,
                            compiler);
        } catch (Exception | java.lang.Error e) {
            GLOBAL_REPORT.addError(new Error(ERROR_EXEC_TEST_BEFORE_AMPLIFICATION, e));
            throw new Exception();
        }
        final List<CtMethod<?>> selectedToBeAmplified;
        try {
            selectedToBeAmplified = testSelector.selectToAmplify(testClassToBeAmplified, passingTests);
        } catch (Exception | java.lang.Error e) {
            GLOBAL_REPORT.addError(new Error(ERROR_PRE_SELECTION, e));
            throw new Exception();
        }
        return selectedToBeAmplified;
    }

    public List<CtMethod<?>> fullSelectorSetup(CtType<?> testClassToBeAmplified,
                                               List<CtMethod<?>> currentTestListToBeAmplified) throws AmplificationException {
        final List<CtMethod<?>> selectedToBeAmplified;
        try {
            selectedToBeAmplified = testSelector.selectToAmplify(testClassToBeAmplified, currentTestListToBeAmplified);
        } catch (Exception | java.lang.Error e) {
            GLOBAL_REPORT.addError(new Error(ERROR_PRE_SELECTION, e));
            throw new AmplificationException("");
        }
        if (selectedToBeAmplified.isEmpty()) {
            LOGGER.warn("No test could be selected to be amplified.");
            // todo should we break the loop?
            throw new AmplificationException("");
        }
        LOGGER.info("{} tests selected to be amplified over {} available tests",
                selectedToBeAmplified.size(),
                currentTestListToBeAmplified.size()
        );
        return selectedToBeAmplified;
    }
}
