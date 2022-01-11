import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base32;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

public class TokenGeneratorTigerking {

	protected Shell shlTokenGenerator;
	private Text txtToken;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			TokenGeneratorTigerking window = new TokenGeneratorTigerking();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shlTokenGenerator.open();
		shlTokenGenerator.layout();
		while (!shlTokenGenerator.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents(){
		shlTokenGenerator = new Shell();
		shlTokenGenerator.setSize(283, 202);
		shlTokenGenerator.setText("Token Generator");
		
		Button btnGenerate = new Button(shlTokenGenerator, SWT.NONE);
		btnGenerate.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				String key = "36X52SVEULD2434ACJSWS66GQYDJGOKW"; //key for tigerking
				try {
					String verificationCode = generateCodeString(key);
					txtToken.setText(verificationCode);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnGenerate.setBounds(80, 116, 106, 37);
		btnGenerate.setText("Generate");
		
		txtToken = new Text(shlTokenGenerator, SWT.BORDER);
		txtToken.setFont(SWTResourceManager.getFont("Segoe UI", 20, SWT.BOLD));
		txtToken.setEditable(false);
		txtToken.setBounds(39, 44, 187, 48);

	}
	private static int generateVerificationCode(final String secret) throws Exception {
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
	public String generateCodeString(final String secret) throws Exception {
	    int verificationCode = generateVerificationCode(secret);
	    return String.format("%06d", verificationCode);
	}
}
