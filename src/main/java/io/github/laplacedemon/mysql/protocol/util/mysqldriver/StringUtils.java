package io.github.laplacedemon.mysql.protocol.util.mysqldriver;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;


public class StringUtils {
	public static byte[] getBytes(String value) {
        return value.getBytes();
    }
	
	public static byte[] getBytes(String s, String encoding) throws UnsupportedEncodingException {
        if (encoding == null) {
            return getBytes(s);
        }
        
        return s.getBytes(encoding);
    }
	
	public static byte[] getBytesNullTerminated(String value, String encoding) {
        Charset cs = Charset.forName(encoding);
        ByteBuffer buf = cs.encode(value);
        int encodedLen = buf.limit();
        byte[] asBytes = new byte[encodedLen + 1];
        buf.get(asBytes, 0, encodedLen);
        asBytes[encodedLen] = 0;

        return asBytes;
    }
	
	public static byte[] getBytesNullTerminated(String value) {
        ByteBuffer buf = ByteBuffer.wrap(value.getBytes());
        int encodedLen = buf.limit();
        byte[] asBytes = new byte[encodedLen + 1];
        buf.get(asBytes, 0, encodedLen);
        asBytes[encodedLen] = 0;

        return asBytes;
    }
}
