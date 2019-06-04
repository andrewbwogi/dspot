package eu.stamp_project;

//package example;

public class Ob {

    public Ob(int a, int b) {
        this.a = a;
        this.b = b;
    }

    public int a() {
        return this.a;
    }

    public void setB(int b) {
        this.b = b;
    }


    public int getB() {
        return this.b;
    }

    private int a;
    private int b;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        Ob ob = (Ob) o;

        return a == ob.a() &&
                b == ob.getB();
    }
}
