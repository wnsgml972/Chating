
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ChatClient extends JFrame
{
	private JTextArea ta = new JTextArea();
	private JTextField tf = new JTextField();

	private User userSocket = new User();

	private MyPanel myPanel = new MyPanel(ta, tf, userSocket);
	private LoginPanel loginPanel = new LoginPanel(this, myPanel, userSocket);
	
	private JFrame myFrame = this;
	
	public ChatClient()
	{
		setSize(400, 500);
		setTitle("JunKing TALK");
		setLayout(new BorderLayout());
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		ta.setEditable(false);
		setContentPane(loginPanel);
		makeTrayIcon();
		startConnect();
	}
	
	/* 
	 * public Method
	 */
	public void startConnect()
	{
		userSocket.setup();
		loginPanel.initRequestFocus();
	}
	public void startChat()
	{
		new ServiceThread().start();
	}
	public void makeTrayIcon()
	{
	      PopupMenu menu = new PopupMenu("My Menu");
	      MenuItem exititem = new MenuItem("Exit");
	      exititem.addActionListener(new ActionListener() {               
	         @Override
	         public void actionPerformed(ActionEvent e) {
	            System.exit(1);                  
	         }
	      });
	      menu.add(exititem);
	      
	      
	      SystemTray tray = SystemTray.getSystemTray();
	      TrayIcon myTray = new TrayIcon(Toolkit.getDefaultToolkit().getImage("Image//exit.png"),"Chat", menu);
	      try {
	         tray.add(myTray);
	      } catch (AWTException e1) {
	         System.out.println(e1.getMessage());
	      }
	      myTray.addMouseListener(new MouseAdapter() {
	         @Override
	         public void mouseClicked(MouseEvent e) {
	            if(e.getButton() == MouseEvent.BUTTON1)
	            {
	            	myFrame.setState(JFrame.NORMAL);
	            	myFrame.setAlwaysOnTop(false);
	            }
	         }
	         @Override
	        public void mouseReleased(MouseEvent e) {
	        	 if(e.getButton() == MouseEvent.BUTTON1)
	        	 {
	        		 myFrame.setAlwaysOnTop(true);
	        	 }
	        }
	      });
	}
	
	
	/* 
	 * Inner Class
	 */
	
	private class ServiceThread extends Thread
	{
		public ServiceThread()
		{
			tf.addActionListener(new ActionListener() 
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					String message = tf.getText();
					if(message.equals(""))
						return;
					
					userSocket.write("login" + userSocket.getName() + ">> " + message);
					tf.setText("");
				}
			});
		}
		
		@Override
		public void run()
		{
			while(true)
			{
				String message = userSocket.read();
				
/*				if(message.equals("#$file")){
					userSocket.write("file go");
					userSocket.fRead();
				}*/
				
				ta.append(message + "\n");
				ta.setCaretPosition(ta.getText().length());
			}
		}
	}
	
	public static void run(){
		new ChatClient();
	}
}
