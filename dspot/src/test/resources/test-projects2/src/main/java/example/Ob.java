package example;

public class Ob {
	
	public Ob(int a, int b) {
		this.a = a;
		this.b = b;
	}

	public int a() {
		return this.a;
	}

	public int b() {
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
                b == ob.b();
    }
}
