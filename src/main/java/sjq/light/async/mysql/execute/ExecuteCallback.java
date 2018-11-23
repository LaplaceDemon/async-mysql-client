package sjq.light.async.mysql.execute;

import java.sql.ResultSet;

@FunctionalInterface
public interface ExecuteCallback {
	public void callback(ResultSet resultSet); 
}
