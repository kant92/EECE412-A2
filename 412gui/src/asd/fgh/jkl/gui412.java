package asd.fgh.jkl;

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

import asd.fgh.jkl.TCP_serverside;
import asd.fgh.jkl.tcp_client;

public class gui412 {
	
	private GridBagLayout gb = new GridBagLayout();
	private GridBagConstraints c = new GridBagConstraints();
	public TCP_serverside serverside = new TCP_serverside();
	public tcp_client client1 = new tcp_client();
	
	private JFrame select_mode, server_frame, client_frame, connecting_frame;
	
	private JTextField port_number, host_ip, client_port_number;
	
	private String portNumber;

	
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
		JButton enter_button = new JButton("Enter");
		
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(15, 0, 5, 10);
		gb.setConstraints(port_number, c);
		server_frame_panel.add(port_number);
		
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(15, 0, 5, 10);
		gb.setConstraints(enter_button, c);
		server_frame_panel.add(enter_button);
		enter_button.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e){
				
				portNumber = port_number.getText();
				
				System.out.println("Listening to" + portNumber);

				String key = "HELLOWORLD";
				String received_data;
				serverside.initialization(portNumber, key);
				
				received_data = serverside.receive_Data();
				System.out.println("I received:" + received_data);
				System.out.println("Relaying:" + received_data);
				serverside.send_data(received_data);
				System.out.println("Data sent");
				

				//server_frame.dispose();
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
		gb.setConstraints(Enter, c);
		client_frame_panel.add(Enter);
		Enter.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e){
				
				System.out.println(client_port_number.getText());
				
				System.out.println(host_ip.getText());
				
				connecting();
				//TCP stuff here
			
				System.out.println(client_port_number.getText());
				client1 = new tcp_client();
				//System.out.println(host_ip.getText());
				int test = Integer.parseInt(client_port_number.getText());
				System.out.println(test);
				client1.createTCPclientconnect(test,host_ip.getText());
				String t1 = "testingcase1";
				client1.SendTCP(t1);
				
				String r1 = client1.RecieveTCP();
				System.out.println("Recieved:"+ r1 + "\n");
				//client_frame.dispose();
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
	
	public gui412() {
		
		select_mode();

	}
	
	public static void main( String args[] ){
		
		gui412 a = new gui412();
	}
	
	
	
}