package io.github.laplacedemon.mysql.protocol.commons;

public interface ColumnFeaturesFlag {
    /**
     * 空
     */
    short NULL = (short)0x0000;
    short NOT_NULL_FLAG = (short)0x0001;
    short PRI_KEY_FLAG = (short)0x0002;
    short UNIQUE_KEY_FLAG = (short)0x0004;
    short MULTIPLE_KEY_FLAG = (short)0x0008;
    short BLOB_FLAG = (short)0x0010;
    short UNSIGNED_FLAG = (short)0x0020;
    short ZEROFILL_FLAG = (short)0x0040;
    short BINARY_FLAG = (short)0x0080;
    short ENUM_FLAG = (short)0x0100;
    short AUTO_INCREMENT_FLAG = (short)0x0200;
    short TIMESTAMP_FLAG = (short)0x0400;
    short SET_FLAG = (short)0x0800;
}
