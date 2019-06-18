package me.maiz.se.mini.datatype;

public class ComplexType {

	public static void main(String[] args) {
		int x = 10;
		int y = 10;
		Product p1 = new Product("青岛啤酒",20);
		Product p2 = new Product("山城啤酒",21);

		int[] arr = new int[]{12,45,1,23,32};
		Character c = new Character('Y');

		Product prd1 = new Product("青岛啤酒",20);
		Product prd2 = new Product("青岛啤酒",20);
		//比较是否为同一个引用，false
		System.out.println("prd1==prd2 "+(prd1==prd2));
		Product prd3 = prd2;
		//比较是否为同一个引用，true
		System.out.println("prd3==prd2 "+(prd3==prd2));
		//比较内容是否相同，true
		System.out.println("prd1.equals(prd2) "+prd1.equals(prd2));


		System.out.println(x==y);

		String s  = null;
		String s1 = "";
		System.out.println(s1.length());
		
	}
}
