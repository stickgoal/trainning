package me.maiz.trainning.framework.spring.instantiation;

public class Car {

	private Steer steer;
	private double weight;
	
	public Car() {
	}
	public Car(Steer steer) {
		this.steer=steer;
	}
	
	public Car(Steer steer,double weight) {
		this.steer=steer;
		this.weight=weight;
	}
	
	public void setSteer(Steer steer) {
		this.steer = steer;
	}
	
	

	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
	@Override
	public String toString() {
		return "Car [steer=" + steer + ", weight=" + weight + "]";
	}


	public static Car make(){
		System.out.println("使用工厂方法创建Car");
		Car c = new Car();
		c.setSteer(new Steer());
		c.weight=1.5;
		return c; 
	}

}
