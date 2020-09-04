import java.awt.*;
import java.io.FileNotFoundException;
import java.nio.file.Path;
/**
 * Class provides useful methods to choose files from file system.
 *
 * @author nikjag
 * @version 1.0
 */
public class ExplorerFileManaging {

    /**
     * Provides an explorer window to choose a file.
     *
     * @param file_title name of file to be found.
     *
     * @return {@link Path} object which represent a path to the file.
     * @throws FileNotFoundException if file not found.
     */
    public static Path getFile(String file_title) throws FileNotFoundException {
        FileDialog dialog = new FileDialog((Frame)null, "Select " + file_title + " to Open");
        dialog.setMode(FileDialog.LOAD);
        dialog.setVisible(true);
        String file = dialog.getFile();
        dialog.dispose();
        if (file != null)
            return Path.of(file);
        else
            throw new FileNotFoundException();
    }

    /**
     * Provides an explorer window to create a file.
     *
     * @param file_title name of file to be found.
     *
     * @return {@link Path} object which represent a path to the file.
     * @throws FileNotFoundException if file not found.
     */
    public static Path createFile(String file_title) throws FileNotFoundException {
        FileDialog dialog = new FileDialog((Frame)null, "Save a " + file_title);
        dialog.setMode(FileDialog.SAVE);
        dialog.setVisible(true);
        String file = dialog.getFile();
        dialog.dispose();
        if (file != null)
            return Path.of(file);
        else
            throw new FileNotFoundException();
    }
}
