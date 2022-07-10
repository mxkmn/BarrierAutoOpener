package barrierautoopener;

import java.net.*;
import java.io.*;
import java.time.LocalDateTime;

public class WebServer extends Thread {
	ServerSocket _listenSocket;
	LocalDateTime _lastFoundDateTime = LocalDateTime.of(1970, 1, 1, 0, 0, 0);
	final int serverPort = 6880; // port to listen on, you can use any available port

	public WebServer() throws IOException {
		_listenSocket = new ServerSocket(serverPort);
		System.out.println("Server listening on port " + serverPort);
	}

	public void setLastFoundDateTime(LocalDateTime lastFoundDateTime) {
		this._lastFoundDateTime = lastFoundDateTime;
	}

	public void run() {
		while (true) {
			try {
				Socket clientSocket = _listenSocket.accept();
				InputStream input = clientSocket.getInputStream();

				BufferedReader br = new BufferedReader(new InputStreamReader(input));

				try {
					PrintWriter output = new PrintWriter(clientSocket.getOutputStream());
					output.write("HTTP/1.1 200 OK\nContent-Type: application/xml\n\n" + getInfo());
					output.close();
				} catch (IOException e) {
					System.out.println("Connection: " + e.getMessage());
				}

				br.close();
			} catch (IOException e) {
				System.out.println("Listen :" + e.getMessage());
			}
		}
	}

	private String getInfo() {
		boolean isFoundRecently = LocalDateTime.now().minusSeconds(5).isBefore(_lastFoundDateTime);
		return (isFoundRecently ? "Open" : "Close") + "\n";
	}
}