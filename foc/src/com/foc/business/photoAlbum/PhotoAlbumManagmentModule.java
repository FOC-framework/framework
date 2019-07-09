package com.foc.business.photoAlbum;

import com.foc.Globals;
import com.foc.admin.FocVersion;
import com.foc.db.DBManager;
import com.foc.desc.FocModule;

public class PhotoAlbumManagmentModule extends FocModule{

	public static final int VERSION_ID = 1001;
	
	public static final String MODULE_NAME = "DocumentManagement";

  private boolean alter_OBJECT_REF_Size = false;
  
  public PhotoAlbumManagmentModule(){
  	super();
  }
  
  @Override
  public void declare() {
    FocVersion.addVersion(MODULE_NAME, "Document management 1.0", VERSION_ID);
  	super.declare();
  }
  
  @Override
  public void declareFocObjectsOnce() {
  	declareFocDescClass(PhotoAlbumConfigDesc.class);
  	declareFocDescClass(PhotoAlbumAppGroup.class);
    declareFocDescClass(PhotoAlbumDesc.class);
    declareFocDescClass(PhotoAlbumAccessDesc.class);
    declareFocDescClass(DocumentTypeDesc.class);
    declareFocDescClass(DocTypeAccessDesc.class);
  }
  
  @Override
  public void beforeAdaptDataModel() {
  	super.beforeAdaptDataModel();
		FocVersion dbVersion = FocVersion.getDBVersionForModule(MODULE_NAME);
		if(			dbVersion == null 				
				&&  Globals.getApp() != null 
				&&  Globals.getApp().getDBManager() != null 
				&&  Globals.getApp().getDBManager().getProvider() == DBManager.PROVIDER_ORACLE) {
			alter_OBJECT_REF_Size = true;
		}
  }
  
  @Override
  public void afterAdaptDataModel() {
  	super.afterAdaptDataModel();
  	if(alter_OBJECT_REF_Size) {
  		StringBuffer buffer = new StringBuffer();
  		buffer.append("ALTER TABLE "+PhotoAlbumDesc.DB_TABLE_NAME+" MODIFY "+PhotoAlbumDesc.FNAME_OBJECT_REF+" INT");
  		Globals.logString("PhotoAlbumModule: "+buffer.toString());
  		Globals.getApp().getDataSource().command_ExecuteRequest(buffer);
  	}
  }
  
  private static PhotoAlbumManagmentModule module = null;
  public static PhotoAlbumManagmentModule getInstance(){
    if(module == null){
      module = new PhotoAlbumManagmentModule();
    }
    return module;
  }
}
