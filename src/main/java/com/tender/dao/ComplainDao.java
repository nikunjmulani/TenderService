package com.tender.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.tender.beans.Complain;
import com.tender.beans.Item;
import com.tender.utils.DBConnection;

public class ComplainDao {

	public List<Complain> getComplains(String billYear, String billMonth) throws Exception {
		List<Complain> list = new ArrayList<Complain>();
		DBConnection database = new DBConnection();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = database.getConnection();
			ps = con.prepareStatement("SELECT complaint_id, complain_code, complain_desc FROM complaints where bill_year = ? and bill_month=?");
			ps.setString(1, billYear);
			ps.setString(2, billMonth);
			rs = ps.executeQuery();

			Complain complain = null;
			while (rs.next()) {
				complain = new Complain();
				complain.setComplainId(rs.getInt("complaint_id"));
				complain.setComplainDesc(rs.getString("complain_desc"));
				complain.setComplainCode(rs.getString("complain_code"));
				list.add(complain);
			}

		} finally {
			if (ps != null)
				ps.close();
			if (rs != null)
				rs.close();
			if (con != null)
				con.close();
		}

		return list;
	}

	public String saveComplain(Complain complain) throws Exception {
		DBConnection database = new DBConnection();
		Connection connection = null;

		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		PreparedStatement psItem = null;
		ResultSet rs = null;
		String save = "No";
		try {
			connection = database.getConnection();
			connection.setAutoCommit(false);
			int complainId = complain.getComplainId();
			if (complainId != 0) {
				ps1 = connection.prepareStatement("update complaints set complain_desc= ? where complaint_id=?");
				ps1.setString(1, complain.getComplainDesc());
				ps1.setInt(2, complain.getComplainId());
				ps1.executeUpdate();

				ps2 = connection.prepareStatement("delete from complaint_items where complaint_id=?");
				ps2.setInt(1, complain.getComplainId());
				ps2.executeUpdate();
			} else {
				ps1 = connection.prepareStatement("select max(complaint_id) as max_complaint_id from complaints");
				rs = ps1.executeQuery();
				if (rs.next()) {
					complainId = rs.getInt("max_complaint_id");
					complainId++;
				} else {
					complainId = 1;
				}

				ps2 = connection.prepareStatement("insert into complaints values(?,?,?,?,?)");
				ps2.setInt(1, complainId);
				ps2.setString(2, complain.getBillYear());
				ps2.setString(3, complain.getBillMonth());
				ps2.setString(4, complain.getComplainCode());
				ps2.setString(5, complain.getComplainDesc());
				int result = ps2.executeUpdate();
				if (result > 0) {
					save = "Yes";
				}
			}

			psItem = connection.prepareStatement("insert into complaint_items (complaint_id, item_id, rate, quantity, unit, amount) values(?,?,?,?, ?,?)");

			for (Item item : complain.getItemList()) {
				if (item.getItemName() != null) {
					psItem.setInt(1, complainId);
					psItem.setInt(2, item.getItemId());
					psItem.setDouble(3, item.getRate());
					psItem.setInt(4, item.getItemQuantity());
					psItem.setDouble(5, item.getUnit());
					psItem.setDouble(6, item.getAmount());
					psItem.execute();
				}
			}
			connection.commit();
		} catch (Exception e) {
			connection.rollback();
			throw e;
		} finally {
			if (psItem != null)
				psItem.close();
			if (ps2 != null)
				ps2.close();
			if (ps1 != null)
				ps1.close();
			if (rs != null)
				rs.close();
			if (connection != null)
				connection.close();
		}
		return save;
	}

	public List<Item> getComplainItems(int complainId) throws Exception {
		List<Item> itemList = new ArrayList<Item>();
		DBConnection database = new DBConnection();
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			connection = database.getConnection();
			ps = connection.prepareStatement("SELECT T1.ITEM_ID, T1.ITEM_NAME, T2.UNIT, T2.RATE, T2.QUANTITY, T2.AMOUNT FROM MST_ITEMS T1, COMPLAINT_ITEMS T2 WHERE T1.ITEM_ID = T2.ITEM_ID AND T2.COMPLAINT_ID=? ORDER BY T1.ITEM_ID");
			ps.setInt(1, complainId);
			rs = ps.executeQuery();
			Item item = null;
			int index = 0;

			while (rs.next()) {
				item = new Item();
				item.setIndex(index);
				item.setItemId(rs.getInt("ITEM_ID"));
				item.setItemName(rs.getString("ITEM_NAME"));
				item.setUnit(rs.getInt("UNIT"));
				item.setRate(rs.getInt("RATE"));
				item.setItemQuantity(rs.getInt("QUANTITY"));
				item.setAmount(rs.getDouble("AMOUNT"));
				itemList.add(item);
				index++;
			}
		} finally {
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
				if (connection != null)
					connection.close();
		}
		return itemList;
	}

	public void deleteComplaint(int complainId) throws Exception {

		DBConnection database = new DBConnection();
		Connection connection = null;
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;

		try {
			connection = database.getConnection();
			connection.setAutoCommit(false);

			ps1 = connection.prepareStatement("delete from complaint_items where complaint_id = ?");
			ps1.setInt(1, complainId);
			ps1.execute();

			ps2 = connection.prepareStatement("delete from complaints where complaint_id = ?");
			ps2.setInt(1, complainId);
			ps2.execute();

			connection.commit();

		} catch (Exception e) {
			connection.rollback();
			throw e;
		} finally {
			if (ps1 != null)
				ps1.close();
			if (ps2 != null)
				ps2.close();
			if (connection != null)
				connection.close();
		}
	}
}
