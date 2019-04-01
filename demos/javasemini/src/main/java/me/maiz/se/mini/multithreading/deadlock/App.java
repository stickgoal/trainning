package me.maiz.se.mini.multithreading.deadlock;

public class App {
	public static void main(String[] args) {
		String x1 = "Hello";
		String x2 = "World";
		Philosopher p1 = new Philosopher(x1, x2, 1);
		Philosopher p2 = new Philosopher(x1, x2, 2);
		p1.start();
		p2.start();
	}
}
