import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Main class of application.
 * <p>All methods act like a part of a {@code main()} method.
 * <p>All class fields act like local variables.
 * <p>All code looks quite cluttered, but remember: it's only a pet-project where i tried to do some good things.
 *
 * <p>P.S. Console app wasn't the greatest idea, and one day it will be replaced by some UI app.
 *
 * @author nikjag
 * @version 1.0
 */
public class Main {

    /**
     * Local variable, marking if new GDrive repository is created.
     * <p>true, if new GDrive repository is created;
     * <p>false, if either existing repository or password file has been opened.
     */
    private static boolean fileIsCreated = false;

    /**
     * Local variable, marking that password file is taken from and will be saved into GDrive repository.
     */
    private static boolean isDrive = false;
    /**
     * Local variable, marking that password file is taken from and will be saved
     * into password file in the file system.
     */
    private static boolean isFileSystem = false;

    /**
     * {@link Path} object represents a path to the password file.
     * <p>Initialized by a default file name.
     */
    private static Path PASS_FILE = Paths.get("password.txt");

    /**
     * {@link Path} object represents a path to the key file.
     * <p>Initialized by a default file name.
     */
    private static Path KEY_FILE = Paths.get("key.txt");

    /**
     * {@link DriveAccess} object provides all methods needed to manage GDrive repository
     */
    private static DriveAccess drive;

    /**
     * {@link ArrayList} of {@link Record} objects contains all login-password pairs with their tags.
     */
    private static ArrayList<Record> recordList;

    /**
     * {@link BufferedReader} for console input purposes.
     */
    private static BufferedReader consoleReader;

    /**
     * Contains information about changes in {@code recordList} after last save option.
     * Used to prevent unnecessary IO calls.
     * <p> true, if something changed;
     * <p> false otherwise.
     */
    private static boolean recordListIsChanged = false;

    /**
     * Method makes new authentication and download password file into {@code PASS_FILE}.
     * <p>Catches some IOE exceptions and writes an information about them.
     *
     * @return true, if authentication and downloading was successful;
     *         <p>false otherwise.
     */
    private static boolean driveOption() {
        try {
            drive = new DriveAccess();
            drive.downloadFile(PASS_FILE);
        }
        catch (FileNotFoundException fnfe) {
            System.out.println("Establishing GDrive connection is successful!");
            System.out.println("File hasn't been found.");
            System.out.println("Creating a new one...");
            return true;
        }
        catch (IOException ioe) {
            System.out.println("Failed managing a connection with Google Drive.");
            System.out.println("Try to repeat establishing or choose another option." +
                    "\n");
            return false;
        }
        catch (GeneralSecurityException gse) {
            System.out.println("GeneralSecurityException has occurred.");
            System.out.println("Try to repeat establishing or choose another option." +
                    "\n");
            return false;
        }
        catch (Exception ex) {
            System.out.println("Exception occurred.");
            System.out.println("Try to repeat establishing or choose another option." +
                    "\n");
            return false;
        }
        System.out.println("Establishing GDrive connection is successful!");
        System.out.println("File has been found!");
        return true;
    }

    /**
     * Method contains all code responsible for finding a password key stored in file system.
     *
     * @return true, if file is found;
     *         <p>false otherwise.
     */
    private static boolean fileSystemOption() {
        System.out.print("Please, choose a password file from file system: ");
        try {
            PASS_FILE = ExplorerFileManaging.getFile("password file");
        }
        catch (FileNotFoundException fnfe) {
            System.out.println();
            System.out.println("File hasn't been found!");
            System.out.println("Try to repeat getting a password file or choose another option." +
                    "\n");
            return false;
        }
        System.out.println(PASS_FILE.toAbsolutePath().toString());
        System.out.println("File has been found!");
        return true;
    }

    /**
     * Method outputs some messages to user, waits pressing Enter button and closes an application.
     *
     * @throws IOException if input was unsuccessful.
     */
    private static void exitProgram() throws IOException {
        System.out.println("Exit the program.");
        System.out.println("Press Enter to close the application...");
        System.in.skip(System.in.available());
        System.in.read();
        System.exit(0);
    }

    /**
     * Method outputs a repeat message and waits pressing Enter button to continue.
     *
     * @throws IOException if input was unsuccessful.
     */
    private static void repeatInput() throws IOException {
        System.out.println("Your choice hasn't been recognized; repeat the input.");
        System.out.println("Press Enter to continue...");
        System.in.skip(System.in.available());
        System.in.read();
        System.out.println("\n");
    }

