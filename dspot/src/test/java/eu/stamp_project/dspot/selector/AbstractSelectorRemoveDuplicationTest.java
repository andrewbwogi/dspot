package eu.stamp_project.dspot.selector;

import eu.stamp_project.Utils;
import eu.stamp_project.dspot.DSpot;
import eu.stamp_project.dspot.amplifier.StringLiteralAmplifier;
import eu.stamp_project.dspot.amplifier.value.ValueCreator;
import eu.stamp_project.utils.AmplificationHelper;
import eu.stamp_project.utils.RandomHelper;
import eu.stamp_project.utils.program.InputConfiguration;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Benjamin DANGLOT
 * benjamin.danglot@inria.fr
 * on 16/03/18
 */
public abstract class AbstractSelectorRemoveDuplicationTest {

    protected String getPathToProperties() {
        return "src/test/resources/regression/test-projects_0/test-projects.properties";
    }

    public static final String nl = System.getProperty("line.separator");

    protected abstract TestSelector getTestSelector();

    protected CtMethod<?> getTest() {
        return Utils.findMethod("example.TestSuiteExample", "test2");
    }

    protected CtClass<?> getTestClass() {
        return Utils.findClass("example.TestSuiteExample");
    }

    protected abstract String getPathToReportFileDuplication();

    protected abstract String getContentReportFileDuplication();

    protected TestSelector testSelectorUnderTest;

    String path;

    @Before
    public void setUp() throws Exception {
        try {
            FileUtils.deleteDirectory(new File("target/trash"));
        } catch (Exception ignored) {
            //ignored
        }

        RandomHelper.setSeedRandom(23L);
        InputConfiguration.initialize("src/test/resources/test-projects/test-projects.properties");

        path = InputConfiguration.get().getOutputDirectory() + System.getProperty("file.separator")
                + "example.TestSuiteDuplicationExample" + getPathToReportFileDuplication();
        /*final String configurationPath = getPathToProperties();
        Utils.init(configurationPath);
        RandomHelper.setSeedRandom(72L);
        ValueCreator.count = 0;
        this.testSelectorUnderTest = this.getTestSelector();
        this.testSelectorUnderTest.init(Utils.getInputConfiguration());*/


    }


    /*@Test
    public void testRemoveOverlappingTestsWithPitMutantScoreSelector() throws Exception {
        /*try {
            FileUtils.deleteDirectory(new File("target/trash"));
        } catch (Exception ignored) {
            //ignored
        }*/

/*
        DSpot dspot = new DSpot(1, Arrays.asList(new StringLiteralAmplifier()), getTestSelector());
        dspot.amplifyTestClass("example.TestSuiteDuplicationExample");

        try (BufferedReader buffer = new BufferedReader(new FileReader(getPathToReportFileDuplication()))) {
            assertEquals(getContentReportFileDuplication(),
                    buffer.lines()
                            .collect(Collectors.joining(AmplificationHelper.LINE_SEPARATOR)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }*/

    @Test
    public void testRemoveOverlappingTestsWithPitMutantScoreSelector() throws Exception {
        /*try {
            FileUtils.deleteDirectory(new File("target/trash"));
        } catch (Exception ignored) {
            //ignored
        }
        RandomHelper.setSeedRandom(23L);
        InputConfiguration.initialize("src/test/resources/test-projects/test-projects.properties");*/
        DSpot dspot = new DSpot(1, Arrays.asList(new StringLiteralAmplifier()), new PitMutantScoreSelector());
        dspot.amplifyTestClass("example.TestSuiteDuplicationExample");
        /*String path = InputConfiguration.get().getOutputDirectory() + System.getProperty("file.separator")
                + "example.TestSuiteDuplicationExample" + "_mutants_report.txt";*/

        /*try (BufferedReader buffer = new BufferedReader(new FileReader(path))) {
            assertEquals(expectedReport, buffer.lines().collect(Collectors.joining(nl)));
        }*/

        try (BufferedReader buffer = new BufferedReader(new FileReader(path))) {
            assertEquals(getContentReportFileDuplication(),
                    buffer.lines()
                            .collect(Collectors.joining(AmplificationHelper.LINE_SEPARATOR)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static final String expectedReport = AmplificationHelper.LINE_SEPARATOR +
            "======= REPORT =======" + AmplificationHelper.LINE_SEPARATOR +
            "PitMutantScoreSelector: " + AmplificationHelper.LINE_SEPARATOR +
            "The original test suite kills 2 mutants" + AmplificationHelper.LINE_SEPARATOR +
            "The amplification results with 1 new tests" + AmplificationHelper.LINE_SEPARATOR +
            "it kills 3 more mutants";

}
