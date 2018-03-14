package me.maiz.trainning.framework.spring.instantiation;

import org.springframework.beans.factory.FactoryBean;

public class CarFactory2 implements FactoryBean<Car>{

	public Car getObject() throws Exception {
		System.out.println("CarFactory2#getObject");
		return new Car();
	}

	public Class<?> getObjectType() {
		return Car.class;
	}

	public boolean isSingleton() {
		return true;
	}

}
