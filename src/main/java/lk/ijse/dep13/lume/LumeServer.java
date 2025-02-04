package lk.ijse.dep13.lume;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class LumeServer {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(80);
            System.out.println("Lume Server waiting for connection...");
            while (true) {
                Socket localSocket = serverSocket.accept();
                System.out.println("connection accepted from " + localSocket.getRemoteSocketAddress());

                new Thread(() -> {
                    try{
                        
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
