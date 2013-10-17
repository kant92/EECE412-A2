package vpn;
import java.io.*;
import java.net.*;	
import java.util.Random;

class TCP_serverside{

	int socket_number;
	String connection_status = "Not Connected";
	
	ServerSocket WebServerSocket;
	Socket ServerSocket;
	InputStream inClient;
	String myAddress;
	String clientAddress;
	String sharedKey;
	String sessionKey;
	int p = 25;
	int g = 2;
	
	public String initialization(String socket_number, String key){
		try {
			WebServerSocket = new ServerSocket(Integer.parseInt(socket_number));
			ServerSocket = WebServerSocket.accept();
			inClient = ServerSocket.getInputStream(); 
			
			myAddress = ServerSocket.getLocalAddress().getHostAddress();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		return socket_number;
	}

	public byte[] send_data(byte[] b){
		try{
			DataOutputStream output_data = new DataOutputStream(ServerSocket.getOutputStream()); 
			output_data.write(b);
		}
	catch(IOException e){
			e.printStackTrace();
		}
		return b;
	}
	
	public byte[] receive_Data(){
		byte[] data = new byte[2048];
		byte[] b = null;
		int length = 0; // The number of bytes actually read
		
		try{
			length = inClient.read(data);
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
		if(length > 0) {
			b = new byte[length];
			for(int i = 0; i < length; i++) {
				b[i] = data[i];
			}
		} else {
			return null;
		}
			
		return b;
	}
	
	public void closeSocket ()
	{
		try{
			ServerSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int authenticate() {
		int R1 = 0; // Random
		int R2 = 0; // Random
		byte[] dataReceived = null;
		byte[] addressReceived;
		byte[] rand1 = new byte[4];
		byte[] rand2 = new byte[4];
		byte[] toSend;
		byte[] encryptedData = null;
		int b;
		int myKeyHalf = 0;
		int clientKeyHalf = 0;
		String toEncrypt;
		String plainText = null;
		String address = null;
		String random2;
		String keyHalf;
		
		Random rng = new Random();
		b = rng.nextInt(32);
		R2 = rng.nextInt();
		
		// Step 1: Receive authentication request
		// Receive R1 + "Alice"
		while(dataReceived == null) {
			dataReceived = receive_Data(); // Read the data
		}
		
		addressReceived = new byte[dataReceived.length - 4];
		for(int i = 0; i < 4; i++) { // Parse the data
			rand1[i] = dataReceived[i];
		}
		for(int i = 4; i < dataReceived.length; i++) {
			addressReceived[i - 4] = dataReceived[i];
		}
		R1 = byteToInt(rand1);
		clientAddress = new String(addressReceived);
		
		// Step 2: Send challenge
		// Send R2 + ["Bob" + R1 + key half, k]
		rand2 = intToByteArray(R2);
		myKeyHalf = (int)Math.pow(g, b) % p;
		toEncrypt = myAddress + R1 + myKeyHalf;
		
		try {
			encryptedData = AES.encrypt(toEncrypt, sharedKey);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		toSend = new byte[encryptedData.length + 4];
		for(int i = 0; i < 4; i++) {
			toSend[i] = rand2[i];
		}
		for(int i = 4; i < toSend.length; i++) {
			toSend[i] = encryptedData[i - 4];
		}
		
		send_data(toSend); // Send the data
		
		// Step 3: Receive challenge response
		// Receive ["Alice" + R2 + key half, k]
		dataReceived = null;
		while(dataReceived == null) {
			dataReceived = receive_Data();
		}
		
		try {
			plainText = AES.decrypt(dataReceived, sharedKey); // Decrypt the received data
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
//		System.out.print(plainText + "\n");
		
		dataReceived = new byte[plainText.length()];
		dataReceived = plainText.getBytes();
		addressReceived = new byte[clientAddress.length()];

		random2 = plainText.substring(clientAddress.length(), clientAddress.length() + String.valueOf(R2).length());
		keyHalf = plainText.substring(clientAddress.length() + String.valueOf(R2).length(), plainText.indexOf('\0'));
		
		for(int i = 0; i < addressReceived.length; i++) {
			addressReceived[i] = dataReceived[i];
		}
		
		address = new String(addressReceived);
		clientKeyHalf = Integer.parseInt(keyHalf);
		
		if(clientAddress.equals(address) && random2.equals(String.valueOf(R2))) { // Check the challenge response
			// Successfully authenticated!
			// Compute session key
			toSend = new byte[1];
			toSend[0] = 1;
			send_data(toSend);
			sessionKey = String.valueOf((long)Math.pow(clientKeyHalf, b) % p);
			
			System.out.print("Client IP: " + clientAddress + "\n");
			System.out.print("Server IP: " + myAddress + "\n");
			System.out.print("Ra: " + R1 + "\n");
			System.out.print("Rb: " + R2 + "\n");
			System.out.print("g: " + g + "\n");
			System.out.print("p: " + p + "\n");
			System.out.print("b: " + b + "\n");
			System.out.print("g^a % p: " + clientKeyHalf + "\n");
			System.out.print("Shared key: " + sharedKey + "\n");
			System.out.print("Session key: " + sessionKey + "\n\n");
			System.out.print("Successfully connected!\n");
			
			return 1;
		}
		
		return 0;
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