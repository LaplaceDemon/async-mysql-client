package io.github.laplacedemon.asyncmysql;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class TestConnect {
    private Config config;
    private AsyncMySQL asyncMySQL;
    
    static final String createTableSql = "CREATE TABLE `t_student` (`id` INT(8) NOT NULL AUTO_INCREMENT, `name` VARCHAR(255) NULL,`age` VARCHAR(255) NULL, PRIMARY KEY (`id`)) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8";
    static final String dropTableSql = "DROP TABLE `t_student`";
    
    @Before
    public void before() {
        asyncMySQL = AsyncMySQL.create();
        config = asyncMySQL.makeConfig("127.0.0.1", 3306,"root","123456");
    }
    
    @Test
    public void testAsyncConnect() {
        asyncMySQL.connect(config, (Connection con) -> {
            System.out.println("Successfully connected to MySQL.");
        });
        
        asyncMySQL.start();
    }
    
    @Test
    public void TestCloseConnection() throws IOException, InterruptedException {
        asyncMySQL.connect(config, (Connection con) -> {
            System.out.println("Successfully connected to MySQL");
            
            con.close(() -> {
                System.out.println("Connection is closed.");
            });
        });
        
        asyncMySQL.start();
    }
}
