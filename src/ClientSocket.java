import java.io.IOException;
import java.net.*;

public class ClientSocket {

    private final Logger log;
    private final byte[] buffer;
    private DatagramSocket socket;
    private InetAddress hostIP;

    public ClientSocket(Logger log) {
        this.log = log;
        this.buffer = new byte[Constants.OFFSET_SIZE];
        try {
            this.hostIP = InetAddress.getByName("localhost");
            this.socket = new DatagramSocket(Constants.CLIENT_PORT);
//            this.socket.setSoTimeout(500);
        } catch (UnknownHostException | SocketException error) {
            log.error("Erro while creating client socket", error);
            System.exit(-1);
        }
    }

    public void send(Package pack) {
        byte[] data = pack.getData();
        try {
            socket.send(new DatagramPacket(data, data.length, hostIP, Constants.SERVER_PORT));
            log.info("Sending sequence: " + pack.getId());
        } catch (IOException e) {
            log.error("Error while sending packet", e);
        }
    }

    public Package receive() {
        DatagramPacket getAck = new DatagramPacket(buffer, buffer.length);
        try {
            socket.receive(getAck);
        } catch (IOException e) {
            log.error("Error while receiving packet ", e);
        }
        Package pack = Package.decomposeACK(getAck.getData());
        log.info("Receiving ACK: " + pack.getId());
        return pack;
    }
}

