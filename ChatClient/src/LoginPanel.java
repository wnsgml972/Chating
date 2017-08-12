
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;


public class LoginPanel extends JPanel 
{
	private JButton loginBtn = new JButton("Login");
	private JButton joinBtn = new JButton("Join");
	private JTextField idTextField = new JTextField();
	private JPasswordField passWordTextField = new JPasswordField();

	private ChatClient mainFrame;
	private MyPanel myPanel;
	private User userSocket;

	public LoginPanel(ChatClient mainFrame, MyPanel myPanel, User userSocket) {
		this.mainFrame = mainFrame;
		this.myPanel = myPanel;
		this.userSocket = userSocket;

		mainFrame.setBackground(new Color(156, 161, 220));
		setSize(400, 500);
		setLayout(null);

		makeBtn();
		makeTextField();
	}
	
	public void makeTextField()
	{
		idTextField.setBounds(80, 265, 260, 40);
		passWordTextField.setBounds(80, 320, 260, 40);
		passWordTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) 
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER) 
				{
					String name = idTextField.getText();
					String passWord = passWordTextField.getText();
					String text = name + "MySecretNumber19940121" + passWord + "MySecretNumber19940121" + "login";

					if (name.equals("") || passWord.equals("")) {
						userDialog("올바르지 않은 ID or PassWord 입니다");
						return;
					}

					// 서버에서 검사하는 부분
					userSocket.write(text);
					String value = userSocket.read();

					if (value.equals("true")) {
						mainFrame.setContentPane(myPanel);
						mainFrame.startChat();
						mainFrame.setTitle(name);
						mainFrame.revalidate();
						userSocket.setName(name);
					} else if (value.equals("do not exist")) {
						userDialog("존재하지 않는 회원입니다.");
					} else if (value.equals("be logged in")) {
						userDialog("로그인 중인 아이디입니다.");
					} else {
						userDialog("올바르지 않은 ID or PassWord 입니다.");
					}
				}
			}
		});
		
		add(idTextField);
		add(passWordTextField);		
	}

	public void makeBtn()
	{
		loginBtn.setBounds(80, 385, 90, 30);
		joinBtn.setBounds(180, 385, 90, 30);

		joinBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String name = idTextField.getText();
				String passWord = passWordTextField.getText();
				String text = name + "MySecretNumber19940121" + passWord + "MySecretNumber19940121" + "join";
				
				if (name.equals("") || passWord.equals(""))
				{
					userDialog("올바르지 않은 ID or PassWord 입니다");
					return;
				}
				
				//서버에서 검사하는 부분
				userSocket.write(text);
				String value = userSocket.read();

				if(value.equals("true")) {
					userDialog("회원가입 완료");
				}
				else if(value.equals("already used")) {
					userDialog("이미 사용중인 아이디입니다.");
				}
				else {
					userDialog("올바르지 않은 ID or PassWord 입니다");
				}
			}
		});

		loginBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String name = idTextField.getText();
				String passWord = passWordTextField.getText();
				String text = name + "MySecretNumber19940121" + passWord + "MySecretNumber19940121" + "login";

				if (name.equals("") || passWord.equals("")) {
					userDialog("올바르지 않은 ID or PassWord 입니다");
					return;
				} 

				//서버에서 검사하는 부분
				userSocket.write(text);
				String value = userSocket.read();
				
				if(value.equals("true")) {
					mainFrame.setContentPane(myPanel);
					mainFrame.startChat();
					mainFrame.setTitle(name);
					mainFrame.revalidate();
					userSocket.setName(name);
				}
				else if(value.equals("do not exist")) {
					userDialog("존재하지 않는 회원입니다.");
				}
				else if(value.equals("be logged in")) {
					userDialog("로그인 중인 아이디입니다.");
				}
				else {
					userDialog("올바르지 않은 ID or PassWord 입니다.");
				}
			}
		});
		
		add(loginBtn);
		add(joinBtn);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		g.setFont(new Font("맑은 고딕", Font.BOLD, 20));
		g.drawString("ID", 40, 290);
		g.drawString("PW", 35, 345);
		
		g.drawImage(ImageCollection.titleImg, 90, 20, 220, 220, this);
	}
	public void initRequestFocus()
	{
		idTextField.requestFocus();
	}
	
	private void userDialog(String message) {
		JOptionPane.showMessageDialog(mainFrame, message);
		clearTextField();
		idTextField.requestFocus();
	}

	private void clearTextField() {
		idTextField.setText("");
		passWordTextField.setText("");
	}
}