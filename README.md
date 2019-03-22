# async-mysql-client
Asynchronous mysql client.

Basic way of use, asynchronous connection :

```java
final AsyncMySQL asyncMySQL = AsyncMySQL.create("127.0.0.1", 3306,"root","shijiaqi1066");
asyncMySQL.connect((Connection con)->{
  System.out.println("TCP连接成功且MySQL握手成功");
});

asyncMySQL.start();
```

Asynchronous execution of sql code :

```java
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
```

The library is still not perfect, please give me an issue if there is a demand.

