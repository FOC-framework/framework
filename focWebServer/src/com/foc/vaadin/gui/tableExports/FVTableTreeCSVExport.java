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
package com.foc.vaadin.gui.tableExports;

import java.io.FileWriter;
import java.util.ArrayList;

import com.foc.Globals;
import com.foc.desc.FocObject;
import com.foc.list.FocList;
import com.foc.property.FBoolean;
import com.foc.property.FProperty;
import com.foc.vaadin.gui.FocXMLGuiComponent;
import com.foc.vaadin.gui.components.FVMultipleChoiceOptionGroupPopupView;
import com.foc.vaadin.gui.components.FVTableColumn;
import com.foc.vaadin.gui.components.FVTreeTable;
import com.foc.vaadin.gui.components.ITableTree;
import com.foc.vaadin.gui.components.TableTreeDelegate;
import com.foc.web.dataModel.FocTreeWrapper;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Table;

public abstract class FVTableTreeCSVExport extends CSVExport {
	private ArrayList<FVTableColumn> columnsArrayList = null;

	private TableTreeDelegate tableTreeDelegate = null;

	public abstract void scan();

	public FVTableTreeCSVExport(TableTreeDelegate paramTableTreeDelegate) {
		this.tableTreeDelegate = paramTableTreeDelegate;
		init();
	}

	public void dispose() {
		if(this.tableTreeDelegate != null){
			this.tableTreeDelegate = null;
		}
		this.columnsArrayList = null;
		super.dispose();
	}

	protected String getFileName() {
		return getTableName();
	}

	protected void fillFile() {
		setTableTreeColumnsTitle();
		scan();
	}

	protected void setTableTreeColumnsTitle() {
		try{
			ArrayList localArrayList = getVisibleColumns();
			FileWriter localFileWriter = getFileWriter();
			if((localFileWriter != null) && (localArrayList != null)){
				for(int i = 0; i < localArrayList.size(); i++){
					FVTableColumn localFVTableColumn = (FVTableColumn) localArrayList.get(i);
					if(localFVTableColumn != null){
						String str = localFVTableColumn.getCaption();
						localFileWriter.append(str + ",");
					}
				}
				localFileWriter.append("\n");
			}
		}catch (Exception localException){
			localException.printStackTrace();
		}
	}

	public String getTableName() {
		String str = null;
		TableTreeDelegate localTableTreeDelegate = getTableTreeDelegate();
		if(localTableTreeDelegate != null){
			str = localTableTreeDelegate.getTableName();
			if((str == null) || (str.isEmpty())){
				str = "table_to_csv_export";
			}
		}
		return str;
	}

	public TableTreeDelegate getTableTreeDelegate() {
		return this.tableTreeDelegate;
	}

	public ArrayList<FVTableColumn> getVisibleColumns() {
		if(this.columnsArrayList == null){
			TableTreeDelegate localTableTreeDelegate = getTableTreeDelegate();
			if(localTableTreeDelegate != null){
				this.columnsArrayList = localTableTreeDelegate.getVisiblePropertiesArrayList();
			}
		}
		return this.columnsArrayList;
	}

	public FocList getFocList() {
		FocList localFocList = null;
		TableTreeDelegate localTableTreeDelegate = getTableTreeDelegate();
		if(localTableTreeDelegate != null){
			localFocList = localTableTreeDelegate.getTreeOrTable().getFocList();
		}
		return localFocList;
	}

	public ITableTree getTableOrTree() {
		ITableTree localITableTree = null;
		TableTreeDelegate localTableTreeDelegate = getTableTreeDelegate();
		if(localTableTreeDelegate != null){
			localITableTree = localTableTreeDelegate.getTreeOrTable();
		}
		return localITableTree;
	}

	public FVTreeTable getFVTreeTable() {
		FVTreeTable localFVTreeTable = null;
		ITableTree localITableTree = getTableOrTree();
		if(localITableTree != null){
			localFVTreeTable = (FVTreeTable) localITableTree;
		}
		return localFVTreeTable;
	}

	public FocTreeWrapper getFocTreeWrapper() {
		FocTreeWrapper localFocTreeWrapper = null;
		FVTreeTable localFVTreeTable = getFVTreeTable();
		if(localFVTreeTable != null){
			localFocTreeWrapper = localFVTreeTable.getFocTreeWrapper();
		}
		return localFocTreeWrapper;
	}

	public String getPropertyStringValue(FocObject paramFocObject, FVTableColumn paramFVTableColumn) {
		String str = "";
		if((paramFVTableColumn != null) && (paramFocObject != null)){
			Object localObject1;
			if(paramFVTableColumn.isColumnFormula()){
				localObject1 = paramFVTableColumn.computeFormula_ForFocObject(paramFocObject);
				if(localObject1 != null){
					str = String.valueOf(localObject1);
				}
			}else{
				Object localObject2;
				if(paramFVTableColumn.getDataPath().equals("_COUNT")){
					localObject1 = (FVTreeTable) getTableOrTree();
					if((localObject1 != null) && (paramFocObject.getReference() != null)){
						int i = paramFocObject.getReference().getInteger();
						if(((FVTreeTable) localObject1).hasChildren(Integer.valueOf(i))){
							localObject2 = Integer.valueOf(((FVTreeTable) localObject1).getChildren(Integer.valueOf(i)).size());
							str = String.valueOf(localObject2);
						}
					}
				}else{
					localObject1 = (getTableTreeDelegate() != null) && (getTableTreeDelegate().getTable() != null) ? getTableTreeDelegate().getTable().getColumnGenerator(paramFVTableColumn.getName()) : null;
					if((localObject1 instanceof Table.ColumnGenerator)){
						Table.ColumnGenerator localColumnGenerator = (Table.ColumnGenerator) localObject1;
						if(localColumnGenerator != null){
							Long itemID = Long.valueOf(paramFocObject.getReference().getLong());
							localObject2 = localColumnGenerator.generateCell(getTableTreeDelegate().getTable(), itemID, paramFVTableColumn.getName());
							if(localObject2 != null){
								if((localObject2 instanceof FVMultipleChoiceOptionGroupPopupView)){
									str = ((FocXMLGuiComponent) localObject2).getValueString();
								}else if((localObject2 instanceof FocXMLGuiComponent)){
									str = ((FocXMLGuiComponent) localObject2).getValueString();
								}else if(localObject2 instanceof Embedded){
									ITableTree tableTree = getTableOrTree();
									FocList list = tableTree != null ? tableTree.getFocList() : null;
									FocObject focObject = list != null ? list.searchByReference(itemID) : null;
									FProperty property = focObject != null ? focObject.getFocPropertyForPath(paramFVTableColumn.getDataPath()) : null;
									if(property instanceof FBoolean){
										str = property.getString();
									}
								}else{
									try{
										str = String.valueOf(localObject2);
									}catch (Exception localException){
										Globals.logExceptionWithoutPopup(localException);
									}
								}
							}
						}
					}
				}
			}
		}
		if((str != null) && (!str.isEmpty()) && (str.contains("\""))){
			str = str.replaceAll("\"", "''");
		}
		return str;
	}
}
