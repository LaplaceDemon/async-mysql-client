package io.github.laplacedemon.mysql.protocol.util;

import java.math.BigInteger;

/**
 * @author jackie.sjq
 *
 */
public class LitteEndianNumberUtils {
    
    private LitteEndianNumberUtils(){}
    
    /**
     * 小端字节转short
     * @param b
     *      byte
     * @return
     *      short
     */
    public static short toShort(byte[] b) {
        short v = 0;
        v = b[0];
        v &= 0xff;
        v |= ((int) b[1] << 8);
        v &= 0xffff;
        return v;
    }

    /**
     * 小端字节转int
     * @param b
     *      a byte
     * @param offset
     *      offset
     * @return
     *      a int
     */
    public static int toInt(byte[] b, int offset) {
        int v = b[offset + 0];
        v &= 0xff;
        v |= ((int) b[offset + 1] << 8);
        v &= 0xffff;
        v |= ((int) b[offset + 2] << 16);
        v &= 0xffffff;
        v |= ((int) b[offset + 3] << 24);
        v &= 0xffffffff;
        return v;
    }
    
    public static int toInt(byte[] b) {
        return toInt(b, 0);
    }
    
    public static long toUInt(byte[] b){
        byte[] uIntBytes = new byte[]{b[0],b[1],b[2],b[3],0,0,0,0};
        return toLong(uIntBytes);
    }
    
    public static int toUShort(byte[] b) {
    	byte[] uShortBytes = new byte[]{b[0], b[1], 0, 0};
		return toInt(uShortBytes);
	}
    
    public static long toUIntFor3Bytes(byte[] b,int offset) {
        // ToDo
        byte[] uIntBytes = { b[0 + offset], b[1 + offset], b[2 + offset], 0, 0, 0, 0, 0 };
        return toLong(uIntBytes);
    }
    
    public static long toUIntFor3Bytes(byte[] b) {
        return toUIntFor3Bytes(b,0);
    }
    
    public static BigInteger toULongFor8Bytes(byte[] b,int offset) {
//        byte[] bytes = new byte[]{b[15],b[14],b[13],b[12],b[11],b[10],b[9],b[8],b[7],b[6],b[5],b[4],b[3],b[2],b[1],b[0]};
        byte[] bytes = new byte[]{0,b[7],b[6],b[5],b[4],b[3],b[2],b[1],b[0]};
        BigInteger bigInteger0 = new BigInteger(bytes);
        return bigInteger0;
    }
    
    public static BigInteger toULongFor8Bytes(byte[] b) {
      return toULongFor8Bytes(b,0);
  }
    
    public static long toLong(byte[] b, int offset) {
        long l = 0;
        l |= (((long) b[offset + 7] & 0xff) << 56);
        l |= (((long) b[offset + 6] & 0xff) << 48);
        l |= (((long) b[offset + 5] & 0xff) << 40);
        l |= (((long) b[offset + 4] & 0xff) << 32);
        l |= (((long) b[offset + 3] & 0xff) << 24);
        l |= (((long) b[offset + 2] & 0xff) << 16);
        l |= (((long) b[offset + 1] & 0xff) << 8);
        l |= ((long) b[offset + 0] & 0xff);
        return l;
    }

    public static long toLong(byte[] b) {
        return toLong(b, 0);
    }
    
    public static int toLitteEndian(int num){
        return (num & 0x000000FF) << 24 | (num & 0x0000FF00) << 8 | (num & 0x00FF0000) >> 8 | (num & 0xFF000000) >> 24;   
    }
    
    public static short toLitteEndian(short num){
        return (short)((num & 0x00FF) << 8 | (num & 0xFF00) >> 8);   
    }
    
    /**
     * 小端数据转Java整数。
     * @param b
     *      byte
     * @param offset
     *      offset
     * @return
     *      a int
     */
    public static int toIntFrom3Bytes(byte[] b, int offset) {
        int v = b[offset + 0];
        v &= 0xff;
        v |= ((int) b[offset + 1] << 8);
        v &= 0xffff;
        v |= ((int) b[offset + 2] << 16);
        v &= 0xffffff;
        return v;
    }
    
    /**
     * 小端字节数转成Java整数。
     * @param b
     *      byte
     * @return
     *      a int
     */
    public static int toIntFrom3Bytes(byte[] b) {
        return toIntFrom3Bytes(b,0);
    }
    
    /**
     * 0x00abcdef --- ef cd ab
     * 
     * @param x
     *      a int number
     * @return
     *      a byte array
     */
    public static byte[] to3Bytes(int x) {
    	byte[] bytes = new byte[3];
    	bytes[0] = (byte) (0xff & x);
        bytes[1] = (byte) ((0xff00 & x) >> 8);
        bytes[2] = (byte) ((0xff0000 & x) >> 16);
        return bytes;
    }
    
    public static byte[] toBytes(short x) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (0xff & x);
        bytes[1] = (byte) ((0xff00 & x) >> 8);
        return bytes;
    }
    
    /**
     * 0x00abcdef --- 00 ef cd ab
     * @param x
     *      a int number
     * @return
     *      a byte array
     */
    public static byte[] toBytes(int x) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (0xff & x);
        bytes[1] = (byte) ((0xff00 & x) >> 8);
        bytes[2] = (byte) ((0xff0000 & x) >> 16);
        bytes[3] = (byte) ((0xff000000 & x) >> 24);
        return bytes;
    }

}
