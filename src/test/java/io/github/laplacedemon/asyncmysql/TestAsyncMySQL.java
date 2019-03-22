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
	
	@Test
	public void testConnect() throws IOException, InterruptedException {
		final AsyncMySQL asyncMySQL = AsyncMySQL.create("127.0.0.1", 3306,"root","shijiaqi1066");
		asyncMySQL.connect((Connection con)->{
			System.out.println("TCP连接成功且MySQL握手成功");
		});
		
		asyncMySQL.start();
	}
	
	@Test
	public void execute() throws IOException, InterruptedException {
		/*
		AsyncMySQL asyncMySQL = AsyncMySQL.create("127.0.0.1", 3306, "root","shijiaqi1066");
		asyncMySQL.connect();
		
		asyncMySQL.startInNewThread();
		
		Thread.sleep(3000);
		asyncMySQL.execute(sql, resultset -> {
			System.out.println(resultset);
		});
		
		Thread.sleep(1000000000);
		*/
		final String sql = "select 1+1,1+2,2+3,3+5";
		final AsyncMySQL asyncMySQL = AsyncMySQL.create("127.0.0.1", 3306,"root","shijiaqi1066");
		asyncMySQL.connect((Connection con) -> {
			System.out.println("TCP连接成功且MySQL握手成功");
			AsyncPreparedStatement asyncPS = con.prepareStatement(sql);
			con.executeQuery(asyncPS, (ResultSet resultset)->{
				System.out.println("查询完成！");
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
			});
		});
		
		asyncMySQL.start();
	}
	
//	@Test
//	public void testConnectPool() throws IOException, InterruptedException {
//		AsyncMySQL asyncMySQL = AsyncMySQL.create("127.0.0.1", 3306,"root","shijiaqi1066");
//		ConnectionPool cp = asyncMySQL.connectPool();
//		
////		Thread.sleep(500);
//		asyncMySQL.start();
//	}
//	
//	@Test
//	public void testHandshake() throws IOException, InterruptedException {
//		final AsyncMySQL asyncMySQL = AsyncMySQL.create("127.0.0.1", 3306,"root","shijiaqi1066");
//		asyncMySQL.connect((Connection c) -> {
//			
//		});
//		Thread.sleep(500);
//		asyncMySQL.start();
//	}
//	
//	@Test
//	public void testExecute() throws IOException, InterruptedException {
//		AsyncMySQL asyncMySQL = AsyncMySQL.create("127.0.0.1", 3306,"root","shijiaqi1066");
//		
//		asyncMySQL.connect((Connection c) -> {
//			
//		});
//		
////		Thread.sleep(500);
//		asyncMySQL.start();
//	}
//	
//	@Test
//	public void testTxn()  {
//		AsyncMySQL asyncMySQL = AsyncMySQL.create("127.0.0.1", 3306,"root","shijiaqi1066");
//		
//		asyncMySQL.connect((Connection c) -> {
//			
//		});
//		
//		Connection con;
//		con.beginTxn().execute(()->{
//			
//		}).endTxn();
//		
//		
////		Thread.sleep(500);
//		asyncMySQL.start();
//	}
//	
//	
//	
	
}
