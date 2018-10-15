package me.maiz.se.mini.datatype;

public class ComplexType {

	public static void main(String[] args) {
		int x = 10;
		int y = 10;
		Product p1 = new Product("ÇàµºÆ¡¾Æ",20);
		Product p2 = new Product("É½³ÇÆ¡¾Æ",21);

		int[] arr = new int[]{12,45,1,23,32};
		Character c = new Character('Y');


		Product p3 = p2;
		System.out.println(x==y);
		System.out.println(p1==p2);
		System.out.println(p3==p2);
		
		String s  = null;
		String s1 = "";
		System.out.println(s1.length());
		
	}
}
