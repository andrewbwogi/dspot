package fr.inria.amp;

public class ArrayMutation {

    int presentLitInt = 32;
    String presentLitString = "MySecondStringLiteral";
    int[][] presentLitArray = new int[][]{{1,2},{3,4}};

    public void methodInteger() {
        int literalInt = 23;
    }

    public void methodString() {
        String literalString = "MyStringLiteral";
        literalString = null;
        String literalString2 = null;
        literalString = getString("MyStringLiteral3");
        literalString = getString(null);
    }

    private String getString(String s) {
        return s;
    }

    public void methodWithCharArray(char... array) {

    }

    public void methodThatClassmethodWithCharArray() {
        methodWithCharArray('a', 'b');
    }

    public void methodArray() {
	int[][] literalArray = new int[][]{{3,4},{1,2}};
    }
}
