package sjq.light.async.mysql.execute;

public class ExecuteTask {
	private String sql;
	private ExecuteCallback executeCallback;
	
	
	public ExecuteTask(String sql, ExecuteCallback executeCallback) {
		super();
		this.sql = sql;
		this.executeCallback = executeCallback;
	}
	
	public String getSql() {
		return sql;
	}
	
	public ExecuteCallback getExecuteCallback() {
		return executeCallback;
	}
	
}
