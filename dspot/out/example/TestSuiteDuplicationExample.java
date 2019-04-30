package example;


public class TestSuiteDuplicationExample {
    @org.junit.Test(timeout = 10000)
    public void test1() throws java.lang.Exception {
        example.example.Example ex = new example.example.Example();
        org.junit.Assert.assertEquals("string", ((example.example.Example) (ex)).getString());
        java.lang.String s = "abcd";
        org.junit.Assert.assertEquals("abcd", s);
        char o_test1__4 = ex.charAt(s, ((s.length()) - 1));
        org.junit.Assert.assertEquals('d', ((char) (o_test1__4)));
        org.junit.Assert.assertEquals("string", ((example.example.Example) (ex)).getString());
        org.junit.Assert.assertEquals("abcd", s);
    }

    @org.junit.Test(timeout = 10000)
    public void test2() throws java.lang.Exception {
        example.Example ex = new example.Example();
        org.junit.Assert.assertEquals("string", ((example.Example) (ex)).getString());
        java.lang.String s = "abcd";
        org.junit.Assert.assertEquals("abcd", s);
        char o_test2__4 = ex.charAt(s, ((s.length()) - 1));
        org.junit.Assert.assertEquals('d', ((char) (o_test2__4)));
        org.junit.Assert.assertEquals("string", ((example.Example) (ex)).getString());
        org.junit.Assert.assertEquals("abcd", s);
    }

    @org.junit.Test(timeout = 10000)
    public void test1litString7() throws java.lang.Exception {
        example.example.Example ex = new example.example.Example();
        org.junit.Assert.assertEquals("string", ((example.example.Example) (ex)).getString());
        java.lang.String s = "\n";
        org.junit.Assert.assertEquals("\n", s);
        char o_test1litString7__4 = ex.charAt(s, ((s.length()) - 1));
        org.junit.Assert.assertEquals('\n', ((char) (o_test1litString7__4)));
        org.junit.Assert.assertEquals("string", ((example.example.Example) (ex)).getString());
        org.junit.Assert.assertEquals("\n", s);
    }

    @org.junit.Test(timeout = 10000)
    public void test1litString6_failAssert0() throws java.lang.Exception {
        try {
            example.example.Example ex = new example.example.Example();
            java.lang.String s = "";
            ex.charAt(s, ((s.length()) - 1));
            org.junit.Assert.fail("test1litString6 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException expected) {
            org.junit.Assert.assertEquals("String index out of range: 0", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void test1litString1() throws java.lang.Exception {
        example.example.Example ex = new example.example.Example();
        org.junit.Assert.assertEquals("string", ((example.example.Example) (ex)).getString());
        java.lang.String s = "abcd";
        org.junit.Assert.assertEquals("abcd", s);
        char o_test1litString1__4 = ex.charAt(s, ((s.length()) - 1));
        org.junit.Assert.assertEquals('d', ((char) (o_test1litString1__4)));
        org.junit.Assert.assertEquals("string", ((example.example.Example) (ex)).getString());
        org.junit.Assert.assertEquals("abcd", s);
    }

    @org.junit.Test(timeout = 10000)
    public void test2litString32() throws java.lang.Exception {
        example.example.Example ex = new example.example.Example();
        org.junit.Assert.assertEquals("string", ((example.example.Example) (ex)).getString());
        java.lang.String s = "abcd";
        org.junit.Assert.assertEquals("abcd", s);
        char o_test2litString32__4 = ex.charAt(s, ((s.length()) - 1));
        org.junit.Assert.assertEquals('d', ((char) (o_test2litString32__4)));
        org.junit.Assert.assertEquals("string", ((example.example.Example) (ex)).getString());
        org.junit.Assert.assertEquals("abcd", s);
    }

    @org.junit.Test(timeout = 10000)
    public void test2litString38() throws java.lang.Exception {
        example.example.Example ex = new example.example.Example();
        org.junit.Assert.assertEquals("string", ((example.example.Example) (ex)).getString());
        java.lang.String s = "\n";
        org.junit.Assert.assertEquals("\n", s);
        char o_test2litString38__4 = ex.charAt(s, ((s.length()) - 1));
        org.junit.Assert.assertEquals('\n', ((char) (o_test2litString38__4)));
        org.junit.Assert.assertEquals("string", ((example.example.Example) (ex)).getString());
        org.junit.Assert.assertEquals("\n", s);
    }

    @org.junit.Test(timeout = 10000)
    public void test2litString37_failAssert0() throws java.lang.Exception {
        try {
            example.example.Example ex = new example.example.Example();
            java.lang.String s = "";
            ex.charAt(s, ((s.length()) - 1));
            org.junit.Assert.fail("test2litString37 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException expected) {
            org.junit.Assert.assertEquals("String index out of range: 0", expected.getMessage());
        }
    }
}

