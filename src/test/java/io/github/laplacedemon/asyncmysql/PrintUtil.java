package io.github.laplacedemon.asyncmysql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class PrintUtil {
    public final static void printResultSet(final long count, final long id) {
        System.out.println("count : " + count + ", insert id : " + id);
    }
    
    public final static void printResultSet(final ResultSet resultset) {
        try {
            ResultSetMetaData metaData = resultset.getMetaData();
            int columnCount = metaData.getColumnCount();
            for(int i = 1; i<=columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                System.out.print("[" + columnName + "]");
            }
            System.out.println();
            while(resultset.next()) {
                for(int i = 1; i<=columnCount; i++) {
                    String object = resultset.getString(i);
                    System.out.print("[" + object + "]");
                }
                System.out.println();
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
