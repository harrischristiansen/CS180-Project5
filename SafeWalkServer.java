/*
 CS180 - Project 5 - Safe Walk Server
 SafeWalkServer.java
 
 @Harris Christiansen (christih@purdue.edu) (CS180-802) (HarrisChristiansen.com)
 @Deepthi Naidu (naidud@purdue.edu) (CS180-804)
 @2014-11-12
 */

import java.io.*;
import java.net.*;
import java.util.*;

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
    private ArrayList requestQue;
    
    ///// Constructors /////
    public SafeWalkServer(int port) throws IOException { // Complete
	    requestQue = new ArrayList();
        try {
            if (port < 1025 || port > 65535) { // Validate Port Number
                System.out.println("ERROR: Invalid Port Number");
                return false;
            }
            
            ss = ServerSocket(port);
            ss.setReuseAddress(true);
        } catch (IOException e) {}
                e.printStackTrace();
        }
    }
    public SafeWalkServer() throws IOException { // Complete
	    requestQue = new ArrayList();
	    try {
	        ss = ServerSocket();
	        ss.setReuseAddress(true);
	        System.out.println("Port not specified. Using free port " + getLocalPort() + ".")
    	} catch (IOException e) {
            e.printStackTrace();
    	}
    }
    
    public int getLocalPort() { // Complete
        return ss.getLocalPort();
    }
    
    public void run() {
        while (true) {
            Socket client = ss.accept(); // Wait For Socket Connection
            handleClient(client); // Handle Client
        }
    }
    
    private void handleClient(Socket client) throws IOException {
        try {
            // Create Input Stream
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            
            // Read Client String
            String request = (String) in.readLine();
            
            // Close Stream
            in.close();
            
            // Check Request Validity
            if(checkRequest(request)) { // Request Valid
	            if(request.charAt(0) == ':') { // Request starts with :
		            handleCommand(client,request);
				} else {
                	handleRequest(client,request);
				}
            } else {
	            client.close(); // Invalid Command, Close Client
	        }
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
    
    
    public void handleRequest(Socket client, String request) throws IOException {
	    String[] requestList = string.split(",");
	    
        try {
	        // Check if match in que
	        for (int i = 0; i < requestQue.size(); i++) {
		        if (requestQue.get(i).from.equals(requestList[1])) { // FROM Locations Match
			        if (requestQue.get(i).to.equals(requestList[2])) { // To Locations Match
				        respondToClient(client, i);
				    }
				    if (requestQue.get(i).to.equals("*") && !requestList[2].equals("*")) { // Que Is Helper
					    respondToClient(client, i);
					}
				    if (!requestQue.get(i).to.equals("*") && requestList[2].equals("*")) { // Requester Is Helper
					    respondToClient(client, i);
					}
			    }
		    }
		    
		    // No Match, Add To Que
	        requestQue.add(new requestQueObject(requestList, client));
	        
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void respondToClient(Socket client, int queIndex) {
        // Create Output Streams
        PrintWriter outQue = new PrintWriter(requestQue.get(queIndex).client.getOutputStream());
        outQue.flush();
        PrintWriter outReq = new PrintWriter(client.getOutputStream());
        outReq.flush();
        
        outQue.print();
        outReq.print();
        
        outQue.close(); // Close Stream
        outReq.close(); // Close Stream
        requestQue.get(queIndex).client.close(); // Close Que Socket
        requestQue.remove(queIndex); // Remove Que Person From Que
        client.close(); // Close Requester Socket
	}
    
    public void handleCommand(Socket client, String command) {
	    if (command.equals(":LIST_PENDING_REQUESTS")) {
		    listPendingRequests(client);
		} else if (command.equals(":RESET")) {
			resetServer(client);
		} else if (command.equals(":SHUTDOWN")) {
			shutdownServer(client);
		}
	    client.close(); // Close Socket
    }
    
    public void listPendingRequests(Socket client) {
		// Create Output Stream
		PrintWriter out = new PrintWriter(client.getOutputStream());
		out.flush();
		
		out.print("[");
	    for (int i = 0; i < requestQue.size(); i++) {
			out.print("["+requestQue.get(i).name+", "+requestQue.get(i).from+", "+requestQue.get(i).to+", "+requestQue.get(i).type+"]");
			if(i != requestQue.size()-1) {
				out.print(", ");
			}
		}
		out.print("]")
		
		out.close(); // Close Stream
	}
	
	public void resetServer(Socket client) {
		for (int i = 0; i < requestQue.size(); i++) {
			// Open Stream
			PrintWriter out = new PrintWriter(requestQue.get(i).client.getOutputStream());
			out.flush();
			
			out.print("ERROR: connection reset"); // Send ERROR: connection reset
			
			out.close(); // Close Stream
			requestQue.get(i).client.close(); // Close Client
			requestQue.remove(i); // Remove Client From Que
		}
		
		// Respond Success To Command Origin Client
		PrintWriter out = new PrintWriter(client.getOutputStream());
		out.flush();
		out.print("RESPONSE: success");
		out.close(); // Close Stream
		// Client Will Be Closed In HandleCommand
	}
	
	public void shutdownServer(Socket client) {
		resetServer(client);
		
		// TO-DO: Shutdown Server
	}
}

public class requestQueObject {
	public final String name;
	public final String from;
	public final String to;
	public final String type;
	public final Socket client;
	
	public requestQueObject(String[] requestList, Socket theClient) {
		name = requestList[0];
		from = requestList[1];
		to = requestList[2];
		type = requestList[3];
		client = theClient;
	}
}