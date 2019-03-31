# async-mysql-client

Asynchronous mysql client.

## Base

Create a asyncMySQL :

```java
AsyncMySQL asyncMySQL = AsyncMySQL.create();
```

Create a config : 

```java
Config config = asyncMySQL.makeConfig("192.168.56.101", 3306, "root", "123456");
```

## Connection

Get useful MySQL connections asynchronously :

```java
asyncMySQL.connect(config, (Connection con) -> {
    System.out.println("Connection completed!");
});

asyncMySQL.start();
```

## Asynchronous execution of sql statement

Query  sql statement :

```java
final String sql = "select 1+1,1+2,2+3,3+5";

asyncMySQL.connect(config, (Connection con) -> {
    System.out.println("Connection completed!");
    AsyncPreparedStatement asyncPS = con.prepareStatement(sql);
    con.executeQuery(asyncPS, (ResultSet resultset)->{
        System.out.println("查询完成！");
        printResultSet(resultset);
    });
});

asyncMySQL.start();
```

Written sql statement :

```java
final String sql = "INSERT INTO `testdb`.`t_student` (`name`, `age`) VALUES ('xiaoming04', '4')";

asyncMySQL.connect(config, (Connection con) -> {
    System.out.println("Connection completed!");
    con.executeUpdate(sql, (long count, long id)->{
        printResultSet(count, id);
    });
});

asyncMySQL.start();
```

## Connection Pool

```java
final String sql = "select 1+1,1+2,2+3,3+5";

final ConnectionPool cp = asyncMySQL.createPool(config, 10);

cp.get(con -> {
    System.out.println("Number of idle connections:" 
                        + cp.getFreeConnectionCount());
    con.executeQuery(sql, r -> {
        printResultSet(r);
    });
});
System.out.println("Number of idle connections:" + cp.getFreeConnectionCount());

asyncMySQL.start();
```


```java
asyncMySQL.createPool(config, 10, (ConnectionPool cp)->{
    cp.get(con -> {
        System.out.println("Number of idle connections:"
                            + cp.getFreeConnectionCount());
        con.executeQuery(sql, r -> {
            printResultSet(r);
        });
    });
    
    System.out.println("Number of idle connections:" + cp.getFreeConnectionCount());
});

asyncMySQL.start();
```



## Use transaction

```java
asyncMySQL.connect(config, (Connection con) -> {
    con.beginTxn(() -> {
        con.executeUpdate("INSERT INTO `testdb`.`t_student` (`name`, `age`) VALUES ('xiaohong01', 12)", (long count0, long id0)->{
            System.out.println("Completion of transaction 1...");

            con.executeUpdate("INSERT INTO `testdb`.`t_student` (`name`, `age`) VALUES ('xiaohong02', 13)",(long count1, long id1) -> {
                System.out.println("Completion of transaction 2...");
                
                con.endTxn(()->{
                    System.out.println("End of transaction");
                });
            });
        });
    });
});

asyncMySQL.start();
```





