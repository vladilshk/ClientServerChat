package ClientServer;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Operations {
    public final static int SERVICE_PORT = 50001;
    static Lock lock1 = new ReentrantLock();
    static Lock lock2 = new ReentrantLock();

    public static String[] send(String name, InetAddress senderAddress, int senderPort, DatagramSocket socket) throws IOException {
        lock1.lock();
        String massage = massageInput(name);
        String[] strings = new String[2];

        if (massage.matches("@quit.*")) {
            strings[0] = "0";
            notifyUser(name + " was disconnected.", senderAddress, senderPort, socket);
            lock1.unlock();
            return strings;
        } else if (massage.matches("@name.+")) {
            strings[0] = "1";
            strings[1] = getNameFromMassage(massage);
            notifyUser(name + " changed his/her name to " + strings[1], senderAddress, senderPort, socket);
            lock1.unlock();
            return strings;
        } else {
            massage = name + ": " + massage;
            massage = massage.length() + "/" + massage;
            DatagramPacket outputPacket = new DatagramPacket(
                    massage.getBytes(), massage.getBytes().length,
                    senderAddress, senderPort
            );
            socket.send(outputPacket);
            strings[0] = "2";
            lock1.unlock();
            return strings;
        }
    }

    public static void receive(DatagramSocket socket) throws IOException {
        lock2.lock();
        byte[] buf = new byte[1024];
        DatagramPacket inputPacket = new DatagramPacket(buf, buf.length);
        socket.receive(inputPacket);
        String receivedData = new String(inputPacket.getData(), StandardCharsets.UTF_8);
        System.out.println(massageDecoding(receivedData));

        lock2.unlock();
    }

    public static String getNameFromMassage(String massage) {
        StringBuilder name = new StringBuilder();
        for (int i = 6; i < massage.length(); i++) {
            name.append(massage.charAt(i));
        }
        return String.valueOf(name);

    }

    public static String massageInput(String name){
        String massage;
        do{
            Scanner scanner = new Scanner(System.in);
            massage = scanner.nextLine();
            if(massage.length() + name.length() + 2 > 512) {
                System.out.println("Your massage is too long, it shouldn't be longer then 512 simbols");
            }
        }
        while (massage.length() + name.length() + 2 > 512);
        return massage;
    }

    public static String massageDecoding(String massage) {
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

    public static void notifyUser(String massage, InetAddress senderAddress, int senderPort, DatagramSocket socket) throws IOException {
        massage = massage.length() + "/" + massage;
        DatagramPacket outputPacket = new DatagramPacket(
                massage.getBytes(), massage.getBytes().length,
                senderAddress, senderPort
        );
        socket.send(outputPacket);
    }


}
