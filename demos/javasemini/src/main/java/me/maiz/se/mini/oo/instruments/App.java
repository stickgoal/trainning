package me.maiz.se.mini.oo.instruments;

public class App {

	public static void main(String[] args) {
		Piano piano = new Piano();
		ErHu erHu = new ErHu();
		Pipa pipa = new Pipa();
		Musician musician = new Musician();
		musician.play(piano);
		musician.play(erHu);
		musician.play(pipa);
	}

}
