package sjq.test.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

public class TestJDBC {
	/**
	<pre>
	CREATE SCHEMA `testdb` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci ;
	
	CREATE TABLE `t_student` (
		`id` int(11) NOT NULL AUTO_INCREMENT,
		`name` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
		`age` int(11) DEFAULT NULL,
		PRIMARY KEY (`id`)
	) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
	</pre>
	 */
	
	@Test
	public void testInsert() throws SQLException {
		String sql = "INSERT INTO `testdb`.`t_student` (`name`, `age`) VALUES ('xiaoming', '18')";
		
		JDBCUtils jdbc = new JDBCUtils();
		
		jdbc.initConnection();
		
		jdbc.startTxn();
		
		jdbc.updateByPreparedStatement(sql, null);
		jdbc.updateByPreparedStatement(sql, null);
		
		jdbc.commit();
		
		jdbc.endTxn();
		
		jdbc.releaseConn();
	}
	
	@Test
	@Ignore
	public void main1() throws SQLException {
		JDBCUtils jdbc = new JDBCUtils();
		
		jdbc.initConnection();
		
		List<Object> params = new ArrayList<>();
//		params.add("xiaoming");
		
		Map<String, Object> results = jdbc.findSimpleResult("select * from t_user_data", params);
		System.out.println(results);
		
		jdbc.releaseConn();
	}
	
	@Test
	@Ignore
	public void main2() throws SQLException {
		JDBCUtils jdbc = new JDBCUtils();
		jdbc.initConnection();
		
		List<Object> params = new ArrayList<>(0);
		ResultSet results = jdbc.queryResult("select * from t_user_data", params);
		
		System.out.println(results);
		
		jdbc.releaseConn();
	}
	
	@Test
	public void main3() throws SQLException {
		JDBCUtils jdbc = new JDBCUtils();
		
		jdbc.initConnection();
		
		List<Object> params = new ArrayList<>();
		params.add("1-1-1-1-1-1");
		
		ResultSet results = jdbc.queryResult("select * from t_user_data where user_name = ?", params);
		System.out.println(results);
		
		jdbc.releaseConn();
	}
}


