package io.github.laplacedemon.asyncmysql;

public class TestFutureMySQL {
	/*
	@Test
	public void testAsyncConnect() throws IOException, InterruptedException {
		final AsyncMySQL asyncMySQL = AsyncMySQL.create();
		Config config = asyncMySQL.makeConfig("127.0.0.1", 3306,"root","shijiaqi1066");
		asyncMySQL.connect(config)
		.then((recever, Connection con)->{
			con.executeQuery("select 1+1");
		}).then((ResultSet r)-> {
			
		});
		
		asyncMySQL.start();
	}
	
	@Test
	public void testTxn() throws IOException, InterruptedException {
		final AsyncMySQL asyncMySQL = AsyncMySQL.create();
		Config config = asyncMySQL.makeConfig("127.0.0.1", 3306,"root","shijiaqi1066");
		asyncMySQL.connect(config)
		.then((Connection con) -> {
			return con.startTxn();
		}).then((Connection con) -> {
			return con.executeUpdate("");
		}).then((Connection con) -> {
			return con.endTxn();
		}).then(() -> {
			System.out.println("结束");
		});
		
		asyncMySQL.start();
	}
	
	@Test
	public void testTxn() throws IOException, InterruptedException {
		final AsyncMySQL asyncMySQL = AsyncMySQL.create();
		Config config = asyncMySQL.makeConfig("127.0.0.1", 3306,"root","shijiaqi1066");
		asyncMySQL.connect(config)
			.startTxn()
			.executeUpdate("", (long count, long id)->{
				
			})
			.executeUpdate("", ()->{})
			.endTxn();
		
		asyncMySQL.start();
	}
	*/
}
