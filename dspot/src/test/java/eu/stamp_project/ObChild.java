package eu.stamp_project;

public class ObChild extends Ob {

    public ObChild(int a, int b, int c) {
        super(a,b);
        this.c = c;
    }

    public void setC(int c) {
        this.c = c;
    }


    public int getC() {
        return this.c;
    }

    private int c;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        ObChild ob = (ObChild) o;

        return a() == ob.a() &&
                getB() == ob.getB() && c == ob.getC();
    }
}
