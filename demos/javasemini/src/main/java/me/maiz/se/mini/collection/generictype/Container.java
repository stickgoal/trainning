package me.maiz.se.mini.collection.generictype;

/**
 * 一个可以存放任何对象的类
 */
public class Container {

	private Object thing;
	
	public Object get(){
		return thing;
	}
	
	public void add(Object any){
		this.thing=any;
	}
	
	public void remove(){
		thing=null;
	}
	
}
