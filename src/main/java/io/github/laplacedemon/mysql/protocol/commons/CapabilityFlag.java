package io.github.laplacedemon.mysql.protocol.commons;

public interface CapabilityFlag {
    
    final static short CLIENT_LONG_PASSWORD = 0x0001;  // 0x00000001
    final static short CLIENT_FOUND_ROWS = 0x0002; // 0x00000002
    final static short CLIENT_LONG_FLAG = 0x0004;  // 0x00000004
    final static short CLIENT_CONNECT_WITH_DB = 0x0008; // 0x00000008
    final static short CLIENT_NO_SCHEMA = 0x0010;  // 0x00000010
    final static short CLIENT_COMPRESS = 0x0020;  // 0x00000020
    final static short CLIENT_ODBC = 0x0040;    // 0x00000040
    final static short CLIENT_LOCAL_FILES = 0x0080;  // 0x00000080
    final static short CLIENT_IGNORE_SPACE = 0x0100;  // 0x00000100
    final static short CLIENT_PROTOCOL_41 = 0x0200;  // 0x00000200
    final static short CLIENT_INTERACTIVE = 0x0400;  // 0x00000400
    final static short CLIENT_SSL = 0x0800;  // 0x00000800
    final static short CLIENT_IGNORE_SIGPIPE = 0x1000;  // 0x00001000
    final static short CLIENT_TRANSACTIONS = 0x2000;  // 0x00002000
    final static short CLIENT_RESERVED = 0x4000;  // 0x00004000
    final static short CLIENT_SECURE_CONNECTION = (short)0x8000;  // 0x00008000
    
    public interface Upper {
        final static short CLIENT_MULTI_STATEMENTS = 0x0001;   // 0x00010000;
        final static short CLIENT_MULTI_RESULTS = 0x0002;  // 0x00020000;
        final static short CLIENT_PS_MULTI_RESULTS = 0x0004;  // 0x00040000;
        final static short CLIENT_PLUGIN_AUTH = 0x0008;  // 0x00080000;
        final static short CLIENT_CONNECT_ATTRS = 0x0010;  // 0x00100000;
        final static short CLIENT_PLUGIN_AUTH_LENENC_CLIENT_DATA = 0x0020; // 0x00200000;
        final static short CLIENT_CAN_HANDLE_EXPIRED_PASSWORDS = 0x0040;  // 0x00400000;
        final static short CLIENT_SESSION_TRACK = 0x0080; //  0x00800000;
        final static short CLIENT_DEPRECATE_EOF = 0x0100;  // 0x01000000;
        
        // 以下两个字段由最新代码中体现。官方的源码说明手册中没有提及。
        final static short CLIENT_SSL_VERIFY_SERVER_CERT = 0x4000;
        final static short CLIENT_REMEMBER_OPTIONS = (short)0x8000;
    }
    
}
