package com.foc.web.microservice.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.foc.Globals;
import com.foc.admin.FocGroup;
import com.foc.admin.FocGroupDesc;
import com.foc.desc.FocDesc;
import com.foc.list.FocList;
import com.foc.util.Utils;
import com.foc.web.microservice.entity.FocSimpleMicroServlet;
import com.foc.web.microservice.loockups.WSLookupFactory;

public class ReloadServlet extends FocSimpleMicroServlet {

	public static final String PARAM_TABLE_LIST = "tables";
	public static final String PARAM_TABLE_NAME = "table_name";
	public static final String PARAM_RELOAD_ALL = "reload_all";
	public static final String PARAM_OBJECT_REF = "object_refs";
	
	@Override
	public int getAuthenticationMethod() {
		return AUTH_NONE;
	}
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		SessionAndApplication sessionAndApp = pushSession(request, response);
		Globals.logString(" => GET Begin ReloadServlet /reload");
		try{
			StringBuffer buffer = getRequestAsStringBuffer(request);
			String       reqStr = buffer.toString();
			JSONObject mainObject = new JSONObject(reqStr);
			if(mainObject.has(PARAM_TABLE_LIST)) {
				JSONArray tables = new JSONArray(mainObject.getString(PARAM_TABLE_LIST));
				if (tables != null) {
					for(int i=0; i<tables.length(); i++) {
						String table = tables.getString(i);
						if(!Utils.isStringEmpty(table)) {
							JSONObject jsonObject = new JSONObject(table);
							if(jsonObject.has(PARAM_TABLE_NAME)) {
								String table_name = jsonObject.getString(PARAM_TABLE_NAME);
								if (!Utils.isStringEmpty(table_name)) {
									boolean reload_all = true;
									if(jsonObject.has(PARAM_RELOAD_ALL)) {
										String allData = jsonObject.getString(PARAM_RELOAD_ALL);
										if(allData != null && allData.equalsIgnoreCase("false")) reload_all = false;
									}
									String object_refs = "";
									if(!reload_all && jsonObject.has(PARAM_OBJECT_REF)) object_refs = jsonObject.getString(PARAM_OBJECT_REF);
									Globals.logString(" - Reloading table:"+table_name+" all:"+reload_all+" refs:"+object_refs);
									executeRefreshTable(table_name, reload_all, object_refs);
								}
							}
						}
					}
				}
			}
		} catch (JSONException e){
			e.printStackTrace();
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
	
	public void executeRefreshTable(String table_name, boolean reload_all, String object_refs) throws JSONException {
		if(!Utils.isStringEmpty(table_name)) {
//			if (table_name.equals(FocGroupDesc.DB_TABLE_NAME) || table_name.equals(GrpMobileModuleRightsDesc.DB_TABLE_NAME)) {
//				refreshGroupList();
//			} else {
			FocDesc desc = Globals.getApp().getFocDescByName(table_name);
			if(desc == null) {
				refreshLookupByName(table_name, reload_all, object_refs);
			} else {
				refreshLookupByDesc(desc, reload_all, object_refs);
			}
//			}
		}
	}
	
	private void refreshGroupList() {
		FocGroupDesc groupDesc = FocGroupDesc.getInstance();
		FocList groupList = groupDesc.getFocList();
		groupList.reloadFromDB();
		for(int g=0; g<groupList.size(); g++) {
			FocGroup group = (FocGroup) groupList.getFocObject(g);
			if(group.getMobileModuleRightsList() != null) group.getMobileModuleRightsList().reloadFromDB();
			if(group.getWebModuleRightsList() != null) group.getWebModuleRightsList().reloadFromDB();
		}
	}
	
	private void refreshLookupByName(String table_name, boolean reload_all, String object_refs) throws JSONException {
		WSLookupFactory factory = WSLookupFactory.getInstance();
		if(factory.hasLookupByName(table_name)) {
			if(reload_all || Utils.isStringEmpty(object_refs)) {
				factory.refreshLookupByName(table_name);
			}else if(!Utils.isStringEmpty(object_refs)){
				JSONArray array = new JSONArray(object_refs);
				for(int j=0; j < array.length(); j++) {
					try{
						Long ref = array.getLong(j);
						factory.refreshLookupByNameAndObjectReference(table_name,ref);
					} catch (JSONException e){
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	private void refreshLookupByDesc(FocDesc desc, boolean reload_all, String object_refs) throws JSONException {
		WSLookupFactory factory = WSLookupFactory.getInstance();
		boolean noRefreshDone = true;
		boolean hasLookupByDesc = factory.hasLookupByFocDesc(desc); 
		if(hasLookupByDesc && reload_all) {
			noRefreshDone = factory.refreshLookupByDesc(desc);
		}else if(!Utils.isStringEmpty(object_refs)){
			JSONArray array = new JSONArray(object_refs);
			boolean itemRefreshFailed = false; 
			for(int j=0; j < array.length(); j++) {
				try{
					boolean itemDidntRefresh = true;
					Long ref = array.getLong(j);
					if(hasLookupByDesc) {
						itemDidntRefresh = factory.refreshLookupByDescAndObjectReference(desc, ref);
					} else {
						if (desc != null) {
							itemDidntRefresh = desc.refreshCachedListFocObject(ref);
						}
					}
					if(itemDidntRefresh) itemRefreshFailed = true;
				} catch (JSONException e){
					e.printStackTrace();
				}
			}
			noRefreshDone = itemRefreshFailed;
		}
		if(noRefreshDone) {
			refreshCachedListByDesc(desc, reload_all, object_refs);
		}
	}
	
	private void refreshCachedListByDesc(FocDesc desc, boolean reload_all, String object_refs) throws JSONException {
		if(desc != null && desc.getFocList() != null) {
			if(reload_all || Utils.isStringEmpty(object_refs)) {
				desc.getFocList().reloadFromDB();
			} else if(!Utils.isStringEmpty(object_refs)){
				JSONArray array = new JSONArray(object_refs);
				for(int j=0; j < array.length(); j++) {
					try{
						Long ref = array.getLong(j);
						desc.refreshCachedListFocObject(ref);
					} catch (JSONException e){
						e.printStackTrace();
					}
				}
			}
		}
	}
	
}
