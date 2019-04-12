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
		config = asyncMySQL.makeConfig("192.168.56.101", 3306,"root","123456","testdb");
	}
	
	@Test
	public void executeQuery() throws IOException, InterruptedException {
		final String sql = "select 1+1,1+2,2+3,3+5";
		
		asyncMySQL.connect(config, (Connection con) -> {
			System.out.println("TCP连接成功且MySQL握手成功");
			AsyncPreparedStatement asyncPS = con.prepareStatement(sql);
			con.executeQuery(asyncPS, (ResultSet resultset)->{
				System.out.println("查询完成！");
				PrintUtil.printResultSet(resultset);
			});
		});
		
		asyncMySQL.start();
	}
	
	@Test
	public void testUseDatabase() throws IOException, InterruptedException {
		final String sql = "INSERT INTO t_student(name, age) VALUES ('xiaosha', 32)";
		
		asyncMySQL.connect(config, (Connection con) -> {
			System.out.println("TCP连接成功且MySQL握手成功");
			con.executeUpdate(sql, () -> {
				System.out.println("执行完成！");
			});
		});
		
		asyncMySQL.start();
	}
	
	@Test
	public void executeUpdate() throws IOException, InterruptedException {
		final String sql = "INSERT INTO `testdb`.`t_student` (`name`, `age`) VALUES ('xiaoming04', '4')";
		
		asyncMySQL.connect(config, (Connection con) -> {
			System.out.println("TCP连接成功且MySQL握手成功");
			con.executeUpdate(sql, (long count, long id)->{
				System.out.println("执行完成！");
				PrintUtil.printResultSet(count, id);
			});
		});
		
		asyncMySQL.start();
	}
	
	@Test
	public void testExecuteUpdateWithAsyncPreparedStatement() throws IOException, InterruptedException {
		final String sql = "INSERT INTO `testdb`.`t_student` (`name`, `age`) VALUES (?, ?)";
		
		asyncMySQL.connect(config, (Connection con) -> {
			System.out.println("TCP连接成功且MySQL握手成功");
			AsyncPreparedStatement asyncPS = con.prepareStatement(sql, "xiaoming5", 18);
			con.executeUpdate(asyncPS, (long count, long id)->{
				System.out.println("执行完成！");
				PrintUtil.printResultSet(count, id);
			});
		});
		
		asyncMySQL.start();
	}
	
	@Test
	public void testSyncCreateConnectPool() throws IOException, InterruptedException {
		final String sql = "select 1+1,1+2,2+3,3+5";
		
		long t0 = System.currentTimeMillis();
		final ConnectionPool cp = asyncMySQL.createPool(config, 10);
		long t1 = System.currentTimeMillis();
		System.out.println("线程池创建所花时间：" + (t1-t0)/1000 + "s");
		cp.get(con -> {
			System.out.println("空闲连接数：" + cp.getFreeConnectionCount());
			con.executeQuery(sql, r -> {
			    PrintUtil.printResultSet(r);
			});
		});
		System.out.println("空闲连接数：" + cp.getFreeConnectionCount());
		
		asyncMySQL.start();
	}
	
	@Test
	public void testTxn() throws IOException, InterruptedException {
		asyncMySQL.connect(config, (Connection con) -> {
			System.out.println("TCP连接成功且MySQL握手成功");
			con.beginTxn(()-> {
				con.executeUpdate("INSERT INTO `testdb`.`t_student` (`name`, `age`) VALUES ('xiaohong01', 12)", (long count0, long id0)->{
					System.out.println("执行事物1完成！");
					
					con.executeUpdate("INSERT INTO `testdb`.`t_student` (`name`, `age`) VALUES ('xiaohong02', 13)",(long count1, long id1)-> {
						System.out.println("执行事物2完成！");
						con.endTxn(()->{
							System.out.println("事物执行完毕");
						});
					});
				});
			});
		});
		
		asyncMySQL.start();
	}
	
}
