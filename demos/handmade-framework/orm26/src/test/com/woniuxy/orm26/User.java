package test.com.woniuxy.orm26;

import com.woniuxy.orm26.annotations.Column;
import com.woniuxy.orm26.annotations.Id;

import lombok.Data;
@Data
public class User {
	
	@Id
	private int userId;
	
	@Column(length = 256)
	private String username;
	
	@Column(length = 256)
	private String password;
	

	

}
