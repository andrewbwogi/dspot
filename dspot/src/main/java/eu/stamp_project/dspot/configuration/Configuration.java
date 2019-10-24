package eu.stamp_project.dspot.configuration;

import eu.stamp_project.automaticbuilder.AutomaticBuilder;
import eu.stamp_project.automaticbuilder.maven.DSpotPOMCreator;
import eu.stamp_project.dspot.Amplification;
import eu.stamp_project.dspot.DSpot;
import eu.stamp_project.dspot.amplifier.Amplifier;
import eu.stamp_project.dspot.assertiongenerator.assertiongenerator.AssertionGeneratorUtils;
import eu.stamp_project.dspot.common.testTuple;
import eu.stamp_project.dspot.input_ampl_distributor.InputAmplDistributor;
import eu.stamp_project.dspot.selector.TestSelector;
import eu.stamp_project.test_framework.TestFramework;
import eu.stamp_project.testrunner.EntryPoint;
import eu.stamp_project.utils.*;
import eu.stamp_project.utils.collector.Collector;
import eu.stamp_project.utils.collector.CollectorFactory;
import eu.stamp_project.utils.compilation.DSpotCompiler;
import eu.stamp_project.utils.compilation.TestCompiler;
import eu.stamp_project.utils.execution.TestRunner;
import eu.stamp_project.utils.options.AmplifierEnum;
import eu.stamp_project.utils.options.check.Checker;
import eu.stamp_project.utils.options.check.InputErrorException;
import eu.stamp_project.utils.program.InputConfiguration;
import eu.stamp_project.utils.report.GlobalReport;
import eu.stamp_project.utils.report.error.ErrorReportImpl;
import eu.stamp_project.utils.report.output.Output;
import eu.stamp_project.utils.report.output.OutputReportImpl;
import eu.stamp_project.utils.report.output.selector.TestSelectorElementReport;
import eu.stamp_project.utils.report.output.selector.TestSelectorReportImpl;
import eu.stamp_project.utils.smtp.EmailSender;
import eu.stamp_project.utils.test_finder.TestFinder;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static eu.stamp_project.utils.AmplificationHelper.PATH_SEPARATOR;

public class Configuration {

    private static InputConfiguration inputConfiguration;

    // todo move to input configuration
    private static boolean verbose;
    private static List<CtType<?>> testClassesToBeAmplified;
    private static List<String> testMethodsToBeAmplifiedNames;
    private static TestSelector testSelector;
    private static InputAmplDistributor inputAmplDistributor;
    private static Output output;
    private static Collector collector;
    private static DSpotCompiler compiler;
    private static AutomaticBuilder automaticBuilder;
    private static TestFinder testFinder;

    /**
     *
     * FOM MAIN
     *
     * @param args
     */
    public Configuration(String[] args) {
        inputConfiguration = new InputConfiguration();
        final CommandLine commandLine = new CommandLine(inputConfiguration);
        try {
            commandLine.parseArgs(args);
        } catch (Exception e) {
            e.printStackTrace();
            commandLine.usage(System.err);
            return;
        }
        if (commandLine.isUsageHelpRequested()) {
            commandLine.usage(System.out);
            return;
        }
        if (commandLine.isVersionHelpRequested()) {
            commandLine.printVersionHelp(System.out);
            return;
        }
        if (inputConfiguration.shouldRunExample()) {
            inputConfiguration.configureExample();
        }
        try {
            Checker.preChecking(inputConfiguration);
        } catch (InputErrorException e) {
            e.printStackTrace();
            commandLine.usage(System.err);
            return;
        }
        verbose = inputConfiguration.isVerbose();
    }

