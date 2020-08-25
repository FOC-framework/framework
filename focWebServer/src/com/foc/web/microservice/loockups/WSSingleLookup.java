package com.foc.web.microservice.loockups;

import com.foc.Globals;
import com.foc.desc.FocDesc;
import com.foc.list.FocList;
import com.foc.shared.json.B01JsonBuilder;

public class WSSingleLookup {
	private String  key     = null;
	private String  json    = null;
	private FocDesc focDesc = null;
	
	public WSSingleLookup(String key, FocDesc focDesc) {
		this.key = key;
		this.focDesc = focDesc;
	}

	private void dispose() {
		focDesc = null;
	}
	
	public FocDesc getFocDesc() {
		return focDesc;
	}
	
	public synchronized String getJson() {
		if(json == null) {
			jsonRebuild();
		}
		return json;
	}
	
	public String getKey() {
		return key;
	}

	private synchronized void replaceJson(String newJson) {
		json = newJson;
	}

	protected B01JsonBuilder newJsonBuiler() {
		B01JsonBuilder builder = new B01JsonBuilder();
		builder.setPrintForeignKeyFullObject(true);
		builder.setHideWorkflowFields(true);
		builder.setScanSubList(true);
		return builder;
	}

	public FocList getFocList(boolean load) {
		FocDesc focDesc = getFocDesc();
		FocList list = focDesc != null ? focDesc.getFocList() : null;
		if(list != null && load) {
			list.loadIfNotLoadedFromDB();
		}
		return list; 
	}
		
	public void jsonRebuild() {
		try {
			B01JsonBuilder builder = newJsonBuiler();
			FocList focList = getFocList(true); 
			if (focList != null && builder != null) {
				focList.toJson(builder);
				String newJson = builder.toString();
				if(newJson != null) {
					replaceJson(newJson);
				}
			}
		} catch (Exception e) {
			Globals.logException(e);
		}
	}
	
	public synchronized void refresh(){
		FocList focList = getFocList(true); 
		if (focList != null) focList.reloadFromDB();
		jsonRebuild();
	}

}