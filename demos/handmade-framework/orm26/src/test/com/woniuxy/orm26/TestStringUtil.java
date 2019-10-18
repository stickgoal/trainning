package test.com.woniuxy.orm26;

import com.woniuxy.orm26.support.StringUtils;

import junit.framework.TestCase;

public class TestStringUtil extends TestCase{
	public void testCamelCaseToUnderscore() {
		assertEquals("abc",StringUtils.camelCaseToUnderscore("abc"));
		assertEquals("abc",StringUtils.camelCaseToUnderscore("Abc"));
		assertEquals("a_bc",StringUtils.camelCaseToUnderscore("aBc"));
		assertEquals("a_b_c",StringUtils.camelCaseToUnderscore("aBC"));
	}
}
