package com.tender.utils;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;

public class JasperCompiler {
	
	public static void main(String arg[]) {
		
		String sourceFileName = "C:\\Manish\\Practice\\tenderservices\\src\\main\\webapp\\reports\\AbstractReport.jrxml";
		
		try {
			JasperCompileManager.compileReport(sourceFileName);
		} catch (JRException e) {
			e.printStackTrace();
		}
	}
}
