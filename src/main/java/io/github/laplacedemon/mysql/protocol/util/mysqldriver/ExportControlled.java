package io.github.laplacedemon.mysql.protocol.util.mysqldriver;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class ExportControlled {

    private ExportControlled() {
    }

    public static byte[] encryptWithRSAPublicKey(byte[] source, RSAPublicKey key, String transformation) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(source);
    }
    
    public static RSAPublicKey decodeRSAPublicKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        int offset = key.indexOf("\n") + 1;
        int len = key.indexOf("-----END PUBLIC KEY-----") - offset;

        // TODO: use standard decoders with Java 6+
        byte[] certificateData = Base64Decoder.decode(key.getBytes(), offset, len);

        X509EncodedKeySpec spec = new X509EncodedKeySpec(certificateData);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) kf.generatePublic(spec);
    }
}
