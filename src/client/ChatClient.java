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
 * ����ͻ��˵��������
 */
public class ChatClient extends JFrame implements ActionListener{
	public  Date d=new Date();
	public DateFormat sdf = new SimpleDateFormat("HH:mm:ss"); 
	
	private LoginFrame login;
	
	String ip;//���ӵ�����˵�ip��ַ
	int port;//���ӵ�����˵Ķ˿ں�
	String userName;//�û���
	int type = 0;//0��ʾδ���ӣ�1��ʾ������

	Image image;//����ͼ��
	
	JComboBox combobox;//ѡ������Ϣ�Ľ�����
	JTextArea messageShow;//�ͻ��˵���Ϣ��ʾ
	JScrollPane messageScrollPane;//��Ϣ��ʾ�Ĺ�����

	JLabel sendToLabel,messageLabel ;

	JTextField clientMessage;//�ͻ�����Ϣ�ķ���
	JButton clientMessageButton;//������Ϣ
	JTextField showStatus;//��ʾ�û�����״̬
	
	Socket socket;
	ObjectOutputStream output;//�����׽��������
	ObjectInputStream input;//�����׽���������
	
	ClientReceive recvThread;

	//����������
	JToolBar toolBar = new JToolBar();
	//�����������еİ�ť���
	JButton loginButton;//����
	JButton logoffButton;//�û�ע��
	JButton exitButton;//�˳�

	//��ܵĴ�С
	Dimension faceSize = new Dimension(400, 600);

	JPanel downPanel ;
	GridBagLayout girdBag;
	GridBagConstraints girdBagCon;
	
	public ChatClient(LoginFrame login,String ip,int port){
		
		this.login=login;
		this.ip=ip;
		this.port=port;
		userName=login.jTextFieldName.getText();
		
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
		this.setTitle("�����ҿͻ���"); //���ñ���
		
		//����ͼ��
		try {
			String src = "icon.png";//ͼƬ·��
			image=ImageIO.read(this.getClass().getResource(src));//����ͼƬ����
			this.setIconImage(image);//����ͼ��
		} catch (IOException e) {
			 e.printStackTrace();
		}  		

	}

	/**
	 * �����ʼ������
	 */
	public void init(){

		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//��ʼ����ť
		loginButton=new JButton("����");
		logoffButton = new JButton("����");
		exitButton = new JButton("�˳�" );
	
		//����ť��ӵ�������
		toolBar.add(loginButton);
		toolBar.addSeparator();//��ӷָ���
		toolBar.add(logoffButton);
		toolBar.addSeparator();//��ӷָ���
		toolBar.add(exitButton);
		contentPane.add(toolBar,BorderLayout.NORTH);
		
		//��ʼʱ
		loginButton.setEnabled(false);
		logoffButton.setEnabled(true);

		
		//��Ӱ�ť���¼�����
		loginButton.addActionListener(this);
		logoffButton.addActionListener(this);
		exitButton.addActionListener(this);
		
		combobox = new JComboBox();
		combobox.insertItemAt("������",0);
		combobox.setSelectedIndex(0);
		
		messageShow = new JTextArea();
		messageShow.setEditable(false);
		//��ӹ�����
		messageScrollPane = new JScrollPane(messageShow,
			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		messageScrollPane.setPreferredSize(new Dimension(400,400));
		messageScrollPane.revalidate();
		
		clientMessage = new JTextField(23);
		clientMessage.setEnabled(false);
		clientMessageButton = new JButton();
		clientMessageButton.setText("����");

		//���ϵͳ��Ϣ���¼�����
		clientMessage.addActionListener(this);
		clientMessageButton.addActionListener(this);

		sendToLabel = new JLabel("������:");
		messageLabel = new JLabel("������Ϣ:");
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
		
		//�رճ���ʱ�Ĳ���
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
	 * �¼�����
	 */
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == loginButton) { //��¼
			Connect();
		}
		if (obj == logoffButton) { //ע��
			DisConnect();
			showStatus.setText("");
		}
		else if (obj == clientMessage || obj == clientMessageButton) { //������Ϣ
			SendMessage();
			clientMessage.setText("");
		}
		else if (obj == exitButton) { //�˳�
			int j=JOptionPane.showConfirmDialog(
				this,"���Ҫ�˳���?","�˳�",
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
					this,"��ǰ�û��ѵ�¼���޷��ظ���¼��\n","��ʾ",
					JOptionPane.DEFAULT_OPTION,JOptionPane.WARNING_MESSAGE);
			return;
		}
		try{
			socket = new Socket(ip,port);
			DBHandler.Online(userName);//�����ݿ������õ�ǰ�û�Ϊ����״̬
		}
		catch (Exception e){
			JOptionPane.showConfirmDialog(
				this,"�������ӵ�ָ���ķ�������\n��ȷ�����������Ƿ���ȷ��","��ʾ",
				JOptionPane.DEFAULT_OPTION,JOptionPane.WARNING_MESSAGE);
			return;
		}
		login.setVisible(false);
		this.setVisible(true);
		try{
			output = new ObjectOutputStream(socket.getOutputStream());
			output.flush();
			input  = new ObjectInputStream(socket.getInputStream() );
			
			output.writeObject(userName);//���û������͸�������
			output.flush();
			
			recvThread = new ClientReceive(socket,output,input,combobox,messageShow,showStatus);
			recvThread.start();
			
			
			loginButton.setEnabled(false);
			logoffButton.setEnabled(true);
			
			clientMessage.setEnabled(true);
			combobox.setEnabled(true);
			clientMessageButton.setEnabled(true);
			messageShow.append("["+sdf.format(d)+"]"+"  "+"���ӷ����� "+ip+":"+port+" �ɹ�...\n");
			type = 1;//��־λ��Ϊ������
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
			output.writeObject("�û�����");
			output.flush();
		
			input.close();
			output.close();
			socket.close();
			messageShow.append("["+sdf.format(d)+"]"+"  "+"�Ѿ���������Ͽ�����...\n");
			type = 0;//��־λ��Ϊδ����
			
			DBHandler.Offline(userName);//�����ݿ��������û���ǰ״̬Ϊ����
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
			output.writeObject("������Ϣ");
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

	