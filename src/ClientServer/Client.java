package ClientServer;


import java.io.IOException;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.*;
import java.nio.charset.Charset;



public class Client {
    private static String name = "Client";
    private static InetAddress IPAddress;
    private static int serverPort;
    private static boolean isConnected = false;
    private static DatagramSocket clientSocket;

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException, TimeoutException {
        openConnection();
        ReceiveThread receiveThread = new ReceiveThread(isConnected, IPAddress, serverPort, clientSocket, name);
        SendThread sendThread = new SendThread(isConnected, IPAddress, serverPort, clientSocket, name);
        sendThread.start();
        receiveThread.start();
        sendThread.join();
        System.out.println("You have disconnected");
        closeConnection();
    }

    private static void openConnection() throws InterruptedException, ExecutionException, SocketException {
        while (!isConnected) {
            getAddressAndPort();
            clientSocket = new DatagramSocket();
            connection();
            if (!isConnected) {
                System.out.println("The error has occurred. Maybe you have entered wrong address or port. Please try again");
            } else {
                System.out.println("You joined to the chat");
            }
        }
    }

    private static void getAddressAndPort(){
        while(true) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Server IP: ");
            try {
                IPAddress = InetAddress.getByName(scanner.nextLine());
                System.out.println("Server port: ");
                serverPort = scanner.nextInt();
                break;
            } catch (Exception e) {
                System.out.println("The error has occurred. Maybe you have entered wrong address or port. Please try again");
            }
        }
    }

    public static void connection(){
        byte[] sendingDataBuffer = new byte[1024];
        byte[] inputDataBuffer = new byte[1024];
        DatagramPacket sendingPacket = new DatagramPacket(sendingDataBuffer, sendingDataBuffer.length, IPAddress, serverPort);
        DatagramPacket inputPacker = new DatagramPacket(inputDataBuffer, inputDataBuffer.length);

        ExecutorService service = Executors.newSingleThreadExecutor();
        Future future = service.submit(new Runnable() {
            @Override
            public void run() {
                System.out.println("Waiting for connection...");
                try {
                    clientSocket.send(sendingPacket);
                    clientSocket.receive(inputPacker);
                    isConnected = true;
                    return;
                } catch (IOException e) {
                    //throw new RuntimeException(e);
                }
            }
        });
        try {
            future.get(5000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {

        }
    }

    private static void closeConnection() {
        clientSocket.close();
        System.exit(0);
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        Client.name = name;
    }

    public static boolean getIsConnected() {
        return isConnected;
    }
    public static void setIsConnected(boolean isConnected) {
        Client.isConnected = isConnected;
    }
}
