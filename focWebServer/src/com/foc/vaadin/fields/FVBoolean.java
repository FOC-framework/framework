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
package com.foc.vaadin.fields;

import org.xml.sax.Attributes;

import com.foc.property.FProperty;
import com.foc.shared.dataStore.IFocData;
import com.foc.vaadin.gui.FocXMLGuiComponent;
import com.foc.vaadin.gui.FocXMLGuiComponentStatic;
import com.foc.vaadin.gui.components.FVCheckBox;
import com.foc.vaadin.gui.components.multipleChoiceOptionGroupHorizontal.FVBooleanOptionGroupHorizontal;
import com.foc.vaadin.gui.layouts.FVWrapperLayout;
import com.foc.vaadin.gui.xmlForm.FXML;
import com.foc.vaadin.gui.xmlForm.FocXMLLayout;

public class FVBoolean implements FocXMLGuiComponentCreator {
  
  @Override
  public FocXMLGuiComponent newGuiComponent(FocXMLLayout xmlLayout, IFocData focData, Attributes attributes, IFocData rootFocData, String dataPathFromRootFocData) {
  	FocXMLGuiComponent component = null; 
  	FProperty property = (FProperty) focData;
  	
  	boolean optionGroup = false;
		String optGroup = attributes != null ? attributes.getValue(FXML.ATT_OPTION_GROUP) : null;
		if(optGroup != null && optGroup.toLowerCase().equals("true")){
			optionGroup = true;
		}
  	
  	if (property != null && optionGroup) {
  		component = new FVBooleanOptionGroupHorizontal(property, attributes);  
  	} else {
	    FVCheckBox checkBox = new FVCheckBox(property, attributes);
	    component = checkBox;
  	}
  	FocXMLGuiComponentStatic.setRootFocDataWithDataPath(component, rootFocData, dataPathFromRootFocData);
    return FVWrapperLayout.wrapIfNecessary(component);
  }

}
