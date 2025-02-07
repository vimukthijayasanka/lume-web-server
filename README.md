# Lume Web Server 🚀

## 🌟 Overview
The **Lume Web Server** is a simple HTTP server implemented in Java using sockets. It demonstrates the fundamental concepts of **client-server architecture**, particularly how to handle **HTTP requests** and serve static files.

## 🔥 Features
✅ Accepts incoming HTTP connections on **port 80**.  
✅ Handles **GET requests**.  
✅ Serves **static HTML files**.  
✅ Returns appropriate **HTTP response codes**.  
✅ Uses **multithreading** to handle multiple clients concurrently.

## 🛠 Technologies Used
- **Java 22+** ☕
- **Java Sockets API** 🔌
- **Java NIO (FileChannel, ByteBuffer)** 📂
- **Multithreading** 🧵

## ⚠️ Important Notes Before Running
- Since the server runs on **port 80**, ensure that no other local web server (e.g., Apache, Nginx) is using the port.
- Stop any running web server using:
  ```sh
  sudo systemctl stop apache2  # For Apache
  sudo systemctl stop nginx    # For Nginx
  ```
- Run the project with **admin privileges**:
  ```sh
  sudo idea .  # Open IntelliJ as root (Linux)
  ```

## 🌍 Setting Up Local DNS (Optional)
To access your server with a custom domain locally, edit the **hosts file**:
```sh
sudo nano /etc/hosts  # Linux/macOS
notepad C:\\Windows\\System32\\drivers\\etc\\hosts  # Windows
```
Add a line:
```
127.0.0.1    machan.lk
127.0.0.1    google.lume
```
Then use `nslookup` to verify:
```sh
nslookup mylocalserver.dev
```

## 📥 Installation
1. Clone the repository:
   ```sh
   git clone https://github.com/vimukthijayasanka/lume-web-server
   cd lume-web-server
   ```
2. Compile the project:
   ```sh
   javac -d out src/lk/ijse/dep13/lume/LumeServer.java
   ```
3. Run the server:
   ```sh
   sudo java -cp out lk.ijse.dep13.lume.LumeServer  # Run as admin
   ```

## ⚙️ How It Works
1. **Server Initialization**
    - Starts listening on port **80**.
    - Waits for a client to connect.
    - Spawns a **new thread** for each incoming request.

2. **Handling HTTP Requests**
    - Reads the HTTP **request line** (e.g., `GET /index.html HTTP/1.1`).
    - Parses the **HTTP method**, **requested resource**, and **host**.
    - Validates if the request is properly formatted.
    - Checks if the requested file **exists** in the server's directory.

3. **Generating HTTP Responses**
    - If the request is invalid, returns:
        - `405 Method Not Allowed` (for non-GET requests)
        - `404 Not Found` (for missing resources)
    - If the file exists, serves the content with:
        - `200 OK` status
        - Correct **MIME type** using `Files.probeContentType(path)`
    - Sends the **file data** using **FileChannel & ByteBuffer**.

## 📜 Example Request & Response
### 📨 Client Request
```
GET /index.html HTTP/1.1
Host: google.lume
```

### 📤 Server Response (Success)
```
HTTP/1.1 200 OK
Server: lume-server
Date: Mon, 04 Feb 2025 12:00:00 GMT
Content-Type: text/html

<html>
<head><title>Welcome</title></head>
<body><h1>Hello from Lume Server!</h1></body>
</html>
```

## 🚀 TODO & Future Enhancements
- Implement **POST, PUT, DELETE** methods.
- Add **logging** for request handling.
- Implement **HTTPS support**.
- Support **dynamic content rendering**.

## 🤝 Contributing
Pull requests are welcome. Please follow the standard GitHub **fork and PR workflow**.

## 🏷️ Version

1.0.1

## 📜 License
This project is open-source and licensed under the [**License**](https://github.com/vimukthijayasanka/lume-web-server/blob/main/license.txt).

