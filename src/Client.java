import java.io.File;

public class Client {

    private final UserInterface ui;
    private final FileManager fileManager;
    private final ClientSocket socket;
    private int ack;
    private int ackCount;
    private int sequenceNumber;
    private int bandwidth;
    private boolean congestionAvoidance;
    private byte[][] fragments;

    public Client(File file) {
        this.ui = new UserInterface("[CLIENT]");
        this.fileManager = new FileManager(file).initReader();
        this.socket = new ClientSocket(ui);
        this.bandwidth = 1;
        sendFirst(file);
        send();
    }

    private void sendFirst(File file) {
        Package pack = Package.buildFirst(file);
        fragments = new byte[pack.getTotalPackages()][Constants.BUFFER_SIZE];
        socket.send(pack);
    }

    public void send() {
        while (true) {
            sendPackages();
            receiveAck();
            bandwidthHandler();
        }
    }

    private void sendPackages() {
        byte[][] packages = fileManager.readNPackets(bandwidth, Constants.DATA_SIZE);
        for (byte[] data : packages) {
            fragments[sequenceNumber] = data;
            socket.send(Package.build(sequenceNumber, data));
            sequenceNumber++;
        }
    }

    private void bandwidthHandler() {
        if (bandwidth == Constants.SLOW_START_LIMIT) {
            congestionAvoidance = true;
        }
        if (congestionAvoidance) {
            bandwidth++;
        } else {
            bandwidth *= 2;
        }
    }

    private void receiveAck() {
        for (int i = 0; i < bandwidth; i++){
            if(ack == fragments.length){
                ui.finish("Data sent successfully!");
            }
            int newAck = socket.receive().getId();
            if (newAck == ack) {
                ackCount++;
            } else {
                ack = newAck;
                ackCount = 0;
            }
            if (ackCount > 2) {
                retransmit();
            }
        }
    }

    private void retransmit() {
        socket.send(Package.build(ack, fragments[ack]));
        ackCount = 0;
//        bandwidth /= 2; TODO reativar esse cara depois, mexer aqui vai dar problema com o for do receiveAck
    }

//    //TODO arrumar essa porra
//    private void timeoutFixer(int lossFragment) {
//        this.bandwidth = 1;
//        this.fragments = Arrays.copyOfRange(fragments, lossFragment, fragments.size() - 1);
//        do {
//            if (fragments.size() < bandwidth) {
//                concat(bandwidth - fragments.size());
//            }
//            for (int i = 0; i < bandwidth; i++) {
//                this.send(Package.build(sequenceNumber, fragments[i]));
//            }
//            this.fragments = Arrays.copyOfRange(fragments, bandwidth, fragments.size() - 1);
//            jumpValidator();
//        } while (fragments.size() >= bandwidth);
//        sendPackages();
//    }
//
//    private void concat(int quantity) {
//        fragments = Arrays.copyOf(fragments, bandwidth);
//        System.arraycopy(fileManager.readNPackets(quantity, buffer.length), 0, fragments, fragments.size() - quantity, quantity);
//    }
}
