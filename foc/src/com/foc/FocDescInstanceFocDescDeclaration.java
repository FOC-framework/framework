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
package com.foc;

import com.foc.desc.FocDesc;
import com.foc.desc.FocModule;

public class FocDescInstanceFocDescDeclaration implements IFocDescDeclaration{
	
	private FocDesc   focDescription = null;
	private FocModule module         = null; 
	
	public FocDescInstanceFocDescDeclaration(FocDesc focDescription){
		this.focDescription = focDescription ;
	}
	
	public FocDesc getFocDescription() {
		return focDescription;
	}

	public int getPriority() {
		return 0;
	}

	public void setFocModule(FocModule module) {
		this.module = module;
	}
	
	@Override
	public FocModule getFocModule() {
		return module;
	}
	
	@Override
	public String getName() {
		return (getFocDescription() != null) ? getFocDescription().getStorageName() : null;
	}
}
