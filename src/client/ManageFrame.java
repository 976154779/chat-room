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
	
	private JLabel jLabel = new JLabel("用户管理");
	
	private List list;
	private JScrollPane jScrollPane;
	
	private JButton jButtonDel = new JButton("删除");
	private JButton jButtonExt = new JButton("退出");
	
	Image image;
	
	public ManageFrame(LoginFrame f) {
		loginf = f;
	
		init();
		this.setVisible(true);
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
		
		//从数据库中获得userInfo表中的所有用户总数，并用此初始化用户成员列表。
		int count = DBHandler.getAllUsersNum();
		if (count == -1) {
			list = new List(0, true);
		} else {
			//设置列表支持多选
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
		// 把窗口放在屏幕中间
		this.setPreferredSize(new Dimension(500, 600));
		this.setBounds(screenSize.width / 2 - 250, screenSize.height / 2 - 250,
				500, 500);
		this.setVisible(true);
		setResizable(false);
		pack();
	}
	
	//响应按钮操作
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("删除")) {
			String[] selItems = list.getSelectedItems();
			if (selItems.length == 0) {
				JOptionPane.showMessageDialog(this, "请选择被删除的人!");
				return;
			}
			if (JOptionPane.showConfirmDialog(this, "确定要删除么？", "",
					JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION) {
				return;
			}
			for (int i = 0; i < selItems.length; i++) {
				if (!DBHandler.deleteUser(selItems[i])) {
					JOptionPane.showMessageDialog(this, "删除" + selItems[i]
							+ "失败");
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
