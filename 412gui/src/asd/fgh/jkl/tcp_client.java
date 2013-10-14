package asd.fgh.jkl;
import java.io.*;
import java.net.*;

public class tcp_client {
	Socket clientSocket;
	BufferedReader inServer;
	
	public void createTCPclientconnect(int port, String hostname)
	{
	//int portnum = port;
	//String host = hostname;
	try{
	clientSocket = new Socket ( hostname,port);
	System.out.println("Client Socket Created \n");
	inServer = new BufferedReader (new InputStreamReader(clientSocket.getInputStream()));

	
	}catch (IOException e) 
	{
		e.printStackTrace();
	}
	}
	public void SendTCP (String test)
	{
		try{
			DataOutputStream ToServer = new DataOutputStream (clientSocket.getOutputStream());
			
			String In = test;
			ToServer.writeBytes(In + '\n');
			
		}catch (IOException e)
		{
			e.printStackTrace();
		}
		
	}
	public String RecieveTCP ()
	{
		String msg = null;
		
		try{
		
			
				msg = inServer.readLine();
			
			
		
	}catch (IOException E)
	{
		E.printStackTrace();
		msg = "Error";
	}
		
		return msg;
	}
	public void closeTCPconnect ()
	{
		try{
		clientSocket.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
