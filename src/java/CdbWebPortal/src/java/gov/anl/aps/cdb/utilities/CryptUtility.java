package gov.anl.aps.cdb.utilities;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import org.apache.log4j.Logger;
import org.primefaces.util.Base64;

public class CryptUtility {

    private static final Logger logger = Logger.getLogger(CryptUtility.class.getName());

    private static final String SECRET_KEY_FACTORY_TYPE = "PBKDF2WithHmacSHA1";
    private static final int PBDKF2_ITERATIONS = 1003;
    private static final int PBDKF2_KEY_LENGTH_IN_BITS = 192;
    private static final int SALT_LENGTH_IN_BYTES = 4;
    private static final char[] SALT_CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
    private static final String SALT_DELIMITER = "$";
    
    public static String randomString(char[] characterSet, int length) {
        Random random = new SecureRandom();
        char[] result = new char[length];
        for (int i = 0; i < result.length; i++) {
            // picks a random index out of character set > random character
            int randomCharIndex = random.nextInt(characterSet.length);
            result[i] = characterSet[randomCharIndex];
        }
        return new String(result);
    }

    public static String cryptPasswordWithPbkdf2(String password) {
        String salt = randomString(SALT_CHARSET, SALT_LENGTH_IN_BYTES);
        return saltAndCryptPasswordWithPbkdf2(password, salt);
    }

    public static String saltAndCryptPasswordWithPbkdf2(String password, String salt) {
        char[] passwordChars = password.toCharArray();
        byte[] saltBytes = salt.getBytes();

        PBEKeySpec spec = new PBEKeySpec(
                passwordChars,
                saltBytes,
                PBDKF2_ITERATIONS,
                PBDKF2_KEY_LENGTH_IN_BITS
        );
        SecretKeyFactory key;
        try {
            key = SecretKeyFactory.getInstance(SECRET_KEY_FACTORY_TYPE);
            byte[] hashedPassword = key.generateSecret(spec).getEncoded();
            String encodedPassword = Base64.encodeToString(hashedPassword, true);
            return salt + SALT_DELIMITER + encodedPassword;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            // Should not happen
            logger.error("Password cannot be crypted: " + ex);
        }
        return null;
    }

    public static boolean verifyPasswordWithPbkdf2(String password, String cryptedPassword) {
        int saltEnd = cryptedPassword.indexOf(SALT_DELIMITER);
        String salt = cryptedPassword.substring(0,saltEnd);
        return cryptedPassword.equals(saltAndCryptPasswordWithPbkdf2(password, salt));
    }
    
    
    public static void main(String[] args) {
        String password = "cdb";
        System.out.println("Original password: " + password);
        String cryptedPassword = cryptPasswordWithPbkdf2(password);
        System.out.println("Crypted password: " + cryptedPassword);
        System.out.println("Verified: " + verifyPasswordWithPbkdf2(password, cryptedPassword));
    }
}
