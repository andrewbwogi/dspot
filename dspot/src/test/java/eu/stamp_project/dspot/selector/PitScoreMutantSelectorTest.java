package eu.stamp_project.dspot.selector;

import eu.stamp_project.Utils;
import eu.stamp_project.automaticbuilder.maven.DSpotPOMCreator;
import eu.stamp_project.dspot.DSpot;
import eu.stamp_project.dspot.amplifier.StringLiteralAmplifier;
import eu.stamp_project.utils.AmplificationHelper;
import eu.stamp_project.minimization.PitMutantMinimizer;
import eu.stamp_project.utils.RandomHelper;
import eu.stamp_project.utils.program.InputConfiguration;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import spoon.reflect.declaration.CtMethod;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * Created by Benjamin DANGLOT
 * benjamin.danglot@inria.fr
 * on 1/9/17
 */
public class PitScoreMutantSelectorTest {

      /*
            Test the PitMutantScoreSelector:
                - The amplified test should increase the mutation score of the test suite.
                    we compare the mutation score before and after.
         */

    public static final String nl = System.getProperty("line.separator");

    private static final char DECIMAL_SEPARATOR = (((DecimalFormat) DecimalFormat.getInstance()).getDecimalFormatSymbols().getDecimalSeparator());

    private DuplicationDelegator duplicationDelegator = new DuplicationDelegator();

    private AmplificationDelegator amplificationDelegator = new AmplificationDelegator();

    private class DuplicationDelegator extends AbstractSelectorTest2 {

        @Override
        protected TestSelector getTestSelector() {
            return new PitMutantScoreSelector();
        }

        @Override
        protected String getPathToReportFileDuplication() {
            return "_mutants_report.txt";
        }

        @Override
        protected String getContentReportFileDuplication() {
            return AmplificationHelper.LINE_SEPARATOR +
                    "======= REPORT =======" + AmplificationHelper.LINE_SEPARATOR +
                    "PitMutantScoreSelector: " + AmplificationHelper.LINE_SEPARATOR +
                    "The original test suite kills 2 mutants" + AmplificationHelper.LINE_SEPARATOR +
                    "The amplification results with 1 new tests" + AmplificationHelper.LINE_SEPARATOR +
                    "it kills 3 more mutants";
        }
    }

    private class AmplificationDelegator extends OrigAbstractTest {
        @Override
        @Before
        public void setUp() throws Exception {
            Utils.reset(); // TODO somewhere, there is some states that is why we need to reset here.
            super.setUp();
            Utils.getInputConfiguration().setDescartesMode(false);
            DSpotPOMCreator.createNewPom();
        }

        @Override
        protected TestSelector getTestSelector() {
            return new PitMutantScoreSelector();
        }

        @Override
        protected CtMethod<?> getAmplifiedTest() {
            final CtMethod<?> clone = getTest().clone();
            Utils.replaceGivenLiteralByNewValue(clone, 4);
            return clone;
        }

        @Override
        protected String getPathToReportFile() {
            return "target/trash/example.TestSuiteExample_mutants_report.txt";
        }

        @Override
        protected String getContentReportFile() {
            return AmplificationHelper.LINE_SEPARATOR +
                    "======= REPORT =======" + AmplificationHelper.LINE_SEPARATOR +
                    "PitMutantScoreSelector: " + AmplificationHelper.LINE_SEPARATOR +
                    "The original test suite kills 15 mutants" + AmplificationHelper.LINE_SEPARATOR +
                    "The amplification results with 1 new tests" + AmplificationHelper.LINE_SEPARATOR +
                    "it kills 1 more mutants";
        }

        @Override
        protected Class<?> getClassMinimizer() {
            return PitMutantMinimizer.class;
        }
    }

    @Test
    public void testSelector() throws Exception {
        amplificationDelegator.setUp();
        amplificationDelegator.testSelector();
    }

    /*@Test
    public void testSelector() throws Exception {
        amplificationDelegator.setUp();
        amplificationDelegator.testSelector();
    }*/


    @Test
    public void testRemoveOverlappingTests() throws Exception {
        duplicationDelegator.setUp();
        duplicationDelegator.testRemoveOverlappingTestsWithPitMutantScoreSelector();
    }
/*
    @Test
    public void testRemoveOverlappingTestsWithPitMutantScoreSelector() throws Exception {
        try {
            FileUtils.deleteDirectory(new File("target/trash"));
        } catch (Exception ignored) {
            //ignored
        }
        RandomHelper.setSeedRandom(23L);
        InputConfiguration.initialize("src/test/resources/test-projects/test-projects.properties");
        DSpot dspot = new DSpot(1, Arrays.asList(new StringLiteralAmplifier()), new PitMutantScoreSelector());
        dspot.amplifyTestClass("example.TestSuiteDuplicationExample");
        String path = InputConfiguration.get().getOutputDirectory() + System.getProperty("file.separator")
                + "example.TestSuiteDuplicationExample" + "_mutants_report.txt";
        try (BufferedReader buffer = new BufferedReader(new FileReader(path))) {
            assertEquals(expectedReport, buffer.lines().collect(Collectors.joining(nl)));
        }
    }


* /




    /*

    @Override
    protected String getPathToReportFileDuplication() {
        return "target/trash/example.TestSuiteDuplicationExample_mutants_report.txt";
    }

    @Override
    protected String getContentReportFileDuplication() {
        return AmplificationHelper.LINE_SEPARATOR +
                "======= REPORT =======" + AmplificationHelper.LINE_SEPARATOR +
                "PitMutantScoreSelector: " + AmplificationHelper.LINE_SEPARATOR +
                "The original test suite kills 2 mutants" + AmplificationHelper.LINE_SEPARATOR +
                "The amplification results with 1 new tests" + AmplificationHelper.LINE_SEPARATOR +
                "it kills 3 more mutants";
    }

    @Test
    public void testRemoveOverlappingTestsWithPitMutantScoreSelector() throws Exception {
        try {
            FileUtils.deleteDirectory(new File("target/trash"));
        } catch (Exception ignored) {
            //ignored
        }
        RandomHelper.setSeedRandom(23L);
        InputConfiguration.initialize("src/test/resources/test-projects/test-projects.properties");
        DSpot dspot = new DSpot(1, Arrays.asList(new StringLiteralAmplifier()), new PitMutantScoreSelector());
        dspot.amplifyTestClass("example.TestSuiteDuplicationExample");
        String path = InputConfiguration.get().getOutputDirectory() + System.getProperty("file.separator")
                + "example.TestSuiteDuplicationExample" + "_mutants_report.txt";
        try (BufferedReader buffer = new BufferedReader(new FileReader(path))) {
            assertEquals(expectedReport, buffer.lines().collect(Collectors.joining(nl)));
        }
    }*/
/*
    private static final String expectedReport = AmplificationHelper.LINE_SEPARATOR +
            "======= REPORT =======" + AmplificationHelper.LINE_SEPARATOR +
            "PitMutantScoreSelector: " + AmplificationHelper.LINE_SEPARATOR +
            "The original test suite kills 2 mutants" + AmplificationHelper.LINE_SEPARATOR +
            "The amplification results with 1 new tests" + AmplificationHelper.LINE_SEPARATOR +
            "it kills 3 more mutants";*/
}
