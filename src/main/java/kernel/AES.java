package kernel;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class AES {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORM_ECB_PKCS5 = "AES/ECB/PKCS5Padding";
    private static final String AES_KEY = "TSd5s&RZ86voyU1TE7%sd1xzOcrLn!n*";

    public static String encrypt(final String value, final String key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        final SecretKeySpec keySpec = getSecretKey(key);
        Cipher encipher = Cipher.getInstance(TRANSFORM_ECB_PKCS5);
        encipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encrypted = encipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }
    public static String encrypt(final String value) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        return encrypt(value, AES_KEY);
    }

    public static String decrypt(final String encryptedStr, final String key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        final SecretKeySpec keySpec = getSecretKey(key);
        Cipher encipher = Cipher.getInstance(TRANSFORM_ECB_PKCS5);
        encipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedStr);
        byte[] originalBytes = encipher.doFinal(encryptedBytes);
        return new String(originalBytes);
    }

    public static String decrypt(final String encryptedStr) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        return decrypt(encryptedStr, AES_KEY);
    }


    private static SecretKeySpec getSecretKey(final String KEY) throws NoSuchAlgorithmException {
        KeyGenerator generator = KeyGenerator.getInstance(ALGORITHM);
        SecureRandom random=SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(KEY.getBytes(StandardCharsets.UTF_8));
        generator.init(128, random);
        SecretKey secretKey = generator.generateKey();
        return new SecretKeySpec(secretKey.getEncoded(), ALGORITHM);
    }

}
