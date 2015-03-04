

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatClientInterface extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
		private JTextField msgTF; 
		private JTextField userNameTF;
		private JButton login, logout;
		private JTextArea chatArea;
		private boolean connected;
		private ChatClient client;
	/*
	 * constructing the client interface such that username comes on the top, then below that comes the message editor and then come chat area below that
	 * at the bottom we have login and logout buttons for repective actions
	 * */
		ChatClientInterface(String host, int port) {
			super("Chat Client");
			
			JPanel northPanel = new JPanel(new GridLayout(2,2));
			JPanel namePanel = new JPanel(new GridLayout(1,5, 1, 3));
			namePanel.add(new JLabel("UserName:  "));
			userNameTF = new JTextField("");
			namePanel.add(userNameTF);
			northPanel.add(namePanel);
			add(northPanel);
			
			JPanel messagePanel = new JPanel(new GridLayout(1,5, 1, 3));
			msgTF = new JTextField("");
			msgTF.setBackground(Color.white);
			msgTF.setEditable(false);
			messagePanel.add(new JLabel("Enter Text"));
			messagePanel.add(msgTF);
			northPanel.add(messagePanel);
			add(northPanel, BorderLayout.NORTH);

			chatArea = new JTextArea("Welcome to the Chat room\n", 80, 80);
			JPanel centerPanel = new JPanel(new GridLayout(1,1));
			centerPanel.add(new JScrollPane(chatArea));
			chatArea.setEditable(false);
			add(centerPanel, BorderLayout.CENTER);

			login = new JButton("Login");
			login.addActionListener(this);
			logout = new JButton("Logout");
			logout.addActionListener(this);
			logout.setEnabled(false);		
			
			JPanel southPanel = new JPanel();
			southPanel.add(login);
			southPanel.add(logout);
			add(southPanel, BorderLayout.SOUTH);
			setDefaultCloseOperation(EXIT_ON_CLOSE);
			setSize(300, 300);
			setVisible(true);
			msgTF.requestFocus();
		}
		
		// String or Message called by the Client to append text in the TextArea 
		void append(String str) {
			chatArea.append(str);
			chatArea.setCaretPosition(chatArea.getText().length() - 1);
		}
		
		// called by the GUI  in the case if the connection fails
		void onConnectionFailure() {
			login.setEnabled(true);
			logout.setEnabled(false);
			msgTF.setText("");
			userNameTF.setText("");
			userNameTF.setEditable(true);
			msgTF.setEditable(false);
			chatArea.setText("");
			connected = false;
			msgTF.removeActionListener(this);
		}
		
		public void actionPerformed(ActionEvent e) {
			Object obj = e.getSource();
			if(obj == logout) {
				client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
				connected = false;
				login.setEnabled(true);
				logout.setEnabled(false);
				userNameTF.setEditable(true);
				msgTF.setEditable(false);
				chatArea.setText("");
				msgTF.removeActionListener(this);
				return;
			}
			if(connected) {
				// just have to send the message
				client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, msgTF.getText()));				
				msgTF.setText("");
				return;
			}
			if(obj == login) {
				String username = userNameTF.getText().trim();
				if(username.length() == 0)
					return;	
				String server = "localhost";
				int port = 8888;
				client = new ChatClient(server, port, username, this);
				if(!client.startDialog()) 
					return;
				msgTF.setEditable(true);
				msgTF.setText("");
				connected = true;
				login.setEnabled(false);
				logout.setEnabled(true);
				userNameTF.setEditable(false);
				msgTF.addActionListener(this);
			}
		}
		
		//main method to start the client application
		public static void main(String[] args) {
			new ChatClientInterface("localhost", 8888);
		}
}
