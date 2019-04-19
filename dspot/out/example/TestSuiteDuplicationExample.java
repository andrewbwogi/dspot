package example;


public class TestSuiteDuplicationExample {
    @org.junit.Test(timeout = 10000)
    public void test1litString1() throws java.lang.Exception {
        example.Example ex = new example.Example();
        java.lang.String s = "abcd";
        org.junit.Assert.assertEquals("abcd", s);
        char o_test1litString1__4 = ex.charAt(s, ((s.length()) - 1));
        org.junit.Assert.assertEquals('d', ((char) (o_test1litString1__4)));
        org.junit.Assert.assertEquals("abcd", s);
    }

    @org.junit.Test(timeout = 10000)
    public void test1litString7() throws java.lang.Exception {
        example.Example ex = new example.Example();
        java.lang.String s = "\n";
        org.junit.Assert.assertEquals("\n", s);
        char o_test1litString7__4 = ex.charAt(s, ((s.length()) - 1));
        org.junit.Assert.assertEquals('\n', ((char) (o_test1litString7__4)));
        org.junit.Assert.assertEquals("\n", s);
    }

    @org.junit.Test(timeout = 10000)
    public void test1litString6_failAssert0() throws java.lang.Exception {
        try {
            example.Example ex = new example.Example();
            java.lang.String s = "";
            ex.charAt(s, ((s.length()) - 1));
            org.junit.Assert.fail("test1litString6 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException expected) {
            org.junit.Assert.assertEquals("String index out of range: 0", expected.getMessage());
        }
    }
}

