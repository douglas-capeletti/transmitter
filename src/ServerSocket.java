import java.io.IOException;
import java.net.*;

public class ServerSocket {

    private final UserInterface ui;
    private final byte[] buffer;
    private DatagramSocket socket;
    private InetAddress hostIP;

    public ServerSocket(UserInterface ui) {
        this.ui = ui;
        this.buffer = new byte[Constants.BUFFER_SIZE];
        try {
            this.hostIP = InetAddress.getByName("localhost");
            this.socket = new DatagramSocket(Constants.SERVER_PORT);
            //this.socket.setSoTimeout(3000);
        } catch (SocketException | UnknownHostException e) {
            ui.abort("Error while creating server socket");
        }
    }

    public Package receive() {
        DatagramPacket datagram = new DatagramPacket(buffer, buffer.length);
        try {
            socket.receive(datagram);
        } catch (IOException e) {
            ui.abort("Error while receiving packet");
        }
        Package pack = Package.decompose(datagram.getData());
        ui.log("Receiving sequence: " + pack.getId());
        return pack;
    }

    public void send(Package pack) {
        byte[] data = pack.getData();
        try {
            socket.send(new DatagramPacket(data, data.length, hostIP, Constants.CLIENT_PORT));
            ui.log("Sending ACK: " + pack.getId());
        } catch (IOException e) {
            ui.abort("Error while sending packet");
        }
    }

}

