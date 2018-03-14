package me.maiz.trainning.framework.spring.instantiation;

public class CarFactory {

	public Car makeCar() throws Exception {
		System.out.println("CarFactory.makeCar()");
		Car c = new Car();
		c.setWeight(211);
		return c;
	}

}
