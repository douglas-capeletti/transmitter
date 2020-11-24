import javax.swing.*;
import java.io.File;

public class App {

    private static final UserInterface ui = new UserInterface("[MENU]");

    public static void main(String[] args) {
        if (args.length == 2) {
            run(cliMode(args[0]), cliContent(args[1]));
        } else {
            boolean isClient = requestMode();
            run(isClient, isClient ? requestContent(JFileChooser.FILES_ONLY) : requestContent(JFileChooser.DIRECTORIES_ONLY));
        }
    }

    private static void run(boolean isClient, File file) {
        if (isClient) {
            ui.log("Operation mode -> CLIENT");
            new Client(file);
        } else {
            ui.log("Operation mode -> SERVER");
            new Server(file);
        }
    }

    public static boolean cliMode(String mode) {
        if (mode != null) {
            switch (mode.charAt(0)) {
                case 'C':
                case 'c':
                    return true;
                case 'S':
                case 's':
                    return false;
            }
        }
        return ui.abort("Invalid operation mode");
    }

    public static File cliContent(String path) {
        if (path != null) {
            File file = new File(path);
            if (file.exists()) {
                return file;
            }
        }
        return ui.abort("Invalid path");
    }

    public static boolean requestMode() {
        int response = JOptionPane.showOptionDialog(
            null,
            "Operation Mode",
            Constants.APPLICATION_TITLE,
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            null,
            new String[]{"Client", "Server"},
            -1);
        switch (response) {
            case 0:
                return true;
            case 1:
                return false;
            default:
                return ui.abort("Aborted by the user");
        }
    }

    private static File requestContent(int selectionMode) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(selectionMode);
        chooser.showOpenDialog(null);
        File file = chooser.getSelectedFile();
        if (file != null && file.exists()) {
            return file;
        }
        return ui.abort("Aborted by the user");
    }

}
