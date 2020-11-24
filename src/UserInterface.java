import javax.swing.*;

public class UserInterface {

    private final String prefix;

    public UserInterface(String prefix) {
        this.prefix = prefix + " - ";
    }

    public void log(String message) {
        System.out.println(prefix + message);
    }

    public void finish(String message) {
        System.out.println("\n" + message);
        showMessage(message, 0, JOptionPane.INFORMATION_MESSAGE);
    }

    public <T> T abort(String message) {
        System.err.println("\n" + message);
        return showMessage(message, -1, JOptionPane.ERROR_MESSAGE);
    }

    private <T> T showMessage(String message, int code, int messageType) {
        JOptionPane.showMessageDialog(null, message, Constants.APPLICATION_TITLE, messageType);
        System.exit(code);
        return null;
    }
}
