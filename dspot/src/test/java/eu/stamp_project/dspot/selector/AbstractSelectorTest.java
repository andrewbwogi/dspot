package eu.stamp_project.dspot.selector;

import eu.stamp_project.Main;
import eu.stamp_project.automaticbuilder.AutomaticBuilder;
import eu.stamp_project.automaticbuilder.maven.DSpotPOMCreator;
import eu.stamp_project.dspot.amplifier.value.ValueCreator;
import eu.stamp_project.dspot.assertiongenerator.assertiongenerator.AssertionGeneratorUtils;
import eu.stamp_project.dspot.configuration.Configuration;
import eu.stamp_project.test_framework.TestFramework;
import eu.stamp_project.utils.DSpotCache;
import eu.stamp_project.utils.DSpotUtils;
import eu.stamp_project.utils.RandomHelper;
import eu.stamp_project.utils.compilation.DSpotCompiler;
import eu.stamp_project.utils.compilation.TestCompiler;
import eu.stamp_project.utils.execution.TestRunner;
import eu.stamp_project.utils.options.AutomaticBuilderEnum;
import eu.stamp_project.utils.program.InputConfiguration;
import org.junit.Before;
import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;

import java.io.File;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Benjamin DANGLOT
 * benjamin.danglot@inria.fr
 * on 16/03/18
 */
public abstract class AbstractSelectorTest {

    protected String getPathToAbsoluteProjectRoot() {
        return "src/test/resources/regression/test-projects_0/";
    }

    protected abstract TestSelector getTestSelector();

    protected CtMethod<?> getTest() {
        return this.factory.Class().get("example.TestSuiteExample").getMethodsByName("test2").get(0);
    }

    protected CtClass<?> getTestClass() {
        return this.factory.Class().get("example.TestSuiteExample");
    }

    protected abstract CtMethod<?> getAmplifiedTest();

    protected abstract String getContentReportFile();

    protected TestSelector testSelectorUnderTest;

    protected Factory factory;

    protected InputConfiguration configuration;

    protected AutomaticBuilder builder;

    protected final String outputDirectory = "target/dspot/output";

    protected DSpotCompiler compiler;

    @Before
    public void setUp() throws Exception {
        Main.verbose = true;
        this.configuration = new InputConfiguration();
        this.configuration.setAbsolutePathToProjectRoot(getPathToAbsoluteProjectRoot());
        this.configuration.setOutputDirectory(outputDirectory);
        this.configuration.setGregorMode(true);
        this.builder = AutomaticBuilderEnum.Maven.getAutomaticBuilder(configuration);
        String dependencies = Configuration.completeDependencies(configuration, this.builder);
        DSpotUtils.init(false, outputDirectory,
                this.configuration.getFullClassPathWithExtraDependencies(),
                this.getPathToAbsoluteProjectRoot()
        );
        this.compiler = DSpotCompiler.createDSpotCompiler(
                configuration,
                dependencies
        );
        DSpotCache.init(10000);
        Launcher launcher = new Launcher();
        launcher.getEnvironment().setNoClasspath(true);
        launcher.addInputResource(this.getPathToAbsoluteProjectRoot());
        launcher.buildModel();
        this.factory = launcher.getFactory();
        TestFramework.init(this.factory);
        TestCompiler.init(0, false, this.getPathToAbsoluteProjectRoot(), this.configuration.getClasspathClassesProject(), 10000);
        TestRunner.init(this.getPathToAbsoluteProjectRoot(), "", false);
        AssertionGeneratorUtils.init(false);
        DSpotPOMCreator.createNewPom(configuration);
        RandomHelper.setSeedRandom(72L);
        ValueCreator.count = 0;
        this.testSelectorUnderTest = this.getTestSelector();
    }

    @Test
    public void testSelector() throws Exception {
        this.testSelectorUnderTest.init();
        this.testSelectorUnderTest.selectToKeep(
                this.testSelectorUnderTest.selectToAmplify(
                        getTestClass(), Collections.singletonList(getTest())
                )
        );
        assertTrue(this.testSelectorUnderTest.getAmplifiedTestCases().isEmpty());

        this.testSelectorUnderTest.selectToKeep(
                this.testSelectorUnderTest.selectToAmplify(
                        getTestClass(), Collections.singletonList(getAmplifiedTest())
                )
        );
        assertFalse(this.testSelectorUnderTest.getAmplifiedTestCases().isEmpty());
        final File directory = new File(DSpotUtils.shouldAddSeparator.apply(outputDirectory ));
        if (!directory.exists()) {
            directory.mkdir();
        }
        assertEquals(getContentReportFile(), this.testSelectorUnderTest.report().output(this.getTestClass(), outputDirectory ));
    }
}
