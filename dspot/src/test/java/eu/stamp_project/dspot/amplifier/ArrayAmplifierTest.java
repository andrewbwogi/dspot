package eu.stamp_project.dspot.amplifier;

import eu.stamp_project.AbstractTest;
import eu.stamp_project.Utils;
import eu.stamp_project.dspot.amplifier.value.ValueCreator;
import eu.stamp_project.utils.RandomHelper;
import eu.stamp_project.utils.program.InputConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.code.CtNewArrayImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * Created by Andrew Bwogi
 * abwogi@kth.se
 * on 12/09/19
 */
public class ArrayAmplifierTest extends AbstractTest {

    ArrayAmplifier amplifier;

    CtClass<Object> literalMutationClass;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        literalMutationClass = Utils.getFactory().Class().get("fr.inria.amp.ArrayMutation");
        RandomHelper.setSeedRandom(42L);
        amplifier = getAmplifier(literalMutationClass);
    }

    /*
        Test so that correct number of mutants is generated, that the mutant does not equal the original
        and the value of the mutant.
     */
    @Test
    public void testArrayMutation() {
        final String nameMethod = "methodArray";
        List<String> expectedValues = Arrays.asList("new int[][]{ new int[]{ 3, 4 }, new int[]{ 1, 2 }, new int[]{ 3, 4 } }",
                "new int[][]{ new int[]{ 1, 2 } }","new int[][]{  }","null");
        callAssert(nameMethod,expectedValues);
    }

    private void callAssert(String nameMethod,List<String> expectedValues){
        amplifier.reset(literalMutationClass);
        CtMethod method = literalMutationClass.getMethod(nameMethod);
        List<CtMethod> mutantMethods = amplifier.amplify(method, 0).collect(Collectors.toList());
        assertEquals(expectedValues.size(), mutantMethods.size());
        for (int i = 0; i < mutantMethods.size(); i++) {
            CtMethod mutantMethod = mutantMethods.get(i);
            assertEquals(nameMethod + "litArray" + (i + 1), mutantMethod.getSimpleName());
            CtExpression mutantLiteral = mutantMethod.getBody().getElements(new TypeFilter<>(CtExpression.class)).get(0);
            assertTrue(mutantLiteral + " not in expected values",
                    expectedValues.contains(mutantLiteral.toString()));
        }
    }

    @Test
    public void testNullArrayMutation() {
        final String nameMethod = "methodNullArray";
        List<String> expectedValues = Arrays.asList("new int[][]{ new int[]{ 1 } }","new int[][]{  }");
        callAssert(nameMethod,expectedValues);
    }
