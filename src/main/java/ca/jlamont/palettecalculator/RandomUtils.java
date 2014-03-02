package ca.jlamont.palettecalculator;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Random;

public class RandomUtils {

	public static int[] getRandomInts(int min, int max, int n) {
		BitSet bitset = new BitSet(max - min);

		Random random = new Random();

		int found = 0;

		while (found < n) {
			int current = random.nextInt(max - min) + min;

			if (!bitset.get(current)) {
				bitset.set(current);
				++found;
			}
		}

		found = 0;

		int[] result = new int[n];

		for (int i = 0; i < bitset.size() && found < n; ++i) {
			if (bitset.get(i)) {
				result[found++] = i;
			}
		}

		return result;
	}

	public static void main(String args[]){
		System.out.println(Arrays.toString(RandomUtils.getRandomInts(0, 5640, 10)));
	}
}
