import java.io.IOException;
import java.net.*;

public class ServerSocket {

    private final Logger log;
    private final byte[] buffer;
    private DatagramSocket socket;
    private InetAddress hostIP;

    public ServerSocket(Logger log) {
        this.log = log;
        this.buffer = new byte[Constants.BUFFER_SIZE];
        try {
            this.hostIP = InetAddress.getByName("localhost");
            this.socket = new DatagramSocket(Constants.SERVER_PORT);
        } catch (SocketException | UnknownHostException e) {
            log.error("Erro na inicialização do socket servidor", e);
            System.exit(-1);
        }
    }

    public Package receive() {
        DatagramPacket datagram = new DatagramPacket(buffer, buffer.length);
        try {
            socket.receive(datagram);
        } catch (IOException e) {
            log.error("Erro ao receber pacote", e);
        }
        Package pack = Package.decompose(datagram.getData());
        log.info("Receiving sequence: " + pack.getId());
        return pack;
    }

    public void send(Package pack) {
        byte[] data = pack.getData();
        try {
            socket.send(new DatagramPacket(data, data.length, hostIP, Constants.CLIENT_PORT));
            log.info("Sending ACK: " + pack.getId());
        } catch (IOException e) {
            log.error("Erro ao enviar pacote", e);
        }
    }

}

