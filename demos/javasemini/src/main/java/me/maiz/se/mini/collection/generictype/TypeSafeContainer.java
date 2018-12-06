package me.maiz.se.mini.collection.generictype;

/**
 * 一个可以存放任何对象的类
 */
public class TypeSafeContainer<T> {

	private T thing;
	
	public T get(){
		return thing;
	}
	
	public void add(T any){
		this.thing=any;
	}
	
	public void remove(){
		thing=null;
	}
	
}
