package eu.stamp_project.dspot.selector;

import eu.stamp_project.Utils;
import eu.stamp_project.automaticbuilder.maven.DSpotPOMCreator;
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
import java.nio.file.Paths;
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
        return "src/test/resources/regression/test-projects_2/test-projects.properties";
    }

    protected abstract TestSelector getTestSelector();

    protected abstract String getPathToReportFileDuplication();

    protected abstract String getContentReportFileDuplication();

    protected TestSelector testSelectorUnderTest;

    @Before
    public void setUp() throws Exception {
        Utils.reset();
        final String configurationPath = getPathToProperties();
        Utils.init(configurationPath);
        this.testSelectorUnderTest = this.getTestSelector();
    }

    @Test
    public void testRemoveOverlappingTests() throws Exception {
        DSpot dspot = new DSpot(1, Arrays.asList(new StringLiteralAmplifier()), testSelectorUnderTest);
        dspot.amplifyTestClass("example.TestSuiteDuplicationExample");
        try (BufferedReader buffer = new BufferedReader(new FileReader(getPathToReportFileDuplication()))) {
            assertEquals(getContentReportFileDuplication(),
                    buffer.lines()
                            .collect(Collectors.joining(AmplificationHelper.LINE_SEPARATOR)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
