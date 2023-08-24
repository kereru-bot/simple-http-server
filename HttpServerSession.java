import java.io.*;
import java.net.*;
import java.util.*;

/**
 * An http session that can send files from the server to another client
 */
class HttpServerSession extends Thread {
    private Socket socket;
    private BufferedReader reader;
    private BufferedOutputStream output;
    private HttpServerRequest request;

    /**
     * Creates an HttpServerSession using the given socket
     * @param socket The socket that's to be used to communication, must not be null
     */
    public HttpServerSession(Socket socket) {
	setSocket(socket);
    }

    /**
     * Sets the socket for this session
     * @param socket The socket to be used for communication, must not be null
     */
    public void setSocket(Socket socket) {
	if(socket != null) {
	    this.socket = socket;
	    return;
	}
	
	System.err.println("Please provide a non-null socket");
	return;
    }

    //entry point for the session
    public void run() {
	try {	    
	    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	    output = new BufferedOutputStream(socket.getOutputStream());
	    request = new HttpServerRequest();
	    
	    String input = null;

	    //process the http request
	    while(!request.isDone()) {
		input = reader.readLine();
		request.process(input);
	    }

	    if(request.getFile() == null) {
		System.err.println("No file has been requested");
		socket.close();
	    }
	    
	    sendFile(request.getFile(), output);
	    
	    //remember to actually flush the output
	    output.flush();
	    
	    socket.close();
	}
	catch(IOException ex) {
	    System.err.println("IOException: " + ex);
	    socket.close();
	}
    }

    /**
     * Writes the provided string to the provided stream and complies with http
     * @param bos The output stream that will be written to
     * @param s The string that will be written to the stream
     * @return True if it successfully wrote to the stream, false if it did not
     */
    private boolean println(BufferedOutputStream bos, String s) {
	String news = s + "\r\n";
	byte[] array = news.getBytes();
	try {
	    bos.write(array, 0, array.length);
	}
	catch(IOException e) {
	    return false;
	}
	return true;
    }

    /**
     * Sends a file over the provided stream using http/1.1
     * @param file The name of the file that is to be sent
     * @param output The stream for the file to be transferred over
     */
    private void sendFile(String file, BufferedOutputStream output) {
	try {
	    FileInputStream fileStream = new FileInputStream(file);
	    byte[] bytes = new byte[1];
	    int status = fileStream.read(bytes);

	    println(output, "HTTP/1.1 200 OK");
	    println(output, "");
	 
	    while(status != -1) {        
		output.write(bytes, 0, bytes.length);
		status = fileStream.read(bytes);
		//remember to actually flush the output 
		//sleep(1000);
	    }
	}
	catch(FileNotFoundException ex) {
	    System.out.println("Requested file not found: " + file);
	    println(output, "HTTP/1.1 404 File Not Found");
	    println(output, "");
	    println(output, file + " not found");
	    return;
	}
	catch(IOException ex) {
	    System.err.println("IOException: " + ex);
	    return;
	}
	//catch(InterruptedException ex) {
	//
	//}
	return;
    }
}
