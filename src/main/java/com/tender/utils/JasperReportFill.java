package com.tender.utils;

import java.io.File;
import java.sql.Connection;
import java.util.Map;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

public class JasperReportFill {
	
	private static ClassLoader loader = JasperReportFill.class.getClassLoader();
	private static String resourcePath = loader.getResource("com").toString();
	
	
	static {
		resourcePath = resourcePath.replaceAll("file:/", "");
		resourcePath = resourcePath.replaceAll("%20", " ");
		resourcePath = resourcePath.substring(0, resourcePath.indexOf("tenderservices")) + "tenderservices";	
	}
	
	public static void main(String str[]) {
		resourcePath = "file:/setup%20tool/hello";
		resourcePath = resourcePath.replaceAll("file:/setup%20tool/hello", "12");
		System.out.println("JasperReportFill.main()"+resourcePath);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static File getReport(String jasperName, Map parameters) throws Exception  {
		String sourceFileName = resourcePath + "/reports/"+jasperName+".jasper";
		DBConnection conUtil = new DBConnection();
		Connection con = null;

		try {
			con = conUtil.getConnection();
			JasperPrint jp = JasperFillManager.fillReport(sourceFileName, parameters, con);
			JasperExportManager.exportReportToPdfFile(jp, resourcePath + "/reports/"+jasperName+".pdf");
			return new File(resourcePath + "/reports/"+jasperName+".pdf");
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (con != null) con.close();
		}
	}
}
