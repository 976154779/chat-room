package server;

import java.sql.*;
import java.util.*;

public class DBHandler {

	private static Connection con = getConn();

	private static Statement smt = null;

	private static Connection getConn() {
		// ���ݿ��URL
		String url = "jdbc:mysql://localhost:3306/chatroom?useSSL=false";

		try {
			Class.forName("com.mysql.jdbc.Driver");
			String name = "root";
			String password = "sunshy123";
			return (DriverManager.getConnection(url, name, password));

		} catch (SQLException e) {

			e.printStackTrace();

		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		}

		return null;
	}
	
	// ִ�в�ѯ���
	private static ResultSet exeQuery(String query) throws SQLException {
		if (con == null) {
			con = getConn();
		}
		if (smt == null) {
			smt = con.createStatement();
		}
		return smt.executeQuery(query);

	}

	// ִ��ɾ���Ͳ������
	private static void exeUpdateQuery(String query) throws SQLException {
		if (smt == null) {
			smt = con.createStatement();
		}
		smt.executeUpdate(query);//����int
	}

	//��֤�û�
	public static boolean isAuthorized(String name, String pw) {
		String query = "select * from userInfo where binary name='" + name//Ҫ���Ϲؼ���binary  MYSQL��ѯĬ���ǲ����ִ�Сд��
				+ "' and binary password='" + pw + "'";

		try {
			ResultSet rs = exeQuery(query);
			return rs.next();
		} catch (SQLException e) {

			e.printStackTrace();
			return false;//����������ݿ��쳣��Ҳ������false
		}

	}
	
	//ɾ���û� ����Ա��ʹ��
	/*��һ�����ж���һ������Ϊstatic���Ǿ���˵�����豾��Ķ��󼴿ɵ��ô˷���

   	����Ϊstatic�ķ��������¼������ƣ� 
	�� ���ǽ��ܵ���������static ������ 
	�� ����ֻ�ܷ���static���ݡ� 
	�� ���ǲ������κη�ʽ����this ��super��
	*/
	public static boolean deleteUser(String name) {
		String query = "delete from userInfo where binary name='" + name + "'";

		try {
			exeUpdateQuery(query);
			return true;
		} catch (SQLException e) {

			e.printStackTrace();
			return false;
		}

	}

	//��õ�½��Ȩ�� ����һ���û�1 �͹���Ա0
	public static int getPrio(String name, String password) {
		String query = "select role from userInfo where binary name='" + name
				+ "' and  binary password='" + password + "'";

		ResultSet rs;
		try {
			rs = exeQuery(query);
			rs.next();
			return rs.getInt(1);
		} catch (SQLException e) {

			e.printStackTrace();
			return -1;
		}

	}
	
	//ע���û�
	public static int regUser(String name, String password) {
		String query1 = "insert into userInfo(name,password,role,Online) values('"
				+ name + "','" + password + "',1,0)";
		String query2 = "select count(*) from userInfo where binary name ='" + name
				+ "'";
		ResultSet rs;
		try {
			rs = exeQuery(query2);
			rs.next();
			if (rs.getInt(1) > 0) {
				return 1;//������
			}
			exeUpdateQuery(query1);
			return 0;//�ɹ�
		} catch (SQLException e) {

			e.printStackTrace();
			return -1;//ʧ��
		}

	}
	
	//ͳ���û�����
	public static int getAllUsersNum() {

		String query = "select count(*) from userInfo";

		try {
			ResultSet rs = exeQuery(query);
			rs.next();
			return rs.getInt(1);
		} catch (SQLException e) {

			e.printStackTrace();
			return -1;
		}

	}

	//��������û�����Ϣ
	public static List getAllUsers() {

		String query = "select name from userInfo where role>0";
		List names = new ArrayList();
		try {
			ResultSet rs = exeQuery(query);
			while (rs.next()) {
				names.add(rs.getString(1));
			}

		} catch (SQLException e) {

			e.printStackTrace();
		}

		return names;

	}

	//�ж��Ƿ��ǹ���Ա
	public static boolean isAdmin(String name, String password) {
		String query = "select * from userInfo where binary name='" + name
				+ "' and binary password='" + password + "' and role=0";

		ResultSet rs;
		try {
			rs = exeQuery(query);
			return rs.next();
		} catch (SQLException e) {

			e.printStackTrace();
			return false;
		}
	}
	
	//�����û���ǰ״̬Ϊ����
		public static void Online(String name){
			String query= "update userInfo set Online = 1 where binary name='" + name+ "'";
			
			try {
				
				exeUpdateQuery(query);
				
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
		}
		//�����û���ǰ״̬Ϊ����
		public static void Offline(String name){
			String query= "update userInfo set Online = 0 where binary name='" + name+ "'";
					
			try {
						
				exeUpdateQuery(query);
						
			} catch (SQLException e) {
						
				e.printStackTrace();
 
			}
		}
		//�����û���ǰ״̬Ϊ����
				public static void OfflineAllUsers(){
					String query= "update userInfo set Online = 0 ";
							
					try {
								
						exeUpdateQuery(query);
								
					} catch (SQLException e) {
								
						e.printStackTrace();
		 
					}
				}
	//�ж��Ƿ�����
	public static boolean isOnline(String name){
		String query = "select * from userInfo where binary name='" + name+ "' and online=1";

		ResultSet rs;
		try {
			rs = exeQuery(query);
			return rs.next();
		} catch (SQLException e) {

			e.printStackTrace();
			return false;
		}
	}
}
			
			
			
			
			
			