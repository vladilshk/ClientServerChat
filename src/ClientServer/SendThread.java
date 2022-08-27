package ClientServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class SendThread extends Thread {
    InetAddress IPAddress;
    int port;
    DatagramSocket socket;

    final String name;

    public SendThread(boolean isConnected, InetAddress IPAddress, int port, DatagramSocket socket, String name) {
        this.IPAddress = IPAddress;
        this.port = port;
        this.socket = socket;
        this.name = name;
    }

    public void run() {
        startMassage();
        while (getConnection()) {
            String[] str = new String[2];
            while (getConnection()) {
                try {
                    str = send(getUserName(), IPAddress, port, socket);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (str[0].equals("1")) {
                    setUserName(str[1]);
                    System.out.println("You new name is: " + getUserName());
                }
                if (str[0].equals("0")) {
                     setConnection(false);
                }
            }
        }
    }

    public  String[] send(String name, InetAddress senderAddress, int senderPort, DatagramSocket socket) throws IOException {
        String massage = massageInput(name);
        String[] strings = new String[2];

        if (massage.matches("@quit.*")) {
            strings[0] = "0";
            notifyUser( getUserName() + " was disconnected.", senderAddress, senderPort, socket);
            return strings;
        } else if (massage.matches("@name.+")) {
            strings[0] = "1";
            strings[1] = getNameFromMassage(massage);
            notifyUser(getUserName() + " changed his/her name to " + strings[1], senderAddress, senderPort, socket);
            return strings;

        } else if (massage.matches("@.*")) {
            System.out.println("Wrong command, try again");
            strings[0] = "2";
            return strings;
        } else {
            massage = getUserName() + ": " + massage;
            massage = massage.length() + "/" + massage;
            DatagramPacket outputPacket = new DatagramPacket(
                    massage.getBytes(StandardCharsets.UTF_8), massage.getBytes().length,
                    senderAddress, senderPort
            );
            socket.send(outputPacket);
            strings[0] = "2";
            return strings;
        }
    }

    public String getNameFromMassage(String massage) {
        StringBuilder name = new StringBuilder();
        for (int i = 6; i < massage.length(); i++) {
            name.append(massage.charAt(i));
        }
        return String.valueOf(name);

    }

    public String massageInput(String name){
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

    public void notifyUser(String massage, InetAddress senderAddress, int senderPort, DatagramSocket socket) throws IOException {
        massage = massage.length() + "/" + massage;
        DatagramPacket outputPacket = new DatagramPacket(
                massage.getBytes(), massage.getBytes().length,
                senderAddress, senderPort
        );
        socket.send(outputPacket);
    }

    public boolean getConnection(){
        if(name.matches("Client"))
            return Client.getIsConnected();
        else
            return Server.getIsConnected();

    }

    public void setConnection(boolean connection){
        if(name.matches("Client"))
            Client.setIsConnected(connection);
        else
            Server.setIsConnected(connection);
    }

    public String getUserName(){
        if(name.matches("Client"))
            return Client.getName();
        else
            return Server.getName();
    }

    public void setUserName(String name){
        if(this.name.matches("Client"))
            Client.setName(name);
        else
            Server.setName(name);
    }

    public void startMassage(){
        System.out.println("\n1. Set the user name (@name Vasya)");
        System.out.println("2. Send a text message (Hello)");
        System.out.println("3. Exit (@quit)\n");
    }



}
