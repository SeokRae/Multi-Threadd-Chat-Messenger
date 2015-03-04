

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

	public class ChatServer {
		private ArrayList<ClientThread> clientList;
		private ChatServerInterface ServerGui;
		private static int uniqueId;
		private SimpleDateFormat dateformat;
		private int port;
		private boolean running;
	
		public ChatServer(int port, ChatServerInterface sg) {
			this.ServerGui = sg;
			this.port = port;
			dateformat = new SimpleDateFormat("HH:mm:ss");
			clientList = new ArrayList<ClientThread>();
		}
		
		public void startDialog() {
			running = true;
			// create socket server and wait for connection requests 
			try 
			{
				ServerSocket serverSocket = new ServerSocket(port);
				while(running) 
				{
					display("Server waiting for Clients on port " + port + ".");
					Socket socket = serverSocket.accept();  	
					if(!running)
						break;
					ClientThread t = new ClientThread(socket);  
					clientList.add(t);									
					t.start();
				}
				//  if asked to stop
				try {
					serverSocket.close();
					for(int i = 0; i < clientList.size(); ++i) {
						ClientThread tc = clientList.get(i);
						try {
							tc.input.close();
							tc.output.close();
							tc.socket.close();
						}
						catch(IOException e) {
							display("input/output exception while closing the streams");
						}
					}
				}
				catch(Exception e) {
					display("Exception closing the server and clients: " + e);
				}
			}
			catch (IOException e) {
				String msg = dateformat.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
				display(msg);
			}
		}		
		
		//GUI used this method to stop the server process
		protected void stop() {
			running = false;
			try {
				new Socket("localhost", port);
			}
			catch(Exception e) {
				display("unable to create new server socket"+e);
			}
		}
		
		// Display an event (not a message) to the console or the GUI
		private void display(String msg) {
			String time = dateformat.format(new Date()) + " " + msg;
			ServerGui.appendOutput(time + "\n");
		}
		
		//to broadcast a message to all Clients
		private synchronized void broadcastMessages(String message) {
			String time = dateformat.format(new Date());
			String messageLf = time + " " + message + "\n";
			ServerGui.appendOutput(messageLf); 
			for(int i = clientList.size(); --i >= 0;) {
				ClientThread ct = clientList.get(i);	
				if(!ct.writeMsg(messageLf)) {
					clientList.remove(i);
					display("Disconnected Client " + ct.username + " removed from list.");
				}
			}
		}
		
		synchronized void remove(int id) {
			for(int i = 0; i < clientList.size(); ++i) {
				ClientThread ct = clientList.get(i);
				if(ct.id == id) {
					clientList.remove(i);
					return;
				}
			}
		}
		
		class ClientThread extends Thread {
			Socket socket;
			ObjectInputStream input;
			ObjectOutputStream output;
		    String username;
			ChatMessage chatMsg;
			String date;
			int id;
			ClientThread(Socket socket) {
				id = ++uniqueId;
				this.socket = socket;
				try
				{
					output = new ObjectOutputStream(socket.getOutputStream());
					input  = new ObjectInputStream(socket.getInputStream());
					username = (String) input.readObject();
					display(username + " just connected.");
				}
				catch (IOException e) {
					display("Exception creating new Input/output Streams: " + e);
					return;
				}
				catch (ClassNotFoundException e) {
					display("Exception ClassNotFound: " + e);
					
				}				
				date = new Date().toString() + "\n";
			}
			
			public void run() {
				boolean running = true;
				while(running) {
					try {
						chatMsg = (ChatMessage) input.readObject();
					}
					catch (IOException e) {
						display(username + " Exception reading Streams: " + e);
						break;
					}
					catch(ClassNotFoundException e) {
						display(username + " Exception class not found: " + e);
						break;
					}
					String message = chatMsg.getMessage();
					// handle message based on the type of the message
					switch(chatMsg.getType()) {
						case ChatMessage.MESSAGE:
							broadcastMessages(username + ": " + message);
							break;
						case ChatMessage.LOGOUT:
							display(username + " client logged out.");
							running = false;
							break;
					}
				}
				
				//remove client thread from the list
				remove(id);
				close();
			}
			
			// closing all server resource
			private void close() {
				try {
					if(output != null) output.close();
				}
				catch(Exception e) {
					display("Exception Output stream is not closed" +e);
				}
				try {
					if(input != null) input.close();
				}
				catch(Exception e) {
					display("Exception input stream is not closed" +e);
				};
				try {
					if(socket != null) socket.close();
				}
				catch (Exception e) {
					display("Exception Socket is not closed" +e);
				}
			}
			
			private boolean writeMsg(String msg) {
				// if client is not connected
				if(!socket.isConnected()) {
					close();
					return false;
				}
				try {
					output.writeObject(msg);
				}
				catch(IOException e) {
					display("Error sending message to " + username);
					display(e.toString());
				}
				return true;
			}
		}
}

