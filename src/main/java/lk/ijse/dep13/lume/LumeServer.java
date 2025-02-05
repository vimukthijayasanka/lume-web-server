package lk.ijse.dep13.lume;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

public class LumeServer {
    private static Socket localSocket;
    private static String command;
    private static String host;

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(80);
            System.out.println("Lume Server waiting for connection...");
            while (true) {
                localSocket = serverSocket.accept();
                System.out.println("connection accepted from " + localSocket.getRemoteSocketAddress());

                new Thread(() -> {
                    try{
                        readHttpRequest();
                        writeHttpResponse();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private static void readHttpRequest(){
        try {
            InputStream is = localSocket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader reader = new BufferedReader(isr);

            String cmdLine = reader.readLine();
            String[] array = cmdLine.split(" ");
            command = array[0];
            String resourcePath = array[1];
            System.out.println(command + " " + resourcePath);

            host = null;
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

    private static void writeHttpResponse(){
        OutputStream os = null;
        try {
             os = localSocket.getOutputStream();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static void serveResource(OutputStream os, String host, String resourcePath) throws IOException {
        Path path = resourcePath.equals("/") ? Paths.get(host, "index.html") : Paths.get(host, resourcePath);

        if (!Files.exists(path)) {
            sendErrorResponse(os,404,"Resource Not Found", "Lume Server doesn't have request content");
            return;
        }

        String contentType = Files.probeContentType(path);
        sendResponseHeader(os,200, "OK", contentType);

        try(FileChannel fileChannel = FileChannel.open(path)){
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            while (fileChannel.read(buffer) != -1) {
                buffer.flip();
                os.write(buffer.array(), 0, buffer.limit());
                buffer.clear();
            }
        }
        os.flush();
    }

    private static void sendResponseHeader(OutputStream os, int statusCode, String statusMessage, String contentType) throws IOException {
        String response = """
                HTTP/1.1 %d %s
                Server: Lume Server
                Date: %s
                content-type: %s
                
                """.formatted(statusCode, statusMessage, LocalDateTime.now(),contentType);
        os.write(response.getBytes());
        os.flush();
    }

    private static void sendErrorResponse(OutputStream os, int statusCode, String statusMessage, String errorMessage) throws IOException {
        sendResponseHeader(os, statusCode, statusMessage, "text/html");
        String responseBody = """
                <!DOCTYPE html>
                <html>
                <head><title>Lume Server | %d %s</title></head>
                <body><h1>%s</h1></body>
                </html>
                """.formatted(statusCode, statusMessage, errorMessage);
        os.write(responseBody.getBytes());
        os.flush();
    }
}
