package server;


import java.awt.*;
import java.awt.event.*;

import javax.imageio.ImageIO;//设置图标
import javax.swing.*;
import javax.swing.event.*;
import java.net.*;
import java.io.*;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
/*
 * 聊天服务端的主框架类
 */
public class ChatServer extends JFrame implements ActionListener{
	public  Date d=new Date();
	public DateFormat sdf = new SimpleDateFormat("HH:mm:ss"); 
	
	public static int port = 8888;//服务端的侦听端口

	ServerSocket serverSocket;//服务端Socket
	
	Image image;//图标
	
	JTextArea messageShow;//服务端的信息显示
	JScrollPane messageScrollPane;//信息显示的滚动条
	
	JLabel sendToLabel,messageLabel;
	JComboBox combobox;//选择发送消息的接受者
	
	JTextField sysMessage;//服务端消息的发送
	JButton sysMessageButton;//服务端消息的发送按钮
	
	JTextField showStatus;//显示用户连接数目
	UserLinkList userLinkList;//用户链表

	//建立工具栏
	JToolBar toolBar = new JToolBar();

	//建立工具栏中的按钮组件
	JButton portSet;//启动服务端端口设置
	JButton startServer;//启动服务端侦听
	JButton stopServer;//关闭服务端侦听
	JButton exitButton;//退出按钮
	
	//框架的大小
	Dimension faceSize = new Dimension(400, 600);
	
	ServerListen listenThread;//监听线程

	JPanel downPanel ;
	GridBagLayout girdBag;
	GridBagConstraints girdBagCon;


	/**
	 * 服务端构造函数
	 */
	public ChatServer(){
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

		this.setTitle("聊天室服务端"); //设置标题

		//程序图标
		try {

		     String src = "icon.png";//图片路径
		     image=ImageIO.read(this.getClass().getResource(src));//创建图片对象
		     this.setIconImage(image);//设置图标

		} catch (IOException e) {
		        e.printStackTrace();
		    }  
		show();
		
	}
	
	/**
	 * 程序初始化函数
	 */
	public void init(){

		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());


		//初始化按钮
		portSet = new JButton("端口设置");
		startServer = new JButton("启动服务");
		stopServer = new JButton("停止服务" );
		exitButton = new JButton("退出" );
		//将按钮添加到工具栏
		toolBar.add(portSet);
	//	toolBar.addSeparator();//添加分隔栏
		toolBar.add(startServer);
		toolBar.add(stopServer);
	//	toolBar.addSeparator();//添加分隔栏
		toolBar.add(exitButton);
		contentPane.add(toolBar,BorderLayout.NORTH);

		//初始时，令停止服务按钮不可用
		stopServer.setEnabled(false);
		
		
		//添加按钮的事件侦听
		portSet.addActionListener(this);
		startServer.addActionListener(this);
		stopServer.addActionListener(this);
		exitButton.addActionListener(this);
		
		combobox = new JComboBox();
		combobox.insertItemAt("所有人",0);
		combobox.setSelectedIndex(0);
		
		messageShow = new JTextArea();
		messageShow.setEditable(false);
		
