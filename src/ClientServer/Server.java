package ClientServer;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Random;
import java.util.Scanner;

public class Server {

    public final static int SERVICE_PORT = 50001;
    static DatagramSocket serverSocket;
    static InetAddress IPAddress;
    static InetAddress clientAddress;
    static int clientPort;
    static boolean isRunning = false;

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        Server.name = name;
    }

    private static String name = "Server";


    private static Operations operations = new Operations();


    public static void main(String[] args) throws IOException, InterruptedException {
        startServer();
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                String[] str = new String[2];
                str[0] = "2";
                while (isRunning){
                    try {
                        str = operations.send(getName() ,clientAddress, clientPort, serverSocket);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if(str[0].equals("1")){
                        setName(str[1]);
                        System.out.println("You new name is: " + getName());
                    }
                    if(str[0].equals("0")){
                        isRunning = false;
                    }
                }
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning){
                    try {
                        operations.receive(serverSocket);
                    } catch (IOException e) {

                    }
                }
            }
        });


        thread1.start();
        thread2.start();

        thread1.join();
        System.out.println("Server is off");
        stopServer();
    }

    public static void startServer() throws IOException {
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
        isRunning = true;
    }


    public static void stopServer(){
        serverSocket.close();
        //System.exit(0);
    }
}
