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

import java.util.Locale;

import me.everpro.everprotreegrid.EverproTreeButtonRenderer;
import me.everpro.everprotreegrid.container.EverproTreeGridHierarchicalIndexedContainer;

import com.foc.desc.field.FField;
import com.foc.desc.field.FIntField;
import com.foc.desc.field.FNumField;
import com.foc.vaadin.gui.components.FVTableColumn;
import com.vaadin.data.Container;
import com.vaadin.data.Container.Hierarchical;
import com.vaadin.data.Property;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Grid.CellReference;
import com.vaadin.ui.Grid.CellStyleGenerator;
import com.vaadin.ui.Grid.RowReference;
import com.vaadin.ui.renderers.ClickableRenderer;
import com.vaadin.ui.renderers.ClickableRenderer.RendererClickEvent;
import com.vaadin.ui.renderers.ClickableRenderer.RendererClickListener;

public class EverproTreeGrid_LOCAL extends Grid {

	private Object itemIdProperty  = null;
	private Object displayProperty = null;
	
	public EverproTreeGrid_LOCAL(Object itemIdProperty, Object displayProperty){
		this.displayProperty  = displayProperty;
		this.itemIdProperty = itemIdProperty;
		Column column = addColumn(itemIdProperty);
		setTreeRendererColumn(column);
		column.setEditable(false);
		
		setCellStyleGenerator(new CellStyleGenerator() {
			@Override
			public String getStyle(CellReference cellReference) {
				return getCellStyle(cellReference);
			}
		});
	}
	
	/**
	 * @param cellReference
	 * @return the style name required for this cell. In your css the style name should be prefixed with:
	 * v-grid-cell. example: if you return "gray" your css style should be .v-grid-row.gray
	 *  
	 */
	protected String getCellStyle(CellReference cellReference){
		String style = null;
		if(cellReference.getValue() instanceof Integer || cellReference.getValue() instanceof Double){
			style = "numeric";
		}
		
		return style;
	}
	
	protected Object convertValueToItemId(String value){
		return value;
	}
	
	protected Object getDisplayProperty(){
		return displayProperty;
	}

	protected Object getItemIdProperty(){
		return itemIdProperty;
	}
	
	public Hierarchical getHierarchicalContainerDataSource() {
		EverproTreeGridHierarchicalIndexedContainer indexed = (EverproTreeGridHierarchicalIndexedContainer) getContainerDataSource();
		return indexed != null ? indexed.getHierachical() : null;
	}
	
	public void setContainerDataSource(Hierarchical hierarchicalContainer) {
		EverproTreeGridHierarchicalIndexedContainer indexed = new EverproTreeGridHierarchicalIndexedContainer(hierarchicalContainer);
		indexed.rebuildIndexedArrayList();
		setContainerDataSource(indexed);
		adjustTreeNodeEditableField();
	}

	/**
	 *  
	 * @param itemId
	 * @return the display String to be displayed as node title
	 */
	protected String getNodeTitleForItemId(Object itemId){
		EverproTreeGridHierarchicalIndexedContainer indexed = (EverproTreeGridHierarchicalIndexedContainer) getContainerDataSource();
		Property displayProperty = indexed.getContainerProperty(itemId, getDisplayProperty());
		String displayValue = displayProperty != null ? (String) displayProperty.getValue() : "";
		return displayValue;
	}

	protected void setNodeTitleForItemId(Object itemId, String value){
		Container indexed = (Container) getContainerDataSource();
		Property displayProperty = indexed.getContainerProperty(itemId, getDisplayProperty());
		if(displayProperty != null){
			displayProperty.setValue(value);
		}
	}

