import javax.swing.*;
import java.io.File;

public class App {

    private static final Logger log = new Logger("[MENU] ");

    public static void main(String[] args) {
        boolean isClient = requestMode();
        if (isClient) {
            log.info("Modo de operação -> CLIENT");
//            new Client(requestContent(JFileChooser.FILES_ONLY));
            new Client(new File("/home/douglas-paz/personal/tests/source/file"));
        } else {
            log.info("Modo de operação -> SERVER");
//            new Server(requestContent(JFileChooser.DIRECTORIES_ONLY));
            new Server(new File("/home/douglas-paz/personal/tests/destination"));
        }
    }

    /*
     * True  - Client
     * False - Server
     */
    public static Boolean requestMode() {
        int response = JOptionPane.showOptionDialog(
            null,
            "Escolha o modo de execução",
            "Transmissor",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            null,
            new String[]{"Cliente", "Servidor"},
            -1);
        switch (response) {
            case 0:
                return true;
            case 1:
                return false;
            default:
                return Exit();
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
        return Exit();
    }

    private static <T> T Exit() {
        log.error("Programa abortado pelo usuário");
        System.exit(9);
        return null;
    }
}
