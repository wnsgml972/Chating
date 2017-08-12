
import java.io.*;
import java.net.*;

import javax.swing.JOptionPane;

public class User 
{
	private Socket socket = null;
	private BufferedReader in = null;
	private BufferedWriter out = null;	
	private String name = null;	
	
	private FileOutputStream fOut = null;
	private FileInputStream fIn = null;
	private DataOutputStream dOut = null;
	private DataInputStream dIn = null;
	
	private AES aes = new AES();
	
	public void setup(){
		try {
			socket = new Socket("localhost", 9995);	//준희 컴퓨터 IP
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			dOut = new DataOutputStream(socket.getOutputStream());
			dIn = new DataInputStream(socket.getInputStream());
			
		} catch (IOException e) {
			handleError(e);
		}
	}
	public void closeSocket(){
		try {
			socket.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	public void handleError(Exception e)
	{
		System.out.println(e.getMessage());
		e.printStackTrace();
	}
	public boolean check(String ID, String PW)
	{
		boolean value = false;
		
		write(ID + "MySecretNumber19940121" + PW);
		String tf = read();
		value = Boolean.parseBoolean(tf);

		return value;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getName()
	{
		return name;
	}
	
/*	public void fRead()
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
//			dOut.close();
			
			System.out.println("(client 파일 읽기 완료)");
		} catch (Exception e) {
			handleError(e);
		}
	}
	
	public void fWrite(String fileName)
	{
		try {
			fIn = new FileInputStream(new File("크레이지아케이드.txt"));
			byte[] buffer = new byte[1024];
			int len;
			int data = 0;
			
			while((len = fIn.read(buffer))>0){
				data++;
			}
			
			int datas = data;
			
			fIn.close();
			fIn = new FileInputStream(new File("크레이지아케이드.txt"));
			dOut.writeInt(data);
			dOut.writeUTF("크레이지아케이드.txt");
			
			len = 0;
			
			for(;data>0;data--){
				len = fIn.read(buffer);
				dOut.write(buffer, 0, len);
			}
			
			System.out.println("(client) 파일 읽기 완료");
		} catch (Exception e) {
			handleError(e);
		}
	}*/
	
	public void write(String text)
	{
		try {
			text = aes.encrypt(text);
			out.write(text + "\n");
			out.flush();
		} catch (Exception e) {
			handleError(e);
		}
	}
	public String read() 
	{
		String text = null;
		if (socket != null) {
			try {
				text = in.readLine();
				new SoundClass("Sounds//클릭.wav").play();
//				System.out.println(text);
				text = aes.decrypt(text);
//				System.out.println(text);
			} catch (Exception e) {
				handleError(e);
				if(text == null)	//서버가 꺼져서 메시지를 받아오지 않는 경우
				{
					JOptionPane.showMessageDialog(null, "The server is out");
					closeSocket();
					System.exit(1);
				}
			}
		}
		return text;
	}
	public BufferedWriter getOutputReader(){
		return out;
	}
	public BufferedReader getInputReader(){
		return in;
	}
	public Socket getSocket(){
		return socket;
	}
}
