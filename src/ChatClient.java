

import java.net.*;
import java.io.*;

public class ChatClient  {
	private ObjectInputStream input;		// to read from the socket
	private ObjectOutputStream output;		// to write on the socket
	private Socket socket;
	private ChatClientInterface clientGui;
	private String server, username;
	private int port;
	serverListener servlistener;
	
	ChatClient(String server, int port, String username, ChatClientInterface cg) {
		this.server = server;
		this.port = port;
		this.username = username;
		this.clientGui = cg;
	}
	
	//This method is to start dialog between client and server
	public boolean startDialog() {
		try {
			socket = new Socket(server, port);
		} 
		catch(Exception e) {
			display("Error: Unable to connect to server:" + e);
			return false;
		}
		String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
		display(msg);
	
		// Creating input and output stream for communication 
		try
		{
			input  = new ObjectInputStream(socket.getInputStream());
			output = new ObjectOutputStream(socket.getOutputStream());
		}
		catch (IOException ex) {
			display("Exception in creating new Input/output Streams: " + ex);
			return false;
		}
		
		// creates the Thread to listen from the server 
		servlistener=new serverListener();
		servlistener.start();
		
		//here we are sending our username to the server as string later on we will use 
		//the ChatMessage objects to communicate with the server
		try
		{
			output.writeObject(username);
		}
		catch (IOException e) {
			display("Login Exception : " + e);
			disconnect();
			return false;
		}
		return true;
	}

	//To display messages in client gui 
	private void display(String msg) {
		// append to the clientGui
		clientGui.append(msg + "\n");		
	}
	
	//To send a message to the server
	void sendMessage(ChatMessage msg) {
		try {
			output.writeObject(msg);
		}
		catch(IOException e) {
			display("Exception unable to send message to server: " + e);
		}
	}
	 
	// Close the Input/Output streams and disconnect 
	private void disconnect() {
		try { 
			if(input != null) input.close();
		}
		catch(Exception e) {
			display("Exception input stream is not closed" +e);
		} 
		try {
			if(output != null) output.close();
		}
		catch(Exception e) {
			display("Exception outut stream is not closed" +e);
		} 
        try{
			if(socket != null) socket.close();
		}
		catch(Exception e) {
			display("Exception Socket  is not closed" +e);
		} 
		
		if(clientGui!= null)
			clientGui.onConnectionFailure();
	}
	//Thread class waits for the message from server and append them to the JText Area
	class serverListener extends Thread {	
		public void run() {
			while(true) {
				try {
					String msg = (String) input.readObject();
					clientGui.append(msg);
				}
				catch(IOException ex) {
					display("Server connection is colsed: " + ex);
					if(clientGui != null) 
						clientGui.onConnectionFailure();
						break;
				}	
				catch(ClassNotFoundException ex) {
					display("ClassNotFoundException: " + ex);
				}
				
			}
		}
	}
}

