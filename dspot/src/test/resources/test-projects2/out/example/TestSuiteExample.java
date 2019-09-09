package example;


import org.junit.Assert;
import org.junit.Test;


public class TestSuiteExample {
    @Test(timeout = 10000)
    public void test3() throws Exception {
        Example ex = new Example();
        Boolean b = null;
        int[] i = new int[]{ 1, 2 };
        int[] j = new int[]{ 3, 4 };
        String s = "abcd";
        Assert.assertEquals("abcd", s);
        char o_test3__7 = ex.charAt(s, ((s.length()) - 1));
        Assert.assertEquals('d', ((char) (o_test3__7)));
        Assert.assertEquals("abcd", s);
    }

    @Test(timeout = 10000)
    public void test3litBool1() throws Exception {
        Example ex = new Example();
        Boolean b = true;
        int[] i = new int[]{ 1, 2 };
        int[] j = new int[]{ 3, 4 };
        String s = "abcd";
        Assert.assertEquals("abcd", s);
        char o_test3litBool1__7 = ex.charAt(s, ((s.length()) - 1));
        Assert.assertEquals('d', ((char) (o_test3litBool1__7)));
        Assert.assertEquals("abcd", s);
    }

    @Test(timeout = 10000)
    public void test4() throws Exception {
        Example ex = new Example();
        String s = "abcd";
        Assert.assertEquals("abcd", s);
        char o_test4__4 = ex.charAt(s, 12);
        Assert.assertEquals('d', ((char) (o_test4__4)));
        Assert.assertEquals("abcd", s);
    }

    @Test(timeout = 10000)
    public void test7() throws Exception {
        Example ex = new Example();
        char o_test7__3 = ex.charAt("abcd", 2);
        Assert.assertEquals('c', ((char) (o_test7__3)));
    }

    @Test(timeout = 10000)
    public void test8() throws Exception {
        Example ex = new Example();
        char o_test8__3 = ex.charAt("abcd", 1);
        Assert.assertEquals('b', ((char) (o_test8__3)));
    }

    @Test(timeout = 10000)
    public void test9() throws Exception {
        Example ex = new Example();
        char o_test9__3 = ex.charAt("abcdefghijklm", 5);
        Assert.assertEquals('f', ((char) (o_test9__3)));
    }

    @Test(timeout = 10000)
    public void test2() throws Exception {
        Example ex = new Example();
        char o_test2__3 = ex.charAt("abcd", 3);
        Assert.assertEquals('d', ((char) (o_test2__3)));
    }
}

