package io.github.laplacedemon.mysql.protocol.commons;

public interface ColumnTypeFlag {
    byte MYSQL_TYPE_DECIMAL= (byte)0x00;     // Implemented by ProtocolBinary::MYSQL_TYPE_DECIMAL
    byte MYSQL_TYPE_TINY = (byte)0x01;       // Implemented by ProtocolBinary::MYSQL_TYPE_TINY
    byte MYSQL_TYPE_SHORT = (byte)0x02;      // Implemented by ProtocolBinary::MYSQL_TYPE_SHORT
    byte MYSQL_TYPE_LONG = (byte)0x03;       // Implemented by ProtocolBinary::MYSQL_TYPE_LONG
    byte MYSQL_TYPE_FLOAT= (byte)0x04;       // Implemented by ProtocolBinary::MYSQL_TYPE_FLOAT
    byte MYSQL_TYPE_DOUBLE = (byte)0x05;     // Implemented by ProtocolBinary::MYSQL_TYPE_DOUBLE
    byte MYSQL_TYPE_NULL = (byte) 0x06;      // Implemented by ProtocolBinary::MYSQL_TYPE_NULL
    byte MYSQL_TYPE_TIMESTAMP = (byte)0x07;  // Implemented by ProtocolBinary::MYSQL_TYPE_TIMESTAMP
    byte MYSQL_TYPE_LONGLONG = (byte)0x08 ;  // Implemented by ProtocolBinary::MYSQL_TYPE_LONGLONG
    byte MYSQL_TYPE_INT24 = (byte)0x09;      // Implemented by ProtocolBinary::MYSQL_TYPE_INT24
    byte MYSQL_TYPE_DATE = (byte)0x0a;       // Implemented by ProtocolBinary::MYSQL_TYPE_DATE
    byte MYSQL_TYPE_TIME = (byte)0x0b ;      // Implemented by ProtocolBinary::MYSQL_TYPE_TIME
    byte MYSQL_TYPE_DATETIME = (byte)0x0c;   // Implemented by ProtocolBinary::MYSQL_TYPE_DATETIME
    byte MYSQL_TYPE_YEAR = (byte)0x0d;       // Implemented by ProtocolBinary::MYSQL_TYPE_YEAR
    byte MYSQL_TYPE_NEWDATE_A  = (byte)0x0e; // see byte MYSQL_TYPE_DATE
    byte MYSQL_TYPE_VARCHAR  = (byte)0x0f;   // Implemented by ProtocolBinary::MYSQL_TYPE_VARCHAR
    byte MYSQL_TYPE_BIT  = (byte)0x10;       // Implemented by ProtocolBinary::MYSQL_TYPE_BIT
    byte MYSQL_TYPE_TIMESTAMP2_A= (byte)0x11; // see byte MYSQL_TYPE_TIMESTAMP
    byte MYSQL_TYPE_DATETIME2_A = (byte)0x12; // see byte MYSQL_TYPE_DATETIME
    byte MYSQL_TYPE_TIME2_A = (byte)0x13;     // see byte MYSQL_TYPE_TIME
    byte MYSQL_TYPE_NEWDECIMAL= (byte)0xf6;   // Implemented by ProtocolBinary::MYSQL_TYPE_NEWDECIMAL
    byte MYSQL_TYPE_ENUM  = (byte)0xf7;       // Implemented by ProtocolBinary::MYSQL_TYPE_ENUM
    byte MYSQL_TYPE_SET  = (byte)0xf8;        // Implemented by ProtocolBinary::MYSQL_TYPE_SET
    byte MYSQL_TYPE_TINY_BLOB = (byte)0xf9;   // Implemented by ProtocolBinary::MYSQL_TYPE_TINY_BLOB
    byte MYSQL_TYPE_MEDIUM_BLOB  = (byte)0xfa; // Implemented by ProtocolBinary::MYSQL_TYPE_MEDIUM_BLOB
    byte MYSQL_TYPE_LONG_BLOB = (byte)0xfb;    // Implemented by ProtocolBinary::MYSQL_TYPE_LONG_BLOB
    byte MYSQL_TYPE_BLOB  = (byte)0xfc;        // Implemented by ProtocolBinary::MYSQL_TYPE_BLOB
    byte MYSQL_TYPE_VAR_STRING = (byte)0xfd;   // Implemented by ProtocolBinary::MYSQL_TYPE_VAR_STRING
    byte MYSQL_TYPE_STRING = (byte)0xfe;       // Implemented by ProtocolBinary::MYSQL_TYPE_STRING
    byte MYSQL_TYPE_GEOMETRY = (byte)0xff; 
}

