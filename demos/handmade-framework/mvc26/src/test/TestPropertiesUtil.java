package test;

import java.util.Properties;

import com.woniuxy.mvc26.support.PropertiesUtils;

import junit.framework.TestCase;

public class TestPropertiesUtil extends TestCase{
	
	public void test() {
		Properties p = PropertiesUtils.load("mvc.properties");
		assertNotNull(p);
		assertEquals("hello",p.getProperty("url"));
	}
	
}
