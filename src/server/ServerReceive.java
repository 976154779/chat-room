package server;

import javax.swing.*;
import java.io.*;
import java.net.*;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
/*
 * �������շ���Ϣ����
 */
public class ServerReceive extends Thread {
	public  Date d=new Date();
	public DateFormat sdf = new SimpleDateFormat("HH:mm:ss"); 
	
	JTextArea textarea;
	JTextField textfield;
	JComboBox combobox;
	
	Node client;
	UserLinkList userLinkList;//�û�����
	
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
		//�������˷����û����б�
		sendUserList();
		
		while(!isStop && !client.socket.isClosed()){
			try{
				String type = (String)client.input.readObject();
				
				if(type.equalsIgnoreCase("������Ϣ")){
					String toSomebody = (String)client.input.readObject();
					String message = (String)client.input.readObject();
					
					String msg = client.username 
							+ "�� "
							+ toSomebody 
							+ " ˵ : "
							+ message
							+ "\n";
					
					textarea.append("["+sdf.format(d)+"]"+"  "+msg);//�ڷ������ĶԻ�����Ҳ��ʾ���������Ϣ
					
					if(toSomebody.equalsIgnoreCase("������")){
						sendToAll(msg);//�������˷�����Ϣ
					}
					else{//˽��
						try{//�ڷ��ͷ������������Ҳ��ʾ���Լ����͵������Ϣ
							client.output.writeObject("������Ϣ");
							client.output.flush();
							client.output.writeObject(msg);
							client.output.flush();
						}
						catch (Exception e){
							System.out.println(e);
						}
						
						Node node = userLinkList.findUser(toSomebody);
						//���͸�ָ���ĶԷ�
						if(node != null){
							node.output.writeObject("������Ϣ"); 
							node.output.flush();
							node.output.writeObject(msg);
							node.output.flush();
						}
					}
				}
				else if(type.equalsIgnoreCase("�û�����")){
					Node node = userLinkList.findUser(client.username);
					userLinkList.delUser(node);//ɾ������Ӧ�û��Ľڵ�
					
					String msg = "�û� " + client.username + " ����\n";
					int count = userLinkList.getCount();

					combobox.removeAllItems();
					combobox.addItem("������");
					int i = 0;
					while(i < count){//�������˵��û��б��������Ŀǰ�Ե�½���û�
						node = userLinkList.findUser(i);
						if(node == null) {
							i ++;
							continue;
						} 
			
						combobox.addItem(node.username);
						i++;
					}
					combobox.setSelectedIndex(0);

					textarea.append("["+sdf.format(d)+"]"+"  "+msg);//�ڷ������ĶԻ�������ʾ����û����ߵ�֪ͨ��Ϣ
					textfield.setText("�����û�" + userLinkList.getCount() + "��\n");
					
					sendToAll(msg);//�������˷�������û����ߵ�֪ͨ��Ϣ���ԡ�������Ϣ��������
					sendUserList();//���·����û��б�,ˢ��
					
					break;
				}
			}
			catch (Exception e){
				System.out.println(e);
			}
		}
	}
	
	/*
	 * �������˷�����Ϣ
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
			
			try{//��ÿһ���û��ĶԻ������涼��ʾ���������Ϣ
				node.output.writeObject("������Ϣ");
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
	 * �������˷����û����б�
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
			
			try{//��ÿһλ�û��������û��б���Ϣ
				node.output.writeObject("�û��б�");
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
