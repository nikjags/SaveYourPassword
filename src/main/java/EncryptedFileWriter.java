import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;

/**
 * Class provides methods to encrypt and write a data.
 * <p>It contains two {@code File writer} objects (see {@link FileWriter}) for both password and key files.
 * <p>{@code key_file} contains nothing but a UTF_8 symbols needed for decrypting a data from {@code pass_file}.
 * <p>{@code pass_file} contains several string representing a {@code Record} object(s) (see {@link Record})
 * <p>Each {@code Record} object is written in the password file by the following template:
 * <blockquote><pre>
 *     1)length of a tag field;
 *     2)':' char to mark an end of length;
 *     3)tag field;
 *     4)length of a login field;
 *     5)':' char to mark an end of length;
 *     6)login field;
 *     7)length of a password field;
 *     8)':' char to mark an end of length;
 *     9)password field.
 * </pre></blockquote>
 *
 * @author nikjag
 * @version 1.0
 */
public class EncryptedFileWriter{

    /**
     * {@code FileWriter} objects representing both password and key files.
     */
    private FileWriter passFile;
    private FileWriter keyFile;

    /**
     * {@code SecureRandom} object provides a cryptographically strong random number generator (RNG).
     */
    private SecureRandom rand = new SecureRandom();


    /**
     * Initializes both {@code FileWriter} objects for further writing.
     *
     * @param pass_file_path
     *        path to password file.
     * @param key_file_path
     *        path to key file.
     * @throws IOException like all {@link FileWriter} do.
     */
    EncryptedFileWriter(String pass_file_path, String key_file_path) throws IOException {
        passFile = new FileWriter(pass_file_path, StandardCharsets.UTF_8);
        keyFile = new FileWriter(key_file_path, StandardCharsets.UTF_8);
    }

    /**
     * Encrypt a {@code str} string, writes it to the password file; writes key sequence to the key file.
     * @param str a string to be encrypted and writed
     * @throws IOException if write was unsuccessful.
     */
    private void write(String str) throws IOException{
        String key_str = "";
        for (int i = 0; i < str.getBytes().length ; i++){
            key_str += (char)rand.nextInt(1024);
        }
        passFile.write(EncryptVernam.encrypt(str, key_str));
        keyFile.write(key_str);
    }


    /**
     * Writes a {@code Record} object to the file (and flushes it).
     *
     * <p>Note that {@code Record} object either be fully written on the password file or not written at all.
     * So do key sequence.
     * @param record
     *        {@code Record} object to be written.
     * @throws IOException if write was unsuccessful.
     * @throws NullPointerException if {@code record} is null.
     */
    private void write(Record record) throws IOException, NullPointerException{
        if (record != null) {
            passFile.write(Integer.toString(record.getTag().getBytes().length));
            passFile.write(":");
            write(record.getTag());

            passFile.write(Integer.toString(record.getLogin().getBytes().length));
            passFile.write(":");
            write(record.getLogin());

            passFile.write(Integer.toString(record.getPassword().getBytes().length));
            passFile.write(":");
            write(record.getPassword());

            passFile.flush();
            keyFile.flush();
        }
        else{
            throw new NullPointerException();
        }
    }

    /**
     * Writes an {@link ArrayList} of a {@link Record}.
     * <p>Method writes {@code Record} objects one by one;
     * so object either be written to the file or not written at all.
     *
     * @param recordList an {@code ArrayList} of {@code Record} to be written.
     * @throws IOException if write was unsuccessful.
     */
    public void write(ArrayList<Record> recordList) throws IOException, NullPointerException{
        if (recordList != null) {
            for (Record rec : recordList)
                write(rec);
        }
        else {
            throw new NullPointerException();
        }
    }
}