    public static void run() {
        testFinder = new TestFinder(
                Arrays.stream(inputConfiguration.getExcludedClasses().split(",")).collect(Collectors.toList()),
                Arrays.stream(inputConfiguration.getExcludedTestCases().split(",")).collect(Collectors.toList())
        );
        automaticBuilder = inputConfiguration.getBuilderEnum().getAutomaticBuilder(inputConfiguration);
        final String dependencies = completeDependencies(inputConfiguration, automaticBuilder);
        compiler = DSpotCompiler.createDSpotCompiler(
                inputConfiguration,
                dependencies
        );
        initHelpers(inputConfiguration, compiler.getLauncher().getFactory());
        inputConfiguration.setFactory(compiler.getLauncher().getFactory());
        final EmailSender emailSender = new EmailSender(
                inputConfiguration.getSmtpUsername(),
                inputConfiguration.getSmtpPassword(),
                inputConfiguration.getSmtpHost(),
                inputConfiguration.getSmtpPort(),
                inputConfiguration.isSmtpAuth(),
                inputConfiguration.getSmtpTls()
        );
        collector = CollectorFactory.build(inputConfiguration, emailSender);
        collector.reportInitInformation(
                inputConfiguration.getAmplifiers(),
                inputConfiguration.getSelector(),
                inputConfiguration.getNbIteration(),
                inputConfiguration.isGregorMode(),
                !inputConfiguration.isGregorMode(),
                inputConfiguration.getNumberParallelExecutionProcessors()
        );
        testClassesToBeAmplified = testFinder.findTestClasses(inputConfiguration.getTestClasses());
        testMethodsToBeAmplifiedNames = inputConfiguration.getTestCases();
        testSelector = inputConfiguration.getSelector().buildSelector(automaticBuilder, inputConfiguration);
        final List<Amplifier> amplifiers = inputConfiguration
                .getAmplifiers()
                .stream()
                .map(AmplifierEnum::getAmplifier)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        inputAmplDistributor = inputConfiguration
                .getInputAmplDistributor()
                .getInputAmplDistributor(inputConfiguration.getMaxTestAmplified(), amplifiers);
        output = new Output(
                inputConfiguration.getAbsolutePathToProjectRoot(),
                inputConfiguration.getOutputDirectory(),
                collector

        );
    }

    private static void initHelpers(InputConfiguration configuration, Factory factory){
        TestFramework.init(factory);
        AmplificationHelper.init(
                configuration.getTimeOutInMs(),
                configuration.shouldGenerateAmplifiedTestClass(),
                configuration.shouldKeepOriginalTestMethods()
        );
        RandomHelper.setSeedRandom(configuration.getSeed());
        createOutputDirectories(configuration);
        DSpotCache.init(configuration.getCacheSize());
        TestCompiler.init(
                configuration.getNumberParallelExecutionProcessors(),
                configuration.shouldExecuteTestsInParallel(),
                configuration.getAbsolutePathToProjectRoot(),
                configuration.getClasspathClassesProject(),
                configuration.getTimeOutInMs()
        );
        DSpotUtils.init(
                configuration.withComment(),
                configuration.getOutputDirectory(),
                configuration.getFullClassPathWithExtraDependencies(),
                configuration.getAbsolutePathToProjectRoot()
        );
        initSystemProperties(configuration.getSystemProperties());
        AssertionGeneratorUtils.init(configuration.shouldAllowPathInAssertion());
        CloneHelper.init(configuration.shouldExecuteTestsInParallel());
        TestRunner.init(
                configuration.getAbsolutePathToProjectRoot(),
                configuration.getPreGoalsTestExecution(),
                configuration.shouldUseMavenToExecuteTest()
        );
    }

    private static void initSystemProperties(String systemProperties) {
        if (!systemProperties.isEmpty()) {
            Arrays.stream(systemProperties.split(","))
                    .forEach(systemProperty -> {
                        String[] keyValueInArray = systemProperty.split("=");
                        System.getProperties().put(keyValueInArray[0], keyValueInArray[1]);
                    });
        }
    }

