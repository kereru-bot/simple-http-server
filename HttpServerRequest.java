
/**
 * HttpServerRequest is used to process and hold details about an http request
 */
class HttpServerRequest {
    /** The name of the file that has been requested */
    private String file;

    /** Hostname of the computer that sent the request */
    private String host;

    /** Whether the end of the request has been processed or not */
    private boolean done;

    /** The current line of the request being processed */
    private int line;

    public HttpServerRequest() {
	file = null;
	host = null;
	done = false;
	line = 0;
    }
    
    /**
     * Gets the status of the request to see if it's finished
     * @return True if the end of the request has been reached, false if not
     */
    public boolean isDone() {
	return done;
    }

    /**
     * Gets the name of the file being requested
     * @return The name of the file being requested
     */
    public String getFile() {
	return file;
    }

    /**
     * Gets the host name of the requester
     * @return The name of the host giving the request
     */
    public String getHost() {
	return host;
    }

    /**
     * Processes a line of an http request (currently only processes
     * the hostname and GEt request)
     * @param in The line of the request that you want to process
     */
    public void process(String in) {
	if(in == null || in.compareTo("") == 0) {
	    this.done = true;
	    return;
	}

	//first line should be the request
	if(line == 0) {
	    String parts[] = in.split(" ");
	    if(parts.length == 3) {
		if(parts[0].compareTo("GET") != 0) {
		    System.out.println("Not a get request");
		    return;
		}

		String filename = parts[1].substring(1);

		if(filename.endsWith("/")) {
		    filename += "index.html";
		}

		this.file = filename;
		
		line++;
		return;
	    }
	}

	if(in.startsWith("Host:")) {
	    //hostname
	    this.host = in.substring(7);
	}

	line++;
	return;
    }
}
