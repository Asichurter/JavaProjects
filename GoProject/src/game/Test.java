package game;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.regex.Matcher;

public class Test {
	public static void main(String[] args) {
		/*
		java.util.regex.Pattern p;
		LinkedList<Integer> test = new LinkedList<>();
		test.add(7);
		test.add(5);
		test.add(8);
		test.add(2);
		test.sort((n1, n2)->n1-n2);
		System.out.println(Arrays.toString(Arrays.copyOfRange(test.toArray(), 0, 3)));
	    
		int n1 = 0, n2 = 0, n3 = 0, counter = 0, total;
		int lines, rows;
		String m1 = "/0/_________/0//1/_________/1//2/__O______/2//3/_________/3//4/_____OXO_/4//5/_________/5//6/_________/6//7/_________/7//8/_________/8/";
		String m2 = "[.]*?/4/.*OXO.4/[.]*?";
		java.util.regex.Pattern p = java.util.regex.Pattern.compile(m2);
		Matcher matcher = p.matcher(m1);
		int shift = 3;
		if (matcher.find()) {
			System.out.println(matcher.group());
			n1 = matcher.start();
			n2 = matcher.end();
		}
		else System.err.println("Error£¡");
		p = java.util.regex.Pattern.compile("[.]?OXO[.]?");
		matcher = p.matcher(m1).region(n1, n2);
		if (matcher.find())
			n3 = matcher.start();
		
		p = java.util.regex.Pattern.compile("[.]*?/[0-9]/[.]*?");
		matcher = p.matcher(m1).region(0, n2);
		while (matcher.find()) {
			counter++;
		}
		*/
		//n3 -= (n1 + 3 - 3);
		//System.out.println(n3);
		/*System.out.println(m1.substring(0, n1+n2-5) + "X" + m1.substring(n1+n2-4));

		total = n1 + n2 - 3*counter - 5;
		lines = total / 9;
		rows = total % 9;
		System.out.println(counter + " " + total + " " + lines + " " + rows);
		System.out.println(matcher.region(0, n1+n2-4).groupCount());*/
		String a = "01234ABCD";
		System.out.println(a.indexOf(65));
	}
}