    public static String completeDependencies(InputConfiguration configuration,
                                              AutomaticBuilder automaticBuilder) {
        String dependencies = configuration.getDependencies();
        final String additionalClasspathElements = configuration.getAdditionalClasspathElements();
        final String absolutePathToProjectRoot = configuration.getAbsolutePathToProjectRoot();
        if (dependencies.isEmpty()) {
            dependencies = automaticBuilder.compileAndBuildClasspath();
            configuration.setDependencies(dependencies);
        }
//      TODO checks this. Since we support different Test Support, we may not need to add artificially junit in the classpath
        if (!dependencies.contains("junit" + File.separator + "junit" + File.separator + "4")) {
            dependencies = Test.class
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .getFile() +
                    AmplificationHelper.PATH_SEPARATOR + dependencies;
        }
        if (!additionalClasspathElements.isEmpty()) {
            String pathToAdditionalClasspathElements = additionalClasspathElements;
            if (!Paths.get(additionalClasspathElements).isAbsolute()) {
                pathToAdditionalClasspathElements =
                        DSpotUtils.shouldAddSeparator.apply(absolutePathToProjectRoot + additionalClasspathElements);
            }
            dependencies += PATH_SEPARATOR + pathToAdditionalClasspathElements;
        }
        return dependencies;
    }

    public static void createOutputDirectories(InputConfiguration configuration) {
        final File outputDirectory = new File(configuration.getOutputDirectory());
        try {
            if (configuration.shouldClean() && outputDirectory.exists()) {
                FileUtils.forceDelete(outputDirectory);
            }
            if (!outputDirectory.exists()) {
                FileUtils.forceMkdir(outputDirectory);
            }
        } catch (IOException ignored) {
            // ignored
        }
    }


    public static List<CtType<?>> getTestClassesToBeAmplified() {
        return testClassesToBeAmplified;
    }

    public static List<String> getTestMethodsToBeAmplifiedNames() {
        return testMethodsToBeAmplifiedNames;
    }

    public static TestSelector getTestSelector() {
        return testSelector;
    }

    public static InputAmplDistributor getInputAmplDistributor() {
        return inputAmplDistributor;
    }

    public static Output getOutput() {
        return output;
    }

    public static InputConfiguration getInputConfiguration() {
        return inputConfiguration;
    }

    public static Collector getCollector() {
        return collector;
    }

    public static DSpotCompiler getCompiler() {
        return compiler;
    }

    public static AutomaticBuilder getAutomaticBuilder() {
        return automaticBuilder;
    }

    public static TestFinder getTestFinder() {
        return testFinder;
    }

    public static void setInputConfiguration(InputConfiguration inputConfiguration) {
        Configuration.inputConfiguration = inputConfiguration;
    }

    public static void setVerbose(boolean verbose) {
        Configuration.verbose = verbose;
    }

    public static void setTestClassesToBeAmplified(List<CtType<?>> testClassesToBeAmplified) {
        Configuration.testClassesToBeAmplified = testClassesToBeAmplified;
    }

    public static void setTestMethodsToBeAmplifiedNames(List<String> testMethodsToBeAmplifiedNames) {
        Configuration.testMethodsToBeAmplifiedNames = testMethodsToBeAmplifiedNames;
    }

    public static void setTestSelector(TestSelector testSelector) {
        Configuration.testSelector = testSelector;
    }

    public static void setInputAmplDistributor(InputAmplDistributor inputAmplDistributor) {
        Configuration.inputAmplDistributor = inputAmplDistributor;
    }

    public static void setOutput(Output output) {
        Configuration.output = output;
    }

    public static void setCollector(Collector collector) {
        Configuration.collector = collector;
    }

    public static void setCompiler(DSpotCompiler compiler) {
        Configuration.compiler = compiler;
    }

    public static void setAutomaticBuilder(AutomaticBuilder automaticBuilder) {
        Configuration.automaticBuilder = automaticBuilder;
    }

    public static void setTestFinder(TestFinder testFinder) {
        Configuration.testFinder = testFinder;
    }

    /**
     *
     * END MAIN
     */

