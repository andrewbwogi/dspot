package example;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.ArrayList;

/**
 * created by Benjamin DANGLOT
 * benjamin.danglot@inria.fr
 * on 26/11/18
 */
@RunWith(Parameterized.class)
public class ParametrizedTestSuiteExample {

    private String string;

    public ParametrizedTestSuiteExample(String string) {
        this.string = string;
    }


    @Parameterized.Parameters
    public static Collection<Object[]> strategies() {
        return Arrays.asList(
                new Object[]{
                        "abcd"
                },
                new Object[]{
                        "abcd"
                }
        );
    }

    @org.junit.Test
    public void test3() {
        /*example.Example ex = new example.Example();
        java.lang.String s = "abcd";
	java.lang.String s2 = s;
	org.junit.Assert.assertEquals(s2, s);
        org.junit.Assert.assertEquals('d', ex.charAt(s, ((s.length()) - 1)));*/

	int[] values = {0, 0, 5, 0};
	//int[] values2 = {0, 1, 2, 3};
	int[] values2 = new int[4];
	values2[2] = 5;
	String[] values3 = {"one","two","three"};
	org.junit.Assert.assertEquals(0, values[0]);
	// org.junit.Assert.assertArrayEquals(values, values2);
        char[] valuesnew = {'d', 'w', 'd', 'd'};
        char[] valuesnew2 = {'d', 'w', 'd', 'd'};

        char[] valuesnew3 = "t\"t y	o".toCharArray();
        char[] valuesnew4 = "t\"t y	o".toCharArray();

        float[] f1 = {1.2F,4.5F};
        float[] f2 = {1.2F,4.5F};
        org.junit.Assert.assertArrayEquals(f1,f2,1e-9F);

	int one = 1;
	int two = 1;
	org.junit.Assert.assertEquals(one,two);
	//ex.getInt();

/*
	int i = 2;
	int i2 = 2;
	org.junit.Assert.assertEquals(i2, i);*/

	/*ArrayList<Integer> al = new ArrayList<>();
        al.add(1);
	al.add(2);
	al.add(3);
        ArrayList<Integer> al2 = al;
        org.junit.Assert.assertEquals(al, al2);*/

	/*ArrayList<ArrayList<Integer>> mulal = new ArrayList<>();
        mulal.add(new ArrayList<>());
	mulal.add(new ArrayList<>());
	mulal.get(0).add(1);
	mulal.get(0).add(2);
	mulal.get(1).add(3);
	mulal.get(1).add(4);
        ArrayList<ArrayList<Integer>> mulal2 = mulal;
        org.junit.Assert.assertEquals(mulal, mulal2);*/
	
	/*Ob o = new Ob(1, 2);
	Ob o2 = new Ob(1, 2);
	org.junit.Assert.assertEquals(o, o2);*/
    }

}
