import java.io.IOException;
import java.net.*;

public class ServerSocket {

    private final Logger log = new Logger("[SERVER] ");
    protected final byte[] buffer = new byte[512];
    private final DatagramSocket socket;

    public ServerSocket() {
        try {
            this.socket = new DatagramSocket(9876);
            this.receive();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private void receive() throws IOException {
        DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
        this.socket.receive(receivePacket);
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Server started at port: 9876");

        byte[] receiveData = new byte[100000];
        while (true) {


            String sentence = new String(receivePacket.getData());
            System.out.println("Mensagem recebida: " + sentence);
        }
    }

}

