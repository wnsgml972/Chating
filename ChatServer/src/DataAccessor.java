

/*
 * DB를 사용하기 위해선 프로젝트 파일에 jar 파일을 추가해줘야 한다.
 */

/*
 * Connection 으로 연결
 * Statement 생성
 * ResultSet으로 결과 받기
 * Connection 으로 연결 해제
 */


import java.sql.*;
import java.util.Scanner;

public class DataAccessor
{
	private static DataAccessor INSTANCE;
	
	private Connection con = null;
	private Statement stat = null;
	private ResultSet rs = null;

	private final String url = "jdbc:mysql://localhost:3306/login";	// 마지막 login 은 스키마
	private final String user = "root";
	private String password = "1234";

	public static DataAccessor getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new DataAccessor();
		}
		return INSTANCE;
	}
	
	private DataAccessor() { }
	
	public void connectDB()
	{
		try {
			Scanner sc = new Scanner(System.in);
			System.out.print("Mysql 패스워드 입력 >> ");
			Integer pw = sc.nextInt();
			if(!pw.toString().equals(password))
			{	
				System.out.println("Wrong PassWord");
				System.exit(1);
			}
			con = DriverManager.getConnection(url, user, password);
			stat = con.createStatement(); //Statement 생성
						

		} catch (SQLException e) {
			errorHandler(e);
		}
	}
	public void closeDB()
	{
		try {
			con.close();
			stat.close();
			rs.close();
		} catch (SQLException e) {
			errorHandler(e);
		}
	}
	
	private void errorHandler(Exception e)
	{
		System.out.println(e.getMessage());
		e.printStackTrace();
	}

	
	synchronized public boolean memberLogin(String name, String password) 
	{
		try {
			rs = stat.executeQuery("select name,password,login from user where name=\"" + name + "\"");
			while (rs.next()) {
				if (rs.getString("name").toUpperCase().equals(name.toUpperCase()) && rs.getString("password").equals(password)
						&& rs.getString("login").equals("false")) {						
					stat.executeUpdate("update user set login=\"" + "true" + "\"where name=\"" + name + "\"");
					return true;
				}
			}
		} catch (SQLException e) {
			errorHandler(e);
		}
		return false;
	}
	synchronized public boolean memberLogout(String name) 
	{
		try {
			rs = stat.executeQuery("select name,login from user where name=\"" + name + "\"");
			while (rs.next()) {
				if (rs.getString("name").toUpperCase().equals(name.toUpperCase()) && rs.getString("login").equals("true"))
				{
					stat.executeUpdate("update user set login=\"" + "false" + "\"where name=\"" + name + "\"");
					return true;
				}
			}
		} catch (SQLException e) {
			errorHandler(e);
		}
		return false;
	}
	synchronized public boolean allMemberLogout(String login)
	{
		try {
			rs = stat.executeQuery("select login from user where login=\"" + login + "\"");
			while (rs.next()) {
				if (rs.getString("login").equals("true"))
				{
					stat.executeUpdate("update user set login=\"" + "false" + "\"");
					return true;
				}
			}
		} catch (SQLException e) {
			errorHandler(e);
		}
		return false;
	}
	
	synchronized public boolean loginCheck(String name) 
	{
		try {
			rs = stat.executeQuery("select name,login from user where name=\"" + name + "\"");
			while (rs.next()) {
				if (rs.getString("name").toUpperCase().equals(name.toUpperCase()) && rs.getString("login").equals("false")) {
					return false;
				}
			}
		} catch (SQLException e) {
			errorHandler(e);
		}
		return true;
	}
	synchronized public boolean joinCheck(String name) {
		try {
			rs = stat.executeQuery("select name from user where name=\"" + name + "\"");
			while (rs.next()) {
				if (rs.getString("name").toUpperCase().equals(name.toUpperCase())) {
					return false;	//대소문자 구별을 없애기 위해
				}
			}
		} catch (SQLException e) {
			errorHandler(e);
		}
		return true;
	}
	synchronized public boolean memberJoin(String name, String password) {
		try {
			rs = stat.executeQuery("select name from user where name=\"" + name + "\"");
			while (rs.next()) {
				if (rs.getString("name").toUpperCase().equals(name.toUpperCase())) {
					return false;
				}
			}
		} catch (SQLException e) {
			errorHandler(e);
		}
		try {
			stat.execute("insert into user(name, password, login) values(\"" + name + "\",\"" + password + "\",\"" + "false\")");
		} catch (SQLException e) {
			errorHandler(e);
		}
		return true;
	}	
	
	
	synchronized public boolean DeleteData(String name) {
		try {
			rs = stat.executeQuery("select name from user where name=\"" + name + "\"");
			while (rs.next()) {
				if (rs.getString("name").toUpperCase().equals(name.toUpperCase())) {
					stat.execute("delete from user where name=\"" + name + "\"");
					return true;
				}
			}
		} catch (SQLException e) {
			errorHandler(e);
		}
		return false;
	}

}
