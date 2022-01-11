import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.rmi.UnknownHostException;
import java.security.MessageDigest;
import java.util.Scanner;

import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.eclipse.swt.SWT;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class Auth {

	protected Shell shlLoginClient;
	private Text txtID;
	private Text txtToken;
	private Text txtPW;
	int port = 9090;
    String hostName = "localhost";
    private Text txtStatus;
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) throws IOException{
		try {
			Auth window = new Auth();
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
		shlLoginClient.open();
		shlLoginClient.layout();
		while (!shlLoginClient.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlLoginClient = new Shell();
		shlLoginClient.setSize(520, 405);
		shlLoginClient.setText("Login Client");
		
		Label lblUsername = new Label(shlLoginClient, SWT.NONE);
		lblUsername.setFont(SWTResourceManager.getFont("Segoe UI", 20, SWT.NORMAL));
		lblUsername.setBounds(39, 39, 134, 43);
		lblUsername.setText("Username:");
		
		Label lblToken = new Label(shlLoginClient, SWT.NONE);
		lblToken.setFont(SWTResourceManager.getFont("Segoe UI", 20, SWT.NORMAL));
		lblToken.setBounds(39, 88, 90, 37);
		lblToken.setText("Token:");
		
		Label lblPassword = new Label(shlLoginClient, SWT.NONE);
		lblPassword.setFont(SWTResourceManager.getFont("Segoe UI", 20, SWT.NORMAL));
		lblPassword.setBounds(39, 131, 134, 37);
		lblPassword.setText("Password:");
		
		txtID = new Text(shlLoginClient, SWT.BORDER);
		txtID.setFont(SWTResourceManager.getFont("Segoe UI", 15, SWT.NORMAL));
		txtID.setBounds(189, 43, 244, 31);
		
		txtToken = new Text(shlLoginClient, SWT.BORDER);
		txtToken.setFont(SWTResourceManager.getFont("Segoe UI", 15, SWT.NORMAL));
		txtToken.setBounds(189, 88, 244, 31);
		
		txtPW = new Text(shlLoginClient, SWT.BORDER | SWT.PASSWORD);
		txtPW.setFont(SWTResourceManager.getFont("Segoe UI", 15, SWT.NORMAL));
		txtPW.setBounds(189, 135, 244, 31);
		
		// login button press actions
		Button btnLogin = new Button(shlLoginClient, SWT.NONE);
		btnLogin.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				if (e.getSource().equals(btnLogin)) {
					try {
						loginServer(); 
					} catch (UnknownHostException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					
				}
			}
		});
		
		
		btnLogin.setFont(SWTResourceManager.getFont("Segoe UI", 20, SWT.NORMAL));
		btnLogin.setBounds(189, 274, 119, 43);
		btnLogin.setText("Login");
		
		//clear button press actions
		Button btnClear = new Button(shlLoginClient, SWT.NONE);
		btnClear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				txtID.setText("");
				txtPW.setText("");
				txtToken.setText("");
				txtStatus.setText("");
			}
		});
		btnClear.setText("Clear");
		btnClear.setFont(SWTResourceManager.getFont("Segoe UI", 20, SWT.NORMAL));
		btnClear.setBounds(314, 274, 119, 43);
		
		txtStatus = new Text(shlLoginClient, SWT.BORDER);
		txtStatus.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_RED));
		txtStatus.setFont(SWTResourceManager.getFont("Segoe UI", 19, SWT.NORMAL));
		txtStatus.setEditable(false);
		txtStatus.setBounds(76, 201, 357, 37);

	}
	
	public void loginServer() throws UnknownHostException, IOException{
		String userID, password, pwHex , token;
		Socket socket = new Socket(hostName,port);
		PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
	    
		
	    userID = txtID.getText(); //input username to server
		output.println(userID);

		token = txtToken.getText(); //input token to server
		output.println(token);
		
		password = txtPW.getText(); //input password to server
		try { //one-way sha-256 password hash
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			digest.update(password.getBytes(StandardCharsets.UTF_8));
			byte[] pwHash = digest.digest();
			pwHex = String.format("%064x", new BigInteger(1, pwHash));
			output.println(pwHex);
			output.flush();
		}catch(Exception e1) {
            e1.printStackTrace();
        }
		
		BufferedReader read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		txtStatus.setText((read.readLine())); //reads server response
		read.close();
		socket.close();
	}
}