/*
    @Test
    public void testEmptyArrayMutation2() {
        CtClass<Object> literalMutationClass = Utils.getFactory().Class().get("fr.inria.amp.ArrayMutation");
        RandomHelper.setSeedRandom(42L);
        ArrayAmplifier amplifier = getAmplifier(literalMutationClass);
        ArrayList<String> list = new ArrayList<>();
        list.addAll(Arrays.asList("Byte","Short","Long","Float","Double","Char","String","Object"));
        for(String type : list) {
            final String nameMethod = type.toLowerCase() + "EmptyArray";
            if(type.equals("String") || type.equals("Object")){
                type = "java.lang." + type;
            }
            String originalValue = "new" + type + "[][]{ }";
            CtMethod method = literalMutationClass.getMethod(nameMethod);

            // just add new methods for primitive reference types
            if(hasReferenceType(type)){

            }
            else {
                List<String> expectedValues = Arrays.asList("new " + type + "[][]{ new " + type + "[]{ " + constructAdditionalElement(type) + " } }");
            }
            List<CtMethod> mutantMethods = amplifier.amplify(method, 0).collect(Collectors.toList());
            assertEquals(2, mutantMethods.size());
            for (int i = 0; i < mutantMethods.size(); i++) {
                System.out.println("method: " + i);
                CtMethod mutantMethod = mutantMethods.get(i);
                System.out.println(mutantMethod);
                assertEquals(nameMethod + "litArray" + (i + 1), mutantMethod.getSimpleName());
                CtExpression mutantLiteral = mutantMethod.getBody().getElements(new TypeFilter<>(CtExpression.class)).get(i);
                assertNotEquals(originalValue, mutantLiteral);
                assertTrue(mutantLiteral + " not in expected values",
                        expectedValues.contains(mutantLiteral.toString()));
            }
        }



        ArrayList<String> list = new ArrayList<>();
        list.addAll(Arrays.asList("byte","int","short","long","float","double","char",
                "Integer","Byte","Short","Long","Float","Double","Char","String","Object"));
        for(String type : list) {
            final String nameMethod = type.toLowerCase() + "EmptyArray";
            if(type.equals("String") || type.equals("Object")){
                type = "java.lang." + type;
            }
            String originalValue = "new" + type + "[][]{ }";
            CtClass<Object> literalMutationClass = Utils.getFactory().Class().get("fr.inria.amp.ArrayMutation");
            RandomHelper.setSeedRandom(42L);
            ArrayAmplifier amplifier = getAmplifier(literalMutationClass);
            CtMethod method = literalMutationClass.getMethod(nameMethod);
            List<String> expectedValues = Arrays.asList("new " + type + "[][]{ new " + type +"[]{ " + constructAdditionalElement(type) + " } }");
            List<CtMethod> mutantMethods = amplifier.amplify(method, 0).collect(Collectors.toList());
            assertEquals(2, mutantMethods.size());
            for (int i = 0; i < mutantMethods.size(); i++) {
                System.out.println("method: " + i);
                CtMethod mutantMethod = mutantMethods.get(i);
                System.out.println(mutantMethod);
                assertEquals(nameMethod + "litArray" + (i + 1), mutantMethod.getSimpleName());
                CtExpression mutantLiteral = mutantMethod.getBody().getElements(new TypeFilter<>(CtExpression.class)).get(i);
                assertNotEquals(originalValue, mutantLiteral);
                assertTrue(mutantLiteral + " not in expected values",
                        expectedValues.contains(mutantLiteral.toString()));
            }
        }
    }*/



    @Test
    public void testEmptyArrayMutation2() {
        ArrayList<String> list = new ArrayList<>();
        list.addAll(Arrays.asList("int","byte","short","long","float","double","char","String","Object"));
        for(String type : list) {
            final String nameMethod = type.toLowerCase() + "EmptyArray";
            if(type.equals("String") || type.equals("Object")){
                type = "java.lang." + type;
            }
            List<String> expectedValues = Arrays.asList("new " + type + "[][]{ new " + type +"[]{ " + constructAdditionalElement(type) + " } }","null");
            System.out.println("expected: " + expectedValues.get(0));
            callAssert(nameMethod,expectedValues);
        }
    }

    @Test
    public void testEmptyArrayMutation3() {
        ArrayList<String> list = new ArrayList<>();
        list.addAll(Arrays.asList("Integer","Byte","Short","Long","Float","Double","Character"));
        for(String type : list) {
            final String nameMethod = type.toLowerCase() + "Reference" + "EmptyArray";
            String fullType = "java.lang." + type;
            List<String> expectedValues = Arrays.asList("new " + fullType + "[][]{ new " + fullType +"[]{ " + constructAdditionalElement(type) + " } }","null");
            callAssert(nameMethod,expectedValues);

        }
    }




    private String constructAdditionalElement(String type) {
        type = type.toLowerCase();
        if(type.equals("int") || type.equals("integer") || type.equals("short") || type.equals("byte")){
            return "1";
        }
        else if(type.equals("long")){
            return "1L";
        }
        else if(type.equals("float")){
            return "1.1F";
        }
        else if(type.equals("double")){
            return "1.1";
        }
        else if(type.equals("byte")){
            return "1";
        }
        else if(type.equals("boolean")){
            return "true";
        }
        else if(type.equals("char") || type.equals("character")){
            return "'a'";
        }
        else if(type.equals("java.lang.string")){
            return "\"a\"";
        }
        else {
            return "null";
        }
    }

    /*
    @TestFactory
    Stream<DynamicTest> dynamicTestsFromIntStream() {
        // Generates tests for the first 10 even integers.
        return IntStream.iterate(0, n -> n + 2).limit(10).mapToObj(
                n -> dynamicTest("test" + n, () -> assertTrue(n % 2 == 0)));
    }

    @TestFactory
    Stream<DynamicTest> dynamicTestsFromStream() {
        return Stream.of("int","byte","short","long","float","double","char","String")
                .map(text -> dynamicTest(text, () -> assertTrue(isPalindrome(text))));
    }*/

    // https://junit.org/junit5/docs/current/user-guide/#writing-tests-dynamic-tests-examples
/*
    @Test
    public void testEmptyArrayMutation() {
        final String nameMethod = "methodEmptyArray";
        String originalValue = "new int[][]{ }";
        CtClass<Object> literalMutationClass = Utils.getFactory().Class().get("fr.inria.amp.ArrayMutation");
        RandomHelper.setSeedRandom(42L);
        ArrayAmplifier amplifier = getAmplifier(literalMutationClass);
        CtMethod method = literalMutationClass.getMethod(nameMethod);
        List<String> expectedValues = Arrays.asList("new int[][]{{1}}");
        List<CtMethod> mutantMethods = amplifier.amplify(method, 0).collect(Collectors.toList());
        assertEquals(1, mutantMethods.size());
        for (int i = 0; i < mutantMethods.size(); i++) {
            CtMethod mutantMethod = mutantMethods.get(i);
            assertEquals(nameMethod + "litArray" + (i + 1), mutantMethod.getSimpleName());
            CtExpression mutantLiteral = mutantMethod.getBody().getElements(new TypeFilter<>(CtExpression.class)).get(0);
            assertNotEquals(originalValue, mutantLiteral);
            assertTrue(mutantLiteral + " not in expected values",
                    expectedValues.contains(mutantLiteral.toString()));
        }
    }*/


    private ArrayAmplifier getAmplifier(CtClass<Object> literalMutationClass) {
        ArrayAmplifier amplifier = new ArrayAmplifier();
        amplifier.reset(literalMutationClass);
        return amplifier;
    }
}
