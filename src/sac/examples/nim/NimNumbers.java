package sac.examples.nim;

import java.util.Arrays;
import java.util.List;

public class NimNumbers {
	public static int sum(int a, int b) {
		return a ^ b;
	}
	public static int sum(Integer[] list) {
		int sum = 0;
		for (int addend : list) {
			sum = sum ^ addend;
		}
		return sum;
	}
	public static int sum(List<Integer> list) {
		return NimNumbers.sum(list.toArray(new Integer[0]));
	}
	public static void main(String[] args) {
		int s = NimNumbers.sum(3,4);
		int s3 =NimNumbers.sum(s,5);
		int s4 =NimNumbers.sum(s3,0);
		System.out.println(s);		
		System.out.println(s3);
		System.out.println(s4);
		
		Integer [] tab = {1,4,5};
		System.out.println(NimNumbers.sum(Arrays.asList(tab)));
	}	
}
