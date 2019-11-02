package eu.stamp_project.dspot.selector;

import eu.stamp_project.Main;
import eu.stamp_project.automaticbuilder.AutomaticBuilder;
import eu.stamp_project.dspot.configuration.Configuration;
import eu.stamp_project.utils.compilation.DSpotCompiler;
import eu.stamp_project.utils.execution.TestRunner;
import eu.stamp_project.utils.options.AutomaticBuilderEnum;
import eu.stamp_project.utils.pit.AbstractPitResult;
import eu.stamp_project.utils.program.InputConfiguration;
import org.junit.Before;
import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.factory.Factory;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * created by Benjamin DANGLOT
 * benjamin.danglot@inria.fr
 * on 29/01/19
 */
public class OneTestClassPitScoreMutantSelectorTest {

    private String FULL_QUALIFIED_NAME_TEST_CLASS = "example.TestSuiteExample";

    private AutomaticBuilder builder;

    private InputConfiguration configuration;

    private TestRunner testRunner;

    @Before
    public void setUp() {
        Main.verbose = true;
        this.configuration = new InputConfiguration();
        this.configuration.setAbsolutePathToProjectRoot("src/test/resources/test-projects/");
        this.builder = AutomaticBuilderEnum.Maven.getAutomaticBuilder(configuration);
        DSpotCompiler.createDSpotCompiler(
                configuration,
                Configuration.completeDependencies(configuration, this.builder)
        );
        Launcher launcher = new Launcher();
        launcher.getEnvironment().setNoClasspath(true);
        launcher.addInputResource("src/test/resources/test-projects/");
        launcher.buildModel();
        Factory factory = launcher.getFactory();
        this.configuration.setFactory(factory);
        this.configuration.setTestClasses(Collections.singletonList(FULL_QUALIFIED_NAME_TEST_CLASS));
        this.configuration.setTargetOneTestClass(true);
        this.testRunner = new TestRunner(this.configuration.getAbsolutePathToProjectRoot(), "", false);
    }

    @Test
    public void test() throws NoSuchFieldException, IllegalAccessException {
        final PitMutantScoreSelector pitMutantScoreSelector = new PitMutantScoreSelector(this.builder, this.configuration);
        pitMutantScoreSelector.init();
        final Field field = pitMutantScoreSelector.getClass().getDeclaredField("originalKilledMutants");
        field.setAccessible(true);
        List<AbstractPitResult> originalResult = (List<AbstractPitResult>) field.get(pitMutantScoreSelector);
        assertTrue(originalResult.stream().allMatch(abstractPitResult -> abstractPitResult.getFullQualifiedNameOfKiller().equals(FULL_QUALIFIED_NAME_TEST_CLASS)));
    }
}
