package asd.fgh.jkl;
import java.io.*;
import java.net.*;	

class TCP_serverside{

	int socket_number;
	String connection_status = "Not Connected";
	
	ServerSocket WebServerSocket;
	Socket ServerSocket;
	BufferedReader input_data;
//	PrintWriter output_data;

public String initialization(String socket_number, String key){
	
	try{
		WebServerSocket = new ServerSocket(Integer.parseInt(socket_number));
		ServerSocket = WebServerSocket.accept();
		input_data = new BufferedReader(new InputStreamReader(ServerSocket.getInputStream())); 
		//output_data = new PrintWriter(new OutputStreamWriter(ServerSocket.getOutputStream()));
	}
	catch(IOException e){
		e.printStackTrace();
	}
	return socket_number;
}

public String send_data(String data_out){
	try{
		DataOutputStream output_data = new DataOutputStream(ServerSocket.getOutputStream()); 
		output_data.writeBytes(data_out + '\n');
	}
catch(IOException e){
		e.printStackTrace();
	}
	return data_out;
}

public String receive_Data(){
	String data_in = null;
	try{
		data_in = input_data.readLine();
	}
	catch(IOException e){
		e.printStackTrace();
	}
	return data_in;
}
}