/*
 CS180 - Project 5 - Safe Walk Server
 SafeWalkServer.java
 
 @Harris Christiansen (christih@purdue.edu) (CS180-802) (HarrisChristiansen.com)
 @Deepthi Naidu (naidud@purdue.edu) (CS180-804)
 @2014-11-12
 */

import java.io.*;
import java.net.*;

public class SafeWalkServer implements Runnable {
    public static void main(String[] args) {
        SafeWalkServer server;
        // Constructor
        if(args.length > 0) {
            server = new SafeWalkServer(Integer.parseInt(args[0]));
        } else {
            server = new SafeWalkServer();
        }
        
        // Handle Connections
        /*
         while (true) {
         Socket socket = serverSocket.accept();
         EchoServer server = new EchoServer(socket);
         new Thread(server).start();
         }
         */
    }
    
    private ServerSocket ss;
    
    ///// Constructors /////
    public SafeWalkServer(int port) throws IOException { // Complete
        try {
            if (port < 1025 || port > 65535) { // Validate Port Number
                System.out.println("ERROR: Invalid Port Number");
                return false;
            }
            
            ss = ServerSocket(port);
            ss.setReuseAddress(true);
        } catch {
            (IOException e)
                e.printStackTrace();
        }
    }
    public SafeWalkServer() throws IOException { // Complete
        try {
            if {
        ss = ServerSocket();
        ss.setReuseAddress(true);
        System.out.println("Port not specified. Using free port " + getLocalPort() + ".")
    }
     } catch {
        (IOException e)
            e.printStackTrace();
    }
        }
    public int getLocalPort() { // Complete
        return ss.getLocalPort();
    
    public void run() {
        while (true) {
            Socket client = ss.accept(); // Wait For Socket Connection
            handleClient(client); // Repond To Client
            client.close(); // Closes Socket
        }
    }
    
    private void handleClient(Socket client) throws IOException {
        try {
            // Create Input Stream
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            
            // Read Client String
            String request = (String) in.readLine();
            
            // Check Request Validity
            if(checkRequest(request)) { // Request Valid
	            if(request.charAt(0) == ':') { // Request starts with :
		            handleCommand(request);
				} else {
                	respondToClient(client,request);
				}
            }
            
            // Close Stream
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private boolean checkRequest(String request) { // Validity Check
        if(request.charAt(0) == ':') { // Request starts with :
            if(request.equals(":LIST_PENDING_REQUESTS")||request.equals(":RESET")||request.equals(":SHUTDOWN")) {
                return true;
            }
            return false;
        }
        
        String[] requestList = string.split(",");
        if (requestList.length != 4) { // Check Request List Length
            return false;
        }
        if (!requestList[1].equals("CL50") && !requestList[1].equals("EE") && !requestList[1].equals("LWSN") && !requestList[1].equals("PMU") && !requestList[1].equals("PUSH")) { // Validate Request FROM
            return false;
        }
        if (!requestList[2].equals("CL50") && !requestList[2].equals("EE") && !requestList[2].equals("LWSN") && !requestList[2].equals("PMU") && !requestList[2].equals("PUSH") && !requestList[2].equals("*")) { // Validate Request TO
            return false;
        }
        if (requestList[1].equals(requestList[2])) { // Check if FROM == TO
            return false;
        }
        return true;
    }
    
    public void respondToClient(Socket client, String request) throws IOException {
        try {
            // Create Output Stream
            PrintWriter out = new PrintWriter(client.getOutputStream());
            out.flush();
            
            // Respond
            
            
            // Close Stream
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void handleCommand(String command) {
	    
    }
}