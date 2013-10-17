package vpn;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import vpn.TCP_serverside;
import vpn.tcp_client;


public class gui412 {
	
	// -- VARIABLES --
	
	private GridBagLayout gb = new GridBagLayout();
	private GridBagConstraints c = new GridBagConstraints();
	
	public TCP_serverside serverside = new TCP_serverside();
	public tcp_client client1 = new tcp_client();
	public String SessionKey;
	
	public JFrame select_mode, server_frame, client_frame, connecting_frame;
	public JFrame secret_value_frame;	
	public JFrame data_entry_frame;
	
	public JTextField port_number, host_ip, client_port_number;
	public JTextField the_secret_value;
	public JTextField data_to_b_sent;
							  
	public JTextArea data_received;
	
	public String portNumber;

	public int mode = 3; //0 = Server Mode, 1 = Client Mode
	
	//-- FUNCTIONS --
	
	public void select_mode() {
		
		select_mode = new JFrame("Select Mode");

		JPanel select_mode_panel = new JPanel(gb);
		
		JButton server_mode_button = new JButton("Server Mode");
		JButton client_mode_button = new JButton("Client Mode");
		JLabel text = new JLabel("Select a Mode");
		
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(15, 0, 5, 10);
		gb.setConstraints(text, c);
		select_mode_panel.add(text);
		
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(15, 0, 5, 10);
		gb.setConstraints(server_mode_button, c);
		select_mode_panel.add(server_mode_button);
		server_mode_button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				mode = 0;
				server_mode();
				select_mode.dispose();	// window closes 
			}

		});
		
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(15, 0, 5, 10);
		gb.setConstraints(client_mode_button, c);
		select_mode_panel.add(client_mode_button);
		client_mode_button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
										
				mode = 1;
				client_mode();
				select_mode.dispose();	// window closes
			}

		});
		
		select_mode.setVisible(true);
		select_mode.setSize(300,250);
		select_mode.add(select_mode_panel);
		
	}

	public void server_mode() {
		
		server_frame = new JFrame("Server Mode");
		
		JPanel server_frame_panel = new JPanel(gb);
		
		port_number = new JTextField("Port Number", 20);
		the_secret_value = new JTextField("Secret Value", 20);
		
		JButton enter_button = new JButton("Enter");
		
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(15, 0, 5, 10);
		gb.setConstraints(port_number, c);
		server_frame_panel.add(port_number);
		
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(15, 0, 5, 10);
		gb.setConstraints(the_secret_value, c);
		server_frame_panel.add(the_secret_value);
		
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(15, 0, 5, 10);
		gb.setConstraints(enter_button, c);
		server_frame_panel.add(enter_button);
		enter_button.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e){
				String key = "HELLOWORLD";
				
				//INITITIALIZATION************************************************************************
				
				// Close the window
				server_frame.dispose();
				
				// Parse the port number
				portNumber = port_number.getText();
				System.out.println("Listening to " + portNumber);				
				
				// Start the server 
				serverside.initialization(portNumber, key);
				//***********************************************************************************************
				serverside.setSecretValue(the_secret_value.getText());
				// Authenticate here
				if(serverside.authenticate() == 1) {
					SessionKey = serverside.sessionKey;
					data_entry();
				}
			}
			
			
		});
		
		
		server_frame.setVisible(true);
		server_frame.setSize(300, 250);
		server_frame.add(server_frame_panel);
	}
	
	public void client_mode(){
		client_frame = new JFrame("Client Mode");
		
		JPanel client_frame_panel = new JPanel(gb);
		
		client_port_number = new JTextField("Port #", 20);
		host_ip = new JTextField("IP Address", 20);
		the_secret_value = new JTextField("Secret Value", 20);
		
		JButton Enter = new JButton("Enter");
		
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(15, 0, 5, 10);
		gb.setConstraints(host_ip, c);
		client_frame_panel.add(host_ip);
		
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(15, 0, 5, 10);
		gb.setConstraints(client_port_number, c);
		client_frame_panel.add(client_port_number);
		
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(15, 0, 5, 10);
		gb.setConstraints(the_secret_value, c);
		client_frame_panel.add(the_secret_value);		
		
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(15, 0, 5, 10);
		gb.setConstraints(Enter, c);
		client_frame_panel.add(Enter);
		Enter.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e){			
				//connecting();
				
				//INITIALIZATION*********************************************************************************************
				// Create TCP client instance
				client1 = new tcp_client();

				// Parse the port number and IP
				int portNum = Integer.parseInt(client_port_number.getText());
				client1.createTCPclientconnect(portNum, host_ip.getText());
				client1.setSecretValue(the_secret_value.getText());
				System.out.print("Connected to " + host_ip.getText() + " on port " + client_port_number.getText() + "\n");
				
				// Authenticate here
				if(client1.authenticate() == 1) {
					SessionKey = client1.sessionKey;
				
					// Close the window
					client_frame.dispose();
					//************************************************************************************************************
				
					data_entry();
				}

			}
			
			
		});
		
		client_frame.setVisible(true);
		client_frame.setSize(300, 250);
		client_frame.add(client_frame_panel);
		
	}

	public void connecting(){
		
		connecting_frame = new JFrame("Connecting");
		
		JPanel connecting_frame_panel = new JPanel(gb);
		
		JTextArea connecting_text = new JTextArea("Connecting, Please Wait...\n This window will close automatically");
		connecting_text.setEditable(false);		
		
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(15, 0, 5, 10);
		gb.setConstraints(connecting_text, c);
		connecting_frame_panel.add(connecting_text);
		
		connecting_frame.setVisible(true);
		connecting_frame.setSize(300,250);
		connecting_frame.add(connecting_frame_panel);
		
	}
	
	public void secret_value() {
		
		secret_value_frame = new JFrame();
		
		JPanel secret_value_panel = new JPanel(gb);
		
		the_secret_value = new JTextField("Secret Value", 20);
		
		JButton enter = new JButton("Enter");
		
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(15, 0, 5, 10);
		gb.setConstraints(the_secret_value, c);
		secret_value_panel.add(the_secret_value);
		
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(15, 0, 5, 10);
		gb.setConstraints(enter, c);
		secret_value_panel.add(enter);
		enter.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e){
			
				secret_value_frame.dispose();
				data_entry();
			
			}
			
			
		});
		
		secret_value_frame.setVisible(true);
		secret_value_frame.setSize(300, 250);
		secret_value_frame.add(secret_value_panel);
	
	}
	
	public void data_entry() {
		
		(new Thread(new receiverRunnable())).start();
		
		data_entry_frame = new JFrame();
		
		JPanel data_entry_frame_panel = new JPanel(gb);
		
		data_to_b_sent = new JTextField("Data to be sent", 20);		
		data_to_b_sent.setSize(50, 50);
		
		data_received = new JTextArea("Data Received");
		data_received.setSize(50,50);
		data_received.setEditable(false);
		
		JButton send = new JButton("Send");
		
		// JButton refresh = new JButton("Refresh");
		
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(15, 0, 5, 10);
		gb.setConstraints(data_to_b_sent, c);
		data_entry_frame_panel.add(data_to_b_sent);
		
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(15, 0, 5, 10);
		gb.setConstraints(send, c);
		data_entry_frame_panel.add(send);
		send.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e){
				GUI_send();
			}
			
			
		});
		
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(15, 0, 5, 10);
		gb.setConstraints(data_received, c);
		data_entry_frame_panel.add(data_received);
	
		data_entry_frame.setVisible(true);
		data_entry_frame.setSize(300,250);
		data_entry_frame.add(data_entry_frame_panel);
		
	}	
	
	public gui412() {
		
		select_mode();

	}
	
	public static void main( String args[] ){
		
		gui412 a = new gui412();
	}
	
	public void GUI_send(){
		String unencrypted_data_out =null;
		byte [] messageoutByte = null;
		
			if(mode ==0){ //Serverside Send
				unencrypted_data_out = data_to_b_sent.getText();
				try {
					messageoutByte = AES.encrypt(unencrypted_data_out, SessionKey);
				} catch(Exception ex) {
					ex.printStackTrace();
				}
				serverside.send_data(messageoutByte);
			}
			else if(mode ==1){ //Clientside Send
				unencrypted_data_out = data_to_b_sent.getText();
				try {
					messageoutByte = AES.encrypt(unencrypted_data_out, SessionKey);
				} catch(Exception ex) {
					ex.printStackTrace();
				}
				client1.SendTCP(messageoutByte);
			}
			else{
				System.out.println("Mode Error");
			}
	}
	
	public void GUI_refresh(){
		byte [] messageinByte = null;
		String unencrypted_data_in = null;
			// when refresh is clicked, update the data that is received
			if(mode ==0){ //Serverside Receive
				messageinByte = serverside.receive_Data();
				if(messageinByte != null) {
					try {
						unencrypted_data_in = AES.decrypt(messageinByte, SessionKey);
					} catch(Exception ex) {
						ex.printStackTrace();
					}
					data_received.setText(unencrypted_data_in);
				}
				
			}
			else if(mode ==1){ // Clientside Receive
				messageinByte = client1.RecieveTCP();
				if(messageinByte != null) {
					try {
						unencrypted_data_in = AES.decrypt(messageinByte, SessionKey);
					} catch(Exception ex) {
						ex.printStackTrace();
					}
					data_received.setText(unencrypted_data_in);
				}
			}
			else{
				System.out.println("Mode Error");
			}
	}
	
	public boolean close_sockets(){
		if (mode == 0){ //Server Mode socket close
			serverside.closeSocket();
			return true;
		}
		else if(mode ==1){ //Client Mode socket close
			client1.closeTCPconnect();
			return true;
		}
		else
		{
			System.out.println("Socket Closing Mode Error");
			return false;
		}
	}
	
	class receiverRunnable extends Thread{
		public receiverRunnable(){
			super();
		}
		
		public void run(){
			while(true){
			GUI_refresh();
			}
			//System.out.println("THREAD RUNNING");
		}
	}
}

