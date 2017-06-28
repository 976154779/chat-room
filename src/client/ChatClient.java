package client;

import java.awt.*;
import java.awt.event.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;

import server.DBHandler;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.io.*;
import java.net.*;

/*
 * 聊天客户端的主框架类
 */
public class ChatClient extends JFrame implements ActionListener{
	public  Date d=new Date();
	public DateFormat sdf = new SimpleDateFormat("HH:mm:ss"); 
	
	private LoginFrame login;
	
	String ip;//连接到服务端的ip地址
	int port;//连接到服务端的端口号
	String userName;//用户名
	int type = 0;//0表示未连接，1表示已连接

	Image image;//程序图标
	
	JComboBox combobox;//选择发送消息的接受者
	JTextArea messageShow;//客户端的信息显示
	JScrollPane messageScrollPane;//信息显示的滚动条

	JLabel sendToLabel,messageLabel ;

	JTextField clientMessage;//客户端消息的发送
	JButton clientMessageButton;//发送消息
	JTextField showStatus;//显示用户连接状态
	
	Socket socket;
	ObjectOutputStream output;//网络套接字输出流
	ObjectInputStream input;//网络套接字输入流
	
	ClientReceive recvThread;

	//建立工具栏
	JToolBar toolBar = new JToolBar();
	//建立工具栏中的按钮组件
	JButton loginButton;//上线
	JButton logoffButton;//用户注销
	JButton exitButton;//退出

	//框架的大小
	Dimension faceSize = new Dimension(400, 600);

	JPanel downPanel ;
	GridBagLayout girdBag;
	GridBagConstraints girdBagCon;
	
	public ChatClient(LoginFrame login,String ip,int port){
		
		this.login=login;
		this.ip=ip;
		this.port=port;
		userName=login.jTextFieldName.getText();
		
		init();//初始化程序

		//添加框架的关闭事件处理
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		//设置框架的大小
		this.setSize(faceSize);

		//设置运行时窗口的位置
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation( (int) (screenSize.width - faceSize.getWidth()) / 2,
						 (int) (screenSize.height - faceSize.getHeight()) / 2);
		this.setResizable(false);
		this.setTitle("聊天室客户端"); //设置标题
		
		//程序图标
		try {
			String src = "icon.png";//图片路径
			image=ImageIO.read(this.getClass().getResource(src));//创建图片对象
			this.setIconImage(image);//设置图标
		} catch (IOException e) {
			 e.printStackTrace();
		}  		

	}

