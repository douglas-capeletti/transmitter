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
        for (int i = 0; i < bandwidth; i++) {
            if (ack == fragments.length) {
                ui.finish("Data sent successfully!");
            }
            int newAck = socket.receive().getId();
            if (newAck == ack) {
                ackCount++;
            } else if (newAck != ack + 1) {
                ackCount = 0;
                timeoutRetransmit(ack + 1);
            } else {
                ack = newAck;
                ackCount = 0;
            }
            if (ackCount > 2) { // fast retransmit
                ackCount = 0;
                retransmit();
            }
        }
    }

    private void retransmit() {
        socket.send(Package.build(ack, fragments[ack]));
        ackCount = 0;
//        bandwidth /= 2; TODO reativar esse cara depois, mexer aqui vai dar problema com o for do receiveAck
    }

    private void timeoutRetransmit(int timeoutPackage){
        bandwidth = 1;
        congestionAvoidance = false;
        int sentPacks = 0;
        while (true){
            for (int i = 0; i < bandwidth; i++){
                socket.send(Package.build(timeoutPackage + sentPacks, fragments[timeoutPackage + sentPacks]));
                sentPacks++;
            }
            receiveAck();
            bandwidthHandler();
            if(timeoutPackage + sentPacks + bandwidth > sequenceNumber) {
                break;
            }
        }
        byte[][] packages = fileManager.readNPackets(bandwidth - (sequenceNumber - timeoutPackage - sentPacks), Constants.DATA_SIZE);
        while (timeoutPackage + sentPacks != sequenceNumber) {
            socket.send(Package.build(timeoutPackage + sentPacks, fragments[timeoutPackage + sentPacks]));
            sentPacks++;
        }
        for (byte[] data : packages) {
            fragments[sequenceNumber] = data;
            socket.send(Package.build(sequenceNumber, data));
            sequenceNumber++;
        }
        receiveAck();
        send();
    }

}
