package client;

import java.awt.*;
import java.awt.event.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;

import server.DBHandler;

import java.io.*;
import java.net.*;


public class ManageFrame extends JFrame implements ActionListener {
	
	private LoginFrame loginf;
	
	private JLabel jLabel = new JLabel("�û�����");
	
	private List list;
	private JScrollPane jScrollPane;
	
	private JButton jButtonDel = new JButton("ɾ��");
	private JButton jButtonExt = new JButton("�˳�");
	
	Image image;
	
	public ManageFrame(LoginFrame f) {
		loginf = f;
	
		init();
		this.setVisible(true);
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
		
		//�����ݿ��л��userInfo���е������û����������ô˳�ʼ���û���Ա�б�
		int count = DBHandler.getAllUsersNum();
		if (count == -1) {
			list = new List(0, true);
		} else {
			//�����б�֧�ֶ�ѡ
			list = new List(count, true);
			java.util.List names = DBHandler.getAllUsers();
			for (int i = 0; i < names.size(); i++) {
				list.addItem(names.get(i).toString());
			}
		}
		
		list.setPreferredSize(new Dimension(200, 400));
		list.setBounds(50, 100, 200, 400);
		
		jScrollPane = new JScrollPane(list);
		jScrollPane.setPreferredSize(new Dimension(200, 400));
		jScrollPane.setBounds(50, 100, 200, 400);
		
		jLabel.setFont(new Font("", Font.BOLD, 30));
		jLabel.setBounds(200, 30, 200, 50);
		
		jButtonDel.setPreferredSize(new Dimension(100, 30));
		jButtonDel.setBounds(300, 100, 100, 30);
		jButtonDel.addActionListener(this);
		
		jButtonExt.setPreferredSize(new Dimension(100, 30));
		jButtonExt.setBounds(300, 150, 100, 30);
		jButtonExt.addActionListener(this);
		
		getContentPane().setLayout(null);
		getContentPane().add(jScrollPane);
		getContentPane().add(jButtonDel);
		getContentPane().add(jButtonExt);
		getContentPane().setBounds(0, 0, 500, 600);
		getContentPane().add(jLabel);
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		// �Ѵ��ڷ�����Ļ�м�
		this.setPreferredSize(new Dimension(500, 600));
		this.setBounds(screenSize.width / 2 - 250, screenSize.height / 2 - 250,
				500, 500);
		this.setVisible(true);
		setResizable(false);
		pack();
	}
	
	//��Ӧ��ť����
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("ɾ��")) {
			String[] selItems = list.getSelectedItems();
			if (selItems.length == 0) {
				JOptionPane.showMessageDialog(this, "��ѡ��ɾ������!");
				return;
			}
			if (JOptionPane.showConfirmDialog(this, "ȷ��Ҫɾ��ô��", "",
					JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION) {
				return;
			}
			for (int i = 0; i < selItems.length; i++) {
				if (!DBHandler.deleteUser(selItems[i])) {
					JOptionPane.showMessageDialog(this, "ɾ��" + selItems[i]
							+ "ʧ��");
				} else {
					list.remove(selItems[i]);
				}
			}
			list.repaint();
		} else {
			loginf.show();
			dispose();
		}
	}
}
