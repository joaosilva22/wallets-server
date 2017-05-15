package auth;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

public class AuthHelper {
    public static KeyPair generateRSAKeys() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");

        SecureRandom sr = SecureRandom.getInstanceStrong();
        keyGen.initialize(1024, sr);

        return keyGen.generateKeyPair();
    }

    public static String keyToString(Key key) {
        return new BigInteger(key.getEncoded()).toString(64);
    }

    public static Key stringToKey(String string) {
        return new SecretKeySpec(new BigInteger(string, 64).toByteArray(), "RSA");
    }

    public static boolean validatePassword(String password, String salt, String hash) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String hash1 = generatePasswordHash(password, salt);
        return hash1.equals(hash);
    }

    public static String generatePasswordHash(String password, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        int iterations = 1000;
        char[] chars = password.toCharArray();
        byte[] bytes = fromHex(salt);

        PBEKeySpec spec = new PBEKeySpec(chars, bytes, iterations, 64 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

        return toHex(skf.generateSecret(spec).getEncoded());
    }

    public static String getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return toHex(salt);
    }

    private static String toHex(byte[] array) {
        return DatatypeConverter.printHexBinary(array);
    }

    private static byte[] fromHex(String hex) {
        return DatatypeConverter.parseHexBinary(hex);
    }
}
