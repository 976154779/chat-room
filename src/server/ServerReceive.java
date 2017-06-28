package server;

import javax.swing.*;
import java.io.*;
import java.net.*;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
/*
 * 服务器收发消息的类
 */
public class ServerReceive extends Thread {
	public  Date d=new Date();
	public DateFormat sdf = new SimpleDateFormat("HH:mm:ss"); 
	
	JTextArea textarea;
	JTextField textfield;
	JComboBox combobox;
	
	Node client;
	UserLinkList userLinkList;//用户链表
	
	public boolean isStop;
	
	public ServerReceive(JTextArea textarea,JTextField textfield,
		JComboBox combobox,Node client,UserLinkList userLinkList){

		this.textarea = textarea;
		this.textfield = textfield;
		this.client = client;
		this.userLinkList = userLinkList;
		this.combobox = combobox;
		
		isStop = false;
	}
	
	public void run(){
		//向所有人发送用户的列表
		sendUserList();
		
		while(!isStop && !client.socket.isClosed()){
			try{
				String type = (String)client.input.readObject();
				
				if(type.equalsIgnoreCase("聊天信息")){
					String toSomebody = (String)client.input.readObject();
					String message = (String)client.input.readObject();
					
					String msg = client.username 
							+ "对 "
							+ toSomebody 
							+ " 说 : "
							+ message
							+ "\n";
					
					textarea.append("["+sdf.format(d)+"]"+"  "+msg);//在服务器的对话框里也显示这个聊天信息
					
					if(toSomebody.equalsIgnoreCase("所有人")){
						sendToAll(msg);//向所有人发送消息
					}
					else{//私聊
						try{//在发送方的聊天框里面也显示出自己发送的这段信息
							client.output.writeObject("聊天信息");
							client.output.flush();
							client.output.writeObject(msg);
							client.output.flush();
						}
						catch (Exception e){
							System.out.println(e);
						}
						
						Node node = userLinkList.findUser(toSomebody);
						//发送给指定的对方
						if(node != null){
							node.output.writeObject("聊天信息"); 
							node.output.flush();
							node.output.writeObject(msg);
							node.output.flush();
						}
					}
				}
				else if(type.equalsIgnoreCase("用户下线")){
					Node node = userLinkList.findUser(client.username);
					userLinkList.delUser(node);//删除掉对应用户的节点
					
					String msg = "用户 " + client.username + " 下线\n";
					int count = userLinkList.getCount();

					combobox.removeAllItems();
					combobox.addItem("所有人");
					int i = 0;
					while(i < count){//服务器端的用户列表重新添加目前仍登陆的用户
						node = userLinkList.findUser(i);
						if(node == null) {
							i ++;
							continue;
						} 
			
						combobox.addItem(node.username);
						i++;
					}
					combobox.setSelectedIndex(0);

					textarea.append("["+sdf.format(d)+"]"+"  "+msg);//在服务器的对话框里显示这个用户下线的通知信息
					textfield.setText("在线用户" + userLinkList.getCount() + "人\n");
					
					sendToAll(msg);//向所有人发送这个用户下线的通知信息，以“聊天信息”的类型
					sendUserList();//重新发送用户列表,刷新
					
					break;
				}
			}
			catch (Exception e){
				System.out.println(e);
			}
		}
	}
	
	/*
	 * 向所有人发送消息
	 */
	public void sendToAll(String msg){
		int count = userLinkList.getCount();
		
		int i = 0;
		while(i < count){
			Node node = userLinkList.findUser(i);
			if(node == null) {
				i ++;
				continue;
			}
			
			try{//在每一个用户的对话框里面都显示这段聊天信息
				node.output.writeObject("聊天信息");
				node.output.flush();
				node.output.writeObject(msg);
				node.output.flush();
			}
			catch (Exception e){
				System.out.println(e);
			}
			
			i++;
		}
	}
	
	/*
	 * 向所有人发送用户的列表
	 */
	public void sendUserList(){
		String userlist = "";
		int count = userLinkList.getCount();

		int i = 0;
		while(i < count){
			Node node = userLinkList.findUser(i);
			if(node == null) {
				i ++;
				continue;
			}
			
			userlist += node.username;
			userlist += '\n';
			i++;
		}
		
		i = 0;
		while(i < count){
			Node node = userLinkList.findUser(i);
			if(node == null) {
				i ++;
				continue;
			} 
			
			try{//向每一位用户都发送用户列表信息
				node.output.writeObject("用户列表");
				node.output.flush();
				node.output.writeObject(userlist);
				node.output.flush();
			}
			catch (Exception e){
				System.out.println(e);
			}
			i++;
		}
	}
}
