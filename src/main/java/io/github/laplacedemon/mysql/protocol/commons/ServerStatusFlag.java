package io.github.laplacedemon.mysql.protocol.commons;

public interface ServerStatusFlag {
    final static short SERVER_STATUS_IN_TRANS = 0x0001;
    final static short SERVER_STATUS_AUTOCOMMIT = 0x0002;
    final static short SERVER_MORE_RESULTS_EXISTS = 0x0008;
    
    final static short SERVER_STATUS_NO_GOOD_INDEX_USED = 0x0010;
    final static short SERVER_STATUS_NO_INDEX_USED = 0x0020;
    final static short SERVER_STATUS_CURSOR_EXISTS = 0x0040;
    final static short SERVER_STATUS_LAST_ROW_SENT = 0x0080;
    
    final static short SERVER_STATUS_DB_DROPPED = 0x0100;
    final static short SERVER_STATUS_NO_BACKSLASH_ESCAPES = 0x0200;
    final static short SERVER_STATUS_METADATA_CHANGED = 0x0400;
    final static short SERVER_QUERY_WAS_SLOW = 0x0800;
    
    final static short SERVER_PS_OUT_PARAMS = 0x1000; 
    final static short SERVER_STATUS_IN_TRANS_READONLY = 0x2000;
    final static short SERVER_SESSION_STATE_CHANGED = 0x4000;
}
