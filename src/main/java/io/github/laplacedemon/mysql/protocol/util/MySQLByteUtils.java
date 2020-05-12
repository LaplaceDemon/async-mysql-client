package io.github.laplacedemon.mysql.protocol.util;

import java.security.DigestException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import io.github.laplacedemon.mysql.protocol.buffer.InputMySQLBuffer;
import io.github.laplacedemon.mysql.protocol.util.mysqldriver.ExportControlled;
import io.github.laplacedemon.mysql.protocol.util.mysqldriver.Security;
import io.github.laplacedemon.mysql.protocol.util.mysqldriver.StringUtils;

public class MySQLByteUtils {
	public static int getPacketLength(byte[] bs) {
		int v = bs[0];
		v &= 0xff;

		v |= ((int) bs[1] << 8);
		v &= 0xffff;

		v |= ((int) bs[2] << 16);
		v &= 0xffffff;

		return v;
	}

	/**
	 * https://dev.mysql.com/doc/internals/en/secure-password-authentication.html
	 * <br>
	 * SHA1( password ) XOR SHA1( "20-bytes random data from server"  concat  SHA1( SHA1( password ) ) )
	 * 
	 * @param pass
	 *         pass
	 * @param seed
	 *         seed
	 * @return
	 *         a byte array
	 */
	public static final byte[] scramble411(byte[] pass, byte[] seed) {
		MessageDigest md;
		try {
//			md = MessageDigest.getInstance("SHA-1");
//			byte[] pass1 = md.digest(pass);
//	        md.reset();
//	        
//	        byte[] pass2 = md.digest(pass1);
//	        md.reset();
//	        
//	        md.update(seed);
//	        byte[] pass3 = md.digest(pass2);
//	        for (int i = 0; i < pass3.length; i++) {
//	            pass3[i] = (byte) (pass3[i] ^ pass1[i]);
//	        }
//	        return pass3;

			md = MessageDigest.getInstance("SHA-1");
			byte[] pass1 = md.digest(pass);
			md.reset();

			byte[] pass2 = md.digest(pass1);
			md.reset();

			byte[] bs = new byte[seed.length + pass2.length];
			System.arraycopy(seed, 0, bs, 0, seed.length);
			System.arraycopy(pass2, 0, bs, seed.length, pass2.length);
//	        md.update(seed);
			byte[] pass3 = md.digest(bs);

			for (int i = 0; i < pass3.length; i++) {
				pass3[i] = (byte) (pass3[i] ^ pass1[i]);
			}
			return pass3;

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			System.exit(1);
			return null;
		}
	}

	/**
	 * @param password
	 *         password
	 * @param seed
	 *         seed
	 * @return
	 *         a byte array
	 */
	public static final byte[] cachingSHA2Password(byte[] password, byte[] seed) {
		try {
			return Security.scrambleCachingSha2(password, seed);
		} catch (DigestException | NoSuchAlgorithmException e) {
			e.printStackTrace();
			System.exit(1);
			return null;
		}
	}

	public static final byte[] SHA256Password(byte[] pass, byte[] seed) {
		return null;
	}

	/**
	 * https://dev.mysql.com/doc/internals/en/integer.html#packet-Protocol::LengthEncodedInteger
	 * 
	 * @param firstByte
	 *         first byte 
	 * @param inputMySQLBuffer
	 *         A InputMySQLBuffer object
	 * @return
	 *        a integer number
	 */        
	public static long readLengthEncodedInteger(byte firstByte, InputMySQLBuffer inputMySQLBuffer) {
		if (firstByte < 251) {
			return firstByte;
		} else if (firstByte == 251) {
			return 0;
		} else if (firstByte == 252) {
			byte[] bs = inputMySQLBuffer.readNBytes(2);
			return LitteEndianNumberUtils.toUShort(bs);
		} else if (firstByte == 253) {
			byte[] bs = inputMySQLBuffer.readNBytes(3);
			return LitteEndianNumberUtils.toIntFrom3Bytes(bs);
		} else if (firstByte == 254) {
			byte[] bs = inputMySQLBuffer.readNBytes(8);
			return LitteEndianNumberUtils.toUInt(bs);
		}

		return 0;
	}

	/**
	 * transformation:8.0.5以上的版本，值为 RSA/ECB/OAEPWithSHA-1AndMGF1Padding
	 */
	/**
	 * @param password
	 *         password
	 * @param seed
	 *         seed
	 * @param publicKeyString
	 *         PublicKeyString
	 * @param transformation
	 *         transformation
	 * @return
	 *         A byte array
	 */
	public static byte[] encryptPassword(String password, byte[] seed, String publicKeyString, String transformation) {
		byte[] input = null;
		input = password != null ? StringUtils.getBytesNullTerminated(password) : new byte[] { 0 };
		byte[] mysqlScrambleBuff = new byte[input.length];
		Security.xorString(input, mysqlScrambleBuff, seed, input.length);
		byte[] data = null;
		try {
			data = ExportControlled.encryptWithRSAPublicKey(mysqlScrambleBuff, ExportControlled.decodeRSAPublicKey(publicKeyString), transformation);
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidKeySpecException e) {
			e.printStackTrace();
		}
		return data;
	}

}
