package ClientServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.locks.Lock;

public class ReceiveThread extends Thread {
    boolean isConnected;
    InetAddress IPAddress;
    int port;
    DatagramSocket socket;

    public ReceiveThread(boolean isConnected, InetAddress IPAddress, int port, DatagramSocket socket, String name) {
        this.isConnected = isConnected;
        this.IPAddress = IPAddress;
        this.port = port;
        this.socket = socket;
    }


    public void run() {
        while (isConnected) {
            try {
                receive(socket);
            } catch (IOException e) {

            }
        }
    }


    public void receive(DatagramSocket socket) throws IOException {
        while (isConnected){
        byte[] buf = new byte[1024];
        DatagramPacket inputPacket = new DatagramPacket(buf, buf.length);
        socket.receive(inputPacket);
        String receivedData = new String(inputPacket.getData(), StandardCharsets.UTF_8);
        System.out.println(massageDecoding(receivedData));
        }
    }

    public String massageDecoding(String massage) {
        StringBuilder decodedMassage = new StringBuilder();
        int idx = 0;
        String str = new String();
        while (massage.charAt(idx) != '/') {
            str += massage.charAt(idx);
            idx++;
        }
        idx++;
        int massageLength = Integer.decode(str);
        for (int i = idx; i < massageLength + idx; i++) {
            decodedMassage.append(massage.charAt(i));
        }
        return String.valueOf(decodedMassage);
    }

    public boolean getConnection(){
        return true;
    }

    public String getUserName(){
        return null;
    }


}
