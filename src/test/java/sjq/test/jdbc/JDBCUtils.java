package sjq.test.jdbc;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class JDBCUtils {

	// 定义数据库的用户名
	private final String USERNAME = "root";
	// 定义数据库的密码
	private final String PASSWORD = "shijiaqi1066";
	// 定义数据库的驱动信息
	private final String DRIVER = "com.mysql.cj.jdbc.Driver";
	// 定义访问数据库的地址
	private final String URL = "jdbc:mysql://127.0.0.1:3306?useSSL=false&allowPublicKeyRetrieval=true";

	// 定义访问数据库的连接
	private Connection connection;
	// 定义sql语句的执行对象
	private PreparedStatement pstmt;
	// 定义查询返回的结果集合
	private ResultSet resultSet;

	public JDBCUtils() {
		// TODO Auto-generated constructor stub
		try {
			Class.forName(DRIVER);
			System.out.println("注册驱动成功！！");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("注册驱动失败！！");
		}
	}

	// 定义获得数据库的连接
	public void initConnection() throws SQLException {
		connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
	}

	/**
	 * 完成对数据库标的增加删除和修改的操作
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public boolean updateByPreparedStatement(String sql, List<Object> params) throws SQLException {
		boolean flag = false;
		int result = -1;// 表示当用户执行增加删除和修改的操作影响的行数
		int index = 1; // 表示 占位符 ，从1开始
		pstmt = connection.prepareStatement(sql);
		if (params != null && !params.isEmpty()) {
			for (int i = 0; i < params.size(); i++) {
				pstmt.setObject(index++, params.get(i)); // 填充占位符
			}
		}

		result = pstmt.executeUpdate();
		flag = result > 0 ? true : false;
		return flag;

	}

	/**
	 * 查询返回单条记录
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public Map<String, Object> findSimpleResult(String sql, List<Object> params) throws SQLException {
		Map<String, Object> map = new HashMap<String, Object>();
		pstmt = connection.prepareStatement(sql);
		int index = 1;
		if (params != null && !params.isEmpty()) {
			for (int i = 0; i < params.size(); i++) {
				pstmt.setObject(index++, params.get(i));
			}
		}
		resultSet = pstmt.executeQuery(); // 返回查询结果

		ResultSetMetaData metaData = pstmt.getMetaData(); // 获取 结果中，一行所有列的结果
		int cols_len = metaData.getColumnCount(); // 获得列的总数

		while (resultSet.next()) {
			for (int i = 0; i < cols_len; i++) {
				String col_name = metaData.getColumnName(i + 1); // 获得第i列的字段名称
				Object col_value = resultSet.getObject(col_name);// 返回 第i列的内容值
				if (col_value == null) {
					col_value = "";
				}
				map.put(col_name, col_value);
			}

		}

		return map;
	}
	
	public ResultSet queryResult(String sql, List<Object> params) throws SQLException {
		pstmt = connection.prepareStatement(sql);
		int index = 1;
		if (params != null && !params.isEmpty()) {
			for (int i = 0; i < params.size(); i++) {
				pstmt.setObject(index++, params.get(i));
			}
		}
		ResultSet resultSet = pstmt.executeQuery();
		return resultSet;
	}

	/**
	 * 查询返回多条记录
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String, Object>> findMoreResult(String sql, List<Object> params) throws SQLException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		pstmt = connection.prepareStatement(sql);
		int index = 1; // 表示占位符
		if (params != null && !params.isEmpty()) {
			for (int i = 0; i < params.size(); i++) {
				pstmt.setObject(index++, params.get(i));
			}
		}
		resultSet = pstmt.executeQuery(); // 返回查询结果集合
		ResultSetMetaData metaData = resultSet.getMetaData(); // 获得列的结果

		while (resultSet.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			int cols_len = metaData.getColumnCount(); // 获取总的列数
			for (int i = 0; i < cols_len; i++) {
				String col_name = metaData.getColumnName(i + 1); // 获取第 i列的字段名称
																	// ,列计算从1开始
				Object col_value = resultSet.getObject(col_name); // 获取第i列的内容值
				if (col_value == null) {
					col_value = "";
				}

				map.put(col_name, col_value);
			}
			list.add(map);
		}

		return list;

	}

	/**
	 * 查询返回单个JavaBean(使用java反射机制)
	 * 
	 * @param sql
	 * @param params
	 * @param cls
	 * @return
	 * @throws Exception
	 */
	public <T> T findSimpleRefResult(String sql, List<Object> params, Class<T> cls) throws Exception {
		T resultObject = null;
		int index = 1; // 占位符
		pstmt = connection.prepareStatement(sql);
		if (params != null && !params.isEmpty()) {
			for (int i = 0; i < params.size(); i++) {
				pstmt.setObject(index++, params.get(i)); // 填充占位符
			}
		}
		resultSet = pstmt.executeQuery(); // 获取查询结果

		ResultSetMetaData metaData = resultSet.getMetaData(); // 获取列的信息
		int cols_len = metaData.getColumnCount(); // 获取总的列数
		while (resultSet.next()) {
			// 通过反射机制创建实例
			resultObject = cls.newInstance(); // java反射机制
			for (int i = 0; i < cols_len; i++) {
				String col_name = metaData.getColumnName(i + 1); // 获取第i列的名称
				Object col_value = resultSet.getObject(col_name); // 获取第i列的值
				if (col_value == null) {
					col_value = "";
				}
				Field field = cls.getDeclaredField(col_name);
				field.setAccessible(true);// 打开 JavaBean的访问 private权限
				field.set(resultObject, col_value);
			}

		}

		return resultObject;
	}

	/**
	 * 查询返回多个JavaBean(通过java反射机制)
	 * 
	 * @param sql
	 * @param params
	 * @param cls
	 * @return
	 * @throws Exception
	 */
	public <T> List<T> findMoreRefResult(String sql, List<Object> params, Class<T> cls) throws Exception {
		List<T> list = new ArrayList<T>();
		int index = 1; // 占位符
		pstmt = connection.prepareStatement(sql);
		if (params != null && !params.isEmpty()) {
			for (int i = 0; i < params.size(); i++) {
				pstmt.setObject(index++, params.get(i));
			}
		}
		resultSet = pstmt.executeQuery(); // 返回查询结果集合

		ResultSetMetaData metaData = resultSet.getMetaData(); // 返回列的信息
		int cols_len = metaData.getColumnCount(); // 结果集中总的列数
		while (resultSet.next()) {
			// 通过反射机制创建一个java实例
			T resultObject = cls.newInstance();
			for (int i = 0; i < cols_len; i++) {
				String col_name = metaData.getColumnName(i + 1); // 获得第i列的名称
				Object col_value = resultSet.getObject(col_name); // 获得第i列的内容
				if (col_value == null) {
					col_value = "";
				}
				Field field = cls.getDeclaredField(col_name);
				field.setAccessible(true); // 打开JavaBean的访问private权限
				field.set(resultObject, col_value);
			}
			list.add(resultObject);

		}

		return list;
	}

	/**
	 * 关闭数据库访问
	 * 
	 * @throws SQLException
	 */
	public void releaseConn() throws SQLException {
		if (resultSet != null) {
			resultSet.close();
		}
		if (pstmt != null) {
			pstmt.close();
		}
		if (connection != null) {
			connection.close();
		}
	}

	public void startTxn() throws SQLException {
		this.connection.setAutoCommit(false);
	}
	
	public void commit() throws SQLException {
		this.connection.commit();
	}

	public void endTxn() throws SQLException {
		this.connection.setAutoCommit(true);
	}

}