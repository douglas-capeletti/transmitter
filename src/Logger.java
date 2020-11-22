public class Logger {

    private final String prefix;

    public Logger(String prefix) {
        this.prefix = prefix;
    }

    public Logger separator(){
        System.out.println();
        return this;
    }

    public void info(String message) {
        System.out.println(prefix + message);
    }

    public void error(String message, Object stackTrace) {
        error(message);
        System.err.println(stackTrace);
    }

    public void error(String message) {
        System.err.println(prefix + message);
    }
}
