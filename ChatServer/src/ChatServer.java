
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.Vector;

public class ChatServer extends JFrame
{
	public Vector<Receiver> users = new Vector<Receiver>();	//같은 패키지 내에서 쓰임
	
	private JTextArea ta = new JTextArea();
	private JTextField tf = new JTextField();

	private MyPanel myPanel = new MyPanel(ta, tf, this);
	
	private AES aes = new AES();
	private DataAccessor myDataBase = DataAccessor.getInstance();
	
	public ChatServer()
	{
		setSize(400, 500);
		setTitle("JunKing Talk (Administer)");
		setLayout(new BorderLayout());
		setContentPane(myPanel);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		ta.setEditable(false);
		
		startConnect();

		setVisible(true);
		
		//프로그램 종료시 DB도 close
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				myDataBase.allMemberLogout("true");
				myDataBase.closeDB();
			}
		});
	}
	
	
	/* 
	 * private Method
	 */
	private void errorHandler(Exception e)
	{
		System.out.println(e.getMessage());
		e.printStackTrace();
	}

	
	/* 
	 * public Method
	 */
	public void startConnect()
	{
		myDataBase.connectDB();
		new Receiver().start();
	}

	/* 
	 * Inner Class
	 * 1. Receiver Thread
	 * 2. Service Thread
	 * 3. ActionListener
	 */	
	public class Receiver extends Thread
	{
		private BufferedReader in = null;
		private BufferedWriter out = null;
		private ServerSocket listener = null;
		private Socket socket = null;

		private FileOutputStream fOut = null;
		private FileInputStream fIn = null;
		private DataOutputStream dOut = null;
		private DataInputStream dIn = null;
		
		private boolean login = false;
		private String name;
		private String ip;
		private String fileName;
		
		public Receiver()
		{
			try {
				listener = new ServerSocket(9995);
			} catch (IOException e) {
				errorHandler(e);
			}
		}
		public Receiver(BufferedReader in, BufferedWriter out, ServerSocket listener, Socket socket
				, DataOutputStream dOut, DataInputStream dIn)
		{
			this.in = in;
			this.out = out;
			this.listener = listener;
			this.socket = socket;
			this.dOut = dOut;
			this.dIn = dIn;
			ip = socket.getInetAddress().toString().substring(1);
		}
/*		public String getFileName(){
			return fileName;
		}
		public void setFileName(String fileName){
			this.fileName = fileName;
		}
		
		public void wRead()
		{
			try {
				int data = dIn.readInt();
				String fileName = dIn.readUTF();
				File file = new File(fileName);
				
				int datas = data;
				byte[] buffer = new byte[1024];
				int len;
				
				for(;data>0;data--){
					len = socket.getInputStream().read(buffer);
					dOut.write(buffer, 0, len);
				}
				
				dOut.flush();
				dOut.close();
				
				System.out.println("(server 파일 읽기 완료)");
			} catch (Exception e) {
				errorHandler(e);
			}
		}
		
		public void fWrite(String fileName)
		{
			try {
				fIn = new FileInputStream(new File(fileName));
				byte[] buffer = new byte[1024];
				int len;
				int data = 0;
				
				while((len = fIn.read(buffer))>0){
					data++;
				}
				
				int datas = data;
				
				fIn.close();
				fIn = new FileInputStream(new File(fileName));
				dOut.writeInt(data);
				dOut.writeUTF(fileName);
				
				len = 0;
				
				for(;data>0;data--){
					len = fIn.read(buffer);
					dOut.write(buffer, 0, len);
				}
				
				System.out.println("(server) 파일 읽기 완료");
			} catch (Exception e) {
				errorHandler(e);
			}
		}*/
		
		public String read()
		{
			String message = "";
			try {
				message = in.readLine();
				message = aes.decrypt(message);
			} catch (Exception e) {
				errorHandler(e);
			}
			return message;
		}
		
		public void write(String message)
		{
			try {
				message = aes.encrypt(message);
				out.write(message + "\n");
				out.flush();
			} catch (Exception e) {
				errorHandler(e);
			}
		}
		
		@Override
		public void run() 
		{
			while (true) 
			{
				try {
					System.out.println("연결대기중");
					socket = listener.accept();
					System.out.println("연결");
					
					in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
					dIn = new DataInputStream(socket.getInputStream());
					dOut = new DataOutputStream(socket.getOutputStream());
					
					users.addElement(new Receiver(in, out, listener, socket, dOut, dIn));
					new ServiceThread(in, out, socket).start();
					
				} catch (IOException e)	{
					errorHandler(e);
				}
			}
		}
	}
	private class ServiceThread extends Thread 
	{
		private BufferedReader in = null;
		private BufferedWriter out = null;
		private Socket socket = null;
		
		public ServiceThread(BufferedReader in, BufferedWriter out, Socket socket) 
		{
			this.in = in;
			this.out = out;
			this.socket = socket;
			tf.addActionListener(new MyActionListener());
		}
		
		public String read() throws IOException
		{
			String message = in.readLine();
//			System.out.println(message);
			try {
				message = aes.decrypt(message);
//				System.out.println(message);
			} catch (Exception e) {
				errorHandler(e);
			}
			return message;
		}
		
		@Override
		public void run()
		{
			while(true)
			{
				try {
				String message = read();	//총 메시지	 @@@
				System.out.println(message);
				if(message == null)
					continue;
				String checkMessage = message.substring(0, 5);
				
				if(checkMessage.equals("login"))
				{
					for(int i=0; i<users.size(); i++)
					{
						Receiver r = users.get(i);
						if(r.login)
							r.write(message.substring(5));
					}

					ta.append(message.substring(5) + "\n");
					ta.setCaretPosition(ta.getText().length());
				}
/*				else if(message.equals("file go"))
				{
					ta.append("Administer>> 파일전송 대기\n");
					ta.setCaretPosition(ta.getText().length());
					
					for(int i=0; i<users.size(); i++)
					{
						Receiver r = users.get(i);
						if(r.login)
							r.fWrite(r.getFileName());
					}

					ta.append("Administer>> 파일전송 완료\n");
					ta.setCaretPosition(ta.getText().length());

				}*/
				else
				{
					//String text = name + "MySecretNumber19940121" + passWord + "MySecretNumber19940121" + "join";
					String []spiltText = message.split("MySecretNumber19940121");
					String name = spiltText[0];
					String password = spiltText[1];
					String state = spiltText[2];
					
					Receiver r = null;
					int index = 100;
					
					for(int i=0; i<users.size(); i++)
					{
						if(users.get(i).socket == socket)
						{
							index = i;
							r = users.get(i);
							break;
						}
					}
					
					if(state.equals("join"))
					{
						if(!myDataBase.joinCheck(name)) {
							r.write("already used");
						}
						else {
							if (myDataBase.memberJoin(name, password)) {
								r.write("true");
							} else {
								r.write("false");
							}
						}
					}
					else if(state.equals("login"))
					{
						if(myDataBase.joinCheck(name)) {
							r.write("do not exist");
						}
						else if(myDataBase.loginCheck(name)) {
							r.write("be logged in");
						}
						else {
							if (myDataBase.memberLogin(name, password)) {
								users.get(index).name = name;
								users.get(index).login = true;
								
								String msg = name + "님이 입장 하셨습니다.";
								ta.append(msg + " (" + users.get(index).ip  + ") " +"\n\n");
								ta.setCaretPosition(ta.getText().length());
								r.write("true");
								for(int i=0; i<users.size(); i++)
								{
									Receiver r1 = users.get(i);
									if(r1.login)
										r1.write(msg + "\n");
								}
							} else {
								r.write("false");
							}
						}
					}
					else {
						System.out.println("뭐야이럴리가없어!");
					}
				}
				
				}catch(IOException e)	//클라이언트가 채팅창을 나갈시 이리로 옴
				{
					try {
						for(int i=0; i<users.size(); i++)
						{
							if(users.get(i).socket == socket)
							{
								if(users.get(i).name == null)
									continue;
								myDataBase.memberLogout(users.get(i).name);
								String msg = users.get(i).name + "님이 방을 나가셨습니다.";
								ta.append(msg + " (" + users.get(i).ip  + ") " + "\n\n");
								ta.setCaretPosition(ta.getText().length());
								users.get(i).login = false;
								for(int j=0; j<users.size(); j++)
								{
									Receiver r1 = users.get(j);
									if(r1.login)
										r1.write(msg + "\n");
								}
								
								out.close();
								socket.close();
								users.remove(i);
							}
						}
					} catch (IOException e1) {
						errorHandler(e1);
					}
					errorHandler(e);
					return;
				}
			}
		}
	}
	private class MyActionListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			String message = tf.getText();
			if(message.equals(""))
				return;
			
			ta.append("Administer>> " + message + "\n");
			ta.setCaretPosition(ta.getText().length());
			
			for(int i=0; i<users.size(); i++)
			{
				Receiver r = users.get(i);
				if(r.login)
					r.write("Administer>> " + message);
			}
			tf.setText("");
		}
	}
	
	
	/* 
	 * main Method
	 */
	public static void main(String[] args) 
	{
		new ChatServer();
	}
}



/*
 * 1. 파일 통신(소켓)
 * 2. 방 구현하기
 * 3. 디자인
 */


