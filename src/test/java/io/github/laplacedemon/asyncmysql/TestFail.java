package io.github.laplacedemon.asyncmysql;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class TestFail {
    private Config config;
    private AsyncMySQL asyncMySQL;
    
    @Before
    public void before() {
        asyncMySQL = AsyncMySQL.create();
        config = asyncMySQL.makeConfig("127.0.0.1", 3306,"root","123456");
    }
    
    @Test
    public void testAsyncCreateConnectPool() throws IOException, InterruptedException {
        final String sql = "error sql";
        
        asyncMySQL.connect(config, (Connection con) -> {
            con.executeQuery(sql, r -> {
                PrintUtil.printResultSet(r);
            }, (Throwable t) -> {
                t.printStackTrace();
            });
        });
        
        asyncMySQL.start();
    }
}
