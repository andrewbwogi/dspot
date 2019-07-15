package eu.stamp_project.dspot;

import eu.stamp_project.AbstractTest;
import eu.stamp_project.Main;
import eu.stamp_project.Utils;
import eu.stamp_project.dspot.amplifier.Amplifier;
import eu.stamp_project.dspot.assertgenerator.AssertionGenerator;
import eu.stamp_project.dspot.budget.TextualDistanceBudgetizer;
import eu.stamp_project.utils.report.error.ErrorEnum;
import eu.stamp_project.dspot.selector.PitMutantScoreSelector;
import eu.stamp_project.dspot.selector.TakeAllSelector;
import eu.stamp_project.utils.program.InputConfiguration;
import eu.stamp_project.utils.compilation.DSpotCompiler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.*;

/**
 * created by Benjamin DANGLOT
 * benjamin.danglot@inria.fr
 * on 29/10/18
 */
public class RecoveryDSpotTest extends AbstractTest {

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        Main.GLOBAL_REPORT.reset();
    }

    @After
    public void tearDown() throws Exception {
        Main.GLOBAL_REPORT.reset();
        InputConfiguration.get().setSelector(new PitMutantScoreSelector());
    }

    public class SelectorThatThrowsError extends TakeAllSelector {

        private boolean throwsToAmplify;

        private boolean throwsToKeep;

        public void setThrowsToAmplify(boolean throwsToAmplify) {
            this.throwsToAmplify = throwsToAmplify;
        }

        public void setThrowsToKeep(boolean throwsToKeep) {
            this.throwsToKeep = throwsToKeep;
        }

        @Override
        public List<CtMethod<?>> selectToAmplify(CtType<?> classTest, List<CtMethod<?>> testsToBeAmplified) {
            if (throwsToAmplify) {
                throw new RuntimeException();
            }
            return super.selectToAmplify(classTest, testsToBeAmplified);
        }

        @Override
        public List<CtMethod<?>> selectToKeep(List<CtMethod<?>> amplifiedTestToBeKept) {
            if (throwsToKeep) {
                throw new RuntimeException();
            }
            return super.selectToKeep(amplifiedTestToBeKept);
        }
    }

    public class AmplifierThatThrowsError implements Amplifier {
        @Override
        public Stream<CtMethod<?>> amplify(CtMethod<?> testMethod, int iteration) {
            throw new RuntimeException();
        }

        @Override
        public void reset(CtType<?> testClass) {

        }
    }

    public class AssertionGeneratorThatThrowsError extends AssertionGenerator {
        public AssertionGeneratorThatThrowsError(DSpotCompiler compiler) {
            super(InputConfiguration.get(), compiler);
        }

        @Override
        public List<CtMethod<?>> assertionAmplification(CtType<?> testClass, List<CtMethod<?>> tests) {
            throw new RuntimeException();
        }
    }

    @Test
    public void testThatDSpotCanRecoverFromError() throws Exception {

        /*
            We test here, with different mock, that DSpot can recover for errors, continue and terminate the amplification process.
         */

        final SelectorThatThrowsError selector = new SelectorThatThrowsError();
        InputConfiguration.get().setSelector(selector);
        selector.setThrowsToAmplify(true);
        Amplification amplification = new Amplification(
                Utils.getCompiler(),
                InputConfiguration.get().getAmplifiers(),
                selector,
                new TextualDistanceBudgetizer()
        );
        amplification.amplification(Utils.findClass("fr.inria.amp.OneLiteralTest"), 1);
        assertEquals(1, Main.GLOBAL_REPORT.getErrors().size());
        assertSame(ErrorEnum.ERROR_PRE_SELECTION, Main.GLOBAL_REPORT.getErrors().get(0).type);
        Main.GLOBAL_REPORT.reset();

        selector.setThrowsToAmplify(false);
        selector.setThrowsToKeep(true);
        amplification.amplification(Utils.findClass("fr.inria.amp.OneLiteralTest"), 1);
        assertEquals(1, Main.GLOBAL_REPORT.getErrors().size());
        assertSame(ErrorEnum.ERROR_SELECTION, Main.GLOBAL_REPORT.getErrors().get(0).type);
        Main.GLOBAL_REPORT.reset();

        final List<Amplifier> amplifiers = Collections.singletonList(new AmplifierThatThrowsError());
        amplification = new Amplification(
                Utils.getCompiler(),
                amplifiers,
                new TakeAllSelector(),
                new TextualDistanceBudgetizer(amplifiers)
        );
        amplification.amplification(Utils.findClass("fr.inria.amp.OneLiteralTest"), 1);
        assertEquals(1, Main.GLOBAL_REPORT.getErrors().size());
        assertSame(ErrorEnum.ERROR_INPUT_AMPLIFICATION, Main.GLOBAL_REPORT.getErrors().get(0).type);
        Main.GLOBAL_REPORT.reset();

        amplification = new Amplification(
                Utils.getCompiler(),
                Collections.emptyList(),
                new TakeAllSelector(),
                new TextualDistanceBudgetizer()
        );
        final Field assertGenerator = amplification.getClass().getDeclaredField("assertGenerator");
        assertGenerator.setAccessible(true);
        assertGenerator.set(amplification, new AssertionGeneratorThatThrowsError(Utils.getCompiler()));
        amplification.amplification(Utils.findClass("fr.inria.amp.OneLiteralTest"), 1);
        assertEquals(1, Main.GLOBAL_REPORT.getErrors().size());
        assertSame(ErrorEnum.ERROR_ASSERT_AMPLIFICATION, Main.GLOBAL_REPORT.getErrors().get(0).type);
        Main.GLOBAL_REPORT.reset();
    }

    @Test
    public void testNoMatchingTestClasses() {
        final DSpot dSpot = new DSpot(new TakeAllSelector());
        dSpot.amplifyTestClass("this.is.not.a.correct.package");
        assertEquals(2, Main.GLOBAL_REPORT.getErrors().size());
        assertSame(ErrorEnum.ERROR_NO_TEST_COULD_BE_FOUND_MATCHING_REGEX, Main.GLOBAL_REPORT.getErrors().get(0).type);
        assertSame(ErrorEnum.ERROR_NO_TEST_COULD_BE_FOUND, Main.GLOBAL_REPORT.getErrors().get(1).type);
    }
}
