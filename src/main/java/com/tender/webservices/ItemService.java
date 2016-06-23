package com.tender.webservices;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.log4j.Logger;

import com.tender.beans.Complain;
import com.tender.beans.Item;
import com.tender.dao.ComplainDao;
import com.tender.dao.ItemDao;
import com.tender.dao.UserDao;
import com.tender.utils.ApplicationProperties;
import com.tender.utils.JasperReportFill;

@Path("/items")
public class ItemService {
	private static final Logger LOGGER = Logger.getLogger(ItemService.class);

	private ItemDao itemDAO;
	private ComplainDao compDao;
	private UserDao userDAO;

	public ItemService() {
		this.itemDAO = new ItemDao();
		this.compDao = new ComplainDao();
		this.userDAO = new UserDao();
	}

	@GET
	@Path("/items")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public List<Item> getItems() throws Exception {
		return itemDAO.getItems();
	}

	@GET
	@Path("/getComplainItems/{complainId}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public List<Item> getComplainItems(@PathParam("complainId") int complainId) throws Exception {
		return this.compDao.getComplainItems(complainId);
	}

	@DELETE
	@Path("/deleteComplaint/{complainId}")
	@Produces({ MediaType.TEXT_PLAIN })
	public String deleteComplaint(@PathParam("complainId") int complainId) throws Exception {
		try {
			this.compDao.deleteComplaint(complainId);
			LOGGER.info("Complaint is deleted successfully - " + complainId);
			return "Complaint is deleted successfully - " + complainId;
		} catch (Exception err) {
			LOGGER.error("Error while deletion complaint:"+complainId, err);
			return "Error while deletion complaint - " + complainId;

		}
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.TEXT_PLAIN })
	@Path("/saveComplain")
	public String saveComplain(Complain complain) {
		try {
			if (compDao.saveComplain(complain) == "Yes") {
				LOGGER.info("Complaint is saved successfully - " + complain);
				return "Complaint has been saved successfully";
			}

		} catch (Exception e) {
			LOGGER.error("Exception while save complaint:"+complain, e);
		}
		return "Complaint not saved";
	}

	@GET
	@Path("/complaint/{billYear}/{billMonth}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public List<Complain> getComplains(@PathParam("billYear") String billYear, @PathParam("billMonth") String billMonth) throws Exception {
		return compDao.getComplains(billYear, billMonth);
	}

	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces("application/text")
	public String login(@QueryParam("username") String username, @QueryParam("password") String password) throws Exception {
		return userDAO.validateUser(username, password);

	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@POST
	@Path("/getReport/{billYear}/{billMonth}/{reportName}")
	@Produces("application/pdf")
	public Response getReport(@PathParam("billYear") String billYear, @PathParam("billMonth") String billMonth, @PathParam("reportName") String reportName) throws Exception {
		ResponseBuilder response = null;
		try {
			Map parameters = new HashMap();
			parameters.put("billYear", billYear);
			parameters.put("billMonth", billMonth);
			parameters.put("billMonth_MMMYY", getDateFormat(billYear, billMonth));
			parameters.put("billNo", getBillNo(billMonth));	 	
			parameters.put("billDate", getBillDate(billYear, billMonth));
		 	parameters.put("companyName", ApplicationProperties.getProperty("companyName"));
		 	parameters.put("panNo", ApplicationProperties.getProperty("panNo"));
		 	
		 	
			File file = JasperReportFill.getReport(reportName, parameters);
			response = Response.ok((Object) file);
			response.header("Content-Disposition", "attachment; filename=" + reportName + "_" + billYear + "_" + billMonth + ".pdf");
			return response.build();
		} catch (Exception err) {
			LOGGER.error("Exception while Generate Report["+billYear+"|"+billMonth+"|"+reportName+"]:", err);
		}
		return null;
	}

	
	private static String getBillDate(String billYear, String billMonth) {
		String year[] = billYear.split("-");
		int billMonthInt = Integer.parseInt(billMonth);
		int billYearInt = Integer.parseInt(year[0]);

		if (billMonthInt == 0 || billMonthInt == 1 || billMonthInt == 2) {
			billYearInt = Integer.parseInt(year[1]);
		}
		
		Calendar c1 = Calendar.getInstance();
		c1.set(billYearInt, billMonthInt, 1);
		c1.add(Calendar.MONTH, 1);
		c1.add(Calendar.DAY_OF_MONTH, -1);
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		return dateFormat.format(c1.getTime());
	}


	private static String getDateFormat(String billYear, String billMonth) {

		String year[] = billYear.split("-");
		int billMonthInt = Integer.parseInt(billMonth);
		int billYearInt = Integer.parseInt(year[0]);

		if (billMonthInt == 0 || billMonthInt == 1 || billMonthInt == 2) {
			billYearInt = Integer.parseInt(year[1]);
		}

		Calendar c1 = Calendar.getInstance();
		c1.set(billYearInt, billMonthInt, 1);
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMM - yyyy");
		return dateFormat.format(c1.getTime());
	}

	private static String getBillNo(String billMonth) {
		String billNo;
		int billMonthInt = Integer.parseInt(billMonth);

		if (billMonthInt == 0 || billMonthInt == 1 || billMonthInt == 2) {
			billMonthInt = billMonthInt + 10;
			billNo = "RA" + billMonthInt;
		} else {
			billMonthInt = billMonthInt - 2;
			billNo = "RA" + billMonthInt;
		}
		return billNo;
	}

}
