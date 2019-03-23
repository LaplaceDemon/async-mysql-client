package io.github.laplacedemon.asyncmysql.resultset;

import java.sql.SQLException;

import javax.sql.RowSetMetaData;

import io.github.laplacedemon.mysql.protocol.packet.response.resultset.FieldPacket;

public class AsyncResultSetMetaData implements RowSetMetaData {
	private MySQLResultPacket resultMySQLPacket;
	
	public AsyncResultSetMetaData(MySQLResultPacket resultMySQLPacket) {
		this.resultMySQLPacket = resultMySQLPacket;
	}
	
	@Override
	public int getColumnCount() throws SQLException {
		return (int)this.resultMySQLPacket.resultSetHeaderPacket().getFiledCount();
	}

	@Override
	public boolean isAutoIncrement(int column) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCaseSensitive(int column) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSearchable(int column) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCurrency(int column) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int isNullable(int column) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isSigned(int column) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getColumnDisplaySize(int column) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getColumnLabel(int column) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getColumnName(int column) throws SQLException {
		FieldPacket fieldPacket = this.resultMySQLPacket.fieldPacketList().get(column-1);
		return fieldPacket.getName();
	}

	@Override
	public String getSchemaName(int column) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getPrecision(int column) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getScale(int column) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getTableName(int column) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCatalogName(int column) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getColumnType(int column) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getColumnTypeName(int column) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isReadOnly(int column) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isWritable(int column) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDefinitelyWritable(int column) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getColumnClassName(int column) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setColumnCount(int columnCount) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAutoIncrement(int columnIndex, boolean property) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCaseSensitive(int columnIndex, boolean property) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSearchable(int columnIndex, boolean property) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCurrency(int columnIndex, boolean property) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setNullable(int columnIndex, int property) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSigned(int columnIndex, boolean property) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setColumnDisplaySize(int columnIndex, int size) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setColumnLabel(int columnIndex, String label) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setColumnName(int columnIndex, String columnName) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSchemaName(int columnIndex, String schemaName) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPrecision(int columnIndex, int precision) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setScale(int columnIndex, int scale) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTableName(int columnIndex, String tableName) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCatalogName(int columnIndex, String catalogName) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setColumnType(int columnIndex, int SQLType) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setColumnTypeName(int columnIndex, String typeName) throws SQLException {
		// TODO Auto-generated method stub
		
	}

}
