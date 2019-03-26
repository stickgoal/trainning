package me.maiz.se.mini.multithreading;

public class Operations {
	public static void main(String[] args) throws InterruptedException {
		OpThread opt = new OpThread();
		System.out.println("main方法 start前：" + opt.getState());
		opt.start();
		System.out.println("main方法 start后：" + opt.getState());

		Thread.sleep(500);
		System.out.println("0.5s后 ： " + opt.getState());

		Thread.sleep(5000);

		System.out.println("5s后 ： " + opt.getState());
	}
}

class OpThread extends Thread {
	public OpThread() {
		System.out.println("构造器：" + getState());
	}

	@Override
	public void run() {
		System.out.println("run方法：" + getState());
		try {
			Thread.sleep(1000);
			System.out.println("休眠结束：" + getState());

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
