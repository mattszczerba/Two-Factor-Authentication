
import java.io.*;
import java.net.*;
import java.util.Scanner;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base32;
public class Server {
    ServerSocket serversocket;
    Socket client;
    BufferedReader input;
    PrintWriter output;
    int port = 9090;
    String hostName = "localhost";
    File dataFile = new File("UserData.txt");
	Scanner in = new Scanner(System.in);
	String userID, password, token, s;
	Boolean userExists = false;
	
    public void start() throws IOException{
        System.out.println("Connection Starting on port:" + port);
        serversocket = new ServerSocket(port);
        client = serversocket.accept();
        System.out.println("Waiting for connection from client");
        try {
            logInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        client.close();
    }

    public void logInfo() throws Exception{
        //open buffered reader for reading data from client
        input = new BufferedReader(new InputStreamReader(client.getInputStream()));
        
        BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("UserData.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
        String inputUser = input.readLine();
        String inputToken = input.readLine();
        String inputPW = input.readLine();

        //open printwriter for writing data to client
        output = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));

        if (br != null) {
			while ((s = br.readLine()) != null) {
				String[] splitted = s.split(",");
				if (inputUser.equalsIgnoreCase(splitted[0]) && inputPW.contentEquals(splitted[1]) && inputToken.contentEquals(generateAuthenticationCode(splitted[2]))) {
					output.println("Login Successful");
					userExists = true;
					break;
				}
				if (inputUser.equalsIgnoreCase(splitted[0]) && !inputPW.contentEquals(splitted[1]) && inputToken.contentEquals(generateAuthenticationCode(splitted[2]))) {
					output.println("Incorrect password");
					userExists = true;
					break;
				}
				if (inputUser.equalsIgnoreCase(splitted[0]) && inputPW.contentEquals(splitted[1]) && !inputToken.contentEquals(generateAuthenticationCode(splitted[2]))) {
					output.println("Incorrect token");
					userExists = true;
					break;
				}
			}
			
			if (userExists == false) {
				output.println("User doesn't Exist");
			}
			
		}
        output.flush();
        output.close();

    }

    public String generateAuthenticationCode(final String secret) throws Exception {
    	int verificationCode = generateVerificationCode(secret);
    	return String.format("%06d", verificationCode);
    }
    	 
    private int generateVerificationCode(final String secret) throws Exception {
    	Base32 base32 = new Base32();
    	byte[] key = base32.decode(secret);
    	 
    	byte[] timeCounter = new byte[8];
    	 
    	// Calculate time
    	long time = System.currentTimeMillis() / 1000 / 30;
    	 
    	// Determine time counter from time
    	    for (int i = timeCounter.length - 1; time > 0; i--) {
    	        timeCounter[i] = (byte) (time & 0xFF);
    	        time >>= 8;
    	    }
    	 
    	    // Encrypt the data with the key and return the HMAC SHA1 of it in hex
    	    SecretKeySpec signKey = new SecretKeySpec(key, "HmacSHA1");
    	    Mac mac = Mac.getInstance("HmacSHA1");
    	    mac.init(signKey);
    	 
    	    // HMAC result as 20-byte String
    	    byte[] hmacString = mac.doFinal(timeCounter);
    	 
    	    // Take byte at index 19
    	    int lastByte = hmacString[hmacString.length - 1];
    	 
    	    // Take lower-order 4 bits
    	    int offset = lastByte & 0xf;
    	 
    	    int offsetValue = 0;
    	 
    	    // Loop over the following 3 indices
    	    for (int i = offset; i <= offset + 3; i++) {
    	        // Get bytes of next offset index
    	        int nextByte = hmacString[i] & 0xff;
    	 
    	        // Shift bytes to the left
    	        offsetValue = offsetValue << 8;
    	 
    	        // Add bytes of next offset index
    	        offsetValue = offsetValue | nextByte;
    	    }
    	 
    	    // Cut off first bit
    	    offsetValue = offsetValue & 0x7fffffff;
    	 
    	    // Take the last 6 digits as verification code
    	    return offsetValue % 1000000;
    	}
    public static void main(String[] args){
        Server server = new Server();
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }  
}