    /**
     *
     *
     * FORM DSPOT
     *
     */

    public static final GlobalReport GLOBAL_REPORT =
            new GlobalReport(new OutputReportImpl(), new ErrorReportImpl(), new TestSelectorReportImpl());
    private static final Logger LOGGER = LoggerFactory.getLogger(DSpot.class);

    final List<CtType<?>> amplifiedTestClasses = new ArrayList<>();

    Amplification testAmplification;

    long time;

    public testTuple preAmplification(CtType<?> testClassToBeAmplified, List<String> testMethodsToBeAmplifiedAsString){
        Configuration.getInputAmplDistributor().resetAmplifiers(testClassToBeAmplified);
        testAmplification = new Amplification(
                Configuration.getInputConfiguration().getDelta(),
                Configuration.getCompiler(),
                Configuration.getTestSelector(),
                Configuration.getInputAmplDistributor(),
                Configuration.getInputConfiguration().getNbIteration()
        );
        final List<CtMethod<?>> testMethodsToBeAmplified =
                Configuration.getTestFinder().findTestMethods(testClassToBeAmplified, testMethodsToBeAmplifiedAsString);

        // here, we base the execution mode to the first test method given.
        // the user should provide whether JUnit3/4 OR JUnit5 but not both at the same time.
        // TODO DSpot could be able to switch from one to another version of JUnit, but I believe that the ROI is not worth it.
        final boolean jUnit5 = TestFramework.isJUnit5(testMethodsToBeAmplified.get(0));
        EntryPoint.jUnit5Mode = jUnit5;
        DSpotPOMCreator.isCurrentlyJUnit5 = jUnit5;

        Counter.reset();
        if (Configuration.getInputConfiguration().shouldGenerateAmplifiedTestClass()) {
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

        //Optimization: this object is not required anymore
        //and holds a dictionary with large number of cloned CtMethods.
        testAmplification = null;
        //but it is clear before iterating again for next test class
        LOGGER.debug("OPTIMIZATION: GC invoked");
        System.gc(); //Optimization: cleaning up heap before printing the amplified class

        Configuration.getAutomaticBuilder().reset();
        try {
            final TestSelectorElementReport report = Configuration.getTestSelector().report();
            this.output.reportSelectorInformation(report.getReportForCollector());
            GLOBAL_REPORT.addTestSelectorReportForTestClass(testClassToBeAmplified, report);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Something bad happened during the report fot test-criterion.");
            LOGGER.error("Dspot might not have output correctly!");
        }

        final CtType<?> amplifiedTestClass = this.output.output(testClassToBeAmplified, amplifiedTestMethods);

        amplifiedTestClasses.add(amplifiedTestClass);
        cleanAfterAmplificationOfOneTestClass(Configuration.getCompiler(), testClassToBeAmplified);
    }


    public Amplification getTestAmplifier(){
        return testAmplification;
    }

    public List<CtType<?>> getAmplifiedTestClasses(){
        return amplifiedTestClasses;
    }


    private void cleanAfterAmplificationOfOneTestClass(DSpotCompiler compiler, CtType<?> testClassToBeAmplified) {
        /* Cleaning modified source directory by DSpot */
        try {
            FileUtils.cleanDirectory(compiler.getSourceOutputDirectory());
        } catch (Exception exception) {
            exception.printStackTrace();
            LOGGER.warn("Something went wrong when trying to cleaning temporary sources directory: {}", compiler.getSourceOutputDirectory());
        }
        /* Cleaning binary generated by Dspot */
        try {
            String pathToDotClass = compiler.getBinaryOutputDirectory().getAbsolutePath() + "/" +
                    testClassToBeAmplified.getQualifiedName().replaceAll("\\.", "/") + ".class";
            FileUtils.forceDelete(new File(pathToDotClass));
        } catch (IOException ignored) {
            //ignored
        }
    }

    /**
     *
     * END DSPOT
     *
     */

}
