
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.io.*;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

/**
 * Class provides methods to read an encrypted data.
 * <p>It contains two {@code File reader} objects (see {@link FileReader}) for both password and key files.
 * <p>{@code key_file} contains nothing but a UTF_8 symbols needed for decrypting a data from {@code pass_file}.
 * <p>{@code pass_file} contains several string representing a {@code Record} object(s) (see {@link Record})
 * <p>Every {@code Record} object is stored in the password file by the following template:
 * <pre>
 *     1)length of a tag field;
 *     2)':' char to mark an end of length;
 *     3)tag field;
 *     4)length of a login field;
 *     5)':' char to mark an end of length;
 *     6)login field;
 *     7)length of a password field;
 *     8)':' char to mark an end of length;
 *     9)password field.
 * </pre>
 *
 * @author nikjag
 * @version 1.0
 */
public class EncryptedFileReader{

    /**
     * {@code FileReader} objects representing both password and key files.
     */
    FileReader pass_file;
    FileReader key_file;

    /**
     * Initializes {@code FileReader} objects for further read.
     * @param pass_file_path
     *        a path to the stored pass file;
     * @param key_file_path
     *        a path to the stored key file.
     * @throws IOException like all {@link FileReader} throw.
     */
    EncryptedFileReader(String pass_file_path, String key_file_path) throws IOException {
        pass_file = new FileReader(pass_file_path, StandardCharsets.UTF_8);
        key_file = new FileReader(key_file_path, StandardCharsets.UTF_8);
    }

    /**
     * Reads an encrypted string by the pattern (see {@link EncryptedFileReader}) from {@code pass_fie},
     * reads a key sequence of the same length from {@code key_file} and decrypts it.
     *
     * @return an decrypted String.
     * @throws IOException if read was unsuccessful.
     */
    private String readString() throws IOException{
        String str_length_of_string = "";
        do {
            int read_ch = pass_file.read();
            if ((read_ch == -1) & (str_length_of_string.length() == 0))
                return null;
            else if ((char)read_ch == ':')
                break;
            str_length_of_string += (char) read_ch;
        } while (true);

        int len_of_string = Integer.parseInt(str_length_of_string);
        char[] ch_string = new char[len_of_string];
        char[] ch_key_string = new char[len_of_string];
        pass_file.read(ch_string);
        key_file.read(ch_key_string);

        String encrypted_string = String.valueOf(ch_string);
        String key_string = String.valueOf(ch_key_string);

        return EncryptVernam.encrypt(encrypted_string,key_string);
    }

    /**
     * Creates a Record objects from strings returned by a {@code readString()} method.
     *
     * @return {@code Recrod} object.
     * @throws IOException if read was unsuccessful.
     */
    private Record readRecord() throws IOException {
        String tag_of_new_string = readString();
        if(tag_of_new_string == null)
            return null;
        return new Record(tag_of_new_string, readString(), readString());
    }

    /**
     * Forms an {@link ArrayList} of a {@link Record} which contains all Record objects was stored in password file.
     *
     * @return {@code ArrayList<Record>} object if files contained some Record;
     *          {@code null} otherwise.
     * @throws IOException if read was unsuccessful.
     */
    public ArrayList<Record> readList() throws IOException {
        Record new_record;

        ArrayList<Record> recordList = new ArrayList<>();
        do {
            new_record = readRecord();
            if (new_record == null)
                return recordList;
            recordList.add(new_record);
        } while (true);
    }
}