    /**
     *  Method contains a part of the program where user should choose
     *  a source of a password (either GDrive repository or file system)
     *  and key file (stored in file system).
     *
     * @throws IOException if input was unsuccessful.
     * @throws InterruptedException if sleep was interrupted.
     */
    private static void openFiles() throws IOException, InterruptedException {
        boolean key_file_selected = false;

        while (!(isDrive || isFileSystem)) {
            System.out.println("\n------------OPEN FILE MENU------------");
            System.out.println("Choose an option by typing a corresponding symbol:");
            System.out.println("1: Establish a connection with Google Drive and get a password file;");
            System.out.println("2: Take a password file from file system;");
            System.out.println("q: Quit the program.");
            System.out.print("Choice: ");
            char choice = (char) System.in.read();
            System.in.skip(System.in.available());
            System.out.println();

            switch (choice) {
                case '1': {
                    isDrive = driveOption();
                    break;
                }
                case '2': {
                    isFileSystem = fileSystemOption();
                    break;
                }
                case 'q': {
                    exitProgram();
                    // break here isn't needed because program terminates in exitProgram().
                }
                default: {
                    repeatInput();
                    break;
                }
            }
        }

        TimeUnit.SECONDS.sleep(2); // add some pause for user between choosing a pass and key file

        while (!key_file_selected) {
            System.out.println();
            System.out.print("Choose a key file from file system: ");
            try {
                KEY_FILE = ExplorerFileManaging.getFile("key file");
            } catch (IOException ioe) {
                System.out.println();
                System.out.println("Key file hasn't been found!");
                System.out.println("Do you want to try again? (y\\n)");
                System.out.print("Choice: ");
                System.in.skip(System.in.available());
                char choice = (char) System.in.read();
                if (choice == 'n')
                    exitProgram();

            }

            System.out.println(KEY_FILE.toAbsolutePath().toString());
            key_file_selected = true;
            System.out.println("Key file has been found!");
        }
    }

    /**
     * Adds a new {@link Record} in {@code recordList} and prints an information about added Record.
     *
     * @throws IOException if reading is failed.
     */
    private static void addRecord() throws IOException {
        System.out.println("-------------------");
        System.out.println("Adding new record:");
        System.out.print("Tag(s): ");
        String tag = consoleReader.readLine();
        System.out.print("Login: ");
        String login = consoleReader.readLine();
        System.out.print("Password: ");
        String password = consoleReader.readLine();
        recordList.add(new Record(tag,login,password));
        System.out.println("Record [tag: " + tag +"; login: " + login + "; password = " + password + "] added!");
        System.out.println("-------------------");
        recordListIsChanged = true;
    }

    /**
     * Finds {@code Record} objects in the {@code recordList} by a tag.
     * @param search_tag a {@code String} object by which relevant Record is searched
     * @return true, if one or more Record found;
     *         <p>false, if no Record found.
     */
    private static boolean findRecord(String search_tag) {
        boolean somethingIsFound = false;
        String delimiter = "--------------------------------------------------";

        System.out.println("Result for search by \"" + search_tag + "\" search tag");
        for (Record record:
                recordList) {
            if ((search_tag.equals("")) | (record.getTag().contains(search_tag))){
                if (recordList.indexOf(record) == 0)
                    System.out.println("__________________________________________________");
                System.out.printf("%3d|tag: %-40s\n", recordList.indexOf(record), record.getTag());
                System.out.printf("   |login: %-40s\n", record.getLogin());
                System.out.printf("   |password: %-40s\n", record.getPassword());
                System.out.println(delimiter);
                somethingIsFound = true;
            }
        }

        if (!somethingIsFound)
            System.out.println("Records not found!");

        return somethingIsFound;
    }

    /**
     * Tries to find a {@code Record} object in the {@code recordList} and provides opportunity to read
     * or delete a found {@code Record} from list.
     *
     * @throws IOException if input was unsuccessful.
     */
    private static void editRecord() throws IOException {
        String search_tag = "";

        System.out.print("Write a search tag(Enter to see all records): ");
        search_tag += consoleReader.readLine();
        System.in.skip(System.in.available());

        boolean isFound = findRecord(search_tag);

        choice :
        {
            int int_choice;
            if (isFound) {
                System.out.println("Which record you want to edit? (q for quit in main menu)");
                System.out.print("Type a number of a record: ");
                String str_choice = consoleReader.readLine();
                if (str_choice.contains("q"))
                    return;
                try {
                    int_choice = Integer.parseInt(str_choice);
                } catch (NumberFormatException nfe) {
                    repeatInput();
                    break choice;
                }

                edit: {
                    System.out.println("1: Edit record;");
                    System.out.println("2: Delete record;");
                    System.out.print("Choice: ");
                    char ch_choice = (char) System.in.read();
                    System.in.skip(System.in.available());
                    System.out.println();
                    switch (ch_choice) {
                        case '1': {
                            System.out.println("Input new values of the record:");
                            System.out.print("Tag(s): ");
                            recordList.get(int_choice).setTag(consoleReader.readLine());
                            System.out.println();
                            System.out.print("Login: ");
                            recordList.get(int_choice).setLogin(consoleReader.readLine());
                            System.out.println();
                            System.out.print("Password: ");
                            recordList.get(int_choice).setPassword(consoleReader.readLine());
                            System.out.println("Record has been changed!");
                            recordListIsChanged = true;
                            break;
                        }

                        case '2': {
                            recordList.remove(int_choice);
                            System.out.println("Record has been deleted!");
                            recordListIsChanged = true;
                            break;
                        }
                        default: {
                            repeatInput();
                            System.out.println();
                            break edit;
                        }
                    }
                }
            }
            else // if findRecord(search_tag) is false;
                System.out.println("Repeat a search or make new records to edit");
        }
    }

