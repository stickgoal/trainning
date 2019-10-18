package test.com.woniuxy.orm26;

import java.util.List;

import com.woniuxy.orm26.core.Session;
import com.woniuxy.orm26.core.SessionImpl;
import com.woniuxy.orm26.support.PageResult;

import junit.framework.TestCase;

public class TestSession extends TestCase{

	public void testFindById() {
//		Connection conn = JDBCUtils.getConnection();
//		QueryRunner qr = new QueryRunner();
//		qr.update(conn,"",);
		
		Session<User> session = new SessionImpl<>();
		
		User user = session.findById(User.class, 1);
		assertNotNull(user);
		assertEquals("wanger",user.getUsername());
		assertEquals("wohenshuai",user.getPassword());
		System.out.println(user);
	}
	

	public void testFindAll() {
		
		Session<User> session = new SessionImpl<>();
		
		List<User> users = session.findAll(User.class);
		assertNotNull(users);
		assertEquals(1,users.size());
		assertEquals("wanger",users.get(0).getUsername());
		assertEquals("wohenshuai",users.get(0).getPassword());
		System.out.println(users);
	}
	
	public void testUpdate() {
		Session<User> session = new SessionImpl<>();
		User user = new User();
		user.setUserId(1);
		user.setUsername("张三");
		user.setPassword("wohenku");
		session.updateById(user);
		
		User user1 = session.findById(User.class, 1);
		assertEquals("张三",user1.getUsername());
		assertEquals("wohenku",user1.getPassword());
		
	}
	
	public void testSave() {
		Session<User> session = new SessionImpl<>();
		User user = new User();
		user.setUsername("lisi");
		user.setPassword("wohenbang");
		session.save(user);
	}
	
	public void testQueryOne() {
		Session<User> session = new SessionImpl<>();
		
		User user = session.queryOne(User.class, "select * from user where username=? and password=?", "张三","wohenku");
		assertNotNull(user);
		

		List<User> users = session.queryList(User.class, "select * from user limit ?,?", 2,3);
		assertNotNull(users);
		assertEquals(3, users.size());
		assertEquals(3, users.get(0).getUserId());
		assertEquals(4, users.get(1).getUserId());
		assertEquals(5, users.get(2).getUserId());
		
		session.update(User.class, "update user set username=? where user_id=?", "王五",7);
		
		User user2 = session.findById(User.class, 7);
		assertEquals("王五",user2.getUsername());
		
	}
	
	public void testPage() {
		Session<User> s = new SessionImpl<User>();
		
		String countSql = "select count(*) from user where user_id>? and user_id < ? ";
		String querySql = "select * from user where user_id>? and user_id < ? limit ?,?";
		PageResult<User> pr = s.page(User.class, countSql , querySql, 2, 3, 0,10);
		System.out.println(pr);
	}
}
