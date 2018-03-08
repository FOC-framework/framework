package com.foc.business.notifier;

import java.sql.Date;
import java.sql.Time;
import java.util.Calendar;

import com.foc.Globals;
import com.foc.annotations.model.FocChoice;
import com.foc.annotations.model.FocEntity;
import com.foc.annotations.model.fields.FocDate;
import com.foc.annotations.model.fields.FocForeignEntity;
import com.foc.annotations.model.fields.FocMultipleChoice;
import com.foc.annotations.model.fields.FocMultipleChoiceString;
import com.foc.annotations.model.fields.FocTableName;
import com.foc.annotations.model.fields.FocTime;
import com.foc.business.calendar.FCalendar;
import com.foc.business.notifier.actions.FocNotifActionFactory;
import com.foc.business.notifier.actions.IFocNotifAction;
import com.foc.business.notifier.manipulators.IFocNotificationEventManipulator;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocDesc;
import com.foc.desc.parsers.ParsedFocDesc;
import com.foc.desc.parsers.pojo.PojoFocObject;

@FocEntity
@SuppressWarnings("serial")
public class FNotifTrigger extends PojoFocObject implements FocNotificationConst {
  
  private IFocNotificationEventManipulator localEventManipulator = null;//For standard non internal event this is usually null
  private IFocNotifAction action = null;
  
  public static final String DBNAME = "FNotifTrigger";

  public static final int FREQUENCY_ONE_TIME = 0;
  public static final int FREQUENCY_DAILY    = 1;
  public static final int FREQUENCY_WEEKLY   = 7;
  public static final int FREQUENCY_MONTHLY  = 30;

  public static final int ACTION_NONE           = 0;
  public static final int ACTION_SEND_EMAIL     = 1;
  public static final int ACTION_EXECUTE_REPORT = 2;
  
	@FocMultipleChoiceString()
	public static final String FIELD_Transaction = "Transaction";

	@FocMultipleChoice(size = 5, choices = {
			@FocChoice(id=EVT_CREATE_USER_FROM_CONTACT, title="User Creation From Contact"),
			@FocChoice(id=EVT_TABLE_ADD, title="Table Insert"),
			@FocChoice(id=EVT_TABLE_DELETE, title="Table Delete"),
			@FocChoice(id=EVT_TABLE_UPDATE, title="Table Update"),
			@FocChoice(id=EVT_TRANSACTION_APPROVE, title="Transaction Approve"),
			@FocChoice(id=EVT_TRANSACTION_CLOSE, title="Transaction Close"),
			@FocChoice(id=EVT_TRANSACTION_CANCEL, title="Traansaction Cancel"),
			@FocChoice(id=EVT_SCHEDULED, title="Scheduled")
	})
	public static final String FIELD_Event = "Event";

	@FocForeignEntity(table = "NOTIF_EMAIL_TEMPLATE")
	public static final String FIELD_NOTIF_EMAIL_TEMPLATE = "NOTIF_EMAIL_TEMPLATE";

	@FocTableName()
	public static final String FIELD_TABLE_NAME = "TABLE_NAME";

	@FocMultipleChoice(size = 5, choices = {
			@FocChoice(id=FREQUENCY_ONE_TIME, title="One time"),
			@FocChoice(id=FREQUENCY_DAILY, title="Daily")
//			@FocChoice(id=FREQUENCY_WEEKLY, title="Weekly"),
//			@FocChoice(id=FREQUENCY_MONTHLY, title="Monthly")
	})
	public static final String FIELD_Frequency = "Frequency";

	@FocDate()
	public static final String FIELD_NextDate = "NextDate";

	@FocTime()
	public static final String FIELD_NextTime = "NextTime";

	@FocMultipleChoice(size = 5, choices = {
			@FocChoice(id=ACTION_NONE, title="-none-"),
			@FocChoice(id=ACTION_SEND_EMAIL, title="Send eMail"),
			@FocChoice(id=ACTION_EXECUTE_REPORT, title="Execute report")
	})
	public static final String FIELD_Action = "Action";
	
  public FNotifTrigger(FocConstructor constr){
    super(constr);
  }
  
	public static ParsedFocDesc getFocDesc() {
		return ParsedFocDesc.getInstance(DBNAME);
	}
	
  public IFocNotificationEventManipulator getLocalEventManipulator() {
    return localEventManipulator;
  }

  public void setLocalEventManipulator(IFocNotificationEventManipulator localEventManipulator) {
    this.localEventManipulator = localEventManipulator;
  }

	public String getTransaction() {
		return getPropertyString(FIELD_Transaction);
	}

	public void setTransaction(String value) {
		setPropertyString(FIELD_Transaction, value);
	}

	public int getEvent() {
		return getPropertyInteger(FIELD_Event);
	}

	public void setEvent(int value) {
		setPropertyInteger(FIELD_Event, value);
	}

	public FocNotificationEmailTemplate getTemplate() {
		return (FocNotificationEmailTemplate) getPropertyObject(FIELD_NOTIF_EMAIL_TEMPLATE);
	}

