package eu.stamp_project.dspot.amplifier;

import eu.stamp_project.AbstractTest;
import eu.stamp_project.Utils;
import eu.stamp_project.utils.RandomHelper;
import org.junit.Test;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.code.CtNewArrayImpl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Andrew Bwogi
 * abwogi@kth.se
 * on 12/09/19
 */
public class ArrayAmplifierTest extends AbstractTest {
    /*
        Test so that correct number of mutants is generated, that the mutant does not equal the original
        and the value of the mutant.
     */
    @Test
    public void testArrayMutation() {
        final String nameMethod = "methodArray";
        String originalValue = "new int[][]{ new int[]{ 3, 4 }, new int[]{ 1, 2 } }";
        CtClass<Object> literalMutationClass = Utils.getFactory().Class().get("fr.inria.amp.ArrayMutation");
        RandomHelper.setSeedRandom(42L);
        ArrayAmplifier amplifier = getAmplifier(literalMutationClass);
        CtMethod method = literalMutationClass.getMethod(nameMethod);
        List<String> expectedValues = Arrays.asList("new int[][]{ new int[]{ 3, 4 }, new int[]{ 1, 2 }, new int[]{ 3, 4 } }",
                "new int[][]{ new int[]{ 1, 2 } }","new int[][]{  }");
        List<CtMethod> mutantMethods = amplifier.amplify(method, 0).collect(Collectors.toList());
        assertEquals(3, mutantMethods.size());
        for (int i = 0; i < mutantMethods.size(); i++) {
            CtMethod mutantMethod = mutantMethods.get(i);
            assertEquals(nameMethod + "litArray" + (i + 1), mutantMethod.getSimpleName());
            CtExpression mutantLiteral = mutantMethod.getBody().getElements(new TypeFilter<>(CtExpression.class)).get(0);
            assertNotEquals(originalValue, mutantLiteral);
            assertTrue(mutantLiteral + " not in expected values",
                    expectedValues.contains(mutantLiteral.toString()));
        }
    }

    @Test
    public void testNullArrayMutation() {
        //int[][][] j = new int[][][]{{{1}}};
        final String nameMethod = "methodNullArray";
        String originalValue = "new int[][]{ new int[]{ 3, 4 }, new int[]{ 1, 2 } }";
        CtClass<Object> literalMutationClass = Utils.getFactory().Class().get("fr.inria.amp.ArrayMutation");
        RandomHelper.setSeedRandom(42L);
        ArrayAmplifier amplifier = getAmplifier(literalMutationClass);
        CtMethod method = literalMutationClass.getMethod(nameMethod);
        List<String> expectedValues = Arrays.asList("new int[][]{ new int[]{ 3, 4 }, new int[]{ 1, 2 }, new int[]{ 3, 4 } }",
                "new int[][]{ new int[]{ 1, 2 } }","new int[][]{  }");
        List<CtMethod> mutantMethods = amplifier.amplify(method, 0).collect(Collectors.toList());
        assertEquals(3, mutantMethods.size());
        for (int i = 0; i < mutantMethods.size(); i++) {
            CtMethod mutantMethod = mutantMethods.get(i);
            assertEquals(nameMethod + "litArray" + (i + 1), mutantMethod.getSimpleName());
            CtExpression mutantLiteral = mutantMethod.getBody().getElements(new TypeFilter<>(CtExpression.class)).get(0);
            assertNotEquals(originalValue, mutantLiteral);
            assertTrue(mutantLiteral + " not in expected values",
                    expectedValues.contains(mutantLiteral.toString()));
        }
    }

    private ArrayAmplifier getAmplifier(CtClass<Object> literalMutationClass) {
        ArrayAmplifier amplifier = new ArrayAmplifier();
        amplifier.reset(literalMutationClass);
        return amplifier;
    }
}
