package test.com.woniuxy.orm26;

import com.woniuxy.orm26.annotations.Column;
import com.woniuxy.orm26.annotations.Id;
import com.woniuxy.orm26.core.DMLGenerator;

import junit.framework.TestCase;
import lombok.Data;

public class TestDMLGenerator extends TestCase{

	public void testInsert() {
		//insert into user (username,password,user_id) values (?,?,default)
		String sql1 = DMLGenerator.insert(User.class);
		assertEquals("insert into user(username,password,user_id) values (?,?,default)",sql1);
		
		String sql2 = DMLGenerator.insert(Product.class);
		assertEquals("insert into product(product_name,price,product_id) values (?,?,?)",sql2);
		
	}
	
}

@Data
class Product{
	@Id(isAutoIncrement = false)
	private int productId;
	
	@Column(length = 256)
	private String productName;
	
	@Column(length = 10)
	private double price;
	
}