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
package com.foc.web.modules.business;

import java.util.ArrayList;

import org.jfree.chart.JFreeChart;

import com.foc.vaadin.gui.components.chart.JFreeChartWrapper;
import com.foc.vaadin.gui.components.chart.JFreeChartWrapper.RenderingMode;
import com.foc.vaadin.gui.xmlForm.FocXMLLayout;

@SuppressWarnings("serial")
public class Chart_Form extends FocXMLLayout {
	
	private ArrayList<JFreeChartWrapper> array = null;
	
	@Override
	public void dispose(){
		if(array != null){
			for(int i=0; i<array.size(); i++){
				if(array.get(i) != null) array.get(i).detach();
			}
		}
		array.clear();
		super.dispose();
	}

	public void addChart(JFreeChart chartToBeWrapped){
		JFreeChartWrapper chartWrapper = new JFreeChartWrapper(chartToBeWrapped, RenderingMode.PNG, this);
		chartWrapper.attach();
		
		if(array == null) array = new ArrayList<JFreeChartWrapper>();
	}
	
}
