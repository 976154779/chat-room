package server;


import java.awt.*;
import java.awt.event.*;

import javax.imageio.ImageIO;//����ͼ��
import javax.swing.*;
import javax.swing.event.*;
import java.net.*;
import java.io.*;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
/*
 * �������˵��������
 */
public class ChatServer extends JFrame implements ActionListener{
	public  Date d=new Date();
	public DateFormat sdf = new SimpleDateFormat("HH:mm:ss"); 
	
	public static int port = 8888;//����˵������˿�

	ServerSocket serverSocket;//�����Socket
	
	Image image;//ͼ��
	
	JTextArea messageShow;//����˵���Ϣ��ʾ
	JScrollPane messageScrollPane;//��Ϣ��ʾ�Ĺ�����
	
	JLabel sendToLabel,messageLabel;
	JComboBox combobox;//ѡ������Ϣ�Ľ�����
	
	JTextField sysMessage;//�������Ϣ�ķ���
	JButton sysMessageButton;//�������Ϣ�ķ��Ͱ�ť
	
	JTextField showStatus;//��ʾ�û�������Ŀ
	UserLinkList userLinkList;//�û�����

	//����������
	JToolBar toolBar = new JToolBar();

	//�����������еİ�ť���
	JButton portSet;//��������˶˿�����
	JButton startServer;//�������������
	JButton stopServer;//�رշ��������
	JButton exitButton;//�˳���ť
	
	//��ܵĴ�С
	Dimension faceSize = new Dimension(400, 600);
	
	ServerListen listenThread;//�����߳�

	JPanel downPanel ;
	GridBagLayout girdBag;
	GridBagConstraints girdBagCon;


	/**
	 * ����˹��캯��
	 */
	public ChatServer(){
		init();//��ʼ������

		//��ӿ�ܵĹر��¼�����
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		//���ÿ�ܵĴ�С
		this.setSize(faceSize);

		//��������ʱ���ڵ�λ��
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation( (int) (screenSize.width - faceSize.getWidth()) / 2,
						 (int) (screenSize.height - faceSize.getHeight()) / 2);
		this.setResizable(false);

		this.setTitle("�����ҷ����"); //���ñ���

		//����ͼ��
		try {

		     String src = "icon.png";//ͼƬ·��
		     image=ImageIO.read(this.getClass().getResource(src));//����ͼƬ����
		     this.setIconImage(image);//����ͼ��

		} catch (IOException e) {
		        e.printStackTrace();
		    }  
		show();
		
	}
	
	/**
	 * �����ʼ������
	 */
	public void init(){

		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());


		//��ʼ����ť
		portSet = new JButton("�˿�����");
		startServer = new JButton("��������");
		stopServer = new JButton("ֹͣ����" );
		exitButton = new JButton("�˳�" );
		//����ť��ӵ�������
		toolBar.add(portSet);
	//	toolBar.addSeparator();//��ӷָ���
		toolBar.add(startServer);
		toolBar.add(stopServer);
	//	toolBar.addSeparator();//��ӷָ���
		toolBar.add(exitButton);
		contentPane.add(toolBar,BorderLayout.NORTH);

		//��ʼʱ����ֹͣ����ť������
		stopServer.setEnabled(false);
		
		
		//��Ӱ�ť���¼�����
		portSet.addActionListener(this);
		startServer.addActionListener(this);
		stopServer.addActionListener(this);
		exitButton.addActionListener(this);
		
		combobox = new JComboBox();
		combobox.insertItemAt("������",0);
		combobox.setSelectedIndex(0);
		
		messageShow = new JTextArea();
		messageShow.setEditable(false);
		
		combobox.setEnabled(false);
		
		
		//��ӹ�����
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
		sysMessageButton.setText("����");

		sysMessageButton.setEnabled(false);
		
		//���ϵͳ��Ϣ���¼�����
		sysMessage.addActionListener(this);
		sysMessageButton.addActionListener(this);

		sendToLabel = new JLabel("������:");
		messageLabel = new JLabel("������Ϣ:");
		downPanel = new JPanel();
		girdBag = new GridBagLayout();
		downPanel.setLayout(girdBag);//��������Ʋ���

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
		
