package example;


public class TestSuiteExample {
    @org.junit.Test(timeout = 10000)
    public void test3_literalMutationString45() throws java.lang.Exception {
        example.example.Example ex = new example.example.Example();
        java.lang.String s = "abd";
        org.junit.Assert.assertEquals("abd", s);
        char o_test3_literalMutationString45__4 = ex.charAt(s, ((s.length()) - 1));
        org.junit.Assert.assertEquals('d', ((char) (o_test3_literalMutationString45__4)));
        org.junit.Assert.assertEquals("abd", s);
    }

    @org.junit.Test(timeout = 10000)
    public void test32_literalMutationString84() throws java.lang.Exception {
        example.example.Example ex = new example.example.Example();
        java.lang.String s = "a_cd";
        org.junit.Assert.assertEquals("a_cd", s);
        char o_test32_literalMutationString84__4 = ex.charAt(s, ((s.length()) - 1));
        org.junit.Assert.assertEquals('d', ((char) (o_test32_literalMutationString84__4)));
        org.junit.Assert.assertEquals("a_cd", s);
    }

    @org.junit.Test(timeout = 10000)
    public void test3_literalMutationNumber51() throws java.lang.Exception {
        example.example.Example ex = new example.example.Example();
        java.lang.String s = "abcd";
        org.junit.Assert.assertEquals("abcd", s);
        char o_test3_literalMutationNumber51__4 = ex.charAt(s, ((s.length()) - 0));
        org.junit.Assert.assertEquals('d', ((char) (o_test3_literalMutationNumber51__4)));
        org.junit.Assert.assertEquals("abcd", s);
    }

    @org.junit.Test(timeout = 10000)
    public void test2_literalMutationString2() throws java.lang.Exception {
        example.Example ex = new example.Example();
        char o_test2_literalMutationString2__3 = ex.charAt("acd", 3);
        org.junit.Assert.assertEquals('d', ((char) (o_test2_literalMutationString2__3)));
    }

    @org.junit.Test(timeout = 10000)
    public void test2_literalMutationString3() throws java.lang.Exception {
        example.Example ex = new example.Example();
        char o_test2_literalMutationString3__3 = ex.charAt("abcdefghijklm", 3);
        org.junit.Assert.assertEquals('d', ((char) (o_test2_literalMutationString3__3)));
    }

    @org.junit.Test(timeout = 10000)
    public void test2_literalMutationNumber9() throws java.lang.Exception {
        example.Example ex = new example.Example();
        char o_test2_literalMutationNumber9__3 = ex.charAt("abcd", 0);
        org.junit.Assert.assertEquals('a', ((char) (o_test2_literalMutationNumber9__3)));
    }

    @org.junit.Test(timeout = 10000)
    public void test2_mg12() throws java.lang.Exception {
        int __DSPOT_index_1 = 2086707665;
        java.lang.String __DSPOT_s_0 = "cbCS@!x*zH_,y(q2 5[g";
        example.Example ex = new example.Example();
        char o_test2_mg12__5 = ex.charAt("abcd", 3);
        org.junit.Assert.assertEquals('d', ((char) (o_test2_mg12__5)));
        char o_test2_mg12__6 = ex.charAt(__DSPOT_s_0, __DSPOT_index_1);
        org.junit.Assert.assertEquals('g', ((char) (o_test2_mg12__6)));
        org.junit.Assert.assertEquals('d', ((char) (o_test2_mg12__5)));
    }

