package vpn;
import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.Random;

public class tcp_client {
	Socket clientSocket;
	InputStream inServer;
	String serverAddress = null;
	String myAddress = null;
	String sharedKey;
	String sessionKey;
	int p = 25; // Modulo 25
	int g = 2; // 2 is a primitive root of 25
	
	public void createTCPclientconnect(int port, String hostname) {
		try {			
			clientSocket = new Socket (hostname, port);
			inServer = clientSocket.getInputStream();
			System.out.println("Client Socket Created \n");
			
			// Save the IP addresses of the server and client
			serverAddress = hostname;
			myAddress = clientSocket.getLocalAddress().getHostAddress();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void SendTCP(byte[] b)
	{
		try{
			DataOutputStream ToServer = new DataOutputStream (clientSocket.getOutputStream());	
			ToServer.write(b);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public byte[] RecieveTCP()
	{
		int numReceived = 0; // Number of bytes actually received
		byte[] data = new byte[2048]; // Constant size buffer to store the bytes
		byte[] b = null;
		
		try {
			// Read the stream into data
			numReceived = inServer.read(data);
			
			if(numReceived > 0) {
				// Allocate space for b
				b = new byte[numReceived];
				
				// Copy the data over to b
				for(int i = 0; i < numReceived; i++) {
					b[i] = data[i];
				}
			} else {
				return null;
			}
		} catch (IOException E) {
			E.printStackTrace();
		}

		return b;
	}

	public void closeTCPconnect ()
	{
		try{
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Authenticates the client and server
	public int authenticate() {
		int R1 = 0; // Random number
		int R2 = 0;
		int a;
		int myKeyHalf = 0;
		int serverKeyHalf = 0;
		String toEncrypt = null; // The string to be encrypted
		byte[] toSend = null; // The byte array to send
		byte[] addressToSend = myAddress.getBytes(); // Convert address to byte array
		byte[] rand1 = new byte[4];
		byte[] rand2 = new byte[4];
		byte[] dataReceived = null;
		byte[] encryptedData;
		byte[] addressReceived = new byte[serverAddress.length()];
		String plainText = null;
		String random1;
		String keyHalf;
		String address;
		
		Random rng = new Random();
		a = rng.nextInt(32);
		R1 = rng.nextInt();

		// Step 1: Request authentication
		// Send R1 + "Alice"
		rand1 = intToByteArray(R1);
		
		toSend = new byte[addressToSend.length + 4]; // Create the byte array to send
		for(int i = 0; i < 4; i++) {
			toSend[i] = rand1[i];
		}
		for(int i = 4; i < toSend.length; i++) {
			toSend[i] = addressToSend[i - 4];
		}
		
		SendTCP(toSend); // Send the message
		
		
		// Step 2: Receive the challenge
		// Receive R2 + ["Bob" + R1 + key half, k]
		dataReceived = null;
		while(dataReceived == null) {
			dataReceived = RecieveTCP(); // Receive message
		}
		
		encryptedData = new byte[dataReceived.length - 4];
		for(int i = 0; i < 4; i++) { // Parse R2
			rand2[i] = dataReceived[i];
		}
		R2 = byteToInt(rand2);
		for(int i = 4; i < dataReceived.length; i++) { // Parse the ciphertext
			encryptedData[i - 4] = dataReceived[i];
		}
		
		try {
			plainText = AES.decrypt(encryptedData, sharedKey);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
//		System.out.print("Plaintext received: " + plainText + "\n");
		
		dataReceived = new byte[plainText.length()];
		dataReceived = plainText.getBytes();
		addressReceived = new byte[serverAddress.length()];
		for(int i = 0; i < addressReceived.length; i++) {
			addressReceived[i] = dataReceived[i];
		}
		
		address = new String(addressReceived);
		random1 = plainText.substring(addressReceived.length, addressReceived.length + String.valueOf(R1).length());
		keyHalf = plainText.substring(addressReceived.length + String.valueOf(R1).length(), plainText.indexOf('\0'));
		serverKeyHalf = Integer.parseInt(keyHalf);

		if(serverAddress.equals(address) && random1.equals(String.valueOf(R1))) { // Check the challenge response
			// Step 3: Send challenge response
			// Send ["Alice" + R2 + key half, k]
			myKeyHalf = (int)Math.pow(g, a) % p;
			toEncrypt = myAddress + R2 + myKeyHalf;
			encryptedData = null;
			try {
				encryptedData = AES.encrypt(toEncrypt, sharedKey);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			SendTCP(encryptedData);
			
			dataReceived = null;
			while(dataReceived == null) {
				dataReceived = RecieveTCP();
			}
			
			if((0 | dataReceived[0]) == 1) {
				// Successfully authenticated!
				// Create session key
				sessionKey = String.valueOf((long)Math.pow(serverKeyHalf, a) % p);
				
				System.out.print("Client IP: " + myAddress + "\n");
				System.out.print("Server IP: " + serverAddress + "\n");
				System.out.print("Ra: " + R1 + "\n");
				System.out.print("Rb: " + R2 + "\n");
				System.out.print("g: " + g + "\n");
				System.out.print("p: " + p + "\n");
				System.out.print("a: " + a + "\n");
				System.out.print("g^b % p: " + serverKeyHalf + "\n");
				System.out.print("Shared key: " + sharedKey + "\n");
				System.out.print("Session key: " + sessionKey + "\n\n");
				System.out.print("Successfully connected!\n");
				
				return 1;
			}
		}
		
		return 0;
	}
	
	public byte[] stringToByteArray(String s) {
		byte[] b = new byte[s.length()];
		for(int i = 0; i < s.length(); i++) {
			b[i] = (byte)s.charAt(i);
		}
		return b;
	}
	
	public int byteToInt(byte[] b) {
		int result;
		result = (b[0] & 0xFF) << 24 | (b[1] & 0xFF) << 16 | (b[2] & 0xFF) << 8 | (b[3] & 0xFF);
		return result;
	}
	
	public byte[] intToByteArray(int value) {
	    return new byte[] {(byte)(value >>> 24), (byte)(value >>> 16), (byte)(value >>> 8), (byte)value};
	}
	
	public void setSecretValue(String secret) {
		sharedKey = secret;
	}
}
