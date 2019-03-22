package io.github.laplacedemon.asyncmysql.resultset;

import org.junit.Assert;
import org.junit.Test;

import io.github.laplacedemon.asyncmysql.resultset.AsyncPreparedStatement;

public class TestAsyncPreparedStatement {
	
	@Test
	public void testAsyncPreparedStatement() {
		AsyncPreparedStatement asyncPreparedStatement = new AsyncPreparedStatement("select * from testdb.t_user_data");
		String statementsql = asyncPreparedStatement.getStatement();
		Assert.assertEquals(statementsql, "select * from testdb.t_user_data");
	}
	
	@Test
	public void testAsyncPreparedStatementWithParams0() {
		AsyncPreparedStatement asyncPreparedStatement = new AsyncPreparedStatement("select * from t_user_data where user_name = ?", "xiaoming");
		String statementsql = asyncPreparedStatement.getStatement();
		Assert.assertEquals(statementsql, "select * from t_user_data where user_name = 'xiaoming'");
	}
	
	@Test
	public void testAsyncPreparedStatementWithParams1() {
		AsyncPreparedStatement asyncPreparedStatement = new AsyncPreparedStatement("select * from t_user_data where user_name = ? and age < 16", "xiaoming");
		String statementsql = asyncPreparedStatement.getStatement();
		Assert.assertEquals(statementsql, "select * from t_user_data where user_name = 'xiaoming' and age < 16");
	}
	
	@Test
	public void testAsyncPreparedStatementWithParams2() {
		AsyncPreparedStatement asyncPreparedStatement = new AsyncPreparedStatement("select * from t_user_data where user_name = ? and age < ?", "xiaoming", 12);
		String statementsql = asyncPreparedStatement.getStatement();
		Assert.assertEquals(statementsql, "select * from t_user_data where user_name = 'xiaoming' and age < 12");
	}
	
	@Test
	public void testAsyncPreparedStatementWithParams3() {
		AsyncPreparedStatement asyncPreparedStatement = new AsyncPreparedStatement("select * from t_user_data where user_name = ? and age < ? or user_name = 'hello'", "xiaoming", 12);
		String statementsql = asyncPreparedStatement.getStatement();
		System.out.println(statementsql);
		Assert.assertEquals(statementsql, "select * from t_user_data where user_name = 'xiaoming' and age < 12 or user_name = 'hello'");
	}
}
