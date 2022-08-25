package ClientServer;

import java.io.IOException;
import java.net.*;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Client {

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        Client.name = name;
    }

    private static String name = "Client";
    private static InetAddress IPAddress;
    private static int serverPort;
    private static boolean isConnected = false;

    private static DatagramSocket clientSocket;

    //private static InetAddress IPAddress;
    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException, TimeoutException {
        openConnection();
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                String[] str = new String[2];
                while (isConnected) {
                    try {
                        str = Operations.send(getName(), IPAddress, serverPort, clientSocket);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if (str[0].equals("1")) {
                        setName(str[1]);
                        System.out.println("You new name is: " + getName());
                    }
                    if (str[0].equals("0")) {
                        isConnected = false;
                    }
                }
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isConnected) {
                    try {
                        Operations.receive(clientSocket);
                    } catch (IOException e) {

                    }
                }
            }
        });


        thread1.start();
        thread2.start();
        thread1.join();
        System.out.println("You had disconnected");
        closeConnection();
    }

    public static void openConnection() throws InterruptedException, ExecutionException, SocketException {
        while (!isConnected) {
            getAddressAndPort();
            clientSocket = new DatagramSocket(59508);
            connection();
            if (!isConnected) {
                System.out.println("The error has occurred. Maybe you have entered wrong address or port. Please try again");
            } else {
                System.out.println("You joined to the chat");
            }
        }
    }

    public static void getAddressAndPort(){
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

    public static void closeConnection() {
        clientSocket.close();
        //System.exit(0);
    }
}
