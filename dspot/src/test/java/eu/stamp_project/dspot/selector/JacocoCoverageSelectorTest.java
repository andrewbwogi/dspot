package eu.stamp_project.dspot.selector;

import eu.stamp_project.dspot.DSpot;
import eu.stamp_project.dspot.amplifier.MethodGeneratorAmplifier;
import eu.stamp_project.dspot.amplifier.ReturnValueAmplifier;
import eu.stamp_project.dspot.amplifier.StringLiteralAmplifier;
import eu.stamp_project.dspot.amplifier.TestDataMutator;
import eu.stamp_project.utils.AmplificationHelper;
import eu.stamp_project.utils.program.InputConfiguration;
import eu.stamp_project.utils.RandomHelper;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * Created by Benjamin DANGLOT
 * benjamin.danglot@inria.fr
 * on 25/07/17
 */
public class JacocoCoverageSelectorTest {

	// TODO Should extends AbstractSelectorTest

	public static final String nl = System.getProperty("line.separator");

	private static final char DECIMAL_SEPARATOR = (((DecimalFormat) DecimalFormat.getInstance()).getDecimalFormatSymbols().getDecimalSeparator());

	private Delegator delegator = new Delegator();

	private class Delegator extends AbstractSelectorTest2 {

		@Override
		protected TestSelector getTestSelector() {
			return new JacocoCoverageSelector();
		}

		@Override
		protected String getPathToReportFileDuplication() {
			return "target/trash/example.TestSuiteDuplicationExample_jacoco_instr_coverage_report.txt";
		}

		@Override
		protected String getContentReportFileDuplication() {
			return nl + "======= REPORT =======" + nl +
					"Initial instruction coverage: 23 / 34" + nl +
					"67" + DECIMAL_SEPARATOR + "65%" + nl +
					"Amplification results with 3 amplified tests." + nl +
					"Amplified instruction coverage: 27 / 34" + nl +
					"79" + DECIMAL_SEPARATOR + "41%";
		}



	}

	@Test
	public void testRemoveOverlappingTestsWithJacocoCoverageSelector() throws Exception {
		delegator.setUp();
		delegator.testRemoveOverlappingTestsWithPitMutantScoreSelector();
	}

	/*
	@Test
	public void testRemoveOverlappingTestsWithJacocoCoverageSelector() throws Exception {
		try {
			FileUtils.deleteDirectory(new File("target/trash"));
		} catch (Exception ignored) {
			//ignored
		}
		RandomHelper.setSeedRandom(23L);
		InputConfiguration.initialize("src/test/resources/test-projects/test-projects.properties");
		DSpot dspot = new DSpot(1, Arrays.asList(new StringLiteralAmplifier()), new JacocoCoverageSelector());
		dspot.amplifyTestClass("example.TestSuiteDuplicationExample");
		String path = InputConfiguration.get().getOutputDirectory() + System.getProperty("file.separator")
				+ "example.TestSuiteDuplicationExample" + "_jacoco_instr_coverage_report.txt";
		try (BufferedReader buffer = new BufferedReader(new FileReader(path))) {
			assertEquals(expectedReport, buffer.lines().collect(Collectors.joining(nl)));
		}
	}*/

	private static final String expectedReport = nl + "======= REPORT =======" + nl +
			"Initial instruction coverage: 23 / 34" + nl +
			"67" + DECIMAL_SEPARATOR + "65%" + nl +
			"Amplification results with 3 amplified tests." + nl +
			"Amplified instruction coverage: 27 / 34" + nl +
			"79" + DECIMAL_SEPARATOR + "41%";

}
