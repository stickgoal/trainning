package me.maiz.se.mini.multithreading.inner;

import java.util.concurrent.TimeUnit;

public class SynchronizedBlock {

	public static void main(String[] args) throws InterruptedException {
		Bricks bs = new Bricks(20);
		new BrickThread(bs).start();
		new BrickThread(bs).start();
		Thread.sleep(30 * 1000);
		System.out.println(bs.getCount());
	}

}

class Bricks {

	private int count;

	public Bricks(int count) {
		this.setCount(count);
	}

	void transfer() {
		String threadName = Thread.currentThread().getName();
		System.out.println(threadName +": 前置部分操作");
		synchronized (this) {
			System.out.println(threadName +"同步块 : 前置部分操作");
			count--;
			System.out.println(threadName +"同步块 : 后置部分操作");
		}
		System.out.println(threadName +": 后置部分操作");

	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}

class BrickThread extends Thread {
	private Bricks bricks;

	public BrickThread(Bricks bricks) {
		super();
		this.bricks = bricks;
	}

	@Override
	public void run() {
		for (int i = 0; i < 10; i++) {
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			bricks.transfer();
		}
	}
}