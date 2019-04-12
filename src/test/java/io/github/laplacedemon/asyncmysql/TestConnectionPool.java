package io.github.laplacedemon.asyncmysql;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class TestConnectionPool {
    private Config config;
    private AsyncMySQL asyncMySQL;
    
    @Before
    public void before() {
        asyncMySQL = AsyncMySQL.create();
        config = asyncMySQL.makeConfig("192.168.56.101", 3306,"root","123456","testdb");
    }
    
    @Test
    public void testAsyncCreateConnectPool() throws IOException, InterruptedException {
        final String sql = "select 1+1,1+2,2+3,3+5";
        
        final long t0 = System.currentTimeMillis();
        asyncMySQL.createPool(config, 10, (ConnectionPool cp)->{
            long t1 = System.currentTimeMillis();
            System.out.println("线程池创建所花时间：" + (t1-t0)/1000 + "s");
            cp.get(con -> {
                System.out.println("空闲连接数：" + cp.getFreeConnectionCount());
                con.executeQuery(sql, r -> {
                    PrintUtil.printResultSet(r);
                });
            });
            
            System.out.println("空闲连接数：" + cp.getFreeConnectionCount());
        });
        
        asyncMySQL.start();
    }
}
