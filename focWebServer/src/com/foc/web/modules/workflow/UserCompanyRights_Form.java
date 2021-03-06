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
package com.foc.web.modules.workflow;

import java.util.ArrayList;

import com.foc.business.company.Company;
import com.foc.business.company.UserCompanyRights;
import com.foc.business.workflow.WFOperator;
import com.foc.business.workflow.WFSite;
import com.foc.business.workflow.WFSiteDesc;
import com.foc.business.workflow.WFTitle;
import com.foc.business.workflow.WFTitleDesc;
import com.foc.dataWrapper.FocListWrapper;
import com.foc.list.FocList;
import com.foc.shared.xmlView.XMLViewKey;
import com.foc.vaadin.FocCentralPanel;
import com.foc.vaadin.FocWebApplication;
import com.foc.vaadin.gui.layouts.validationLayout.FVValidationLayout;
import com.foc.vaadin.gui.xmlForm.FocXMLLayout;
import com.foc.web.server.xmlViewDictionary.XMLViewDictionary;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class UserCompanyRights_Form extends FocXMLLayout {
	
	@Override
	protected void afterLayoutConstruction() {
		super.afterLayoutConstruction();
	}
	
	private UserCompanyRights getUserCompanyRights(){
		return (UserCompanyRights) getFocObject();
	}
	@Override
	public boolean validationCheckData(FVValidationLayout validationLayout) {
		boolean error = super.validationCheckData(validationLayout);
		if(!error 
				&& getUserCompanyRights() != null 
				&& getUserCompanyRights().getCompany() != null 
				&& getUserCompanyRights().getCompany().getSiteListSize() > 0){
			if(getUserCompanyRights().getCompany().getSiteListSize() == 1){
				if(WFTitleDesc.getInstance().getFocList().size() == 1){
					WFSite site = (WFSite) getUserCompanyRights().getCompany().getAnySite();
					WFTitle title = (WFTitle) WFTitleDesc.getInstance().getFocList().getFocObject(0);
					WFOperator operator = (WFOperator) site.getOperatorList().newEmptyItem();
					operator.setUser(getUserCompanyRights().getUser());
					operator.setTitle(title);
				}else{
					ArrayList<WFSite> selectedSiteList = new ArrayList<WFSite>();
					selectedSiteList.add((WFSite) getUserCompanyRights().getCompany().getAnySite());
					FocList focList = WFTitleDesc.getList(FocList.LOAD_IF_NEEDED);
	        XMLViewKey xmlViewKey = new XMLViewKey(WFTitleDesc.getInstance().getStorageName(), XMLViewKey.TYPE_TABLE,WorkflowWebModule.CTXT_TITLE_SELECTION,XMLViewKey.VIEW_DEFAULT);
	        WFTitle_TitleSelection_Table centralPanel = (WFTitle_TitleSelection_Table) XMLViewDictionary.getInstance().newCentralPanel((FocCentralPanel) getMainWindow(), xmlViewKey, focList);
	        centralPanel.setSelectedSiteList(selectedSiteList);
	        centralPanel.setUser(getUserCompanyRights().getUser());
	        Window titleSelectionWindow = null;
					FocCentralPanel centralWindow = new FocCentralPanel();
					centralWindow.fill();
					centralWindow.changeCentralPanelContent(centralPanel, false);
					titleSelectionWindow = centralWindow.newWrapperWindow();
					titleSelectionWindow.setPositionX(300);
					titleSelectionWindow.setPositionY(100);
					FocWebApplication.getInstanceForThread().addWindow(titleSelectionWindow);
					titleSelectionWindow.setModal(true);
				}
			}else{
				if(WFTitleDesc.getInstance().getFocList().size()>0 && WFTitleDesc.getInstance().getFocList().size() == 1){
					popupWorkfloSiteSelectionTable(false);
				}else{
					popupWorkfloSiteSelectionTable(true);
				}
			}
		}
		return error;
	}
	
	private void popupWorkfloSiteSelectionTable(boolean hasMultipleTitles){
		Company companyToFilterOn = getUserCompanyRights().getCompany();
		if(companyToFilterOn != null){
			//20160107
			FocListWrapper wrapper = companyToFilterOn.newFocListWrapperForCurrentCompany();
			
	    XMLViewKey xmlViewKey = new XMLViewKey(WFSiteDesc.getInstance().getStorageName(), XMLViewKey.TYPE_TABLE,WorkflowWebModule.CTXT_SITE_SELECTION,XMLViewKey.VIEW_DEFAULT);
	    WFSite_SiteSelection_Table centralPanel = (WFSite_SiteSelection_Table) XMLViewDictionary.getInstance().newCentralPanel(getMainWindow(), xmlViewKey, wrapper);
	    centralPanel.setFocDataOwner(true);
	    centralPanel.setHasMultipleTitles(hasMultipleTitles);
	    centralPanel.setUser(getUserCompanyRights().getUser());
	    Window titleSelectionWindow = null;
			FocCentralPanel centralWindow = new FocCentralPanel();
			centralWindow.fill();
			centralWindow.changeCentralPanelContent(centralPanel, false);
			titleSelectionWindow = centralWindow.newWrapperWindow();
			titleSelectionWindow.setPositionX(300);
			titleSelectionWindow.setPositionY(100);
			FocWebApplication.getInstanceForThread().addWindow(titleSelectionWindow);
			titleSelectionWindow.setModal(true);
		}
	}
	
}
