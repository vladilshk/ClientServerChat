package ClientServer;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Server {
    private static DatagramSocket serverSocket;
    private static InetAddress IPAddress;
    private static InetAddress clientAddress;
    private static int clientPort;

    private static boolean isConnected = false;

    private static String name = "Server";

    public static void main(String[] args) throws IOException, InterruptedException {
        startServer();

        SendThread sendThread = new SendThread(isConnected, clientAddress, clientPort, serverSocket, getName());
        ReceiveThread receiveThread = new ReceiveThread(isConnected, clientAddress, clientPort, serverSocket, getName());
        sendThread.start();
        receiveThread.start();
        sendThread.join();
        System.out.println("Server is off");
        stopServer();
    }

    private static void startServer() throws IOException {
        System.out.println("Choose a port for listening: ");
        Scanner scanner = new Scanner(System.in);
        serverSocket = new DatagramSocket(scanner.nextInt());
        IPAddress = InetAddress.getByName("localhost");

        byte[] inputDataBuffer = new byte[1024];
        DatagramPacket inputPacket = new DatagramPacket(inputDataBuffer, inputDataBuffer.length);
        System.out.println("Waiting for a client to connect...");
        serverSocket.receive(inputPacket);
        clientPort = inputPacket.getPort();
        clientAddress = inputPacket.getAddress();
        DatagramPacket outputPacket = new DatagramPacket(inputDataBuffer, inputDataBuffer.length, clientAddress, clientPort);
        serverSocket.send(outputPacket);
        System.out.println("New member" + clientAddress.toString() + " " + clientPort);
        isConnected = true;
    }


    private static void stopServer(){
        serverSocket.close();
        System.exit(0);
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        Server.name = name;
    }

    public static boolean getIsConnected() {
        return isConnected;
    }

    public static void setIsConnected(boolean isConnected) {
        Server.isConnected = isConnected;
    }
}
