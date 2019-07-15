package eu.stamp_project.dspot.assertgenerator;

import eu.stamp_project.AbstractTest;
import eu.stamp_project.Utils;
import eu.stamp_project.dspot.assertgenerator.components.testmethodreconstructor.SyntaxBuilder;
import org.junit.Test;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtMethod;
import java.util.*;
import static junit.framework.TestCase.assertEquals;

public class SyntaxBuilderTest extends AbstractTest {

    @Test
    public void testAssertArrays() {

        /*
            Make sure that observations containing arrays of single or multiple dimensions with components of primitive type
            and empty arrays correctly generate assertions statements.
         */

        Map map = new HashMap<>();
        Set empty = new HashSet<>();
        CtMethod ignore = Utils.findMethod("fr.inria.sample.TestClassWithAssert", "test1");

        int[][] intArray = {{1,1,1},{2,2,2}};
        map.put("test", intArray);
        List<CtStatement> statement = SyntaxBuilder.buildAssert(ignore,empty,map,0.0);
        assertEquals("[org.junit.Assert.assertArrayEquals(new int[][]{{1,1,1},{2,2,2}}, test)]", statement.toString());

        long[] longArray = {1L,2l};
        map.put("test", longArray);
        statement = SyntaxBuilder.buildAssert(ignore,empty,map,0.0);
        assertEquals("[org.junit.Assert.assertArrayEquals(new long[]{1L,2L}, test)]", statement.toString());

        char[] charArray = "\\\"q\u00F1".toCharArray();
        map.put("test", charArray);
        statement = SyntaxBuilder.buildAssert(ignore,empty,map,0.0);
        assertEquals("[org.junit.Assert.assertArrayEquals(new char[]{'\\\\','\\\"','q','\u00F1'}, test)]", statement.toString());

        double[] doubleArray = {1.1,2.2};
        map.put("test", doubleArray);
        statement = SyntaxBuilder.buildAssert(ignore,empty,map,0.0);
        assertEquals("[org.junit.Assert.assertArrayEquals(new double[]{1.1,2.2}, test, 0.1)]", statement.toString());

        float[] floatArray = {1.1F,2.2f};
        map.put("test", floatArray);
        statement = SyntaxBuilder.buildAssert(ignore,empty,map,0.0);
        assertEquals("[org.junit.Assert.assertArrayEquals(new float[]{1.1F,2.2F}, test, 0.1F)]", statement.toString());

        boolean[] booleanArray = {true,false};
        map.put("test", booleanArray);
        statement = SyntaxBuilder.buildAssert(ignore,empty,map,0.0);
        assertEquals("[org.junit.Assert.assertArrayEquals(new boolean[]{true,false}, test)]", statement.toString());

        int[][][] jaggedArray = new int[][][]{{},{{1,1},{2,2,2}},{}};
        map.put("test", jaggedArray);
        statement = SyntaxBuilder.buildAssert(ignore,empty,map,0.0);
        assertEquals("[org.junit.Assert.assertArrayEquals(new int[][][]{{},{{1,1},{2,2,2}},{}}, test)]", statement.toString());

        int[][][] emptyArray1 = new int[][][]{{},{{}}};
        int[][] emptyArray2 = new int[][]{};
        map.put("test", emptyArray1);
        statement = SyntaxBuilder.buildAssert(ignore,empty,map,0.0);
        assertEquals("[org.junit.Assert.assertArrayEquals(new int[][][]{{},{{}}}, test)]", statement.toString());
        map.put("test", emptyArray2);
        statement = SyntaxBuilder.buildAssert(ignore,empty,map,0.0);
        assertEquals("[org.junit.Assert.assertArrayEquals(new int[][]{}, test)]", statement.toString());
    }
}
