package io.github.laplacedemon.asyncmysql.resultset;

import java.util.ArrayList;

public class AsyncPreparedStatement {
	private String sql;
	private ArrayList<Integer> paramIndexList;
	private ArrayList<String> paramValueList;
	private int stringLength;
	private String statement;
	
	public AsyncPreparedStatement(String sql) {
		this.sql = sql;
		
		int sqlLength = this.sql.length();
		this.stringLength = this.sql.length();
		
		this.paramIndexList = new ArrayList<>();
		for(int i = 0;i<sqlLength;i++) {
			char c = this.sql.charAt(i);
			if(c == '?') {
				this.paramIndexList.add(i);
				this.stringLength--;
			}
		}
	}
	
	public AsyncPreparedStatement(String sql, Object... params) {
		this(sql);
		this.paramValueList = new ArrayList<>(this.paramIndexList.size());
		for(int i = 0; i < params.length; i++) {
			Object param = params[i];
			String strParam = null;
			if (param instanceof String) {
				StringBuilder sb = new StringBuilder(2 + params.length);
				sb.append("'");
				sb.append(param);
				sb.append("'");
				strParam = sb.toString();
			} else if (param instanceof Number) {
				strParam = param.toString();
			}
			
			this.stringLength += strParam.length();
			paramValueList.add(strParam);
		}
	}
	
	public String getStatement() {
		if(this.statement == null) {
			StringBuilder sb = new StringBuilder(this.stringLength);
			if(this.paramIndexList.size() == 0) {
				this.statement = this.sql;
			} else {
				int lastIndex = 0;
				for(int i = 0; i < this.paramIndexList.size(); i++) {
					int index = this.paramIndexList.get(i);
					sb.append(this.sql.substring(lastIndex, index));
					sb.append(this.paramValueList.get(i));
					lastIndex = index + 1;
				}
				
				if (lastIndex < this.sql.length() - 1) {
					sb.append(this.sql.substring(lastIndex, this.sql.length()));
				}
				
				this.statement = sb.toString();
			}
		}
		
		return statement;
	}
	
}
