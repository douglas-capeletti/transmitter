import java.io.File;

public class Server {

    private final UserInterface ui;
    private final FileManager fileManager;
    private final ServerSocket socket;
    private int ack;
    private byte[][] data;
    private boolean[] ackBuffer;

    public Server(File file) {
        this.ui = new UserInterface("[SERVER]");
        this.fileManager = new FileManager(file);
        this.socket = new ServerSocket(ui);
        this.ack = 0;
        listen();
    }

    public void listen() {
        while (true) {
            Package pack = socket.receive();
            if (pack.isFirst()) {
                fileManager.setOutputFilename(pack.getFileName());
                fileManager.setMd5Hash(pack.getMd5Hash());
                data = new byte[pack.getTotalPackages()][Constants.BUFFER_SIZE];
                ackBuffer = new boolean[pack.getTotalPackages()];
            } else {
                pack.validate();
                sendAck(pack.getId());
                data[pack.getId()] = pack.getData();
                if (pack.getId() == (ackBuffer.length - 1)) {
                    finish();
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
        fileManager.initWriter().writeAndValidate(data);
        ui.finish("Data saved successfully!");
    }
}
