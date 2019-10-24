package eu.stamp_project;

import eu.stamp_project.automaticbuilder.AutomaticBuilder;
import eu.stamp_project.dspot.DSpot;
import eu.stamp_project.utils.program.InputConfiguration;
import eu.stamp_project.utils.report.GlobalReport;
import eu.stamp_project.utils.report.error.ErrorReportImpl;
import eu.stamp_project.utils.report.output.OutputReportImpl;
import eu.stamp_project.utils.report.output.selector.TestSelectorReportImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Benjamin DANGLOT benjamin.danglot@inria.fr on 2/9/17
 */
public class Main {

    public static final GlobalReport GLOBAL_REPORT =
            new GlobalReport(new OutputReportImpl(), new ErrorReportImpl(), new TestSelectorReportImpl());

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static boolean verbose = false;

    public static void main(String[] args) {
        final DSpot dspot = new DSpot(args);
        dspot.run();
    }
}
