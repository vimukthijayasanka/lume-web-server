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

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(80);
            System.out.println("Lume Server waiting for connection...");

            while (true) {
                Socket localSocket = serverSocket.accept();
                System.out.println("connection accepted from " + localSocket.getRemoteSocketAddress());

                // handle the clientRequest
                new Thread(() -> handleClient(localSocket)).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


  private static void handleClient(Socket socket){
        try(InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream()){

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String requestLine = reader.readLine();
            if (requestLine == null) return;

            String[] requestParts = requestLine.split(" ");
            if (requestParts.length < 2) return;

            String method = requestParts[0];
            String resourcePath = requestParts[1];

            String host = getHost(reader);
            System.out.println("Host: " + host + " Method: " + method + " ResourcePath: " + resourcePath);

            if (!method.equalsIgnoreCase("GET")){
                sendErrorResponse(os,405, "Method Not Allowed", "Lume Server doesn't support " + method);
                return;
            }
            if (host == null){
                sendErrorResponse(os, 404, "Not Found", "Lume Server cannot find the Host Name");
                return;
            }
            serveResource(os, host, resourcePath);
        } catch (IOException e) {
            System.out.println("Connection error: " + e.getMessage());
        }
  }

    private static String getHost(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null && !line.isBlank()) {
            String[] headerParts = line.split(":",2);
            if (headerParts.length == 2 && headerParts[0].trim().equalsIgnoreCase("host")) {
                return headerParts[1].trim();
            }
        }
        return null;
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
