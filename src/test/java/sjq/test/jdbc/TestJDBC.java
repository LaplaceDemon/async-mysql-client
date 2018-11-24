package sjq.test.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

public class TestJDBC {
	
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


