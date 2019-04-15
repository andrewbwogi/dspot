package example;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

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
        example.Example ex = new example.Example();
        java.lang.String s = "abcd";
        org.junit.Assert.assertEquals('d', ex.charAt(s, ((s.length()) - 1)));
	int[] values = {0, 1, 2, 3};
	int[] values2 = {0, 1, 2, 3};
	String[] values3 = {"one","two","three"};
	org.junit.Assert.assertEquals(0, values[0]);
	org.junit.Assert.assertArrayEquals(values, values2);
	//ex.getInt();
	int i = 2;
	int i2 = 2;
	org.junit.Assert.assertEquals(i2, i);
    }

    @org.junit.Test
    public void test4() {
        example.Example ex = new example.Example();
        java.lang.String s = "abcd";
        org.junit.Assert.assertEquals('d', ex.charAt(s, 12));
    }

    @org.junit.Test
    public void test7() {
        example.Example ex = new example.Example();
        org.junit.Assert.assertEquals('c', ex.charAt("abcd", 2));
    }

    @org.junit.Test
    public void test8() {
        example.Example ex = new example.Example();
        org.junit.Assert.assertEquals('b', ex.charAt("abcd", 1));
    }

    @org.junit.Test
    public void test9() {
        example.Example ex = new example.Example();
        org.junit.Assert.assertEquals('f', ex.charAt("abcdefghijklm", 5));
    }

    @org.junit.Test
    public void test2() {
        example.Example ex = new example.Example();
        org.junit.Assert.assertEquals('d', ex.charAt("abcd", 3));
    }

}
