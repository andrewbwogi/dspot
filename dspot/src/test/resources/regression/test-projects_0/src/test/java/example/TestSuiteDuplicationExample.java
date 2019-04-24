package example;

public class TestSuiteDuplicationExample {

    @org.junit.Test
    public void test1() {
        example.Example ex = new example.Example();
        java.lang.String s = "abcd";
        org.junit.Assert.assertEquals('d', ex.charAt(s, ((s.length()) - 1)));
    }

    @org.junit.Test
    public void test2() {
        example.Example ex = new example.Example();
        java.lang.String s = "abcd";
        org.junit.Assert.assertEquals('d', ex.charAt(s, ((s.length()) - 1)));
    }
}

