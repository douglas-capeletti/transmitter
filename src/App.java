import javax.swing.*;
import java.io.File;

public class App {

    private static final Logger log = new Logger("[MENU] ");

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
            log.info("Modo de operação -> CLIENT");
            new Client(file);
        } else {
            log.info("Modo de operação -> SERVER");
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
        log.error("Modo de operação inválido");
        System.exit(-1);
        return false;
    }

    public static File cliContent(String path) {
        if (path != null) {
            File file = new File(path);
            if (file.exists()) {
                return file;
            }
        }
        log.error("Caminho inválido");
        System.exit(-1);
        return null;
    }

    public static boolean requestMode() {
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
