package hw2;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class LocalMax {

	public static void main(String[] args) {
		int sz = 10000000;
		List<Boolean> o = Arrays.asList(new Boolean[sz]);
		int[] a = fillArray(sz);
		System.out.println("max- " + isLocalMax(a, 0, a.length - 1, o));
		long openBoxes = o.stream().filter(p -> (p != null) && p).count();
		System.out.println("openBoxes- " + openBoxes);
		System.out.format("log(%d)- %d", sz, (int)Math.ceil(Math.log(sz)));
	}

	private static Random randGen = new Random();
	
	private static int[] fillArray(int sz) {
		int[] a = new int[sz];
		for (int ix=0; ix<sz; ix++) {
			a[ix] = randGen.nextInt(sz);
			a[sz/2] = sz;
		}
		return a;
	}

	private static int isLocalMax(int[] a, int s, int e, List<Boolean> o) {
		int sz = e - s + 1;
		if (sz < 2) {
			return -1;
		}
		if (sz == 2) {
			int b0 = a[s]; o.set(s, true);
			int b1 = a[s+1]; o.set(s+1, true);
			return (b0 > b1) ? b0 : -1;
		}
		int m = s + (sz / 2);
		int b0 = a[m]; o.set(m, true);
		int b1 = a[m+1]; o.set(m+1, true);
		int t;
		if (b0 > b1) {
			if ((t = isLocalMax(a, s, m, o)) < 0) {
				return b0;
			}
			else {
				return t;
			}
		}
		else if ((t = isLocalMax(a, m+1, e, o)) < 0) {
			return b1;
		}
		else {
			return t;
		}

	}
	
}
