package io.github.laplacedemon.asyncmysql;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class TestDDL {
    private Config config;
    private AsyncMySQL asyncMySQL;
    
    static final String createTableSql = "CREATE TABLE `t_student` (`id` INT(8) NOT NULL AUTO_INCREMENT, `name` VARCHAR(255) NULL,`age` VARCHAR(255) NULL, PRIMARY KEY (`id`)) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8";
    static final String dropTableSql = "DROP TABLE `t_student`";
    
    @Before
    public void before() {
        asyncMySQL = AsyncMySQL.create();
        config = asyncMySQL.makeConfig("192.168.56.101", 3306,"root","123456","testdb");
    }
    
    @Test
    public void TestCreateTable() throws IOException, InterruptedException {
        asyncMySQL.connect(config, (Connection con) -> {
            System.out.println("TCP连接成功且MySQL握手成功");
            
            con.executeUpdate(createTableSql, (long count, long id) -> {
                PrintUtil.printResultSet(count, id);
            });
        });
        
        asyncMySQL.start();
    }
    
    @Test
    public void TestDropTable() throws IOException, InterruptedException {
        asyncMySQL.connect(config, (Connection con) -> {
            System.out.println("TCP连接成功且MySQL握手成功");
            
            con.executeUpdate(dropTableSql, (long count, long id) -> {
                PrintUtil.printResultSet(count, id);
            });
        });
        
        asyncMySQL.start();
    }
    
}
