package sjq.test.jdbc;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestJDBC {
	public static void main(String[] args) throws SQLException {
		JDBCUtils jdbc = new JDBCUtils();
		
		jdbc.initConnection();
		
		List<Object> params = new ArrayList<>();
//		params.add("xiaoming");
		
		Map<String, Object> results = jdbc.findSimpleResult("select * from t_user_data", params);
		System.out.println(results);
		
		jdbc.releaseConn();
	}
}


