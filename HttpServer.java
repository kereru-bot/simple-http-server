import java.net.*;
import java.io.*;
import java.util.*;
import java.util.Random;

/**
 * Hosts an http server and can take get requests
 */
class HttpServer
{
    public static void main(String args[])
    {
	Random rand = new Random();
	ServerSocket servSocket;

	System.out.println("Web server starting...");
	
	try {
	    //i couldn't pick a number
	    int port = rand.nextInt(50000,65535);
	    servSocket = new ServerSocket(port);
	    System.out.println("Listening on port " + servSocket.getLocalPort());
	}
	catch(IOException ex) {
	    System.err.println("IOException: " + ex);
	    return;
	}

	while(true) {
	    try {
		Socket socket = servSocket.accept();
		HttpServerSession session = new HttpServerSession(socket);
		session.start();
		System.out.println("Connection received from: " + socket.getInetAddress().getHostAddress());
	    }
	    catch(IOException ex) {
		System.err.println("IOException: " + ex);
	    }
	}
    }
}
