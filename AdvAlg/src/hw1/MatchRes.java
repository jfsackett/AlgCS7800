package hw1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/*
while exists unmatched hospital h
	propose to favorite s who has not turned down prior offer
	if (s unmatched or s.pref(h) > s.pref(currMatch)
		accept & renege
	else
	    decline
*/
public class MatchRes {
	private final int numHosp;
	private final int numStdnt;
	private final int[] numHospPos;
	private final Integer[][] hospPref;
	private final Integer[][] stdntPref;
	private Set<Integer>[] hospMatch;
	private Integer[] stdntMatch;
	
	public static void main(String[] args) {
		int h = 16;
		int hs = 180;
		int s = 210;
		try {
			if (args.length > 0) {
				h = Integer.parseInt(args[0]);
			}
			if (args.length > 1) {
				hs = Integer.parseInt(args[1]);
			}
			if (args.length > 2) {
				s = Integer.parseInt(args[2]);
			}
			if (args.length > 3 || h > hs) { // || hs > s) {
				printUsage();
				System.exit(1);
			}
		}
		catch (NumberFormatException ex) {
			printUsage();
			System.exit(1);
		}

		int[] numHospPos = new int[h];
		for (int i=0; i < h; i++) {
			numHospPos[i] = 1;
		}
		Random rand = new Random();
		for (int j=hs-h; j > 0; j--) {
			numHospPos[rand.nextInt(h)]++;
		}
		System.out.println("Available Hospital Positions:");
		for (int i=0; i < h; i++) {
			System.out.format("%4d", numHospPos[i]);
		}
		System.out.println("\n");
		
		Integer[][] hospPref = new Integer[h][s];
		Integer[][] stdntPref = new Integer[s][h];
		for (int i=0; i < h; i++) {
			for (int j=0; j < s; j++) {
				hospPref[i][j] = j;
				stdntPref[j][i] = i;
			}
		}
		
		System.out.println("Hospital to Student Preferences:");
		for (int i=0; i < h; i++) {
			List<Integer> shuff = Arrays.asList(hospPref[i]);
			Collections.shuffle(shuff);
			shuff.toArray(hospPref[i]);
			for (int j=0; j < s; j++) {
				System.out.format("%4d", hospPref[i][j]);
			}
			System.out.println();
		}
		System.out.println("\nStudent to Hospital Preferences:");
		for (int j=0; j < s; j++) {
			List<Integer> shuff = Arrays.asList(stdntPref[j]);
			Collections.shuffle(shuff);
			shuff.toArray(stdntPref[j]);
			for (int i=0; i < h; i++) {
				System.out.format("%4d", stdntPref[j][i]);
			}
			System.out.println();
		}

		MatchRes matching = new MatchRes(h, s, numHospPos, hospPref, stdntPref);
		matching.match();
		System.out.println("\nHospital to Student Matches:");
		for (int i=0; i < h; i++) {
			for (Integer stdnt : matching.getHospMatch()[i]) {
				System.out.format("%5d", stdnt);
			}
			System.out.println();
		}
		System.out.println();
		System.out.println("\nStudent to Hospital Matches:");
		for (int j=0; j < s; j++) {
			System.out.format("%5d", matching.getStdntMatch()[j]);
		}
		System.out.println("\n");
		boolean stable = matching.checkStable();
		System.out.println("Stable: " + stable);
		if (!stable) {
			System.err.println("Unstable matching.");
			System.exit(1);
		}
	}
	
	private static void printUsage() {
		System.err.println("Command line error. Usage:");
		System.err.println("java MatchRes <num-hospitals> <num-students>");
		System.err.println("such that: num-hospitals <= num-students");
	}

	public MatchRes(int h, int s, int[] numHospPos, Integer[][] hospPref, Integer[][] stdntPref) {
		this.numHosp = h;
		this.numStdnt = s;
		this.numHospPos = numHospPos;
		this.hospPref = hospPref;
		this.stdntPref = stdntPref;
		hospMatch = new HashSet[numHosp];
		stdntMatch = new Integer[numStdnt];
		
		for (int i=0; i < numHosp; i++) {
			hospMatch[i] = new HashSet<Integer>(numHospPos[i]);
		}
	}
	
	public void match() {
		List<Integer>[] hospFavs = new ArrayList[numHosp];
		for (int i=0; i < numHosp; i++) {
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
				hospMatch[currHosp].add(currStdnt);
			}
			else if (stdntPref[currStdnt][currHosp] > stdntPref[currStdnt][currStdntHosp]) {
				stdntMatch[currStdnt] = currHosp;
				hospMatch[currHosp].add(currStdnt);
				hospMatch[currStdntHosp].remove(currStdnt);
			}
		}
	}
	
	private int getFreeHosp() {
		for (int i=0; i < numHosp; i++) {
			if (hospMatch[i].size() < numHospPos[i]) {
				return i;
			}
		}
		return -1;
	}

	boolean checkStable() {
		for (int h=0; h < numStdnt; h++) {
			Integer hosp1 = stdntMatch[h];
			if (hosp1 == null) {
				continue;
			}
			for (int i=0; i < numStdnt; i++) {
				Integer hosp2 = stdntMatch[i];
				if (hosp2 == null && hospPref[hosp1][i] > hospPref[hosp1][h]) {
					System.err.format("stdnt1: %d  stdnt2: %d  hosp1: %d  hosp2: %d  hospPref[hosp1][h]: %d  hospPref[hosp1][i]: %d\n", 
						h, i, hosp1, hosp2, hospPref[hosp1][h], hospPref[hosp1][i]);
					return false;
				}
				if (hosp2 != null && hospPref[hosp1][i] > hospPref[hosp1][h] && stdntPref[i][hosp1] > stdntPref[i][hosp2]) {
					System.err.format("stdnt1: %d  stdnt2: %d  hosp1: %d  hosp2: %d  hospPref[hosp1][h]: %d  hospPref[hosp1][i]: %d  stdntPref[i][hosp1]: %d  stdntPref[i][hosp2]:  %d\n", 
						h, i, hosp1, hosp2, hospPref[hosp1][h], hospPref[hosp1][i], stdntPref[i][hosp1], stdntPref[i][hosp2]);
					return false;
				}
			}
		}

		return true;
	}
	
	public Set<Integer>[] getHospMatch() {
		return hospMatch;
	}

	public Integer[] getStdntMatch() {
		return stdntMatch;
	}
}
