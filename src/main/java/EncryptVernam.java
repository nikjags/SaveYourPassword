/**
 * {@code EncryptVernam} class provides a method for encrypting and decrypting {@code String} values by Vernam Cipher.
 *
 * @author nikjag
 * @version 1.0
 */

public class EncryptVernam {

    /**
     * Encrypts a string by Vernam Cipher.
     * <p> If length of a {@code input_str} is less than length of a {@code key_str},
     * encrypts only a part of a {@code input_str}.
     *
     * @param input_str
     *        a string to be encrypted
     * @param key_str
     *        a key string.
     *
     * @return {@code String}, containing an encrypted string
     */
    public static String encrypt(String input_str, String key_str) {
        String encrypted_str = "";

        int actual_length = Math.min(input_str.length(), key_str.length());

        for (int i = 0; i < actual_length; i++) {
            encrypted_str += (char) (input_str.charAt(i) ^ key_str.charAt(i));
        }

        return encrypted_str;
    }
}
