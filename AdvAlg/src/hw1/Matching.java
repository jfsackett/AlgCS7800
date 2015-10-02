package hw1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class Matching {
	private final int numHosp;
	private final int numStdnt;
	private final Integer[][] hospPref;
	private final Integer[][] stdntPref;
	private Integer[] hospMatch;
	private Integer[] stdntMatch;
	
	public static void main(String[] args) {
		int m = 21;
		int n = 21;
		if (args.length > 0) {
			m = Integer.parseInt(args[0]);
		}
		if (args.length > 1) {
			n = Integer.parseInt(args[1]);
		}

		Integer[][] hospPref = new Integer[m][n];
		Integer[][] stdntPref = new Integer[n][m];
		for (int i=0; i < m; i++) {
			for (int j=0; j < n; j++) {
				hospPref[i][j] = j;
				stdntPref[j][i] = i;
			}
		}
		System.out.println("Hospital to Student Preferences:");
		for (int i=0; i < m; i++) {
			List<Integer> shuff = Arrays.asList(hospPref[i]);
			Collections.shuffle(shuff);
			shuff.toArray(hospPref[i]);
			for (int j=0; j < n; j++) {
				System.out.format("%4d", hospPref[i][j]);
			}
			System.out.println();
		}
		System.out.println("\nStudent to Hospital Preferences:");
		for (int j=0; j < n; j++) {
			List<Integer> shuff = Arrays.asList(stdntPref[j]);
			Collections.shuffle(shuff);
			shuff.toArray(stdntPref[j]);
			for (int i=0; i < m; i++) {
				System.out.format("%4d", stdntPref[j][i]);
			}
			System.out.println();
		}

		Matching matching = new Matching(m, n, hospPref, stdntPref);
		matching.match();
		System.out.println("\nHospital to Student Matches:");
		for (int i=0; i < m; i++) {
			System.out.format("%4d", matching.getHospMatch()[i]);
		}
		System.out.println();
		System.out.println("\nStudent to Hospital Matches:");
		for (int j=0; j < n; j++) {
			System.out.format("%4d", matching.getStdntMatch()[j]);
		}
		System.out.println();
		System.out.println("\nStable: " + matching.checkStable());
	}

	public Matching(int m, int n, Integer[][] hospPref, Integer[][] stdntPref) {
		this.numHosp = m;
		this.numStdnt = n;
		this.hospPref = hospPref;
		this.stdntPref = stdntPref;
		hospMatch = new Integer[numHosp];
		stdntMatch = new Integer[numStdnt];
	}
	
	public void match() {
		List<Integer>[] hospFavs = new ArrayList[numHosp];
		for (int i=0; i < numHosp; i++) {
			hospMatch[i] = null;
			hospFavs[i] = new ArrayList<Integer>(numStdnt);
			for (int j=0; j < numStdnt; j++) {
				hospFavs[i].add(hospPref[i][j]);
			}
			for (int j=0; j < numStdnt; j++) {
				hospFavs[i].set((numStdnt-1) - hospPref[i][j], j);
			}
		}
		for (int j=0; j < numStdnt; j++) {
			stdntMatch[j] = null;
		}
		
		int currHosp;
		while ((currHosp = getFreeHosp()) >= 0) {
			int currStdnt = hospFavs[currHosp].get(0);
			hospFavs[currHosp].remove(0);
			Integer currStdntHosp = stdntMatch[currStdnt];
			if (currStdntHosp == null) {
				stdntMatch[currStdnt] = currHosp;
				hospMatch[currHosp] = currStdnt;
			}
			else if (stdntPref[currStdnt][currHosp] > stdntPref[currStdnt][currStdntHosp]) {
				stdntMatch[currStdnt] = currHosp;
				hospMatch[currHosp] = currStdnt;
				hospMatch[currStdntHosp] = null;
			}
		}
/*
while exists unmatched hospital h
	propose to favorite s who has not turned down prior offer
	if (s unmatched or s.pref(h) > s.pref(currMatch)
		accept & renege
	else
	    decline
*/
	}
	
	private int getFreeHosp() {
		for (int i=0; i < numHosp; i++) {
			if (hospMatch[i] == null) {
				return i;
			}
		}
		return -1;
	}

	boolean checkStable() {
		for (int i=0; i < numHosp; i++) {
			for (int j=0; j < numStdnt; j++) {
//				if (hospMatch[i] == stdntMatch[j]) {
//					continue;
//				}
				if (hospPref[i][j] > hospPref[i][hospMatch[i]] && stdntPref[j][i] > stdntPref[j][stdntMatch[j]]) {
					System.out.format("%d  %d  %d  %d  %d  %d\n", i, j, hospPref[i][j], hospPref[i][hospMatch[i]], stdntPref[j][i], stdntPref[j][stdntMatch[j]]);
					return false;
				}
			}
		}
		return true;
	}
	
	public Integer[] getHospMatch() {
		return hospMatch;
	}

	public Integer[] getStdntMatch() {
		return stdntMatch;
	}
}