	/**
	 * 程序初始化函数
	 */
	public void init(){

		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//初始化按钮
		loginButton=new JButton("上线");
		logoffButton = new JButton("下线");
		exitButton = new JButton("退出" );
	
		//将按钮添加到工具栏
		toolBar.add(loginButton);
		toolBar.addSeparator();//添加分隔栏
		toolBar.add(logoffButton);
		toolBar.addSeparator();//添加分隔栏
		toolBar.add(exitButton);
		contentPane.add(toolBar,BorderLayout.NORTH);
		
		//初始时
		loginButton.setEnabled(false);
		logoffButton.setEnabled(true);

		
		//添加按钮的事件侦听
		loginButton.addActionListener(this);
		logoffButton.addActionListener(this);
		exitButton.addActionListener(this);
		
		combobox = new JComboBox();
		combobox.insertItemAt("所有人",0);
		combobox.setSelectedIndex(0);
		
		messageShow = new JTextArea();
		messageShow.setEditable(false);
		//添加滚动条
		messageScrollPane = new JScrollPane(messageShow,
			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		messageScrollPane.setPreferredSize(new Dimension(400,400));
		messageScrollPane.revalidate();
		
		clientMessage = new JTextField(23);
		clientMessage.setEnabled(false);
		clientMessageButton = new JButton();
		clientMessageButton.setText("发送");

		//添加系统消息的事件侦听
		clientMessage.addActionListener(this);
		clientMessageButton.addActionListener(this);

		sendToLabel = new JLabel("发送至:");
		messageLabel = new JLabel("发送消息:");
		downPanel = new JPanel();
		girdBag = new GridBagLayout();
		downPanel.setLayout(girdBag);

		girdBagCon = new GridBagConstraints();
		girdBagCon.gridx = 0;
		girdBagCon.gridy = 0;
		girdBagCon.gridwidth = 5;
		girdBagCon.gridheight = 2;
		girdBagCon.ipadx = 5;
		girdBagCon.ipady = 5;
		JLabel none = new JLabel("    ");
		girdBag.setConstraints(none,girdBagCon);
		downPanel.add(none);

		girdBagCon = new GridBagConstraints();
		girdBagCon.gridx = 0;
		girdBagCon.gridy = 2;
		girdBagCon.insets = new Insets(1,0,0,0);
		girdBagCon.ipadx = 5;
		girdBagCon.ipady = 5;
		girdBag.setConstraints(sendToLabel,girdBagCon);
		downPanel.add(sendToLabel);

		girdBagCon = new GridBagConstraints();
		girdBagCon.gridx =1;
		girdBagCon.gridy = 2;
		girdBagCon.anchor = GridBagConstraints.LINE_START;
		girdBag.setConstraints(combobox,girdBagCon);
		downPanel.add(combobox);

		girdBagCon = new GridBagConstraints();
		girdBagCon.gridx = 0;
		girdBagCon.gridy = 3;
		girdBag.setConstraints(messageLabel,girdBagCon);
		downPanel.add(messageLabel);

		girdBagCon = new GridBagConstraints();
		girdBagCon.gridx = 1;
		girdBagCon.gridy = 3;
		girdBagCon.gridwidth = 3;
		girdBagCon.gridheight = 1;
		girdBag.setConstraints(clientMessage,girdBagCon);
		downPanel.add(clientMessage);

		girdBagCon = new GridBagConstraints();
		girdBagCon.gridx = 4;
		girdBagCon.gridy = 3;
		girdBag.setConstraints(clientMessageButton,girdBagCon);
		downPanel.add(clientMessageButton);

		showStatus = new JTextField(35);
		showStatus.setEditable(false);
		girdBagCon = new GridBagConstraints();
		girdBagCon.gridx = 0;
		girdBagCon.gridy = 5;
		girdBagCon.gridwidth = 5;
		girdBag.setConstraints(showStatus,girdBagCon);
		downPanel.add(showStatus);

		contentPane.add(messageScrollPane,BorderLayout.CENTER);
		contentPane.add(downPanel,BorderLayout.SOUTH);
		
		//关闭程序时的操作
		this.addWindowListener(
			new WindowAdapter(){
				public void windowClosing(WindowEvent e){
					if(type == 1){
						DisConnect();
					}
					System.exit(0);
				}
			}
		);
	}

	/**
	 * 事件处理
	 */
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == loginButton) { //登录
			Connect();
		}
		if (obj == logoffButton) { //注销
			DisConnect();
			showStatus.setText("");
		}
		else if (obj == clientMessage || obj == clientMessageButton) { //发送消息
			SendMessage();
			clientMessage.setText("");
		}
		else if (obj == exitButton) { //退出
			int j=JOptionPane.showConfirmDialog(
				this,"真的要退出吗?","退出",
				JOptionPane.YES_OPTION,JOptionPane.QUESTION_MESSAGE);
			
			if (j == JOptionPane.YES_OPTION){
				if(type == 1){
					DisConnect();
				}
				System.exit(0);
			}
		}
		
	}

	
	public void Connect(){
		if(DBHandler.isOnline(userName)==true){
			JOptionPane.showConfirmDialog(
					this,"当前用户已登录！无法重复登录！\n","提示",
					JOptionPane.DEFAULT_OPTION,JOptionPane.WARNING_MESSAGE);
			return;
		}
		try{
			socket = new Socket(ip,port);
			DBHandler.Online(userName);//在数据库中设置当前用户为在线状态
		}
		catch (Exception e){
			JOptionPane.showConfirmDialog(
				this,"不能连接到指定的服务器。\n请确认连接设置是否正确。","提示",
				JOptionPane.DEFAULT_OPTION,JOptionPane.WARNING_MESSAGE);
			return;
		}
		login.setVisible(false);
		this.setVisible(true);
		try{
			output = new ObjectOutputStream(socket.getOutputStream());
			output.flush();
			input  = new ObjectInputStream(socket.getInputStream() );
			
			output.writeObject(userName);//把用户名发送给服务器
			output.flush();
			
			recvThread = new ClientReceive(socket,output,input,combobox,messageShow,showStatus);
			recvThread.start();
			
			
			loginButton.setEnabled(false);
			logoffButton.setEnabled(true);
			
			clientMessage.setEnabled(true);
			combobox.setEnabled(true);
			clientMessageButton.setEnabled(true);
			messageShow.append("["+sdf.format(d)+"]"+"  "+"连接服务器 "+ip+":"+port+" 成功...\n");
			type = 1;//标志位设为已连接
		}
		catch (Exception e){
			System.out.println(e);
			return;
		}
	}
	
	public void DisConnect(){
		loginButton.setEnabled(true);
		logoffButton.setEnabled(false);
		
		clientMessage.setEnabled(false);
		combobox.setEnabled(false);
		clientMessageButton.setEnabled(false);
		
		if(socket.isClosed()){
			return ;
		}
		
		try{
			output.writeObject("用户下线");
			output.flush();
		
			input.close();
			output.close();
			socket.close();
			messageShow.append("["+sdf.format(d)+"]"+"  "+"已经与服务器断开连接...\n");
			type = 0;//标志位设为未连接
			
			DBHandler.Offline(userName);//在数据库中设置用户当前状态为离线
		}
		catch (Exception e){
			//
		}
	}
	
	public void SendMessage(){
		String toSomebody = combobox.getSelectedItem().toString();
		String status  = "";
		
		String message = clientMessage.getText();
		
		if(socket.isClosed()){
			return ;
		}
		
		try{
			output.writeObject("聊天信息");
			output.flush();
			output.writeObject(toSomebody);
			output.flush();
			output.writeObject(message);
			output.flush();
		}
		catch (Exception e){
			//
		}
	}
}

	