    @org.junit.Test(timeout = 10000)
    public void test2_literalMutationString1_failAssert0() throws java.lang.Exception {
        try {
            example.Example ex = new example.Example();
            ex.charAt("", 3);
            org.junit.Assert.fail("test2_literalMutationString1 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException expected) {
            org.junit.Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void test2_literalMutationString3_mg88() throws java.lang.Exception {
        int __DSPOT_index_5 = -144653405;
        java.lang.String __DSPOT_s_4 = "SO/woO!OKS@Rl&{ha!&B";
        example.Example ex = new example.Example();
        char o_test2_literalMutationString3__3 = ex.charAt("abcdefghijklm", 3);
        char o_test2_literalMutationString3_mg88__8 = ex.charAt(__DSPOT_s_4, __DSPOT_index_5);
        org.junit.Assert.assertEquals('S', ((char) (o_test2_literalMutationString3_mg88__8)));
    }

    @org.junit.Test(timeout = 10000)
    public void test2_literalMutationNumber9_mg91() throws java.lang.Exception {
        int __DSPOT_index_7 = 831880648;
        java.lang.String __DSPOT_s_6 = "vg[?i!rb0/|]6^FT)-ef";
        example.Example ex = new example.Example();
        char o_test2_literalMutationNumber9__3 = ex.charAt("abcd", 0);
        char o_test2_literalMutationNumber9_mg91__9 = ex.charAt(__DSPOT_s_6, __DSPOT_index_7);
        org.junit.Assert.assertEquals('f', ((char) (o_test2_literalMutationNumber9_mg91__9)));
    }

    @org.junit.Test(timeout = 10000)
    public void test2_mg12_mg114() throws java.lang.Exception {
        int __DSPOT_index_11 = -1874903437;
        java.lang.String __DSPOT_s_10 = "{[Iz>YSe|%xHdm7#=ToX";
        int __DSPOT_index_1 = 2086707665;
        java.lang.String __DSPOT_s_0 = "cbCS@!x*zH_,y(q2 5[g";
        example.Example ex = new example.Example();
        char o_test2_mg12__5 = ex.charAt("abcd", 3);
        char o_test2_mg12__6 = ex.charAt(__DSPOT_s_0, __DSPOT_index_1);
        char o_test2_mg12_mg114__13 = ex.charAt(__DSPOT_s_10, __DSPOT_index_11);
        org.junit.Assert.assertEquals('{', ((char) (o_test2_mg12_mg114__13)));
    }

    @org.junit.Test(timeout = 10000)
    public void test2_literalMutationNumber9_literalMutationString48_failAssert0() throws java.lang.Exception {
        try {
            example.Example ex = new example.Example();
            char o_test2_literalMutationNumber9__3 = ex.charAt("", 0);
            org.junit.Assert.fail("test2_literalMutationNumber9_literalMutationString48 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException expected) {
            org.junit.Assert.assertEquals("String index out of range: 0", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void test2_literalMutationString2_mg93() throws java.lang.Exception {
        int __DSPOT_index_7 = -432859314;
        java.lang.String __DSPOT_s_6 = "01yCi*OdwpauR%h1,xav";
        example.example.Example ex = new example.example.Example();
        char o_test2_literalMutationString2__3 = ex.charAt("acd", 3);
        char o_test2_literalMutationString2_mg93__8 = ex.charAt(__DSPOT_s_6, __DSPOT_index_7);
        org.junit.Assert.assertEquals('0', ((char) (o_test2_literalMutationString2_mg93__8)));
    }

    @org.junit.Test(timeout = 10000)
    public void test2_literalMutationString3_mg83() throws java.lang.Exception {
        int __DSPOT_index_5 = -144653405;
        java.lang.String __DSPOT_s_4 = "SO/woO!OKS@Rl&{ha!&B";
        example.example.Example ex = new example.example.Example();
        char o_test2_literalMutationString3__3 = ex.charAt("abcdefghijklm", 3);
        char o_test2_literalMutationString3_mg83__8 = ex.charAt(__DSPOT_s_4, __DSPOT_index_5);
        org.junit.Assert.assertEquals('S', ((char) (o_test2_literalMutationString3_mg83__8)));
    }

    @org.junit.Test(timeout = 10000)
    public void test2_mg12_mg113() throws java.lang.Exception {
        int __DSPOT_index_11 = -1874903437;
        java.lang.String __DSPOT_s_10 = "{[Iz>YSe|%xHdm7#=ToX";
        int __DSPOT_index_1 = 2086707665;
        java.lang.String __DSPOT_s_0 = "cbCS@!x*zH_,y(q2 5[g";
        example.example.Example ex = new example.example.Example();
        char o_test2_mg12__5 = ex.charAt("abcd", 3);
        char o_test2_mg12__6 = ex.charAt(__DSPOT_s_0, __DSPOT_index_1);
        char o_test2_mg12_mg113__13 = ex.charAt(__DSPOT_s_10, __DSPOT_index_11);
        org.junit.Assert.assertEquals('{', ((char) (o_test2_mg12_mg113__13)));
    }

    @org.junit.Test(timeout = 10000)
    public void test2_literalMutationString3_mg82() throws java.lang.Exception {
        int __DSPOT_index_5 = -1920258044;
        java.lang.String __DSPOT_s_4 = "i!rb0/|]6^FT)-ef&bk*";
        example.example.Example ex = new example.example.Example();
        char o_test2_literalMutationString3__3 = ex.charAt("abcdefghijklm", 3);
        char o_test2_literalMutationString3_mg82__8 = ex.charAt(__DSPOT_s_4, __DSPOT_index_5);
        org.junit.Assert.assertEquals('i', ((char) (o_test2_literalMutationString3_mg82__8)));
    }

    @org.junit.Test(timeout = 10000)
    public void test32_literalMutationNumber90() throws java.lang.Exception {
        example.example.Example ex = new example.example.Example();
        java.lang.String s = "abcd";
        org.junit.Assert.assertEquals("abcd", s);
        char o_test32_literalMutationNumber90__4 = ex.charAt(s, ((s.length()) - 0));
        org.junit.Assert.assertEquals('d', ((char) (o_test32_literalMutationNumber90__4)));
        org.junit.Assert.assertEquals("abcd", s);
    }

    @org.junit.Test(timeout = 10000)
    public void test3_literalMutationString44_failAssert0() throws java.lang.Exception {
        try {
            example.example.Example ex = new example.example.Example();
            java.lang.String s = "";
            ex.charAt(s, ((s.length()) - 1));
            org.junit.Assert.fail("test3_literalMutationString44 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException expected) {
            org.junit.Assert.assertEquals("String index out of range: 0", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void test32_literalMutationString83_failAssert0() throws java.lang.Exception {
        try {
            example.example.Example ex = new example.example.Example();
            java.lang.String s = "";
            ex.charAt(s, ((s.length()) - 1));
            org.junit.Assert.fail("test32_literalMutationString83 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException expected) {
            org.junit.Assert.assertEquals("String index out of range: 0", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void test4_literalMutationString123() throws java.lang.Exception {
        example.example.Example ex = new example.example.Example();
        java.lang.String s = "acd";
        org.junit.Assert.assertEquals("acd", s);
        char o_test4_literalMutationString123__4 = ex.charAt(s, 12);
        org.junit.Assert.assertEquals('d', ((char) (o_test4_literalMutationString123__4)));
        org.junit.Assert.assertEquals("acd", s);
    }

    @org.junit.Test(timeout = 10000)
    public void test4_literalMutationString124() throws java.lang.Exception {
        example.example.Example ex = new example.example.Example();
        java.lang.String s = "abcdefghijklm";
        org.junit.Assert.assertEquals("abcdefghijklm", s);
        char o_test4_literalMutationString124__4 = ex.charAt(s, 12);
        org.junit.Assert.assertEquals('m', ((char) (o_test4_literalMutationString124__4)));
        org.junit.Assert.assertEquals("abcdefghijklm", s);
    }

    @org.junit.Test(timeout = 10000)
    public void test4_literalMutationNumber128() throws java.lang.Exception {
        example.example.Example ex = new example.example.Example();
        java.lang.String s = "abcd";
        org.junit.Assert.assertEquals("abcd", s);
        char o_test4_literalMutationNumber128__4 = ex.charAt(s, 0);
        org.junit.Assert.assertEquals('a', ((char) (o_test4_literalMutationNumber128__4)));
        org.junit.Assert.assertEquals("abcd", s);
    }

    @org.junit.Test(timeout = 10000)
    public void test4_literalMutationString122_failAssert0() throws java.lang.Exception {
        try {
            example.example.Example ex = new example.example.Example();
            java.lang.String s = "";
            ex.charAt(s, 12);
            org.junit.Assert.fail("test4_literalMutationString122 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException expected) {
            org.junit.Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void test7_literalMutationString166() throws java.lang.Exception {
        example.example.Example ex = new example.example.Example();
        char o_test7_literalMutationString166__3 = ex.charAt("z2[|", 2);
        org.junit.Assert.assertEquals('[', ((char) (o_test7_literalMutationString166__3)));
    }

    @org.junit.Test(timeout = 10000)
    public void test7_literalMutationNumber172() throws java.lang.Exception {
        example.example.Example ex = new example.example.Example();
        char o_test7_literalMutationNumber172__3 = ex.charAt("abcd", 4);
        org.junit.Assert.assertEquals('d', ((char) (o_test7_literalMutationNumber172__3)));
    }

    @org.junit.Test(timeout = 10000)
    public void test7_literalMutationNumber173() throws java.lang.Exception {
        example.example.Example ex = new example.example.Example();
        char o_test7_literalMutationNumber173__3 = ex.charAt("abcd", 0);
        org.junit.Assert.assertEquals('a', ((char) (o_test7_literalMutationNumber173__3)));
    }

    @org.junit.Test(timeout = 10000)
    public void test7_literalMutationString165_failAssert0() throws java.lang.Exception {
        try {
            example.example.Example ex = new example.example.Example();
            ex.charAt("", 2);
            org.junit.Assert.fail("test7_literalMutationString165 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException expected) {
            org.junit.Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void test8_literalMutationString204() throws java.lang.Exception {
        example.example.Example ex = new example.example.Example();
        char o_test8_literalMutationString204__3 = ex.charAt("amcd", 1);
        org.junit.Assert.assertEquals('m', ((char) (o_test8_literalMutationString204__3)));
    }

    @org.junit.Test(timeout = 10000)
    public void test8_literalMutationNumber211() throws java.lang.Exception {
        example.example.Example ex = new example.example.Example();
        char o_test8_literalMutationNumber211__3 = ex.charAt("abcd", 0);
        org.junit.Assert.assertEquals('a', ((char) (o_test8_literalMutationNumber211__3)));
    }

    @org.junit.Test(timeout = 10000)
    public void test8_literalMutationString205_failAssert0() throws java.lang.Exception {
        try {
            example.example.Example ex = new example.example.Example();
            ex.charAt("", 1);
            org.junit.Assert.fail("test8_literalMutationString205 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException expected) {
            org.junit.Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void test9_literalMutationString244() throws java.lang.Exception {
        example.example.Example ex = new example.example.Example();
        char o_test9_literalMutationString244__3 = ex.charAt("abcefghijklm", 5);
        org.junit.Assert.assertEquals('g', ((char) (o_test9_literalMutationString244__3)));
    }

    @org.junit.Test(timeout = 10000)
    public void test9_literalMutationString248() throws java.lang.Exception {
        example.example.Example ex = new example.example.Example();
        char o_test9_literalMutationString248__3 = ex.charAt("abcd", 5);
        org.junit.Assert.assertEquals('d', ((char) (o_test9_literalMutationString248__3)));
    }

    @org.junit.Test(timeout = 10000)
    public void test9_literalMutationNumber250() throws java.lang.Exception {
        example.example.Example ex = new example.example.Example();
        char o_test9_literalMutationNumber250__3 = ex.charAt("abcdefghijklm", 0);
        org.junit.Assert.assertEquals('a', ((char) (o_test9_literalMutationNumber250__3)));
    }

    @org.junit.Test(timeout = 10000)
    public void test9_literalMutationString243_failAssert0() throws java.lang.Exception {
        try {
            example.example.Example ex = new example.example.Example();
            ex.charAt("", 5);
            org.junit.Assert.fail("test9_literalMutationString243 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException expected) {
            org.junit.Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }
}

