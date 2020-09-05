import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

/**
 * {@code DriveAccess} class encapsulates all Google Drive API stuff and provides some useful upload/download methods.
 * <p> Class is used to create and (<i>kind of</i>) maintain repository based on Google Drive.
 *
 * @author nikjag
 * @version 1.0
 */
public class DriveAccess{
    /**
     * Configuration things; used for managing a connection with the GDrive.
     */
    private static final String APPLICATION_NAME = "Save Your Password";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_FILE);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Password file name in the GDrive.
     */
    private final String PASS_FILE_NAME = "password.txt";

    /**
     * A special folder name where password file is stored.
     */
    private final String PASS_FOLDER_NAME = ".enpass";

    /**
     * Object of {@code Drive} class which represents a Drive and provides all GDrive API things.
     */
    private Drive service;

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = Main.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    /**
     * A constructor builds a connection with GDrive and initializes {@code Drive} object ({@code service}).
     *
     * @throws IOException If failed to get credentials.
     * @throws GeneralSecurityException If something went really wrong.
     */
    DriveAccess() throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Searches a file in the GDrive.
     *
     * @param fileName
     *        string, representing a name of a file to be found.
     *
     * @return {@code String}, if file of specified name is found;
     *         {@code null} otherwise.
     * @throws IOException if upload was unsuccessful.
     */
    private String findFile(String fileName) throws IOException{
        FileList result = service.files().list()
                .setQ("name = '" + fileName + "'")
                .setSpaces("drive")
                .setFields("files(id, name)")
                .execute();

        if(result.getFiles().size() == 0) return null;
        return result.getFiles().get(0).getId();
    }

    /**
     * Downloads a password file from GDrive repository to {@code file_path}.
     *
     * @param file_path
     *        {@code String} representing a path in the file system, where the file will be downloaded.
     *
     * @throws IOException if the password file in GDrive isn't exist.
     */
    public void downloadFile(String file_path) throws IOException {

        if (findFile(PASS_FILE_NAME) == null)
            throw new FileNotFoundException();
        else {
            OutputStream outputStream = new FileOutputStream(file_path);
            service.files().get(findFile(PASS_FILE_NAME))
                    .executeMediaAndDownloadTo(outputStream);
        }
    }

    /**
     * Uploads a password file from file system to GDrive repository into special folder.
     * <p>If a folder isn't exists, creates a new folder.
     * <p>If a folder already contains a password file, deletes a file and uploads a new one.
     *
     * @param path_to_file
     *        {@code String} representing a path, where file to be uploaded is stored.
     *
     * @throws IOException if the password file in GDrive isn't exist.
     */
    public void uploadFile(String path_to_file) throws IOException {
        com.google.api.services.drive.model.File folder;
        String folder_id = findFile(PASS_FOLDER_NAME);

        if (folder_id == null) { // if folder not found
            folder = new com.google.api.services.drive.model.File();
            folder.setName(PASS_FOLDER_NAME);
            folder.setMimeType("application/vnd.google-apps.folder");

            service.files().create(folder)
                    .setFields("id, name")
                    .execute();
            folder_id = findFile(PASS_FOLDER_NAME);
        }

        String old_file_id = findFile(PASS_FILE_NAME);

        com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
        fileMetadata.setName(PASS_FILE_NAME);
        fileMetadata.setParents(Collections.singletonList(folder_id));
        java.io.File filePath = new java.io.File(path_to_file);
        FileContent mediaContent = new FileContent("", filePath);
        service.files().create(fileMetadata, mediaContent)
                .setFields("id, parents")
                .execute();

        if(old_file_id != null)
            service.files().delete(old_file_id)
                    .execute();
    }
}
