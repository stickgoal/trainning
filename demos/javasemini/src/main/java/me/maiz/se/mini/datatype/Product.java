package me.maiz.se.mini.datatype;

import java.util.Objects;

/**
 * 一个自定义的引用类型
 */
public class Product {

	String name;
	double price;

	public Product(String name, double price) {
		this.name = name;
		this.price = price;
	}
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Product product = (Product) o;
		return Double.compare(product.price, price) == 0 &&
				Objects.equals(name, product.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, price);
	}
}
