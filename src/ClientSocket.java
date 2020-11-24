import java.io.IOException;
import java.net.*;

public class ClientSocket {

    private final UserInterface ui;
    private final byte[] buffer;
    private DatagramSocket socket;
    private InetAddress hostIP;

    public ClientSocket(UserInterface ui) {
        this.ui = ui;
        this.buffer = new byte[Constants.OFFSET_SIZE];
        try {
            this.hostIP = InetAddress.getByName("localhost");
            this.socket = new DatagramSocket(Constants.CLIENT_PORT);
//            this.socket.setSoTimeout(500);
        } catch (UnknownHostException | SocketException error) {
            ui.abort("Error while creating client socket");
        }
    }

    public void send(Package pack) {
        byte[] data = pack.getData();
        try {
            socket.send(new DatagramPacket(data, data.length, hostIP, Constants.SERVER_PORT));
            ui.log("Sending sequence: " + pack.getId());
        } catch (IOException e) {
            ui.abort("Error while sending packet");
        }
    }

    public Package receive() {
        DatagramPacket getAck = new DatagramPacket(buffer, buffer.length);
        try {
            socket.receive(getAck);
        } catch (IOException e) {
            ui.abort("Error while receiving packet");
        }
        Package pack = Package.decomposeACK(getAck.getData());
        ui.log("Receiving ACK: " + pack.getId());
        return pack;
    }
}

