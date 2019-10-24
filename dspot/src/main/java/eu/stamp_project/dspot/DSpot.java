package eu.stamp_project.dspot;

import eu.stamp_project.automaticbuilder.AutomaticBuilder;
import eu.stamp_project.automaticbuilder.maven.DSpotPOMCreator;
import eu.stamp_project.dspot.common.testTuple;
import eu.stamp_project.dspot.configuration.Configuration;
import eu.stamp_project.dspot.input_ampl_distributor.InputAmplDistributor;
import eu.stamp_project.dspot.selector.TestSelector;
import eu.stamp_project.test_framework.TestFramework;
import eu.stamp_project.testrunner.EntryPoint;
import eu.stamp_project.utils.AmplificationHelper;
import eu.stamp_project.utils.Counter;
import eu.stamp_project.utils.DSpotCache;
import eu.stamp_project.utils.compilation.DSpotCompiler;
import eu.stamp_project.utils.options.check.Checker;
import eu.stamp_project.utils.report.GlobalReport;
import eu.stamp_project.utils.report.error.ErrorReportImpl;
import eu.stamp_project.utils.report.output.Output;
import eu.stamp_project.utils.report.output.OutputReportImpl;
import eu.stamp_project.utils.report.output.selector.TestSelectorElementReport;
import eu.stamp_project.utils.report.output.selector.TestSelectorReportImpl;
import eu.stamp_project.utils.test_finder.TestFinder;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: Simon
 * Date: 08/06/15
 * Time: 17:36
 */
public class DSpot {

    public static final GlobalReport GLOBAL_REPORT =
            new GlobalReport(new OutputReportImpl(), new ErrorReportImpl(), new TestSelectorReportImpl());
    private static final Logger LOGGER = LoggerFactory.getLogger(DSpot.class);
    public static boolean verbose;

    private static Output output;
    private final long startTime;

    Configuration configuration;

    public DSpot(String[] args){
        configuration = new Configuration(args);
        startTime = System.currentTimeMillis();
        configuration.run();
        this.output = Configuration.getOutput();
    }

    public DSpot(double delta,
                 TestFinder testFinder,
                 DSpotCompiler compiler,
                 TestSelector testSelector,
                 InputAmplDistributor inputAmplDistributor,
                 Output output,
                 int numberOfIterations,
                 boolean shouldGenerateAmplifiedTestClass,
                 AutomaticBuilder automaticBuilder) {
        Configuration.getInputConfiguration().setDelta(delta);
        Configuration.setTestSelector(testSelector);
        Configuration.setInputAmplDistributor(inputAmplDistributor);
        Configuration.getInputConfiguration().setNbIteration(numberOfIterations);
        Configuration.setTestFinder(testFinder);
        Configuration.setCompiler(compiler);
        this.output = output;
        Configuration.getInputConfiguration().setGenerateAmplifiedTestClass(shouldGenerateAmplifiedTestClass);
        Configuration.setAutomaticBuilder(automaticBuilder);
        startTime = System.currentTimeMillis();

    }

    public void run() {

        Checker.postChecking(Configuration.getInputConfiguration());

        // starting amplification
        final List<CtType<?>> amplifiedTestClasses = amplify(Configuration.getTestClassesToBeAmplified(), Configuration.getTestMethodsToBeAmplifiedNames());
        report(amplifiedTestClasses);
    }

    private void report(List<CtType<?>> amplifiedTestClasses) {
        LOGGER.info("Amplification {}.", amplifiedTestClasses.isEmpty() ? "failed" : "succeed");
        final long elapsedTime = System.currentTimeMillis() - startTime;
        LOGGER.info("Elapsed time {} ms", elapsedTime);
        // global report handling
        GLOBAL_REPORT.output(Configuration.getInputConfiguration().getOutputDirectory());
        DSpotCache.reset();
        GLOBAL_REPORT.reset();
        AmplificationHelper.reset();
        DSpotPOMCreator.delete();
        // Send info collected.
        Configuration.getCollector().sendInfo();
    }


    /**
     *
     *
     *
     *
     *
     *
     *
     * @param testClassToBeAmplified
     * @return
     */

    public CtType<?> amplify(CtType<?> testClassToBeAmplified, List<String> testMethodsToBeAmplifiedAsString) {
        return this.amplify(Collections.singletonList(testClassToBeAmplified), testMethodsToBeAmplifiedAsString).get(0);
    }

    public List<CtType<?>> amplify(List<CtType<?>> testClassesToBeAmplified, List<String> testMethodsToBeAmplifiedAsString) {
        for (CtType<?> testClassToBeAmplified : testClassesToBeAmplified) {
            testTuple tuple = configuration.preAmplification(testClassToBeAmplified,testMethodsToBeAmplifiedAsString);
            /**
             * AMPLIFICATION
             */
            // Amplification of the given test methods of the given test class
            final List<CtMethod<?>> amplifiedTestMethods =
                    configuration.getTestAmplifier().amplification(tuple.testClassToBeAmplified, tuple.testMethodsToBeAmplified);
            /**
             * AMPLIFICATION
             */
            configuration.postAmplification(testClassToBeAmplified,amplifiedTestMethods);
        }
        return configuration.getAmplifiedTestClasses();
    }
}
