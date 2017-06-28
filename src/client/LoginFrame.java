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
	
	String ip = "127.0.0.1";//���ӵ�����˵�ip��ַ
	int port = 8888;//���ӵ�����˵Ķ˿ں�
	
	private JLabel jLabel1;//����ϵͳ ��ǩ
	private JLabel jLabel2;//�û��� ��ǩ
	private JLabel jLabel3;//���� ��ǩ
	
	JTextField jTextFieldName;//�û��������
	private JPasswordField jPasswordFieldPw;//���������
	
	private JButton jButtonLogin;//��½��ť
	private JButton jButtonRegister;//ע�ᰴť
	private JButton jButtonManage;//����ť
	private JButton jButtonConf;//���ð�ť
	
	
	private JPanel jPanel1;
	
	Image  image;
	ImageIcon icon;

	public LoginFrame() {	
		init();
	}

	//��ʼ�����пؼ�
	private void init() {
		

		//����ͼ��
		try {
			String src = "icon.png";//ͼƬ·��
			image=ImageIO.read(this.getClass().getResource(src));//����ͼƬ����
			this.setIconImage(image);//����ͼ��

		} catch (IOException e) {
				e.printStackTrace();
		} 
		
		jPanel1 = new JPanel();
	    
		jTextFieldName = new JTextField();
		jPasswordFieldPw = new JPasswordField();
		
		jButtonLogin = new JButton("��¼");
		jButtonRegister = new JButton("ע��");
		jButtonManage = new JButton("����");
		jButtonConf = new JButton("��������");
		
		jLabel1 = new JLabel();
		jLabel2 = new JLabel();
		jLabel3 = new JLabel();
		
		getContentPane().setLayout(null);
		
		getContentPane().add(jLabel1);
		jLabel1.setText("����ϵͳ");
		//����label�е�����ʹ�С�����ô�����ʾ
		jLabel1.setFont(new Font("", Font.BOLD, 30));
		jLabel1.setBounds(200, 100, 400, 50);
		
		getContentPane().add(jPanel1);
		jPanel1.setLayout(null);
		
		jPanel1.add(jLabel2);
		jLabel2.setText("�û�����");
		jLabel2.setPreferredSize(new Dimension(50, 30));
		jLabel2.setBounds(100, 150, 100, 30);
		
		jPanel1.add(jTextFieldName);
		jTextFieldName.setBounds(200, 150, 150, 30);
		
		jLabel3.setText("�� �룺");
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
		//�Ѵ��ڷ�����Ļ�м�
		this.setPreferredSize(new Dimension(500, 500));
		this.setBounds(screenSize.width/2-250, screenSize.height/2-250, 500, 500);
		this.setVisible(true);
		setResizable(false);
		pack();
	}


    //����ť�¼�
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("��¼")) {
			//����û����¡���½����ť
			boolean isAuthrized = DBHandler.isAuthorized(jTextFieldName.getText(), jPasswordFieldPw.getText());
			boolean isOnline=DBHandler.isOnline(jTextFieldName.getText());
			
		if (isAuthrized==true && isOnline==false) {
				
				ChatClient client=new ChatClient(this,ip,port);//����ͻ��˽���
				client.Connect();
				
			}
			else if(isAuthrized==true && isOnline==true){
				JOptionPane.showMessageDialog(this, "��ǰ�û��ѵ�¼���޷��ظ���¼��");
				return;
			}
			else{
				JOptionPane.showMessageDialog(this, "�û��������벻��ȷ��");
				return;
			}
		} else if (e.getActionCommand().equals("ע��")) {
			//����û����¡�ע�ᡱ��ť
			RegisterFrame rgf = new RegisterFrame(this);//����ע�����
			this.setVisible(false);
		} else if (e.getActionCommand().equals("����")){
			//����û����¡�������ť
			if (DBHandler.isAdmin(jTextFieldName.getText(), jPasswordFieldPw.getText())) {
				ManageFrame mf = new ManageFrame(this);
				this.setVisible(false);
			} else {
				JOptionPane.showMessageDialog(this, "�û������벻��ȷ��");
				return;
			}
		}else {
			//����û����¡����á���ť
			//�����������öԻ���
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

