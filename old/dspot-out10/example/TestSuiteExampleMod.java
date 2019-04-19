package example;


public class TestSuiteExampleMod {
    @org.junit.Test(timeout = 10000)
    public void test3_literalMutationString2() throws java.lang.Exception {
        example.Example ex = new example.Example();
        java.lang.String s = "acd";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("acd", s);
        // AssertGenerator create local variable with return value of invocation
        char o_test3_literalMutationString2__4 = ex.charAt(s, ((s.length()) - 1));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('d', ((char) (o_test3_literalMutationString2__4)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("acd", s);
    }

    @org.junit.Test(timeout = 10000)
    public void test3_literalMutationNumber8() throws java.lang.Exception {
        example.Example ex = new example.Example();
        java.lang.String s = "abcd";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("abcd", s);
        // AssertGenerator create local variable with return value of invocation
        char o_test3_literalMutationNumber8__4 = ex.charAt(s, ((s.length()) - // TestDataMutator on numbers
        0));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('d', ((char) (o_test3_literalMutationNumber8__4)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("abcd", s);
    }

    @org.junit.Test(timeout = 10000)
    public void test3_literalMutationString1_failAssert0() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            example.Example ex = new example.Example();
            java.lang.String s = "";
            ex.charAt(s, ((s.length()) - 1));
            org.junit.Assert.fail("test3_literalMutationString1 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException expected) {
            org.junit.Assert.assertEquals("String index out of range: 0", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void test3_literalMutationString2_literalMutationString42() throws java.lang.Exception {
        example.Example ex = new example.Example();
        java.lang.String s = "ad";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ad", s);
        // AssertGenerator create local variable with return value of invocation
        char o_test3_literalMutationString2__4 = ex.charAt(s, ((s.length()) - 1));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ad", s);
    }

    @org.junit.Test(timeout = 10000)
    public void test3_literalMutationString2_literalMutationNumber58() throws java.lang.Exception {
        example.Example ex = new example.Example();
        java.lang.String s = "acd";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("acd", s);
        // AssertGenerator create local variable with return value of invocation
        char o_test3_literalMutationString2__4 = ex.charAt(s, ((s.length()) - // TestDataMutator on numbers
        0));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("acd", s);
    }

    @org.junit.Test(timeout = 10000)
    public void test3_literalMutationString2_literalMutationString41_failAssert0() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            example.Example ex = new example.Example();
            java.lang.String s = "";
            // AssertGenerator create local variable with return value of invocation
            char o_test3_literalMutationString2__4 = ex.charAt(s, ((s.length()) - 1));
            org.junit.Assert.fail("test3_literalMutationString2_literalMutationString41 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException expected) {
            org.junit.Assert.assertEquals("String index out of range: 0", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void test3_literalMutationString2_literalMutationString42_literalMutationString142() throws java.lang.Exception {
        example.Example ex = new example.Example();
        java.lang.String s = "g";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("g", s);
        // AssertGenerator create local variable with return value of invocation
        char o_test3_literalMutationString2__4 = ex.charAt(s, ((s.length()) - 1));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("g", s);
    }

    @org.junit.Test(timeout = 10000)
    public void test3_literalMutationString2_literalMutationString42_literalMutationString144() throws java.lang.Exception {
        example.Example ex = new example.Example();
        java.lang.String s = "abcd";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("abcd", s);
        // AssertGenerator create local variable with return value of invocation
        char o_test3_literalMutationString2__4 = ex.charAt(s, ((s.length()) - 1));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("abcd", s);
    }

    @org.junit.Test(timeout = 10000)
    public void test3_literalMutationString2_literalMutationString42_literalMutationNumber149() throws java.lang.Exception {
        example.Example ex = new example.Example();
        java.lang.String s = "ad";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ad", s);
        // AssertGenerator create local variable with return value of invocation
        char o_test3_literalMutationString2__4 = ex.charAt(s, ((s.length()) - // TestDataMutator on numbers
        0));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ad", s);
    }

    @org.junit.Test(timeout = 10000)
    public void test3_literalMutationString2_literalMutationString42_literalMutationString140_failAssert0() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            example.Example ex = new example.Example();
            java.lang.String s = "";
            // AssertGenerator create local variable with return value of invocation
            char o_test3_literalMutationString2__4 = ex.charAt(s, ((s.length()) - 1));
            org.junit.Assert.fail("test3_literalMutationString2_literalMutationString42_literalMutationString140 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException expected) {
            org.junit.Assert.assertEquals("String index out of range: 0", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void test32_literalMutationString227() throws java.lang.Exception {
        example.Example ex = new example.Example();
        java.lang.String s = ":bcd";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(":bcd", s);
        // AssertGenerator create local variable with return value of invocation
        char o_test32_literalMutationString227__4 = ex.charAt(s, ((s.length()) - 1));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('d', ((char) (o_test32_literalMutationString227__4)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(":bcd", s);
    }

    @org.junit.Test(timeout = 10000)
    public void test32_literalMutationNumber233() throws java.lang.Exception {
        example.Example ex = new example.Example();
        java.lang.String s = "abcd";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("abcd", s);
        // AssertGenerator create local variable with return value of invocation
        char o_test32_literalMutationNumber233__4 = ex.charAt(s, ((s.length()) - // TestDataMutator on numbers
        0));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('d', ((char) (o_test32_literalMutationNumber233__4)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("abcd", s);
    }

    @org.junit.Test(timeout = 10000)
    public void test32_literalMutationString226_failAssert0() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            example.Example ex = new example.Example();
            java.lang.String s = "";
            ex.charAt(s, ((s.length()) - 1));
            org.junit.Assert.fail("test32_literalMutationString226 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException expected) {
            org.junit.Assert.assertEquals("String index out of range: 0", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void test32_literalMutationString227_literalMutationString268() throws java.lang.Exception {
        example.Example ex = new example.Example();
        java.lang.String s = "l>Ug";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("l>Ug", s);
        // AssertGenerator create local variable with return value of invocation
        char o_test32_literalMutationString227__4 = ex.charAt(s, ((s.length()) - 1));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("l>Ug", s);
    }

    @org.junit.Test(timeout = 10000)
    public void test32_literalMutationString227_literalMutationNumber282() throws java.lang.Exception {
        example.Example ex = new example.Example();
        java.lang.String s = ":bcd";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(":bcd", s);
        // AssertGenerator create local variable with return value of invocation
        char o_test32_literalMutationString227__4 = ex.charAt(s, ((s.length()) - // TestDataMutator on numbers
        0));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(":bcd", s);
    }

    @org.junit.Test(timeout = 10000)
    public void test32_literalMutationString227_literalMutationString266_failAssert0() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            example.Example ex = new example.Example();
            java.lang.String s = "";
            // AssertGenerator create local variable with return value of invocation
            char o_test32_literalMutationString227__4 = ex.charAt(s, ((s.length()) - 1));
            org.junit.Assert.fail("test32_literalMutationString227_literalMutationString266 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException expected) {
            org.junit.Assert.assertEquals("String index out of range: 0", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void test32_literalMutationString227_literalMutationString268_literalMutationString367() throws java.lang.Exception {
        example.Example ex = new example.Example();
        java.lang.String s = "lT>Ug";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("lT>Ug", s);
        // AssertGenerator create local variable with return value of invocation
        char o_test32_literalMutationString227__4 = ex.charAt(s, ((s.length()) - 1));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("lT>Ug", s);
    }

    @org.junit.Test(timeout = 10000)
    public void test32_literalMutationString227_literalMutationString268_literalMutationNumber386() throws java.lang.Exception {
        example.Example ex = new example.Example();
        java.lang.String s = "l>Ug";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("l>Ug", s);
        // AssertGenerator create local variable with return value of invocation
        char o_test32_literalMutationString227__4 = ex.charAt(s, ((s.length()) - // TestDataMutator on numbers
        0));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("l>Ug", s);
    }

    @org.junit.Test(timeout = 10000)
    public void test32_literalMutationString227_literalMutationString268_literalMutationString364_failAssert0() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            example.Example ex = new example.Example();
            java.lang.String s = "";
            // AssertGenerator create local variable with return value of invocation
            char o_test32_literalMutationString227__4 = ex.charAt(s, ((s.length()) - 1));
            org.junit.Assert.fail("test32_literalMutationString227_literalMutationString268_literalMutationString364 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException expected) {
            org.junit.Assert.assertEquals("String index out of range: 0", expected.getMessage());
        }
    }
}

