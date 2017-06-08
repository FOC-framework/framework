package com.foc.access;

import com.foc.Globals;

public class FocLogger {

  private FocLogLineList logLineList = null;
  private FocLogLineTree logLineTree = null;
  private FocLogLine     currentNode = null;
  private boolean        hasFailure  = false;
	
  public FocLogger(){
    logLineList = new FocLogLineList();
  }
  
  public void dispose(){
    currentNode = null;
    if(logLineList != null){
      logLineList.dispose();
      logLineList = null;
    }
    if(logLineTree != null){
      logLineTree.dispose();
      logLineTree = null;
    }
  }
  
  public FocLogLineTree getTree(){
    if(logLineTree == null){
      logLineTree = new FocLogLineTree();
      logLineTree.growTreeFromFocList(getLogLineList());      
    }
    return logLineTree;
  }
  
  public FocLogLineList getLogLineList(){
    if(logLineList == null){
      logLineList = new FocLogLineList();
    }
    return logLineList;
  }
  
  public void openNode(String message){
	  //
	  //use the current node the current FocLogLine 
	  //addLogLine();
	  FocLogLine line = addInfo(message);
	  line.setFatherObject(currentNode);
	  currentNode = line;
  }
  
  public void closeNode(){
  	closeNode(null);
  }
  
  public void closeNode(String message){
    if(message != null && !message.isEmpty()){
      addInfo(message);
    }
    if(currentNode != null){
    	//currentNode.setClosed(true);
    }
    currentNode = (FocLogLine) (currentNode != null ? currentNode.getFatherObject() : null);
  }
  
  public FocLogLine addInfo(String message) {
    FocLogLine line = addLogLine(FocLogLine.TYPE_INFO, message);
    line.setSuccessful(true);
    return line;
  }

  public FocLogLine addError(String message) {
    FocLogLine line = addLogLine(FocLogLine.TYPE_ERROR, message);
    line.setSuccessful(false);
    setHasFailure(true);
    return line;
  }
  
  public FocLogLine addFailure(String message) {
    FocLogLine line = addLogLine(FocLogLine.TYPE_FAILURE, message);
    line.setSuccessful(false);
    setHasFailure(true);
    return line;
  }

  public FocLogLine addWarning(String message) {
    FocLogLine line = addLogLine(FocLogLine.TYPE_WARNING, message);
    line.setSuccessful(true);
    return line;
  }
    
  private FocLogLine addLogLine(int type, String message) {
    FocLogLine line = (FocLogLine) getLogLineList().newEmptyItem();
    line.setDateTime(new java.sql.Date(System.currentTimeMillis()));
    line.setType(type);
    line.setMessage(message);
    line.setFatherObject(currentNode);
    boolean backup = getLogLineList().isDisableReSortAfterAdd();
    getLogLineList().setDisableReSortAfterAdd(true);
    getLogLineList().add(line);
    getLogLineList().setDisableReSortAfterAdd(backup);
    return line;
  }
  
  public void displayLogConsole() {
    for(int i=0; i<getLogLineList().size(); i++){
      FocLogLine logLine = (FocLogLine) getLogLineList().getFocObject(i);
      Globals.logString(logLine.getLogLineString());
      }
    }
  
  public static FocLogger getInstance(boolean createIfNeeded) {
    FocLogger logger = null;
    if (Globals.getApp() != null) {
      logger = Globals.getApp().getFocLogger(createIfNeeded);
    }
    return logger;
  }
  
  public static FocLogger getInstance() {
    return getInstance(true);
  }

	public boolean isHasFailure() {
		return hasFailure;
	}

	public void setHasFailure(boolean hasFailure) {
		this.hasFailure = hasFailure;
	}
}
  
