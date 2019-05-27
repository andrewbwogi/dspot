
package fr.inria.sample;

import java.util.Comparator;

import org.junit.Test;

public class TestClassWithArray {

	@Test
	public void test1() {

		char[] valuesnew = {'d', 'w', 'd', 'd'};
        	char[] valuesnew2 = {'d', 'w', 'd', 'd'};

        	char[] valuesnew3 = "t\"t y	o".toCharArray();
        	char[] valuesnew4 = "t\"t y	o".toCharArray();

        	float[] f1 = {1.2F,4.5F};
        	float[] f2 = {1.2F,4.5F};
        	org.junit.Assert.assertArrayEquals(f1,f2,1e-9F);

		double one = 1.1;
		double two = 1.1;
		org.junit.Assert.assertEquals(one,two,0.2);

	}
}