		//�رճ���ʱ�Ĳ���
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
	 * �¼�����
	 */
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == startServer ) { //���������
			startService();
		}
		else if (obj == stopServer ) { //ֹͣ�����
			int j=JOptionPane.showConfirmDialog(
				this,"���ֹͣ������?","ֹͣ����",
				JOptionPane.YES_OPTION,JOptionPane.QUESTION_MESSAGE);
			
			if (j == JOptionPane.YES_OPTION){
				stopService();
			}
		}
		else if (obj == portSet ) { //�˿�����
			//�����˿����õĶԻ���
			PortConf portConf = new PortConf(this);
			portConf.show();
		}
		else if (obj == exitButton ) { //�˳�����
			int j=JOptionPane.showConfirmDialog(
				this,"���Ҫ�˳���?","�˳�",
				JOptionPane.YES_OPTION,JOptionPane.QUESTION_MESSAGE);
			
			if (j == JOptionPane.YES_OPTION){
				stopService();
				System.exit(0);
			}
		}
		else if (obj == sysMessage || obj == sysMessageButton) { //����ϵͳ��Ϣ
			sendSystemMessage();
		}
	}
	
	/**
	 * ���������
	 */
	public void startService(){
		try{
			serverSocket = new ServerSocket(port,10);
			messageShow.append("["+sdf.format(d)+"]"+"  "+"������Ѿ���������"+port+"�˿�����...\n");
			
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
		listenThread.start();//���� listenThread�߳�
	}
	
	/**
	 * �رշ����
	 */
	public void stopService(){
		try{
			//�������˷��ͷ������رյ���Ϣ����"ϵͳ��Ϣ"������
			sendStopToAll();
			listenThread.isStop = true;
			serverSocket.close();
			
			int count = userLinkList.getCount();
			
			int i =0;
			while( i < count){
				Node node = userLinkList.findUser(i);
				//�ر������û������룬�����socket
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
			
			messageShow.append("["+sdf.format(d)+"]"+"  "+"������Ѿ��ر�!\n");//�ڷ������˵ĶԻ���������ʾ�����Ϣ

			combobox.removeAllItems();
			combobox.addItem("������");
			
			//�����ݿ��У��������û���online���Ը�Ϊ0
			DBHandler.OfflineAllUsers();
		}
		catch(Exception e){
			System.out.println(e);
		}
	}
	
	/**
	 * �������˷��ͷ������رյ���Ϣ
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
			
			try{//�������˷��ͷ������رյ���Ϣ����"����ر�"������
				node.output.writeObject("����ر�");
				node.output.flush();
				
			}
			catch (Exception e){
				System.out.println(e);
			}
			
			i++;
		}
	}
	
	/**
	 * �������˷�����Ϣ
	 */
	public void sendMsgToAll(String msg){
		int count = userLinkList.getCount();//�û�����
		
		int i = 0;
		while(i < count){
			Node node = userLinkList.findUser(i);
			if(node == null) {
				i ++;
				continue;
			}
			
			try{//�������˷��ͷ������رյ���Ϣ����"ϵͳ��Ϣ"������
				node.output.writeObject("ϵͳ��Ϣ");
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
	 * ��ͻ����û�������Ϣ
	 */
	public void sendSystemMessage(){
		String toSomebody = combobox.getSelectedItem().toString();
		String message = sysMessage.getText() + "\n";
		
		messageShow.append("["+sdf.format(d)+"]"+"  "+"ϵͳ��Ϣ��"+message);//�����ڷ���������ʾ��ͻ����û����͵���Ϣ
		
		//�������˷�����Ϣ
		if(toSomebody.equalsIgnoreCase("������")){
			sendMsgToAll(message);
		}
		else{
			//��ĳ���û�������Ϣ
			Node node = userLinkList.findUser(toSomebody);
			
			try{
				node.output.writeObject("ϵͳ��Ϣ");
				node.output.flush();
				node.output.writeObject(message);
				node.output.flush();
			}
			catch(Exception e){
				System.out.println(e);
			}
			sysMessage.setText("");//��������Ϣ������Ϣ���
		}
	}


	public static void main(String[] args) {
		ChatServer app = new ChatServer();
	}
}
