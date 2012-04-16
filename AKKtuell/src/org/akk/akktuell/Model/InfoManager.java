package org.akk.akktuell.Model;

import java.util.GregorianCalendar;
import java.util.LinkedList;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.akk.akktuell.database.*;


public class InfoManager {
	
	private boolean isOnline;

	private CalendarBridge calendar;
	
	private ConnectivityManager conMgr;
	
	private Context applicationContext;
	
	private Updater updater;
	
	private Thread t;
	
	private AkkHomepageEventParser parser;
	
	private Database database;
	
	private LinkedList<AkkEvent> eventsSortedByDate;
	
	private int currentMonth = new GregorianCalendar().get(GregorianCalendar.MONTH);
	
	public InfoManager(Context context) {
		applicationContext = context;
		database = Database.getInstance(context);
		eventsSortedByDate = new LinkedList<AkkEvent>();
		//eventsSortedByDate = database.getAllEvents(orderBy, direction);
		calendar = new CalendarBridge();
		updater = new Updater(null, null);
		t = null;
		
		//check online state
		this.isOnline = false;
		conMgr =  (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = conMgr.getAllNetworkInfo();
		for (NetworkInfo netInf: netInfo) {
			if (netInf.isConnected()) {
				this.isOnline = true;
				break;
			}
		}
		//finished checking
		
		parser = new AkkHomepageEventParser(context, this);
		parser.updateEvents();
	}
	
	private void updateEvents() {
		if (this.isOnline) {
			if (updater.updateNeeded()) {
				t = new Thread(updater);
				t.run();
			} else {
				Log.d("InfoManager", "No updated required");
			}
		} else {
			Log.d("Updater", "Unable to update: no internet connection");
		}
	}


	public boolean readyToDisplayData() {
		return !this.eventsSortedByDate.isEmpty();
	}
	
	public boolean isInCalendar(AkkEvent event) {
		return true;//NICHT;)
	}
	
	public void addToCalendar(AkkEvent event) {
		calendar.addEvent(event);
	}

	public AkkEvent[] getEvents() {
		LinkedList<AkkEvent> resultList = new LinkedList<AkkEvent>();
		for (AkkEvent event : eventsSortedByDate) {
			if (event.getEventBeginTime().get(GregorianCalendar.MONTH) == currentMonth) {
				resultList.add(event);
			}
		}
		AkkEvent result[] = new AkkEvent[resultList.size()];
		for (int i = 0; i < resultList.size(); i++) {
			result[i] = resultList.get(i);
		}
		return result;
	}
	
	public void addEventToList(AkkEvent event){
		eventsSortedByDate.addLast(event);
	}
	
	public void setCurrentMonth(int month) {
		if (month >= 0 && month < 12) {
			currentMonth = month;
		}
	}
}
