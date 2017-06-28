package server;

import java.sql.*;
import java.util.*;

public class DBHandler {

	private static Connection con = getConn();

	private static Statement smt = null;

	private static Connection getConn() {
		// 数据库的URL
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
	
	// 执行查询语句
	private static ResultSet exeQuery(String query) throws SQLException {
		if (con == null) {
			con = getConn();
		}
		if (smt == null) {
			smt = con.createStatement();
		}
		return smt.executeQuery(query);

	}

	// 执行删除和插入语句
	private static void exeUpdateQuery(String query) throws SQLException {
		if (smt == null) {
			smt = con.createStatement();
		}
		smt.executeUpdate(query);//返回int
	}

	//验证用户
	public static boolean isAuthorized(String name, String pw) {
		String query = "select * from userInfo where binary name='" + name//要加上关键字binary  MYSQL查询默认是不区分大小写的
				+ "' and binary password='" + pw + "'";

		try {
			ResultSet rs = exeQuery(query);
			return rs.next();
		} catch (SQLException e) {

			e.printStackTrace();
			return false;//如果出现数据库异常，也将返回false
		}

	}
	
	//删除用户 管理员可使用
	/*在一个类中定义一个方法为static，那就是说，无需本类的对象即可调用此方法

   	声明为static的方法有以下几条限制： 
	· 它们仅能调用其他的static 方法。 
	· 它们只能访问static数据。 
	· 它们不能以任何方式引用this 或super。
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

	//获得登陆者权限 区分一般用户1 和管理员0
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
	
	//注册用户
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
				return 1;//有重名
			}
			exeUpdateQuery(query1);
			return 0;//成功
		} catch (SQLException e) {

			e.printStackTrace();
			return -1;//失败
		}

	}
	
	//统计用户总数
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

	//获得所有用户的信息
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

	//判断是否是管理员
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
	
	//设置用户当前状态为在线
		public static void Online(String name){
			String query= "update userInfo set Online = 1 where binary name='" + name+ "'";
			
			try {
				
				exeUpdateQuery(query);
				
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
		}
		//设置用户当前状态为离线
		public static void Offline(String name){
			String query= "update userInfo set Online = 0 where binary name='" + name+ "'";
					
			try {
						
				exeUpdateQuery(query);
						
			} catch (SQLException e) {
						
				e.printStackTrace();
 
			}
		}
		//设置用户当前状态为离线
				public static void OfflineAllUsers(){
					String query= "update userInfo set Online = 0 ";
							
					try {
								
						exeUpdateQuery(query);
								
					} catch (SQLException e) {
								
						e.printStackTrace();
		 
					}
				}
	//判断是否在线
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
			
			
			
			
			
			