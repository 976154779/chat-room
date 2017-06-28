package client;

import javax.swing.*;
import java.io.*;
import java.net.*;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/*
 * ����ͻ�����Ϣ�շ���
 */
public class ClientReceive extends Thread {
	public  Date d=new Date();
	public DateFormat sdf = new SimpleDateFormat("HH:mm:ss"); 
	
	private JComboBox combobox;//ѡ���������
	private JTextArea textarea;//�Ի���
	JTextField clientMessage;//�ͻ�����Ϣ�ķ���
	JButton clientMessageButton;//������Ϣ
	Socket socket;
	ObjectOutputStream output;
	ObjectInputStream  input;
	JTextField showStatus;

	public ClientReceive(Socket socket,ObjectOutputStream output,
		ObjectInputStream  input,JComboBox combobox,JTextArea textarea,JTextField showStatus){

		this.socket = socket;
		this.output = output;
		this.input = input;
		this.combobox = combobox;
		this.textarea = textarea;
		this.showStatus = showStatus;
	}
	
	public void run(){
		while(!socket.isClosed()){
			try{
				String type = (String)input.readObject();
				
				if(type.equalsIgnoreCase("ϵͳ��Ϣ")){
					String sysmsg = (String)input.readObject();
					textarea.append("["+sdf.format(d)+"]"+"  "+"ϵͳ��Ϣ: "+sysmsg);
				}
				else if(type.equalsIgnoreCase("����ر�")){

					output.close();
					input.close();
					socket.close();
					
					textarea.append("["+sdf.format(d)+"]"+"  "+"�������ѹرգ�\n");
					combobox.setEnabled(false);
					
					clientMessage.setEnabled(false);
					clientMessageButton.setEnabled(false);
					showStatus.setText("");//clientMessage,clientMessageButton,showStatus��֪��Ϊʲô�����޷�����Ϊfalse
					
					break;
				}
				else if(type.equalsIgnoreCase("������Ϣ")){
					String message = (String)input.readObject();
					textarea.append("["+sdf.format(d)+"]"+"  "+message);
				}
				else if(type.equalsIgnoreCase("�û��б�")){
					String userlist = (String)input.readObject();
					String usernames[] = userlist.split("\n");
					combobox.removeAllItems();
					
					int i =0;
					combobox.addItem("������");
					while(i < usernames.length){
						combobox.addItem(usernames[i]);
						i ++;
					}
					combobox.setSelectedIndex(0);
					showStatus.setText("�����û� " + usernames.length + " ��");
				}
			}
			catch (Exception e ){
				System.out.println(e);
			}
		}
	}
}