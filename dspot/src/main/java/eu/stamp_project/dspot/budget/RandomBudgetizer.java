package eu.stamp_project.dspot.budget;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.stamp_project.dspot.amplifier.Amplifier;
import eu.stamp_project.utils.DSpotUtils;
import eu.stamp_project.utils.program.InputConfiguration;
import spoon.reflect.declaration.CtMethod;

/**
 * Created by Benjamin DANGLOT, Yosu Gorroñogoitia
 * benjamin.danglot@inria.fr, jesus.gorroñogoitia@atos.net
 * on 24/04/19
 */
public class RandomBudgetizer extends AbstractBugetizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RandomBudgetizer.class);

    public RandomBudgetizer() {
        super();
    }

    public RandomBudgetizer(List<Amplifier> amplifiers) {
        super(amplifiers);
    }

    /**
     * Input amplification for a single test.
     *
     * @param test Test method
     * @return New generated tests
     */
    protected Stream<CtMethod<?>> inputAmplifyTest(CtMethod<?> test, int i) {
        return this.amplifiers.parallelStream()
                .flatMap(amplifier -> amplifier.amplify(test, i));
    }

    /**
     * Input amplification of multiple tests.
     *
     * @param testMethods Test methods
     * @return New generated tests
     */
    @Override
    public List<CtMethod<?>> inputAmplify(List<CtMethod<?>> testMethods, int i) {
        LOGGER.info("Amplification of inputs...");
        List<CtMethod<?>> inputAmplifiedTests = testMethods.parallelStream()
                .flatMap(test -> {
                    final Stream<CtMethod<?>> inputAmplifiedTestMethods = inputAmplifyTest(test, i);
                    DSpotUtils.printProgress(testMethods.indexOf(test), testMethods.size());
                    return inputAmplifiedTestMethods;
                }).collect(Collectors.toList());
        LOGGER.info("{} new tests generated", inputAmplifiedTests.size());
        System.out.println("--printtest");

        for(CtMethod m : inputAmplifiedTests){
            System.out.println(m);
        }
        return reduce(inputAmplifiedTests);
    }

    /**
     * Reduces the number of amplified tests to a practical threshold (see {@link InputConfiguration#getMaxTestAmplified()}).
     * <p>
     * <p>This method randomly selects the tests to keep
     * <p>
     * @param tests List of tests to be reduced
     * @return A subset of the input tests
     */

    public List<CtMethod<?>> reduce(List<CtMethod<?>> tests) {
        final List<CtMethod<?>> reducedTests = new ArrayList<>();
        final int maxNumTests = InputConfiguration.get().getMaxTestAmplified();
        final int testsSize = tests.size();
        if (testsSize > maxNumTests) {
            Random random = new Random();
            LOGGER.warn("Too many tests have been generated: {}", testsSize);
            for (int i=0;i<maxNumTests; i++) {
                reducedTests.add(tests.get(random.nextInt(testsSize)));
            }
            LOGGER.info("Number of generated test reduced to {}", reducedTests.size());
        }
        if (reducedTests.isEmpty()) {
            reducedTests.addAll(tests);
        }
        return reducedTests;
    }
}
