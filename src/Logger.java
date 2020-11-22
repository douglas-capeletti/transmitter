public class Logger {

    public final String prefix;

    public Logger(String prefix) {
        this.prefix = prefix;
    }

    public void info(String message){
        System.out.println(prefix + message);
    }
    public void err(String message){
        System.err.println(prefix + message);
    }
}
