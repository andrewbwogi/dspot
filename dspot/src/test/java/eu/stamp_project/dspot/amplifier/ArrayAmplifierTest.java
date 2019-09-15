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
    /*@Test
    public void testIntMutation() throws Exception {
        final String nameMethod = "methodInteger";
        final int originalValue = 23;
        CtClass<Object> literalMutationClass = Utils.getFactory().Class().get("fr.inria.amp.LiteralMutation");
        RandomHelper.setSeedRandom(42L);
        NumberLiteralAmplifier amplifier = getAmplifier2(literalMutationClass);
        CtMethod method = literalMutationClass.getMethod(nameMethod);
        List<Integer> expectedValues = Arrays.asList(22, 24, 2147483647, -2147483648, 0, -1170105035);
        List<String> expectedFieldReads = Arrays.asList(
                "java.lang.Integer.MAX_VALUE",
                "java.lang.Integer.MIN_VALUE"
        );

        List<CtMethod> mutantMethods = amplifier.amplify(method, 0).collect(Collectors.toList());
        assertEquals(6, mutantMethods.size());
        for (int i = 0; i < mutantMethods.size(); i++) {
            CtMethod mutantMethod = mutantMethods.get(i);
            assertEquals(nameMethod + "litNum" + (i + 1), mutantMethod.getSimpleName());
            CtExpression mutantLiteral = mutantMethod.getBody().getElements(new TypeFilter<>(CtExpression.class)).get(0);
            if (mutantLiteral instanceof CtLiteral) {
                assertNotEquals(originalValue, ((CtLiteral<?>) mutantLiteral).getValue());
                assertTrue(((CtLiteral<?>) mutantLiteral).getValue() + " not in expected values",
                        expectedValues.contains(((CtLiteral<?>) mutantLiteral).getValue()));
            } else {
                assertTrue(mutantLiteral instanceof CtFieldRead);
                assertTrue(expectedFieldReads.contains(mutantLiteral.toString()));
            }
        }
    }*/

    @Test
    public void testArrayMutation() throws Exception {
        final String nameMethod = "methodArray";
        String originalValue = "new int[][]{ new int[]{ 3, 4 }, new int[]{ 1, 2 } }";
        CtClass<Object> literalMutationClass = Utils.getFactory().Class().get("fr.inria.amp.ArrayMutation");
        RandomHelper.setSeedRandom(42L);
        ArrayAmplifier amplifier = getAmplifier(literalMutationClass);
        CtMethod method = literalMutationClass.getMethod(nameMethod);
        List<String> expectedValues = Arrays.asList("new int[][]{ new int[]{ 3, 4 }, new int[]{ 1, 2 }, new int[]{ 3, 4 } }",
                "new int[][]{ new int[]{ 1, 2 } }","new int[][]{  }");
        List<String> expectedFieldReads = Arrays.asList(
                "java.lang.Integer.MAX_VALUE",
                "java.lang.Integer.MIN_VALUE"
        );

        List<CtMethod> mutantMethods = amplifier.amplify(method, 0).collect(Collectors.toList());
        assertEquals(3, mutantMethods.size());
        for (int i = 0; i < mutantMethods.size(); i++) {
            CtMethod mutantMethod = mutantMethods.get(i);
            assertEquals(nameMethod + "litArray" + (i + 1), mutantMethod.getSimpleName());
            CtExpression mutantLiteral = mutantMethod.getBody().getElements(new TypeFilter<>(CtExpression.class)).get(0);
            if (mutantLiteral instanceof CtNewArrayImpl) {
                assertNotEquals(originalValue, mutantLiteral);
                assertTrue(mutantLiteral + " not in expected values",
                        expectedValues.contains(mutantLiteral.toString()));
            } else {
                System.out.println(mutantLiteral.getClass());
                assertTrue(mutantLiteral instanceof CtFieldRead);
                assertTrue(expectedFieldReads.contains(mutantLiteral.toString()));
            }
        }
    }

    private ArrayAmplifier getAmplifier(CtClass<Object> literalMutationClass) {
        ArrayAmplifier amplifier = new ArrayAmplifier();
        amplifier.reset(literalMutationClass);
        return amplifier;
    }

    private NumberLiteralAmplifier getAmplifier2(CtClass<Object> literalMutationClass) {
        NumberLiteralAmplifier amplifier = new NumberLiteralAmplifier();
        amplifier.reset(literalMutationClass);
        return amplifier;
    }
}
