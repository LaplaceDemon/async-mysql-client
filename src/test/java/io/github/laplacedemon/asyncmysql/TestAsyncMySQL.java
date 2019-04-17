package io.github.laplacedemon.asyncmysql;

import java.io.IOException;
import java.sql.ResultSet;

import org.junit.Before;
import org.junit.Test;

import io.github.laplacedemon.asyncmysql.AsyncMySQL;
import io.github.laplacedemon.asyncmysql.Connection;
import io.github.laplacedemon.asyncmysql.resultset.AsyncPreparedStatement;

public class TestAsyncMySQL {
	private Config config;
	private AsyncMySQL asyncMySQL;
	
	@Before
	public void before() {
		asyncMySQL = AsyncMySQL.create();
		config = asyncMySQL.makeConfig("127.0.0.1", 3306,"root","123456","testdb");
	}
	
	@Test
	public void executeQuery() throws IOException, InterruptedException {
		final String sql = "select 1+1,1+2,2+3,3+5";
		
		asyncMySQL.connect(config, (Connection con) -> {
			System.out.println("Successfully connected to MySQL。");
			AsyncPreparedStatement asyncPS = con.prepareStatement(sql);
			con.executeQuery(asyncPS, (ResultSet resultset)->{
				System.out.println("Query completed。");
				PrintUtil.printResultSet(resultset);
			});
		});
		
		asyncMySQL.start();
	}
	
	@Test
	public void testUseDatabase() throws IOException, InterruptedException {
		final String sql = "INSERT INTO t_student(name, age) VALUES ('xiaosha', 32)";
		
		asyncMySQL.connect(config, (Connection con) -> {
			System.out.println("Successfully connected to MySQL。");
			con.executeUpdate(sql, () -> {
				System.out.println("Execution completed.");
			});
		});
		
		asyncMySQL.start();
	}
	
	@Test
	public void executeUpdate() throws IOException, InterruptedException {
		final String sql = "INSERT INTO `testdb`.`t_student` (`name`, `age`) VALUES ('xiaoming04', '4')";
		
		asyncMySQL.connect(config, (Connection con) -> {
			System.out.println("Successfully connected to MySQL。");
			con.executeUpdate(sql, (long count, long id)->{
				System.out.println("Execution completed.");
				PrintUtil.printResultSet(count, id);
			});
		});
		
		asyncMySQL.start();
	}
	
	@Test
	public void testExecuteUpdateWithAsyncPreparedStatement() throws IOException, InterruptedException {
		final String sql = "INSERT INTO `testdb`.`t_student` (`name`, `age`) VALUES (?, ?)";
		
		asyncMySQL.connect(config, (Connection con) -> {
			System.out.println("Successfully connected to MySQL。");
			AsyncPreparedStatement asyncPS = con.prepareStatement(sql, "xiaoming5", 18);
			con.executeUpdate(asyncPS, (long count, long id)->{
				System.out.println("Execution completed.");
				PrintUtil.printResultSet(count, id);
			});
		});
		
		asyncMySQL.start();
	}
	
	
	
	@Test
	public void testTxn() throws IOException, InterruptedException {
		asyncMySQL.connect(config, (Connection con) -> {
			System.out.println("Successfully connected to MySQL。");
			con.beginTxn(()-> {
				con.executeUpdate("INSERT INTO `testdb`.`t_student` (`name`, `age`) VALUES ('xiaohong01', 12)", (long count0, long id0)->{
					System.out.println("The transaction 1 execution completed.");
					
					con.executeUpdate("INSERT INTO `testdb`.`t_student` (`name`, `age`) VALUES ('xiaohong02', 13)",(long count1, long id1)-> {
						System.out.println("The transaction 2 execution completed.");
						
						con.endTxn(()->{
							System.out.println("The transaction is completed.");
						});
					});
				});
			});
		});
		
		asyncMySQL.start();
	}
	
}
