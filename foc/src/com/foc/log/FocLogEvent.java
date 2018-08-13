package com.foc.log;

import java.util.Date;

public interface FocLogEvent {
	public static final int EVENT_NONE            = 0;
	public static final int EVENT_SIGNATURE       = 1;
	public static final int EVENT_CREATION        = 2;
	public static final int EVENT_CANCELLATION    = 3;
	public static final int EVENT_MODIFICATION    = 4;
	public static final int EVENT_APPROVED        = 5;
	public static final int EVENT_CLOSED          = 6;
	public static final int EVENT_UNDO_SIGNATURE  = 7;
	public static final int EVENT_CUSTOM          = 8;
	public static final int EVENT_COMMENT         = 9;
	public static final int EVENT_REJECT          = 10;
	public static final int EVENT_OPENED          = 11;

	public static final int STATUS_EXCLUDED       =  0;
	public static final int STATUS_INCLUDED       =  1;
	public static final int STATUS_POSTED         =  2;
	public static final int STATUS_COMMITTED      =  3;
	public static final int STATUS_ERROR          =  4;
	
	//This indicates the Log Event primary key itself. It is not unique. 
	//If becomes unique when combined with the [EntityName]
	//Should be used to tell FOC if the Log line has been successfully committed or not
	public long   logEvent_GetLogEventReference();
	
	//Entity identification
	public String logEvent_GetEntityName();
	public long   logEvent_GetEntityReference();
	
	//Transaction identification (applicable when the Entity is a transaction) 
	public String logEvent_GetEntityCompanyName();
	public String logEvent_GetEntitySiteName();
	public String logEvent_GetEntityCode();
	public Date   logEvent_GetEntityDate();
	
	//Event Info
	public int    logEvent_GetEventType();
	public String logEvent_GetUsername();
	public Date   logEvent_GetDateTime();
	public String logEvent_GetSQLRequests();
	public int    logEvent_GetStatus();
}