	private void setTreeRendererColumn(Column col){
		RendererClickListener listener = new ClickableRenderer.RendererClickListener() {
			public void click(RendererClickEvent event) {
				EverproTreeGridHierarchicalIndexedContainer indexed = (EverproTreeGridHierarchicalIndexedContainer) getContainerDataSource();
				indexed.toggleCollapseState(event.getItemId());
				indexed.rebuildIndexedArrayList();
			}
		};
		
		EverproTreeButtonRenderer buttonRenderer = new EverproTreeButtonRenderer(listener);
		col.setRenderer(buttonRenderer, new Converter<String, String>(){

			public String convertToModel(String value, Class<? extends String> targetType, Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException {
				return "Not Implemented";
			}

			public String convertToPresentation(String value, Class<? extends String> targetType, Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException {
//				Object itemId = Integer.valueOf(value);
				Hierarchical hierarchical = getHierarchicalContainerDataSource();
				
				Object itemId = convertValueToItemId(value);
				
				//Level
				int level = 0; 
				Object parentId = hierarchical.getParent(itemId);
				while(parentId != null){
					level++;
					parentId = hierarchical.getParent(parentId);
				}

				//Display Value
				String displayValue = getNodeTitleForItemId(itemId);

				//Style Name
				String style = "v-foc-gridtree-node-leaf";
				if(hierarchical.hasChildren(itemId)){
					EverproTreeGridHierarchicalIndexedContainer indexed = (EverproTreeGridHierarchicalIndexedContainer) getContainerDataSource();
					if(indexed != null){
						if(indexed.isCollapsedId(itemId)){
							style = "v-foc-gridtree-node-collapsed";
						}else{
							style = "v-foc-gridtree-node-expanded";
						}
					}
				}
				
				return EverproTreeButtonRenderer.encode(displayValue, style, level*20);
			}

			public Class<String> getModelType() {
				return String.class;
			}

			public Class<String> getPresentationType() {
				return String.class;
			}
		});
		
		adjustTreeNodeEditableField();
	}
	
	public void adjustTreeNodeEditableField(){
		Column refColumn = getColumn(itemIdProperty);
		if(refColumn != null){
			NodeTitleTextField texField = new NodeTitleTextField(); 
			texField.setConverter(new Converter<String, String>(){
				
				@Override
				public String convertToModel(String value, Class targetType, Locale locale) throws ConversionException {
					RefValue refValue = decomposeString(value); 
//					Object itemId = convertValueToItemId(this.itemIdValue);
//					setNodeTitleForItemId(itemId, value);
					return refValue.getRef();
				}
	
				@Override
				public String convertToPresentation(String value, Class targetType, Locale locale) throws ConversionException {
					Object itemId = convertValueToItemId(value);
					String displayValue = getNodeTitleForItemId(itemId);
					return composeString(value, displayValue);
				}
	
				@Override
				public Class getModelType() {
					return String.class;
				}
	
				@Override
				public Class getPresentationType() {
					return String.class;
				}
			});
			refColumn.setEditorField(texField);
		}
	}
	
	public class NodeTitleTextField extends TextField {
		
		private String refPart = null;  
		
    @Override
    public void clear() {
    	refPart = null;
      setValue("");
    }

		@Override
		public String getInternalValue() {
			String value = super.getInternalValue();
//			if(refPart != null){
//				value = refPart+"|"+value;
//			}
			return value;
		}

		@Override
		protected void setInternalValue(String newFieldValue){
			RefValue refValue = decomposeString(newFieldValue);
			if(refValue != null && refValue.getRef() != null && !refValue.getRef().isEmpty()){
				refPart = refValue.getRef();
				newFieldValue = refValue.getValue();
			}
			super.setInternalValue(newFieldValue);
		}
		
    @Override
    public void commit() throws com.vaadin.data.Buffered.SourceException, InvalidValueException {
			if(refPart != null && !refPart.isEmpty()){
				Object itemId = convertValueToItemId(refPart);
				if(itemId != null){
					setNodeTitleForItemId(itemId, getValue());
				}
			}
    }
		
		public String getRefPart(){
			return refPart;
		}
	}
	
	public static class RefValue {
		private String ref = null;
		private String value = null;
		
		public RefValue(String ref, String value){
			this.ref = ref;
		  this.value = value;
		}

		public String getRef() {
			return ref;
		}

		public String getValue() {
			return value;
		}
	}

	public static String composeString(String ref, String value){
		String composed ; 
		if(ref != null){
			composed = ref+"|"+value;
		}else{
			composed = value;
		}
		return composed;
	}
	
	public static RefValue decomposeString(String composite){
		String refPart = null;
		int pipeIndex = composite.indexOf('|');
		if(pipeIndex > 0){
			refPart = composite.substring(0, pipeIndex);
			composite = composite.substring(pipeIndex+1);
		}
		
		RefValue refValue = new RefValue(refPart, composite);
		return refValue;
	}
}
