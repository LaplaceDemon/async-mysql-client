package sjq.test.async.mysql;

import java.io.IOException;

import org.junit.Test;

import sjq.light.async.mysql.AsyncMySQL;

public class TestAsyncMySQL {
	
	@Test
	public void connect() throws IOException, InterruptedException {
		AsyncMySQL asyncMySQL = AsyncMySQL.create("192.168.56.102", 3306,"root","123456");
		asyncMySQL.connect();
		Thread.sleep(500);
		asyncMySQL.start();
	}
	
	@Test
	public void execute() throws IOException, InterruptedException {
		AsyncMySQL asyncMySQL = AsyncMySQL.create("192.168.56.102", 3306, "root","123456");
		asyncMySQL.connect();
		String sql = "select * from testdb.t_user_data";
		asyncMySQL.startInNewThread();
		
		Thread.sleep(3000);
		asyncMySQL.execute(sql, resultset -> {
			System.out.println(resultset);
		});
		
		Thread.sleep(1000000000);
	}
}
