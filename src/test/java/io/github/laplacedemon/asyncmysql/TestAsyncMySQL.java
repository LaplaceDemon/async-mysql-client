package io.github.laplacedemon.asyncmysql;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.junit.Test;

import io.github.laplacedemon.asyncmysql.AsyncMySQL;
import io.github.laplacedemon.asyncmysql.Connection;
import io.github.laplacedemon.asyncmysql.resultset.AsyncPreparedStatement;

public class TestAsyncMySQL {
	public final static void printResultSet(final long count, final long id) {
		System.out.println("count : " + count + ", insert id : " + id);
	}
	
	public final static void printResultSet(final ResultSet resultset) {
		try {
			ResultSetMetaData metaData = resultset.getMetaData();
			int columnCount = metaData.getColumnCount();
			for(int i = 1; i<=columnCount; i++) {
				String columnName = metaData.getColumnName(i);
				System.out.print("[" + columnName + "]");
			}
			System.out.println();
			while(resultset.next()) {
				for(int i = 1; i<=columnCount; i++) {
					String object = resultset.getString(i);
					System.out.print("[" + object + "]");
				}
				System.out.println();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testAsyncConnect() throws IOException, InterruptedException {
		final AsyncMySQL asyncMySQL = AsyncMySQL.create();
		Config config = asyncMySQL.makeConfig("127.0.0.1", 3306,"root","shijiaqi1066");
		asyncMySQL.connect(config, (Connection con) -> {
			System.out.println("TCP连接成功且MySQL握手成功");
		});
		
		asyncMySQL.start();
	}
	
	@Test
	public void executeQuery() throws IOException, InterruptedException {
		final String sql = "select 1+1,1+2,2+3,3+5";
		final AsyncMySQL asyncMySQL = AsyncMySQL.create();
		Config config = asyncMySQL.makeConfig("127.0.0.1", 3306,"root","shijiaqi1066");
		asyncMySQL.connect(config, (Connection con) -> {
			System.out.println("TCP连接成功且MySQL握手成功");
			AsyncPreparedStatement asyncPS = con.prepareStatement(sql);
			con.executeQuery(asyncPS, (ResultSet resultset)->{
				System.out.println("查询完成！");
				printResultSet(resultset);
			});
		});
		
		asyncMySQL.start();
	}
	
	@Test
	public void testUseDatabase() throws IOException, InterruptedException {
		final String sql = "INSERT INTO t_student(name, age) VALUES ('xiaosha', 32)";
		final AsyncMySQL asyncMySQL = AsyncMySQL.create();
		
		Config config = asyncMySQL.makeConfig("127.0.0.1", 3306,"root","shijiaqi1066", "testdb");
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
		final AsyncMySQL asyncMySQL = AsyncMySQL.create();
		Config config = asyncMySQL.makeConfig("127.0.0.1", 3306,"root","shijiaqi1066");
		asyncMySQL.connect(config, (Connection con) -> {
			System.out.println("TCP连接成功且MySQL握手成功");
			con.executeUpdate(sql, (long count, long id)->{
				System.out.println("执行完成！");
				printResultSet(count, id);
			});
		});
		
		asyncMySQL.start();
	}
	
	@Test
	public void testExecuteUpdateWithAsyncPreparedStatement() throws IOException, InterruptedException {
		final String sql = "INSERT INTO `testdb`.`t_student` (`name`, `age`) VALUES (?, ?)";
		final AsyncMySQL asyncMySQL = AsyncMySQL.create();
		Config config = asyncMySQL.makeConfig("127.0.0.1", 3306,"root","shijiaqi1066");
		asyncMySQL.connect(config, (Connection con) -> {
			System.out.println("TCP连接成功且MySQL握手成功");
			AsyncPreparedStatement asyncPS = con.prepareStatement(sql, "xiaoming5", 18);
			con.executeUpdate(asyncPS, (long count, long id)->{
				System.out.println("执行完成！");
				printResultSet(count, id);
			});
		});
		
		asyncMySQL.start();
	}
	
	@Test
	public void testSyncCreateConnectPool() throws IOException, InterruptedException {
		final String sql = "select 1+1,1+2,2+3,3+5";
		final AsyncMySQL asyncMySQL = AsyncMySQL.create();
		Config config = asyncMySQL.makeConfig("127.0.0.1", 3306,"root","shijiaqi1066");
		long t0 = System.currentTimeMillis();
		final ConnectionPool cp = asyncMySQL.createPool(config, 10);
		long t1 = System.currentTimeMillis();
		System.out.println("线程池创建所花时间：" + (t1-t0)/1000 + "s");
		cp.get(con -> {
			System.out.println("空闲连接数：" + cp.getFreeConnectionCount());
			con.executeQuery(sql, r -> {
				printResultSet(r);
			});
		});
		System.out.println("空闲连接数：" + cp.getFreeConnectionCount());
		
		asyncMySQL.start();
	}
	
	@Test
	public void testAsyncCreateConnectPool() throws IOException, InterruptedException {
		final String sql = "select 1+1,1+2,2+3,3+5";
		final AsyncMySQL asyncMySQL = AsyncMySQL.create();
		Config config = asyncMySQL.makeConfig("127.0.0.1", 3306,"root","shijiaqi1066");
		final long t0 = System.currentTimeMillis();
		asyncMySQL.createPool(config, 10, (ConnectionPool cp)->{
			long t1 = System.currentTimeMillis();
			System.out.println("线程池创建所花时间：" + (t1-t0)/1000 + "s");
			cp.get(con -> {
				System.out.println("空闲连接数：" + cp.getFreeConnectionCount());
				con.executeQuery(sql, r -> {
					printResultSet(r);
				});
			});
			
			System.out.println("空闲连接数：" + cp.getFreeConnectionCount());
		});
		
		asyncMySQL.start();
	}
	
	@Test
	public void testTxn() throws IOException, InterruptedException {
		final AsyncMySQL asyncMySQL = AsyncMySQL.create();
		Config config = asyncMySQL.makeConfig("127.0.0.1", 3306,"root","shijiaqi1066");
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
