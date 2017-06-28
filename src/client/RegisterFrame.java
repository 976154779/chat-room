package client;

import java.awt.*;
import java.awt.event.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;

import server.DBHandler;

import java.io.*;
import java.net.*;

public class RegisterFrame extends JFrame implements ActionListener {

	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	
	private javax.swing.JTextField jTextFieldName;
	private javax.swing.JPasswordField jTextFieldPw1;
	private javax.swing.JPasswordField jTextFieldPw2;
	
	private javax.swing.JButton jButtonReg;
	private JButton jBuutonExt;
	
	private LoginFrame f;

	Image image;
	
	public RegisterFrame(LoginFrame f) {
		this.f = f;
	
		init();
	}
	
	// ��ʼ�����пؼ���
	private void init() {
		
		//����ͼ��
		try {
			String src = "icon.png";//ͼƬ·��
			image=ImageIO.read(this.getClass().getResource(src));//����ͼƬ����
			this.setIconImage(image);//����ͼ��
		} catch (IOException e) {
			e.printStackTrace();
		}  
		
		jLabel1 = new JLabel();
		jTextFieldName = new JTextField();
		jLabel2 = new JLabel();
		jTextFieldPw1 = new JPasswordField();
		jLabel3 = new JLabel();
		jTextFieldPw2 = new JPasswordField();
	
		jLabel4 = new JLabel("�û�ע��");
		jLabel4.setFont(new Font("", Font.BOLD, 30));
		jLabel4.setBounds(200, 50, 200, 50);
		
		jButtonReg = new javax.swing.JButton();
		jBuutonExt = new javax.swing.JButton();
		
		getContentPane().setLayout(null);
		
		getContentPane().add(jLabel1);
		getContentPane().add(jTextFieldName);
		getContentPane().add(jLabel2);
		getContentPane().add(jTextFieldPw1);
		getContentPane().add(jLabel3);
		getContentPane().add(jTextFieldPw2);
		getContentPane().add(jLabel4);
		getContentPane().add(jButtonReg);
		getContentPane().add(jBuutonExt);
		
		getContentPane().setVisible(true);
		getContentPane().setPreferredSize(new Dimension(500, 500));
		
		jLabel1.setText("�û���");
		jLabel1.setPreferredSize(new Dimension(80, 30));
		jLabel1.setBounds(100, 150, 80, 30);
		jLabel2.setText("����");
		jLabel2.setPreferredSize(new Dimension(80, 30));
		jLabel2.setBounds(100, 190, 80, 30);
		jLabel3.setText("�ظ�����");
		jLabel3.setPreferredSize(new Dimension(120, 30));
		jLabel3.setBounds(100, 230, 80, 30);
		
		jTextFieldName.setPreferredSize(new Dimension(200, 30));
		jTextFieldName.setBounds(200, 150, 200, 30);
		jTextFieldPw1.setPreferredSize(new Dimension(200, 30));
		jTextFieldPw1.setBounds(200, 190, 200, 30);
		jTextFieldPw2.setPreferredSize(new Dimension(200, 30));
		jTextFieldPw2.setBounds(200, 230, 200, 30);
		
		jButtonReg.setText("ע��");
		jButtonReg.setBounds(200, 270, 80, 30);
		jButtonReg.addActionListener(this);
		jBuutonExt.setText("�˳�");
		jBuutonExt.setBounds(300, 270, 80, 30);
		jBuutonExt.addActionListener(this);
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		// �Ѵ��ڷ�����Ļ�м�
		this.setPreferredSize(new Dimension(500, 500));
		this.setBounds(screenSize.width / 2 - 250, screenSize.height / 2 - 250,
				500, 500);
		this.setVisible(true);
		this.setResizable(false);
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		pack();
	}
//����ť�¼�
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("ע��")) {
       //�û������ע�ᡱ��ť
			if (!(jTextFieldPw1.getText().equals(jTextFieldPw2.getText()))) {
				JOptionPane.showMessageDialog(this, "���벻һ�£�����������");
				return;
			}
			if (jTextFieldPw1.getText().equals("")) {
				JOptionPane.showMessageDialog(this, "���벻��Ϊ�գ�");
				return;
			}
			if (jTextFieldName.getText().equals("")) {
				JOptionPane.showMessageDialog(this, "�û�������Ϊ�գ�");
				return;
			}
			//�ύ��Ϣ�����ݿ⣬���жϽ��
			int status = DBHandler.regUser(jTextFieldName.getText(),
					jTextFieldPw1.getText());
			if (status == 0) {
				JOptionPane.showMessageDialog(this, "ע��ɹ���");
				f.show();
				this.dispose();
			} else if (status == 1) {
				JOptionPane.showMessageDialog(this, "��������");
				return;
			} else {
				JOptionPane.showMessageDialog(this, "���ݿ����ʧ�ܣ�");
				return;
			}
		} else if (e.getActionCommand().equals("�˳�")) {
			//�û����¡��˳�����ť��
			f.show();
			this.dispose();
		}
	}
}
