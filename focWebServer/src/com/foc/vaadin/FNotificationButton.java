/*******************************************************************************
 * Copyright 2016 Antoine Nicolas SAMAHA
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.foc.vaadin;

import com.foc.Globals;
import com.foc.list.FocList;
import com.foc.vaadin.gui.FVIconFactory;
import com.vaadin.ui.NativeButton;

@SuppressWarnings("serial")
public class FNotificationButton extends NativeButton { 
	
	private static final long DELAY = 60 * 1000;
	
	private java.sql.Date      lastUpdate         = null;
	private FocWebVaadinWindow focWebVaadinWindow = null;
	
	public FNotificationButton(FocWebVaadinWindow focWebVaadinWindow) {
		this.focWebVaadinWindow = focWebVaadinWindow;
		setIcon(FVIconFactory.getInstance().getFVIcon(FVIconFactory.ICON_NOTE));
		addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
//        FocList focList = FChatDesc.getInstance().getFocList();
//        getFocWebVaadinWindow().changeCentralPanelContent_ToTableForFocList(focList);
			}
		});
		resetIfNeeded(null);
	}
	
	public void dispose(){
		focWebVaadinWindow = null;
		lastUpdate = null;
	}

	public java.sql.Date getLastUpdate() {
		return lastUpdate;
	}

	public FocWebVaadinWindow getFocWebVaadinWindow() {
		return focWebVaadinWindow;
	}

  private int getNotification_Size(FocList focList){
  	int size = 0;
  	if(focList == null){
//  		focList = FChatDesc.getInstance().getFocList();
  	}else{
  		size = focList.size();
  	}
		return size;
  }	
	
	public void resetIfNeeded(FocList focList){
		boolean doReset = lastUpdate == null;
		
		java.sql.Date currentDate = null;
		if(!doReset){
			currentDate = Globals.getApp().getSystemDate();
			if(currentDate != null){
				long timeDiff = currentDate.getTime() - lastUpdate.getTime();
				doReset = timeDiff > DELAY;
			}
		}
		
		if(doReset){
			if(focWebVaadinWindow != null){
				reset(focList);
	  	}
		}
	}
  
	public void reset(FocList focList){
		int listSize = getNotification_Size(focList);
		setCaption(listSize + "");
//  	setVisible(listSize > 0);
		lastUpdate = Globals.getApp().getSystemDate();
	}
}
