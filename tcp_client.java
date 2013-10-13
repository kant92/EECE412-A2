package tcp;
import java.io.*;
import java.net.*;

public class tcp_client {
	Socket clientSocket;
	public void createTCPclientconnect(int port, String hostname)
	{
	int portnum = port;
	String host = hostname;
	try{
	clientSocket = new Socket ( hostname,port);
	}catch (IOException e) 
	{
		e.printStackTrace();
	}
	}
	public void SendTCP (BufferedReader test)
	{
		try{
			DataOutputStream ToServer = new DataOutputStream (clientSocket.getOutputStream());
			BufferedReader inServer = new BufferedReader (new InputStreamReader(clientSocket.getInputStream()));
			String In = test.readLine();
			ToServer.writeBytes(In + '\n');
			String Out = inServer.readLine();
		}catch (IOException e)
		{
			e.printStackTrace();
		}
		
		
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
