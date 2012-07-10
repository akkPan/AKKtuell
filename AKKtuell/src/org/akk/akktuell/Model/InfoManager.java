package org.akk.akktuell.Model;

import java.text.DateFormatSymbols;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.akk.akktuell.Model.downloader.AkkHomepageEventParser;
import org.akk.akktuell.Model.downloader.EventDownloadListener;
import org.akk.akktuell.Model.downloader.EventDownloader;
import org.akk.akktuell.database.*;


public class InfoManager implements Runnable, EventDownloadListener {

	private CalendarBridge calendar;
	
	private ConnectivityManager conMgr;
	
	private Thread databaseManager;
	
	private EventDownloader parser;
	
	private Database database;
	
	private LinkedList<AkkEvent> eventsSortedByDate;
	
	private int currentMonth = new GregorianCalendar().get(GregorianCalendar.MONTH);
	
	public InfoManager(Context context) {
		eventsSortedByDate = new LinkedList<AkkEvent>();
		database = Database.getInstance(context);
		try {
			database.open();
		} catch (DBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		calendar = new CalendarBridge();
		databaseManager = new Thread(this);
		databaseManager.start();
		
		//check online state
		conMgr =  (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = conMgr.getAllNetworkInfo();
		for (NetworkInfo netInf: netInfo) {
			if (netInf.isConnected()) {
				break;
			}
		}
		//finished checking
		
		parser = new AkkHomepageEventParser(context);
		parser.addEventDownloadListener(this);
		parser.updateEvents();
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
	
	public void addMonth() {
		currentMonth = (currentMonth + 1) % 12;
	}
	
	public void decMonth() {
		currentMonth = (currentMonth + 11) % 12;
	}
	
	public String getCurrentMonthName() {
		return new DateFormatSymbols().getMonths()[currentMonth];
	}

	@Override
	public void downloadStarted() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void downloadFinished(AkkEvent[] events) {
		synchronized (this) {
			for (AkkEvent e : events) {
				if (!eventsSortedByDate.contains(e)) {
					eventsSortedByDate.addLast(e);
					synchronized (databaseManager) {
						databaseManager.notify();
					}
				}
				
			}
		}
		
	}

	@Override
	public void run() {
		AkkEvent[] events = database.getAllEvents(DBFields.EVENT_DATE, DBInterface.DESCENDING);
		downloadFinished(events);
		
		while (true)
		{
			synchronized (this) {
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			try {
				database.insertAkkEvent(eventsSortedByDate.getLast());
			} catch (DBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	public void finish() {
		database.close();
	}
}
