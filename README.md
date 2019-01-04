# async-mysql-client
Asynchronous mysql client.

Basic way of use: 

```java
AsyncMySQL asyncMySQL = AsyncMySQL.create("192.168.56.102", 3306, "root","123456");
asyncMySQL.connect();

String sql = "select * from testdb.t_user_data";
asyncMySQL.startInNewThread();
		
Thread.sleep(3000);
asyncMySQL.execute(sql, resultset -> {
    System.out.println(resultset);
});
```

The library is still not perfect, please give me an issue if there is a demand.