	public void setTemplate(FocNotificationEmailTemplate value) {
		setPropertyObject(FIELD_NOTIF_EMAIL_TEMPLATE, value);
	}

	public String getTABLE_NAME() {
		return getPropertyString(FIELD_TABLE_NAME);
	}

	public void setTABLE_NAME(String value) {
		setPropertyString(FIELD_TABLE_NAME, value);
	}

  public FocDesc getTableDesc(){
    return getPropertyDesc(FIELD_TABLE_NAME);
  }
  
  public void setTableDesc(FocDesc focDesc){
    setPropertyDesc(FIELD_TABLE_NAME, focDesc);
  }

	public int getFrequency() {
		return getPropertyInteger(FIELD_Frequency);
	}

	public void setFrequency(int value) {
		setPropertyInteger(FIELD_Frequency, value);
	}

	public Date getNextDate() {
		return getPropertyDate(FIELD_NextDate);
	}

	public void setNextDate(Date value) {
		setPropertyDate(FIELD_NextDate, value);
	}

	public Time getNextTime() {
		return getPropertyTime(FIELD_NextTime);
	}

	public void setNextTime(Time value) {
		setPropertyTime(FIELD_NextTime, value);
	}

	public int getAction() {
		return getPropertyInteger(FIELD_Action);
	}

	public void setAction(int value) {
		setPropertyInteger(FIELD_Action, value);
	}

	public IFocNotifAction getActionObject() {
		return this.action;
	}
	
	public void setActionObject(IFocNotifAction action) {
		this.action = action;
	}

  private boolean isSameDBTableAsFocObject(FocNotificationEvent event){
    boolean sameTable = false;
//    FocNotificationEmailTemplate template = (FocNotificationEmailTemplate) notifier.getTemplate();
  
    if (event != null && event.getEventFocData() != null && event.getEventFocData().iFocData_getDataByPath("TABLE_NAME") != null && event.getEventFocData().iFocData_getDataByPath("TABLE_NAME").iFocData_getValue() != null  /*&& template != null*/) {
      String eventTableName = (String) event.getEventFocData().iFocData_getDataByPath("TABLE_NAME").iFocData_getValue();
  
      FocDesc existingTableDesc = getTableDesc();
      if (existingTableDesc != null) {
        String notifierTableName = existingTableDesc.getStorageName();
        if (notifierTableName != null && !notifierTableName.isEmpty()) {
  
          if (eventTableName.equals(notifierTableName)) {
            sameTable = true;
          }
        }
      }
    }
    return sameTable;
  }
  
	public boolean isEventMatch(FocNotificationEvent eventFired) {
		boolean match = false;
		if(eventFired != null) {
			if(eventFired.getEventKey() == getEvent()) {
				switch(getEvent()) {
					case EVT_TABLE_ADD:
					case EVT_TABLE_DELETE:
					case EVT_TABLE_UPDATE:
						match = isSameDBTableAsFocObject(eventFired);
						break;
					case EVT_SCHEDULED:
				  	if(FCalendar.compareDatesRegardlessOfTime(getNextDate(), Globals.getApp().getSystemDate()) <= 0) {
				  		if(FCalendar.compareTimesRegardlessOfDates(getNextTime(), Globals.getApp().getSystemDate()) <= 0) {
				  			match = true;
				  			reschedule();
				  		}
				  	}
						break;
					default:
						match = true;
						break;
				}
			}
		}
		return match;
	}
	
	public void execute(FocNotificationEvent eventFired) {
		IFocNotifAction action = getActionObject();
		if(action == null) {
			action = FocNotifActionFactory.getInstance().get(getAction());
		}
		if(action != null) {
			action.execute(this, eventFired);
		}
	}
	
	public void executeIfSameEvent(FocNotificationEvent eventFired) {
		if(isEventMatch(eventFired)) {
			execute(eventFired);
		}
	}
	
	public IFocNotificationEventManipulator getEventExecutor() {
	  IFocNotificationEventManipulator eventManipulator = getLocalEventManipulator();
	  if(eventManipulator == null){
//	    eventManipulator = FocNotificationEventFactory.getInstance().get(get());
	  }
	  return eventManipulator; 
	}
	
	public void reschedule() {
		Date startingDate = getNextDate();
		Time startingTime = getNextTime();
		
		if(FCalendar.isDateZero(startingDate)) {
			startingDate = Globals.getApp().getSystemDate();
		}
//		if(startingTime == null || startingTime.getTime() == 0) {
//			startingTime = new Time();
//		}
		
		int frequency = getFrequency();
		if(frequency == FREQUENCY_DAILY) {
			Calendar cal = FCalendar.getInstanceOfJavaUtilCalandar();
			cal.setTime(Globals.getApp().getSystemDate());
			FCalendar.rollTheCalendar_Day(cal);
			setNextDate(new Date(cal.getTime().getTime()));
		}
	}
}
