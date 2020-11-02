package com.foc.web.microservice.servlets;

import java.io.IOException;
import java.net.URI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import com.foc.ConfigInfo;
import com.foc.Globals;
import com.foc.admin.FocGroup;
import com.foc.admin.FocGroupDesc;
import com.foc.admin.GrpMobileModuleRightsDesc;
import com.foc.desc.FocDesc;
import com.foc.list.FocList;
import com.foc.util.Utils;
import com.foc.vaadin.FocWebEnvironment;
import com.foc.web.microservice.entity.FocSimpleMicroServlet;
import com.foc.web.microservice.loockups.WSLookupFactory;
import com.foc.web.microservice.loockups.WSSingleLookup;

public class ReloadServlet extends FocSimpleMicroServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		SessionAndApplication sessionAndApp = pushSession(request, response);
		Globals.logString(" => GET Begin ReloadServlet /reload");
		String[] filters = request.getParameterValues("table_name");
		if (filters != null) {
			for(int i=0; i<filters.length; i++) {
				String tableName = filters[i];
				Globals.logString("ReloadServlet: table:"+tableName);
				if(!Utils.isStringEmpty(tableName)) {
					if (tableName.equals(FocGroupDesc.DB_TABLE_NAME) || tableName.equals(GrpMobileModuleRightsDesc.DB_TABLE_NAME)) {
						FocGroupDesc groupDesc = FocGroupDesc.getInstance();
						FocList groupList = groupDesc.getFocList();
						groupList.reloadFromDB();
						for(int g=0; g<groupList.size(); g++) {
							FocGroup group = (FocGroup) groupList.getFocObject(g);
							group.getMobileModuleRightsList().reloadFromDB();
						}
					} else {
						FocDesc desc = Globals.getApp().getFocDescByName(tableName);
						if(desc != null && desc.loadCachedListFromServletOnAction_CRUD() && desc.getFocList() != null) {
							WSLookupFactory factory = WSLookupFactory.getInstance();
							WSSingleLookup lookup= factory.getLookupByFocDescName(desc.getName());
							if(lookup !=null) {
								Globals.logString("Reload Servlet : lookup found, name : "+desc.getName());
								lookup.refresh();
							} else {
								desc.getFocList().reloadFromDB();
							}
						}
					}
				}
			}
		}
		response.setHeader("Content-Type", "text/html; charset=UTF-8");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "GET");
		response.setStatus(HttpServletResponse.SC_OK);
		setCORS(response);
		response.getWriter().println("{\"status\":\"ok\"}");
		if(sessionAndApp != null) sessionAndApp.logout();
		Globals.logString(" <= GET End ReloadServlet /reload");
	}
	
	public static void sendReloadRequestToBackend(String tableName, String portConfigInfoString, String port){
		String portConfig=ConfigInfo.getProperty(portConfigInfoString); // "fenix.localhost.port"
		if(Utils.isStringEmpty(portConfig)) portConfig = port;
		String url = "http://localhost:" + portConfig + "/reload";
		try{
			HttpGet someHttpGet = new HttpGet(url);
			URIBuilder uriBuilder = new URIBuilder(someHttpGet.getURI());
			uriBuilder.addParameter("table_name", tableName);
			URI uri = uriBuilder.build();
			someHttpGet.setURI(uri);
			HttpClient client = HttpClientBuilder.create().build();
			HttpResponse response = client.execute(someHttpGet);
			if(response != null) {
				if(response.getStatusLine().getStatusCode() != HttpServletResponse.SC_OK) {
//					Globals.showNotification("Lists refreshed successfully", "", FocWebEnvironment.TYPE_HUMANIZED_MESSAGE);
//				} else {
					Globals.showNotification("Lists refresh failed", "", FocWebEnvironment.TYPE_ERROR_MESSAGE);
				}
			}
		}catch (Exception e){
			Globals.logException(e);
		}
	}
}
