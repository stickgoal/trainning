package me.maiz.se.mini.multithreading.threadpool.executor;

import java.util.concurrent.TimeUnit;

public class App {
	public static void main(String[] args) {
		ThreadExecutor executor = new ThreadExecutor(10);
		for(int j=0;j<100;j++) {
			System.out.println(j);
			executor.execute(() -> {

				String name = Thread.currentThread().getName();
				for (int i = 0; i < 3; i++) {
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println(name + " " + i);
				}
			});
		}
	}
}
