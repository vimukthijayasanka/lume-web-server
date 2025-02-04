package lk.ijse.dep13.lume;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;

public class LumeServer {
    private static Socket localSocket;
    private static String command;

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
            command = array[0];
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

    private void writeHttpResponse(){
        OutputStream os = null;
        try {
             os = localSocket.getOutputStream();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        if (!command.equalsIgnoreCase("GET")){
            String response = """
                    HTTP/1.1 405 Method Not Allowed
                    server: Lume Server
                    Date: %s
                    content-type: text/html
                    
                    """.formatted(LocalDateTime.now());
            try {
                os.write(response.getBytes());
                os.flush();
                String responseBody = """
                        <!DOCTYPE html>
                        <html>
                        <head>
                        <meta charset="UTF-8">
                        <title>Dep Server | 404 Method Not allowed</title>
                        </head>
                        <body>
                        <h1>Dep Server doesn't support %s method</h1>
                        </body>
                        </html>
                        """.formatted(command);
                os.write(responseBody.getBytes());
                os.flush();
                os.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
