package eu.stamp_project;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TempTest {

    /*@org.junit.Test(timeout = 10000)
    public void test3_withlog0() throws java.lang.Exception {
        //example.Example ex = new example.Example();
        //eu.stamp_project.compare.ObjectLog.log(ex, "ex", "test3__1");
        java.lang.String s = "abcd";
        eu.stamp_project.compare.ObjectLog.log(s, "s", "test3__3");
        //char o_test3__4 = ex.charAt(s, ((s.length()) - 1));
        //eu.stamp_project.compare.ObjectLog.log(o_test3__4, "o_test3__4", "test3__4");
        int[] values = new int[]{ 0, 1, 2, 3 };
        eu.stamp_project.compare.ObjectLog.log(values, "values", "test3__6");
        int[] values2 = new int[]{ 0, 1, 2, 3 };
        eu.stamp_project.compare.ObjectLog.log(values2, "values2", "test3__7");
        java.lang.String[] values3 = new java.lang.String[]{ "one", "two", "three" };
        int int_0 = values[0];
        // ex.getInt();
        int i = 2;
        eu.stamp_project.compare.ObjectLog.log(i, "i", "test3__10");
        int i2 = 2;
        eu.stamp_project.compare.ObjectLog.log(i2, "i2", "test3__12");
        java.util.ArrayList<java.lang.Integer> al = new java.util.ArrayList<>();
        eu.stamp_project.compare.ObjectLog.log(al, "al", "test3__13");
        boolean o_test3__15 = al.add(1);
        eu.stamp_project.compare.ObjectLog.log(o_test3__15, "o_test3__15", "test3__15");
        java.util.ArrayList<java.lang.Integer> al2 = al;
        eu.stamp_project.compare.ObjectLog.log(al2, "al2", "test3__16");
        //eu.stamp_project.compare.ObjectLog.log(ex, "ex", "test3__1___end");
        eu.stamp_project.compare.ObjectLog.log(s, "s", "test3__3___end");
        //eu.stamp_project.compare.ObjectLog.log(o_test3__4, "o_test3__4", "test3__4___end");
        eu.stamp_project.compare.ObjectLog.log(values, "values", "test3__6___end");
        eu.stamp_project.compare.ObjectLog.log(values2, "values2", "test3__7___end");
        eu.stamp_project.compare.ObjectLog.log(i, "i", "test3__10___end");
        eu.stamp_project.compare.ObjectLog.log(i2, "i2", "test3__12___end");
        eu.stamp_project.compare.ObjectLog.log(al, "al", "test3__13___end");
        eu.stamp_project.compare.ObjectLog.log(o_test3__15, "o_test3__15", "test3__15___end");
    }*/
/*
    @org.junit.Test(timeout = 10000)
    public void test3_withlog0() throws java.lang.Exception {
        int[] values = new int[]{ 0, 1, 2, 3 };
        eu.stamp_project.compare.ObjectLog.log(values, "values", "test3__6");
        int[] values2 = new int[]{ 0, 1, 2, 3 };
        eu.stamp_project.compare.ObjectLog.log(values2, "values2", "test3__7");
        eu.stamp_project.compare.ObjectLog.log(values, "values", "test3__6___end");
        eu.stamp_project.compare.ObjectLog.log(values2, "values2", "test3__7___end");

    }*/

    @org.junit.Test(timeout = 10000)
    public void test3_withlog0() throws java.lang.Exception {
        Object[][] array = new Object[5][5];
        //array[1][1] = 2;
        List<Object> collection = Arrays.stream(array)  //'array' is two-dimensional
                .flatMap(Arrays::stream)
                .collect(Collectors.toList());

        int[] values2 = new int[]{ 0, 1, 2, 3 };
        int[][] array3 = new int[5][5];
        int[][][] array4 = new int[2][3][4];
        array3[1][1] = 2;
        array3[2][2] = 2;
        array3[3][3] = 2;
        Object[] ob = (Object[]) array3;
        //System.out.println(ob[2]);


       // getArrayType(array3);

        System.out.println(getArrayType(array3.clone()));
        String expr = getExpression(array3);
        System.out.println(expr);
        //array[1][1] = 2;
/*
        Integer[][] array2 new Integer[5][5];
        //array[1][1] = 2;
        List<Integer> collection2 = Arrays.stream(array2)  //'array' is two-dimensional
                .flatMap(Arrays::stream)
                .collect(Collectors.toList());

        Integer[][][] array4 = new Integer[5][5][5];
        //array[1][1] = 2;
        List<Integer> collection4 = Arrays.stream(array4)  //'array' is two-dimensional
                .flatMap(Arrays::stream)
                .collect(Collectors.toList());

        int[] data = {1,2,3,4,5,6,7,8,9,10};

// To boxed array
        Integer[] what = Arrays.stream( data ).boxed().toArray( Integer[]::new );

        int[][] array3 = new int[5][5];
        //array[1][1] = 2;
        List<Integer> collection3 = Arrays.stream(array3)

                //'array' is two-dimensional
                .flatMap(Arrays::stream)
                .collect(Collectors.toList());
*/
    }
/*
    @org.junit.AfterClass
    public static void afterClass() {
        eu.stamp_project.compare.ObjectLog.save();
    }
*/

    private Class getArrayType(Object obj) {
        Class cls = obj.getClass();
        String clsName = cls.getName();
        int nrDims = 1 + clsName.lastIndexOf('[');
        for(int i = 0; i<(nrDims-1); i++){
            obj = Array.get(obj,0);
        }
        return obj.getClass().getComponentType();
    }

String getExpression(Object obj){
    StringBuilder sb = new StringBuilder();
    ArrayList<Integer> al = new ArrayList<>();
    getStuffFromArray(obj,sb,al);
    int dimensions = 0;
    for(int i = 0; sb.charAt(i) == '{'; i++)
        dimensions = i+1;
    for(int i = dimensions; i>0; i--){
        sb.insert(0,"[" + al.get(i-1) + "]");
    }
    for(int i = 1; i<sb.length(); i++){
        if(sb.charAt(i-1) == '}' && sb.charAt(i) == '{')
            sb.insert(i,",");
    }
    for(int i = 0; i<(dimensions-1); i++){
        obj = Array.get(obj,0);
    }
    sb.insert(0,"new " + obj.getClass().getComponentType());
    return sb.toString();
}


    void getStuffFromArray(Object obj, StringBuilder sb, ArrayList al) {
        sb.append("{");
        // assuming we already know obj.getClass().isArray() == true
        int size = Array.getLength(obj);
        al.add(size);
        for (int i = 0; i < size; i++) {
            Object value = Array.get(obj, i);
            if (value.getClass().isArray()) {
                getStuffFromArray(value, sb, al);
            } else {
                // not an array; process it
                sb.append(value);
                if(i+1 < size)
                    sb.append(",");
            }
        }
        sb.append("}");
    }



/*
    @org.junit.Test(timeout = 10000)
    public void arr() {
        int[] values = {0, 0, 2, 3};
        //int[] values2 = {0, 1, 2, 3};
        int[] values2 = new int[4];
        values2[2] = 5;
        org.junit.Assert.assertEquals(0, values[0]);
        org.junit.Assert.assertArrayEquals(values, values2);
    }*/
    }
