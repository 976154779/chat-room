package client;

import java.awt.*;
import java.awt.event.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;

import server.DBHandler;

import java.io.*;
import java.net.*;


public class LoginFrame extends JFrame implements ActionListener {
	
	String ip = "127.0.0.1";//连接到服务端的ip地址
	int port = 8888;//连接到服务端的端口号
	
	private JLabel jLabel1;//聊天系统 标签
	private JLabel jLabel2;//用户名 标签
	private JLabel jLabel3;//密码 标签
	
	JTextField jTextFieldName;//用户名输入框
	private JPasswordField jPasswordFieldPw;//密码输入框
	
	private JButton jButtonLogin;//登陆按钮
	private JButton jButtonRegister;//注册按钮
	private JButton jButtonManage;//管理按钮
	private JButton jButtonConf;//设置按钮
	
	
	private JPanel jPanel1;
	
	Image  image;
	ImageIcon icon;

	public LoginFrame() {	
		init();
	}

	//初始化所有控件
	private void init() {
		

		//程序图标
		try {
			String src = "icon.png";//图片路径
			image=ImageIO.read(this.getClass().getResource(src));//创建图片对象
			this.setIconImage(image);//设置图标

		} catch (IOException e) {
				e.printStackTrace();
		} 
		
		jPanel1 = new JPanel();
	    
		jTextFieldName = new JTextField();
		jPasswordFieldPw = new JPasswordField();
		
		jButtonLogin = new JButton("登录");
		jButtonRegister = new JButton("注册");
		jButtonManage = new JButton("管理");
		jButtonConf = new JButton("连接设置");
		
		jLabel1 = new JLabel();
		jLabel2 = new JLabel();
		jLabel3 = new JLabel();
		
		getContentPane().setLayout(null);
		
		getContentPane().add(jLabel1);
		jLabel1.setText("聊天系统");
		//设置label中的字体和大小，并用粗体显示
		jLabel1.setFont(new Font("", Font.BOLD, 30));
		jLabel1.setBounds(200, 100, 400, 50);
		
		getContentPane().add(jPanel1);
		jPanel1.setLayout(null);
		
		jPanel1.add(jLabel2);
		jLabel2.setText("用户名：");
		jLabel2.setPreferredSize(new Dimension(50, 30));
		jLabel2.setBounds(100, 150, 100, 30);
		
		jPanel1.add(jTextFieldName);
		jTextFieldName.setBounds(200, 150, 150, 30);
		
		jLabel3.setText("密 码：");
		jLabel3.setBounds(100, 200, 100, 30);
		jPanel1.add(jLabel3);
		
		jPanel1.add(jPasswordFieldPw);
		jPasswordFieldPw.setBounds(200, 200, 150, 30);
		
		
		jPanel1.add(jButtonLogin);
		jButtonLogin.setBounds(50, 250, 80, 20);
		jButtonLogin.addActionListener(this);
		
		jPanel1.add(jButtonRegister);
		jButtonRegister.addActionListener(this);
		jButtonRegister.setBounds(150, 250, 80, 20);
		
		jPanel1.add(jButtonManage);
		jButtonManage.setBounds(250, 250, 80, 20);
		jButtonManage.addActionListener(this);
		
		jPanel1.add(jButtonConf);
		jButtonConf.setBounds(350, 250, 100, 20);
		jButtonConf.addActionListener(this);
		
		jPanel1.setBounds(0, 0, 500, 500);
		jPanel1.setVisible(true);
			
		
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		//把窗口放在屏幕中间
		this.setPreferredSize(new Dimension(500, 500));
		this.setBounds(screenSize.width/2-250, screenSize.height/2-250, 500, 500);
		this.setVisible(true);
		setResizable(false);
		pack();
	}


    //处理按钮事件
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("登录")) {
			//如果用户按下“登陆”按钮
			boolean isAuthrized = DBHandler.isAuthorized(jTextFieldName.getText(), jPasswordFieldPw.getText());
			boolean isOnline=DBHandler.isOnline(jTextFieldName.getText());
			
		if (isAuthrized==true && isOnline==false) {
				
				ChatClient client=new ChatClient(this,ip,port);//进入客户端界面
				client.Connect();
				
			}
			else if(isAuthrized==true && isOnline==true){
				JOptionPane.showMessageDialog(this, "当前用户已登录！无法重复登录！");
				return;
			}
			else{
				JOptionPane.showMessageDialog(this, "用户名或密码不正确！");
				return;
			}
		} else if (e.getActionCommand().equals("注册")) {
			//如果用户按下“注册”按钮
			RegisterFrame rgf = new RegisterFrame(this);//进入注册界面
			this.setVisible(false);
		} else if (e.getActionCommand().equals("管理")){
			//如果用户按下“管理”按钮
			if (DBHandler.isAdmin(jTextFieldName.getText(), jPasswordFieldPw.getText())) {
				ManageFrame mf = new ManageFrame(this);
				this.setVisible(false);
			} else {
				JOptionPane.showMessageDialog(this, "用户名密码不正确！");
				return;
			}
		}else {
			//如果用户按下“设置”按钮
			//调出连接设置对话框
			ConnectConf conConf = new ConnectConf(this,ip,port);
			conConf.show();
			ip = conConf.userInputIp;
			port = conConf.userInputPort;
			
		}
	}



	public static void main(String args[]) {
		LoginFrame login=new LoginFrame();
	}
}