		combobox.setEnabled(false);
		
		
		//添加滚动条
		messageScrollPane = new JScrollPane(messageShow,
			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		messageScrollPane.setPreferredSize(new Dimension(400,400));
		messageScrollPane.revalidate();
		
		showStatus = new JTextField(35);
		showStatus.setEditable(false);
		
		sysMessage = new JTextField(24);
		sysMessage.setEnabled(false);
		sysMessageButton = new JButton();
		sysMessageButton.setText("发送");

		sysMessageButton.setEnabled(false);
		
		//添加系统消息的事件侦听
		sysMessage.addActionListener(this);
		sysMessageButton.addActionListener(this);

		sendToLabel = new JLabel("发送至:");
		messageLabel = new JLabel("发送消息:");
		downPanel = new JPanel();
		girdBag = new GridBagLayout();
		downPanel.setLayout(girdBag);//网格组控制布局

		girdBagCon = new GridBagConstraints();
		girdBagCon.gridx = 0;
		girdBagCon.gridy = 0;
		girdBagCon.gridwidth = 3;
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
		girdBag.setConstraints(sysMessage,girdBagCon);
		downPanel.add(sysMessage);

		girdBagCon = new GridBagConstraints();
		girdBagCon.gridx = 2;
		girdBagCon.gridy = 3;
		girdBag.setConstraints(sysMessageButton,girdBagCon);
		downPanel.add(sysMessageButton);

		girdBagCon = new GridBagConstraints();
		girdBagCon.gridx = 0;
		girdBagCon.gridy = 4;
		girdBagCon.gridwidth = 3;
		girdBag.setConstraints(showStatus,girdBagCon);
		downPanel.add(showStatus);

		contentPane.add(messageScrollPane,BorderLayout.CENTER);
		contentPane.add(downPanel,BorderLayout.SOUTH);
		
		//关闭程序时的操作
		this.addWindowListener(
			new WindowAdapter(){
				public void windowClosing(WindowEvent e){
					stopService();
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
		if (obj == startServer ) { //启动服务端
			startService();
		}
		else if (obj == stopServer ) { //停止服务端
			int j=JOptionPane.showConfirmDialog(
				this,"真的停止服务吗?","停止服务",
				JOptionPane.YES_OPTION,JOptionPane.QUESTION_MESSAGE);
			
			if (j == JOptionPane.YES_OPTION){
				stopService();
			}
		}
		else if (obj == portSet ) { //端口设置
			//调出端口设置的对话框
			PortConf portConf = new PortConf(this);
			portConf.show();
		}
		else if (obj == exitButton ) { //退出程序
			int j=JOptionPane.showConfirmDialog(
				this,"真的要退出吗?","退出",
				JOptionPane.YES_OPTION,JOptionPane.QUESTION_MESSAGE);
			
			if (j == JOptionPane.YES_OPTION){
				stopService();
				System.exit(0);
			}
		}
		else if (obj == sysMessage || obj == sysMessageButton) { //发送系统消息
			sendSystemMessage();
		}
	}
	
	/**
	 * 启动服务端
	 */
	public void startService(){
		try{
			serverSocket = new ServerSocket(port,10);
			messageShow.append("["+sdf.format(d)+"]"+"  "+"服务端已经启动，在"+port+"端口侦听...\n");
			
			startServer.setEnabled(false);
			portSet.setEnabled(false);

			stopServer .setEnabled(true);
			sysMessage.setEnabled(true);
			combobox.setEnabled(true);
			sysMessageButton.setEnabled(true);
			showStatus.setEnabled(true);
			showStatus.setText("");
		}
		catch (Exception e){
			System.out.println(e);
		}
		userLinkList = new UserLinkList();
		
		listenThread = new ServerListen(serverSocket,combobox,
			messageShow,showStatus,userLinkList);
		listenThread.start();//启动 listenThread线程
	}
	
	/**
	 * 关闭服务端
	 */
	public void stopService(){
		try{
			//向所有人发送服务器关闭的消息，以"系统信息"的类型
			sendStopToAll();
			listenThread.isStop = true;
			serverSocket.close();
			
			int count = userLinkList.getCount();
			
			int i =0;
			while( i < count){
				Node node = userLinkList.findUser(i);
				//关闭所有用户的输入，输出，socket
				node.input .close();
				node.output.close();
				node.socket.close();
				
				i ++;
			}

			stopServer .setEnabled(false);
			startServer.setEnabled(true);
			portSet.setEnabled(true);
			sysMessage.setEnabled(false);
			combobox.setEnabled(false);
			sysMessageButton.setEnabled(false);
			showStatus.setText("");
			showStatus.setEnabled(false);
			
			messageShow.append("["+sdf.format(d)+"]"+"  "+"服务端已经关闭!\n");//在服务器端的对话框里面显示这个信息

			combobox.removeAllItems();
			combobox.addItem("所有人");
			
			//在数据库中，将所有用户的online属性改为0
			DBHandler.OfflineAllUsers();
		}
		catch(Exception e){
			System.out.println(e);
		}
	}
	
	/**
	 * 向所有人发送服务器关闭的消息
	 */
	public void sendStopToAll(){
		int count = userLinkList.getCount();
		
		int i = 0;
		while(i < count){
			Node node = userLinkList.findUser(i);
			if(node == null) {
				i ++;
				continue;
			}
			
			try{//向所有人发送服务器关闭的消息，以"服务关闭"的类型
				node.output.writeObject("服务关闭");
				node.output.flush();
				
			}
			catch (Exception e){
				System.out.println(e);
			}
			
			i++;
		}
	}
	
	/**
	 * 向所有人发送消息
	 */
	public void sendMsgToAll(String msg){
		int count = userLinkList.getCount();//用户总数
		
		int i = 0;
		while(i < count){
			Node node = userLinkList.findUser(i);
			if(node == null) {
				i ++;
				continue;
			}
			
			try{//向所有人发送服务器关闭的消息，以"系统信息"的类型
				node.output.writeObject("系统信息");
				node.output.flush();
				node.output.writeObject(msg);
				node.output.flush();
			}
			catch (Exception e){
				System.out.println(e);
			}
			
			i++;
		}

		sysMessage.setText("");
	}

	/**
	 * 向客户端用户发送消息
	 */
	public void sendSystemMessage(){
		String toSomebody = combobox.getSelectedItem().toString();
		String message = sysMessage.getText() + "\n";
		
		messageShow.append("["+sdf.format(d)+"]"+"  "+"系统消息："+message);//首先在服务器端显示向客户端用户发送的消息
		
		//向所有人发送消息
		if(toSomebody.equalsIgnoreCase("所有人")){
			sendMsgToAll(message);
		}
		else{
			//向某个用户发送消息
			Node node = userLinkList.findUser(toSomebody);
			
			try{
				node.output.writeObject("系统信息");
				node.output.flush();
				node.output.writeObject(message);
				node.output.flush();
			}
			catch(Exception e){
				System.out.println(e);
			}
			sysMessage.setText("");//将发送消息栏的消息清空
		}
	}


	public static void main(String[] args) {
		ChatServer app = new ChatServer();
	}
}
