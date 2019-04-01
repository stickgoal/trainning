package me.maiz.se.mini.multithreading.deadlock;

public class Philosopher extends Thread {

	private String x1;
	private String x2;
	private int idx;

	public Philosopher(String x1, String x2, int idx) {
		this.x1 = x1;
		this.x2 = x2;
		this.idx = idx;
	}

	public void run() {
		if (idx % 2 == 1) {
			synchronized (x1) {
				System.out.println("A x1锁定");
				sleep();
				System.out.println("A 尝试获取x1的锁");
				try {
					x1.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				synchronized (x2) {
					System.out.println("A x2锁定");
					sleep();
				}
			}
		} else

		{
			synchronized (x2) {
				System.out.println("B x2锁定");
				sleep();
				System.out.println("B 尝试获取x1的锁");
				synchronized (x1) {
					System.out.println("B x1锁定");
					sleep();
					x1.notify();
				}
			}

		}
	}

	private void sleep() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
