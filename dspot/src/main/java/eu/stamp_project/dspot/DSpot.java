package eu.stamp_project.dspot;

import eu.stamp_project.automaticbuilder.AutomaticBuilder;
import eu.stamp_project.dspot.common.testTuple;
import eu.stamp_project.dspot.configuration.Configuration;
import eu.stamp_project.dspot.input_ampl_distributor.InputAmplDistributor;
import eu.stamp_project.dspot.selector.TestSelector;
import eu.stamp_project.utils.compilation.DSpotCompiler;
import eu.stamp_project.utils.report.output.Output;
import eu.stamp_project.utils.test_finder.TestFinder;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import java.util.Collections;
import java.util.List;

/**
 * User: Simon
 * Date: 08/06/15
 * Time: 17:36
 */
public class DSpot {

    public static boolean verbose;

    Configuration configuration;

    public DSpot(String[] args){
        configuration = new Configuration(args);
        configuration.run();
    }

    public DSpot(double delta,
                 TestFinder testFinder,
                 DSpotCompiler compiler,
                 TestSelector testSelector,
                 InputAmplDistributor inputAmplDistributor,
                 Output output,
                 int numberOfIterations,
                 boolean shouldGenerateAmplifiedTestClass,
                 AutomaticBuilder automaticBuilder) {
        Configuration.getInputConfiguration().setDelta(delta);
        Configuration.setTestSelector(testSelector);
        Configuration.setInputAmplDistributor(inputAmplDistributor);
        Configuration.getInputConfiguration().setNbIteration(numberOfIterations);
        Configuration.setTestFinder(testFinder);
        Configuration.setCompiler(compiler);
        Configuration.setOutput(output);
        Configuration.getInputConfiguration().setGenerateAmplifiedTestClass(shouldGenerateAmplifiedTestClass);
        Configuration.setAutomaticBuilder(automaticBuilder);
    }

    public void run() {

        // starting amplification
        final List<CtType<?>> amplifiedTestClasses = amplify(Configuration.getTestClassesToBeAmplified(), Configuration.getTestMethodsToBeAmplifiedNames());
        configuration.report(amplifiedTestClasses);
    }

    public CtType<?> amplify(CtType<?> testClassToBeAmplified, List<String> testMethodsToBeAmplifiedAsString) {
        return this.amplify(Collections.singletonList(testClassToBeAmplified), testMethodsToBeAmplifiedAsString).get(0);
    }

    public List<CtType<?>> amplify(List<CtType<?>> testClassesToBeAmplified, List<String> testMethodsToBeAmplifiedAsString) {
        for (CtType<?> testClassToBeAmplified : testClassesToBeAmplified) {
            testTuple tuple = configuration.preAmplification(testClassToBeAmplified,testMethodsToBeAmplifiedAsString);

            // Amplification of the given test methods of the given test class
            final List<CtMethod<?>> amplifiedTestMethods = getCtMethods(tuple);
            configuration.postAmplification(testClassToBeAmplified,amplifiedTestMethods);
        }
        return configuration.getAmplifiedTestClasses();
    }

    private List<CtMethod<?>> getCtMethods(testTuple tuple) {
        return configuration.getTestAmplifier().amplification(tuple.testClassToBeAmplified, tuple.testMethodsToBeAmplified);
    }
}
