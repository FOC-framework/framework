package com.foc.web.microservice.entity;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.foc.Globals;
import com.foc.admin.FocUser;
import com.foc.admin.FocUserDesc;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.list.FocList;
import com.foc.web.microservice.FocObjectServlet;
import com.foc.web.microservice.FocServletRequest;

public class FocEntityServlet2<O extends FocObject> extends FocObjectServlet<O> {

	private static String uiclassname = null;
	
	public void extractUIClassname(HttpServletRequest request) {
		if(uiclassname == null) {
			uiclassname = request.getParameter("uiclass");
		}
	}
	
	@Override
	protected String getUIClassName() {
		return uiclassname;
	}

	@Override
	public FocDesc getFocDesc() {
		return null;
	}

	@Override
	public String getNameInPlural() {
		return "list";
	}

	public FocList simpleObjectServlet_NewList(HttpServletRequest request, HttpServletResponse response, boolean loaded) {
		FocList list = getFocDesc().getFocList();
		list.reloadFromDB();
		return list;
	}
	
	public void simpleObjectServlet_SetOrder(FocList list, HttpServletRequest request, HttpServletResponse response) {
		if (list != null && list.getFilter() != null) {
			FocDesc focDesc = list.getFocDesc();
			if (focDesc != null) {
				String order = "";
				if(focDesc.getFieldByID(FField.FLD_DATE) != null) {
					order += FField.FNAME_DATE +" DESC";
				}
				if(focDesc.getFieldByID(FField.REF_FIELD_ID) != null) {
					if(order.length() > 0) order += " ";
					order += FField.REF_FIELD_NAME+" DESC";
				}
				list.getFilter().setOrderBy(order);
			}
		}
	}
	
	@Override
	public boolean useCachedList(FocServletRequest focRequest) {
		boolean useCash = false;
		FocDesc focDesc = getFocDesc();
		if (focDesc != null) {
			if (focDesc.isListInCache()) {
				useCash = true;
			} else {
				useCash = false;
			}
		}
		return useCash;
	}
	
	@Override
	public FocList newFocList(HttpServletRequest request, HttpServletResponse response, boolean loaded) {
		FocList list = null;
		
		FocDesc focDesc = getFocDesc();
		if (focDesc != null) {
			if (focDesc.isListInCache()) {
				list = focDesc.getFocList();
				if (loaded) {
					list.loadIfNotLoadedFromDB();
				}
			} else {
				int start = getStartParameter(request);
				int count = getCountParameter(request);
				list = simpleObjectServlet_NewList(request, response, loaded);
				simpleObjectServlet_SetOrder(list, request, response);
				list.getFilter().setOffset(start, count);
				if(loaded) {
					list.loadIfNotLoadedFromDB();
				}
			}
		}
		return list;
	}
	
	@Override
	public void fillFocObjectFromJson(O focObj, JSONObject jsonObj) throws Exception {
	}
	
  @Override
	public void afterPost(FocServletRequest focServletRequest, O focObj, boolean created) {
	}

	@Override
	public SessionAndApplication pushSession(HttpServletRequest request, HttpServletResponse response) throws IOException {
		SessionAndApplication session = super.pushSession(request, response);
		if(session != null){
			String authTokenHeader = request.getHeader("Authorization");
			if(authTokenHeader != null && authTokenHeader.startsWith("Bearer")){
				String token = authTokenHeader.substring("Bearer".length()).trim();
				FocSimpleTokenAuth auth = new FocSimpleTokenAuth();
				String username = auth.verifyToken(token);

				if(username != null){
					FocList list = FocUserDesc.getInstance().getFocList();
					if(list != null){
						list.loadIfNotLoadedFromDB();
						FocUser user = (FocUser) list.searchByPropertyStringValue(FocUserDesc.FLD_NAME, username, false);
						// Reload once if we don't find. This is in case a new user was
						// created
						if(user == null){
							Globals.logString(" = Username: " + username + " not found reloading user list");
							list.reloadFromDB();
							user = (FocUser) list.searchByPropertyStringValue(FocUserDesc.FLD_NAME, username, false);
						}

						if(user != null && !user.isSuspended()){
							session.getWebSession().setFocUser(user);
							Globals.logString(" = Session opened for username: " + username);
						}else{
							Globals.logString(" = Username: " + username + " not found, logout()");
							session.logout();
							session = null;
						}
					}else{
						Globals.logString(" = FocUser list null");
						session.logout();
						session = null;
					}
				}else{
					Globals.logString(" = Token Subject (Username) null!");
					session.logout();
					session = null;
				}
			}else{
				Globals.logString(" = Authorization header with 'Bearer' missing");
				session.logout();
				session = null;
			}
		}
		return session;
	}

}