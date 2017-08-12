
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.event.*;
import java.io.File;
import java.awt.*;

public class MyPanel extends JPanel 
{
	private JTextArea ta;
	private JTextField tf;
	private JToolBar tb;
	private MyPanel myPanel = this;
	
	private User userSocket;
	
	public MyPanel(JTextArea ta, JTextField tf, User userSocket) 
	{
		this.userSocket = userSocket;
		this.ta = ta;
		this.tf = tf;
		
		setSize(400, 500);
		setLayout(new BorderLayout());
		
		ta.setFont(new Font("아이럽우유",Font.PLAIN, 15));
		ta.setForeground(Color.white);
		ta.setBackground(new Color(156, 161, 220));
		ta.setMargin(new Insets(30, 30, 30, 30));
		ta.setLineWrap(true);
		
		tf.setFont(new Font("아이럽우유",Font.PLAIN, 15));
		tf.setMargin(new Insets(10, 10, 10, 10));

		add(new JScrollPane(ta), BorderLayout.CENTER);
		add(tf, BorderLayout.SOUTH);
		makeToolbar();
		
	}
	
	public void makeToolbar() 
	{				
		tb = new JToolBar() 
		{
			@Override
			public void paintComponent(Graphics g) 
			{
				super.paintComponent(g);
//				g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
			}
		};
		
		//대신 이미지 넣을 것
		JButton roomBtn = new JButton("Room");
		JButton chatBtn = new JButton("Chat");
		JButton fileBtn = new JButton("File");
		JButton exitBtn = new JButton("Exit");
		
		
//		roomBtn.setPressedIcon(pressedIcon);
		roomBtn.setOpaque(false);
		roomBtn.setBorder(null);
		roomBtn.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				fileBtn.setEnabled(false);	//채팅에 들어갈 시 사용 가능
			}
		});
		

//		chatBtn.setPressedIcon(pressedIcon);
		chatBtn.setOpaque(false);
		chatBtn.setBorder(null);
		chatBtn.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				fileBtn.setEnabled(true);	//채팅에 들어갈 시 사용 가능
			}
		});
		
		
//		fileBtn.setPressedIcon(pressedIcon);
//		fileBtn.setDisabledIcon(ImageCollection.startBtnOffIcon);
		fileBtn.setEnabled(false);	//채팅에 들어갈 시 사용 가능
		fileBtn.setOpaque(false);
		fileBtn.setBorder(null);
		fileBtn.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser chooser = new JFileChooser();
	            FileNameExtensionFilter filter = new FileNameExtensionFilter(
	                "JPG & GIF Images", "jpg", "gif", "png");

	            chooser.setDialogTitle("JunKing TALK");
	            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

				chooser.setFileFilter(filter);
				int ret = chooser.showOpenDialog(myPanel);
				if(ret == JFileChooser.APPROVE_OPTION)
				{
					File selectedFile = chooser.getSelectedFile();
					String fileName = chooser.getSelectedFile().getName();
					
					userSocket.write("login" + userSocket.getName() + ">> " + fileName);					
				}
			}
		});
		

//		exitBtn.setPressedIcon(pressedIcon);
		exitBtn.setOpaque(false);
		exitBtn.setBorder(null);
		exitBtn.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				fileBtn.setEnabled(false);	//채팅에 들어갈 시 사용 가능
			}
		});
		
		tb.add(roomBtn);
		tb.addSeparator();
		tb.add(chatBtn);
		tb.addSeparator();
		tb.add(fileBtn);
		tb.addSeparator();
		tb.add(exitBtn);
		tb.addSeparator();

		tb.setFloatable(false);
		tb.setBackground(Color.LIGHT_GRAY);

		this.add(tb, BorderLayout.NORTH);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		
	}
}