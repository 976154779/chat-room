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
	
	// 初始化所有控件。
	private void init() {
		
		//程序图标
		try {
			String src = "icon.png";//图片路径
			image=ImageIO.read(this.getClass().getResource(src));//创建图片对象
			this.setIconImage(image);//设置图标
		} catch (IOException e) {
			e.printStackTrace();
		}  
		
		jLabel1 = new JLabel();
		jTextFieldName = new JTextField();
		jLabel2 = new JLabel();
		jTextFieldPw1 = new JPasswordField();
		jLabel3 = new JLabel();
		jTextFieldPw2 = new JPasswordField();
	
		jLabel4 = new JLabel("用户注册");
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
		
		jLabel1.setText("用户名");
		jLabel1.setPreferredSize(new Dimension(80, 30));
		jLabel1.setBounds(100, 150, 80, 30);
		jLabel2.setText("密码");
		jLabel2.setPreferredSize(new Dimension(80, 30));
		jLabel2.setBounds(100, 190, 80, 30);
		jLabel3.setText("重复密码");
		jLabel3.setPreferredSize(new Dimension(120, 30));
		jLabel3.setBounds(100, 230, 80, 30);
		
		jTextFieldName.setPreferredSize(new Dimension(200, 30));
		jTextFieldName.setBounds(200, 150, 200, 30);
		jTextFieldPw1.setPreferredSize(new Dimension(200, 30));
		jTextFieldPw1.setBounds(200, 190, 200, 30);
		jTextFieldPw2.setPreferredSize(new Dimension(200, 30));
		jTextFieldPw2.setBounds(200, 230, 200, 30);
		
		jButtonReg.setText("注册");
		jButtonReg.setBounds(200, 270, 80, 30);
		jButtonReg.addActionListener(this);
		jBuutonExt.setText("退出");
		jBuutonExt.setBounds(300, 270, 80, 30);
		jBuutonExt.addActionListener(this);
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		// 把窗口放在屏幕中间
		this.setPreferredSize(new Dimension(500, 500));
		this.setBounds(screenSize.width / 2 - 250, screenSize.height / 2 - 250,
				500, 500);
		this.setVisible(true);
		this.setResizable(false);
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		pack();
	}
//处理按钮事件
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("注册")) {
       //用户点击“注册”按钮
			if (!(jTextFieldPw1.getText().equals(jTextFieldPw2.getText()))) {
				JOptionPane.showMessageDialog(this, "密码不一致！请重新输入");
				return;
			}
			if (jTextFieldPw1.getText().equals("")) {
				JOptionPane.showMessageDialog(this, "密码不能为空！");
				return;
			}
			if (jTextFieldName.getText().equals("")) {
				JOptionPane.showMessageDialog(this, "用户名不能为空！");
				return;
			}
			//提交信息到数据库，并判断结果
			int status = DBHandler.regUser(jTextFieldName.getText(),
					jTextFieldPw1.getText());
			if (status == 0) {
				JOptionPane.showMessageDialog(this, "注册成功！");
				f.show();
				this.dispose();
			} else if (status == 1) {
				JOptionPane.showMessageDialog(this, "有重名！");
				return;
			} else {
				JOptionPane.showMessageDialog(this, "数据库操作失败！");
				return;
			}
		} else if (e.getActionCommand().equals("退出")) {
			//用户按下“退出”按钮。
			f.show();
			this.dispose();
		}
	}
}
