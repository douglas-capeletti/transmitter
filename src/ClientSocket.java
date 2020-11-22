import java.io.IOException;
import java.net.*;

public class ClientSocket {

    private final Logger log = new Logger("[CLIENT] ");
    private static final int PORT = 3000;
    protected final byte[] buffer = new byte[512];
    private DatagramSocket socket;
    private InetAddress hostIP;

    public ClientSocket() {
        try {
            this.hostIP = InetAddress.getByName("localhost");
            this.socket = new DatagramSocket();
            this.socket.setSoTimeout(500);
        } catch (UnknownHostException | SocketException error) {
            System.err.println("Erro while creating client socket: \n" + error);
            System.exit(-1);
        }
    }

    public void send(byte[] data) {
        try {
            this.socket.send(new DatagramPacket(data, data.length, this.hostIP, PORT));
        } catch (IOException e) {
            log.err("Error while sending packet " + e);
        }
    }

    public byte[] receive(){
        DatagramPacket getAck = new DatagramPacket(this.buffer, this.buffer.length);
        try {
            this.socket.receive(getAck);
        } catch (IOException e) {
            this.log("Error while receiving packet " + e);
        }
        return getAck.getData();
    }

    public void end() {
        byte[] data = "end".getBytes();
        this.send(data);
        this.socket.close();
    }
}

