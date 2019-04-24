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
public abstract class AbstractSelectorTest2 {

    protected String getPathToProperties() {
        return "src/test/resources/regression/test-projects_0/test-projects.properties";
    }

    protected abstract TestSelector getTestSelector();

    protected CtMethod<?> getTest() {
        return Utils.findMethod("example.TestSuiteExample", "test2");
    }

    protected List<CtMethod<?>> getDuplicationTest() {
        Utils.findMethod("example.TestSuiteExample", "test2");
        return Arrays.asList(Utils.findMethod("example.TestSuiteDuplicationExample", "test1"),
                Utils.findMethod("example.TestSuiteDuplicationExample", "test2"));
    }

    protected CtClass<?> getTestClass() {
        return Utils.findClass("example.TestSuiteExample");
    }

    protected CtClass<?> getDuplicationTestClass() {
        return Utils.findClass("example.TestSuiteDuplicationExample");
    }

    protected abstract CtMethod<?> getAmplifiedTest();

    protected abstract List<CtMethod<?>> getAmplifiedTestForDuplicationTest();

    protected abstract String getPathToReportFile();

    protected abstract String getContentReportFile();

    protected abstract String getPathToReportFileDuplication();

    protected abstract String getContentReportFileDuplication();

    protected TestSelector testSelectorUnderTest;

    protected abstract Class<?> getClassMinimizer();

    @Before
    public void setUp() throws Exception {
        final String configurationPath = getPathToProperties();
        Utils.init(configurationPath);
        RandomHelper.setSeedRandom(72L);
        ValueCreator.count = 0;
        this.testSelectorUnderTest = this.getTestSelector();
        this.testSelectorUnderTest.init(Utils.getInputConfiguration());
    }
/*
    @Test
    public void testSelector() throws Exception {
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

        this.testSelectorUnderTest.report();
        try (BufferedReader buffer = new BufferedReader(new FileReader(getPathToReportFile()))) {
            assertEquals(getContentReportFile(),
                    buffer.lines()
                            .collect(Collectors.joining(AmplificationHelper.LINE_SEPARATOR)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertTrue(this.testSelectorUnderTest.getMinimizer().getClass() == getClassMinimizer());
    }*/

    @Test
    public void testRemoveOverlappingTestsWithPitMutantScoreSelector() throws Exception {
        /*try {
            FileUtils.deleteDirectory(new File("target/trash"));
        } catch (Exception ignored) {
            //ignored
        }*/

        /*
        this.testSelectorUnderTest.selectToKeep(
                this.testSelectorUnderTest.selectToAmplify(
                        getDuplicationTestClass(), getDuplicationTest()
                )
        );*/


        /*this.testSelectorUnderTest.selectToKeep(
                this.testSelectorUnderTest.selectToAmplify(
                        getDuplicationTestClass(), getAmplifiedTestForDuplicationTest()
                )
        );*/

        // InputConfiguration.initialize("src/test/resources/test-projects/test-projects.properties");
        DSpot dspot = new DSpot(1, Arrays.asList(new StringLiteralAmplifier()), new PitMutantScoreSelector());
        dspot.amplifyTestClass("example.TestSuiteDuplicationExample");

        //this.testSelectorUnderTest.report();

        try (BufferedReader buffer = new BufferedReader(new FileReader(getPathToReportFileDuplication()))) {
            assertEquals(getContentReportFileDuplication(),
                    buffer.lines()
                            .collect(Collectors.joining(AmplificationHelper.LINE_SEPARATOR)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }




        /*RandomHelper.setSeedRandom(23L);
        InputConfiguration.initialize("src/test/resources/test-projects/test-projects.properties");
        DSpot dspot = new DSpot(1, Arrays.asList(new StringLiteralAmplifier()), new PitMutantScoreSelector());
        dspot.amplifyTestClass("example.TestSuiteDuplicationExample");
        String path = InputConfiguration.get().getOutputDirectory() + System.getProperty("file.separator")
                + "example.TestSuiteDuplicationExample" + "_mutants_report.txt";
        try (BufferedReader buffer = new BufferedReader(new FileReader(path))) {
            assertEquals(expectedReport, buffer.lines().collect(Collectors.joining(nl)));
        }*/
    }
}
