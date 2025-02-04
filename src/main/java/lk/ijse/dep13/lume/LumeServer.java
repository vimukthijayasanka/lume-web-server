package lk.ijse.dep13.lume;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class LumeServer {
    private static Socket localSocket;

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(80);
            System.out.println("Lume Server waiting for connection...");
            while (true) {
                localSocket = serverSocket.accept();
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

    private void readHttpRequest(){
        try {
            InputStream is = localSocket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader reader = new BufferedReader(isr);

            String cmdLine = reader.readLine();
            String[] array = cmdLine.split(" ");
            String command = array[0];
            String resourcePath = array[1];
            System.out.println(command + " " + resourcePath);

            String host = null;
            String line;
            while ((line = reader.readLine()) != null && !line.isBlank()) {
                String header = line.split(":")[0].strip();
                String value = line.substring(line.indexOf(":") + 1).strip();
                if (header == null) return;
                if (header.equalsIgnoreCase("host")) {
                    host = value;
                }
            }
            System.out.println("host name : " + host);
            System.out.println("command : " + command);
            System.out.println("resource path : " + resourcePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
