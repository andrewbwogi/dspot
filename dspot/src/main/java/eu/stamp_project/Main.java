package eu.stamp_project;

import eu.stamp_project.dspot.DSpot;
import eu.stamp_project.dspot.amplifier.TestDataMutator;
import eu.stamp_project.dspot.selector.JacocoCoverageSelector;
import eu.stamp_project.utils.options.BudgetizerEnum;
import eu.stamp_project.utils.options.JSAPOptions;
import eu.stamp_project.utils.program.InputConfiguration;
import eu.stamp_project.utils.RandomHelper;
import eu.stamp_project.utils.report.GlobalReport;
import eu.stamp_project.utils.report.GlobalReportImpl;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.reflect.declaration.CtType;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static java.nio.charset.Charset.forName;

/**
 * Created by Benjamin DANGLOT benjamin.danglot@inria.fr on 2/9/17
 */
public class Main {

	public static final GlobalReport globalReport = new GlobalReportImpl();

	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		System.out.println("ttttttttttttttttttt in Main");
		try {
			FileUtils.forceDelete(new File("target/dspot/"));
		} catch (Exception ignored) {

		}
		final boolean shouldRunExample = JSAPOptions.parse(args);
		System.out.println("ttttttttttttttttttt after Parse");
		try {
			FileUtils.writeStringToFile(new File("/home/andrew/Skrivbord/stamp/dspot/dspot/src/test/resources/test-projects2/target/dspot/dependencies/eu/stamp_project/compare/parse.txt"), "parse", forName("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (shouldRunExample) {
			Main.runExample();
		} else {
			run();
		}
		// global report handling
		Main.globalReport.output();
	}

	public static void run() {
		DSpot dspot = new DSpot(
				InputConfiguration.get().getNbIteration(),
				InputConfiguration.get().getAmplifiers(),
				InputConfiguration.get().getSelector(),
				InputConfiguration.get().getBudgetizer()
		);
		RandomHelper.setSeedRandom(InputConfiguration.get().getSeed());
		createOutputDirectories();
		System.out.println("ttttttttttttttttt \t\tcreateOutputDirectories();");
		final long startTime = System.currentTimeMillis();
		final List<CtType<?>> amplifiedTestClasses;
		if (InputConfiguration.get().getTestClasses().isEmpty() || "all".equals(InputConfiguration.get().getTestClasses().get(0))) {
			System.out.println("in if");
			amplifiedTestClasses = dspot.amplifyAllTests();
		} else {
			System.out.println("in else");
			amplifiedTestClasses = dspot.amplifyTestClassesTestMethods(InputConfiguration.get().getTestClasses(), InputConfiguration.get().getTestCases());
		}
		LOGGER.info("Amplification {}.", amplifiedTestClasses.isEmpty() ? "failed" : "succeed");
		final long elapsedTime = System.currentTimeMillis() - startTime;
		LOGGER.info("Elapsed time {} ms", elapsedTime);
	}

	public static void createOutputDirectories() {
		final File outputDirectory = new File(InputConfiguration.get().getOutputDirectory());
		try {
			if (InputConfiguration.get().shouldClean() && outputDirectory.exists()) {
				FileUtils.forceDelete(outputDirectory);
			}
			if (!outputDirectory.exists()) {
				FileUtils.forceMkdir(outputDirectory);
			}
		} catch (IOException ignored) {
			// ignored
		}
	}

	static void runExample() {
		try {
			InputConfiguration.get().initialize("src/test/resources/test-projects/test-projects.properties");
			DSpot dSpot = new DSpot(1,
					Collections.singletonList(new TestDataMutator()),
					new JacocoCoverageSelector(),
					BudgetizerEnum.NoBudgetizer
			);
			dSpot.amplifyTestClassesTestMethods(Collections.singletonList("example.TestSuiteExample"), Collections.emptyList());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