    /**
     * Saves changes in the password file, then uploads a file into GDrive repository if needed.
     *
     * @throws IOException if input was unsuccessful.
     */
    private static void saveChanges() throws IOException {
        EncryptedFileWriter fileWriter = new EncryptedFileWriter(PASS_FILE.toString(),KEY_FILE.toString());
        fileWriter.write(recordList);
        if (isDrive | fileIsCreated){
            if (fileIsCreated) {
                try{
                    drive = new DriveAccess();
                }
                catch (GeneralSecurityException gse) {
                    System.out.println("GeneralSecurityException has occurred.");
                    System.out.println("Try to save again." +
                            "\n");
                    return;
                }
                catch (IOException ioe) {
                    System.out.println("Failed to upload file into GDrive repository.");
                    System.out.println("Try to save again." +
                            "\n");
                }
            }
            try {
                drive.uploadFile(PASS_FILE);
                System.out.println("File has been saved into GDrive repository!");
            }
            catch (IOException ioe) {
                System.out.println("Failed to upload file into GDrive repository.");
                System.out.println("Try to repeat establishing or choose another option." +
                        "\n");
            }
            File deletingFile = new File(PASS_FILE.toString());
            deletingFile.delete();
        }
        if(isFileSystem){
            System.out.println("Files has been saved!");
        }
        recordListIsChanged = false;
    }

    /**
     * <p>main() method consists of two parts:
     * <pre>
     *     1) Create-open part, where user creates a new GDrive-based repository
     *        or opens an existing one
     *        (or imports a password file from file system).
     *        If user either opens an existing GDrive repository
     *        or imports a password file,
     *        he should choose a key file stored in the file system.
     *     2) Main part, where all add-read-save things happen.
     * </pre>
     * <p>
     * After first part, program initializes {@link ArrayList} of {@link Record} (either empty, if it's new repository,
     * or contained some {@code Record} otherwise).
     *
     * @param args isn't used.
     *
     * @throws IOException if input was unsuccessful.
     * @throws InterruptedException if sleep was interrupted.
     */
    public static void main(String... args) throws IOException,InterruptedException {
        char choice;

        while (!(fileIsCreated | isDrive | isFileSystem)) { // First part
            System.out.println("CREATE\\OPEN FILE MENU");
            System.out.println("Would you like to create a new repository or use an existing one?");
            System.out.println("1: Create a new Google Drive password repository;");
            System.out.println("2: Open an existing password repository;");
            System.out.println("q: Quit the program.");
            System.out.print("Choice: ");
            choice = (char) System.in.read();
            System.in.skip(System.in.available());
            switch (choice) {
                case '1': {
                    fileIsCreated = true;
                    System.out.println();
                    break;
                }
                case '2': {
                    openFiles();
                    break;
                }
                case 'q': {
                    exitProgram();
                }
                default:
                    repeatInput();
            }
        }

        if (fileIsCreated) {
            recordList = new ArrayList<>();
        }
        else {
            EncryptedFileReader fileReader = new EncryptedFileReader(PASS_FILE.toString(),KEY_FILE.toString());
            recordList = fileReader.readList();
        }

        consoleReader = new BufferedReader(new InputStreamReader(System.in));

        while(true) { // Second part
            System.out.println("\n------------MAIN MENU------------");
            System.out.println("Options:");
            System.out.println("1: Add records;");
            System.out.println("2: Find records;");
            System.out.println("3: Edit or delete a record;");
            System.out.println("s: Save changes into repository;");
            System.out.println("q: Quit the program.");
            System.out.print("Choice: ");
            choice = (char) System.in.read();
            System.in.skip(System.in.available());
            System.out.println();

            switch (choice){
                case '1' :{
                    addRecord();
                    break;
                }
                case '2' : {
                    if (!recordList.isEmpty()) {
                        System.out.print("Write a search tag(Enter to see all records): ");
                        String search_tag = "";
                        search_tag += consoleReader.readLine();
                        System.in.skip(System.in.available());
                        findRecord(search_tag);
                    }
                    else
                        System.out.println("No record to search; add new ones before searching.");
                    System.out.print("Press Enter to continue...");
                    System.in.skip(System.in.available());
                    System.in.read();
                    System.in.skip(System.in.available());
                    System.out.println();
                    break;
                }
                case '3' : {
                    if(!recordList.isEmpty())
                        editRecord();
                    else
                        System.out.println("No record to edit; add new ones before changing.");
                    System.out.print("Press Enter to continue...");
                    System.in.skip(System.in.available());
                    System.in.read();
                    System.in.skip(System.in.available());
                    System.out.println();
                    break;
                }
                case 's' : {
                    if (recordListIsChanged)
                        saveChanges();
                    break;
                }
                case 'q' : {
                    if (recordListIsChanged) {
                        saveChanges();
                        System.out.println();
                    }
                    exitProgram();
                }
                default:
                    repeatInput();
            }
        }
    }
}

