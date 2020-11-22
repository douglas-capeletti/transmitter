import java.io.File;

public class Server {

    private final Logger log;
    private final FileManager fileManager;
    private final ServerSocket socket;
    private int ack;
    private byte[][] data;
    private boolean[] ackBuffer;

    public Server(File file) {
        this.log = new Logger("[SERVER] ");
        this.fileManager = new FileManager(file);
        this.socket = new ServerSocket(log);
        this.ack = 0;
        listen();
    }

    public void listen() {
        while (true) {
            Package pack = socket.receive();
            if (pack.isFirst()) {
                fileManager.setOutputFilename(pack.getFileName());
                data = new byte[pack.getTotalPackages()][Constants.BUFFER_SIZE];
                ackBuffer = new boolean[pack.getTotalPackages()];
            } else {
                sendAck(pack.getId());
                if (pack.getId() == ackBuffer.length - 1) {
                    finish();
                } else {
                    data[pack.getId()] = pack.getData();
                }
            }
        }
    }

    private void sendAck(int sequenceNumber) {
        ackBuffer[sequenceNumber] = true;
        if (sequenceNumber == ack) {
            ack = getNextAck();
        }
        socket.send(Package.buildACK(ack));
    }

    private int getNextAck() {
        for (int i = ack; i < ackBuffer.length; i++) {
            if (!ackBuffer[i]) {
                return i;
            }
        }
        return ackBuffer.length;
    }

    public void finish() {
        fileManager.initWriter().writePackages(data);
        log.separator().info("Dados salvos com sucesso!");
        System.exit(0);
    }
}
