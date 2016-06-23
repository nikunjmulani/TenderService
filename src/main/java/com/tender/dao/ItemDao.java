package com.tender.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.tender.beans.Item;
import com.tender.utils.DBConnection;

public class ItemDao {
	public List<Item> getItems() throws Exception {
		List<Item> itemList = new ArrayList<Item>();
		DBConnection database = new DBConnection();
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			connection = database.getConnection();

			ps = connection.prepareStatement("SELECT ITEM_ID, ITEM_NAME, UNIT, RATE FROM MST_ITEMS WHERE ITEM_NAME IS NOT NULL ORDER BY ITEM_ID");
			rs = ps.executeQuery();
			Item item = null;
			while (rs.next()) {
				item = new Item();
				item.setItemId(rs.getInt("ITEM_ID"));
				item.setItemName(rs.getString("ITEM_NAME"));
				item.setUnit(rs.getInt("UNIT"));
				item.setRate(rs.getInt("RATE"));
				itemList.add(item);
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

}
