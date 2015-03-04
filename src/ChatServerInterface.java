

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ChatServerInterface extends JFrame implements ActionListener, WindowListener {
	
	private static final long serialVersionUID = 1L;
	private JButton stopStartButton;
	private JTextArea Event;
	private JTextField TextPortNumber;
	private ChatServer server;
	
	/*
	 * On the top we are showing the server port number with a stop/start button next to it, 
	 * we will use this button to stop and start the server
	 * then at the bottom we have the event log board showing all the events occured in the server
	 * */
	ChatServerInterface(int port) {
		super("Chat Server");
		server = null;
		JPanel north = new JPanel();
		north.add(new JLabel("Port number: "));
		TextPortNumber = new JTextField("  " + port);
		north.add(TextPortNumber);
		
		stopStartButton = new JButton("Start");
		stopStartButton.addActionListener(this);
		north.add(stopStartButton);
		add(north, BorderLayout.NORTH);
		
		JPanel center = new JPanel(new GridLayout(1,1));
		Event = new JTextArea(80,80);
		Event.setEditable(false);
		appendOutput("Events log.\n");
		center.add(new JScrollPane( Event));	
		add(center);
		addWindowListener(this);
		setSize(300, 300);
		setVisible(true);
	}		
	
	// append message to the event logs
	void appendOutput(String str) {
		Event.append(str);
		Event.setCaretPosition( Event.getText().length() - 1);
	}
	
	public void actionPerformed(ActionEvent e) {
		int port;
		if(server != null) {
			server.stop();
			server = null;
			TextPortNumber.setEditable(true);
			stopStartButton.setText("Start");
			return;
		}	
		
		try {
			port = Integer.parseInt(TextPortNumber.getText().trim());
		}
		catch(Exception ex) {
			appendOutput("Invalid port number"+ex);
			return;
		}
		server = new ChatServer(port, this);
		new ServerThread().start();
		stopStartButton.setText("Stop");
		TextPortNumber.setEditable(false);
	}
	
	// main for starting the Server
	public static void main(String[] arg) {	
		new ChatServerInterface(8888);
	}
	
	public void windowClosing(WindowEvent e) {
		if(server != null) {
			try {
				server.stop();			
			}
			catch(Exception ex) {
				appendOutput("exception while stopping server"+ex);
			}
			server = null;
		}
		// dispose the frame
		dispose();
		System.exit(0);
	}
	
	public void windowClosed(WindowEvent e) {
		
	}
	public void windowOpened(WindowEvent e) {
		
	}
	public void windowIconified(WindowEvent e) {
		
	}
	public void windowDeiconified(WindowEvent e) {
		
	}
	public void windowActivated(WindowEvent e) {
		
	}
	public void windowDeactivated(WindowEvent e) {
		
	}
	
	 // A thread to run the Server
	class ServerThread extends Thread {
		public void run() {
			server.startDialog();         
			stopStartButton.setText("Start");
			TextPortNumber.setEditable(true);
			server = null;
		}
	}

}
