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
package com.foc.web.modules.downloadableContent;

import com.foc.business.downloadableContent.DownloadableContent;
import com.foc.business.downloadableContent.DownloadableContentDesc;
import com.foc.property.FCloudStorageProperty;
import com.foc.shared.dataStore.IFocData;
import com.foc.vaadin.gui.components.BlobResource;
import com.foc.vaadin.gui.components.FVButton;
import com.foc.vaadin.gui.xmlForm.FocXMLLayout;
import com.foc.web.gui.INavigationWindow;
import com.foc.web.server.xmlViewDictionary.XMLView;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.Reindeer;

@SuppressWarnings("serial")
public class DownloadableContent_Form extends FocXMLLayout {

	private DownloadableContent downloadableContent = null; 
	
	@Override
	protected void afterLayoutConstruction() {
		super.afterLayoutConstruction();
		FVButton button = (FVButton) getComponentByName("DOCUMENT_NAME_BUTTON");
		button.setStyleName(Reindeer.BUTTON_LINK);
		button.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if(downloadableContent == null) downloadableContent = (DownloadableContent) getFocObject();
				if(downloadableContent != null){
					FCloudStorageProperty cloudStorageProperty = (FCloudStorageProperty) downloadableContent.getFocProperty(DownloadableContentDesc.FLD_DOCUMENT);
					BlobResource blobResource = BlobResource.newBlobResource(cloudStorageProperty);
					blobResource.openDownloadWindow();
				}
			}
		});
	}

	@Override
	public void init(INavigationWindow window, XMLView xmlView, IFocData focData) {
		super.init(window, xmlView, focData);
		if(focData instanceof DownloadableContent){
			downloadableContent = (DownloadableContent) focData;
		}
	}
	
	public void dispose(){
		super.dispose();
		downloadableContent = null;
	}
}
