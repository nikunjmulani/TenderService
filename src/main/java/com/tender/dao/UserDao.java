package com.tender.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.tender.utils.DBConnection;

public class UserDao {

	public String validateUser(String username, String password) throws Exception {
		DBConnection database = new DBConnection();
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			connection = database.getConnection();
			ps = connection.prepareStatement("SELECT * FROM users where user_name=? and user_pwd = ?");

			ps.setString(1, username);
			ps.setString(2, password);

			rs = ps.executeQuery();

			if (rs.next()) {
				return "success";
			}

		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (connection != null)
				connection.close();
		}
		return "fail";

	}
}
