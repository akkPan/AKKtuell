package org.akk.akktuell.Activity;

import org.akk.akktuell.R;
import org.akk.akktuell.Model.AkkEvent;
import org.akk.akktuell.Model.AkkEvent.AkkEventType;
import org.akk.akktuell.Model.InfoManager;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class AKKtuellMainActivity extends Activity  {
	
	private InfoManager infoManager;
	private ListView elementListView;
	private GestureDetector gestureScanner;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        infoManager = new InfoManager(getApplicationContext());
        
        gestureScanner = new GestureDetector(new GestureScanner(this, infoManager));
        
        setContentView(R.layout.main);
        elementListView = (ListView) findViewById(R.id.main_element_listview);
        
        elementListView.setOnItemClickListener(new OnItemClickListener() {  
        	@Override
        	public void onItemClick(AdapterView<?> parent, View view,
        			int position, long id) {
        			AkkEvent clickedEvent = (AkkEvent) AKKtuellMainActivity.this.elementListView.getAdapter().getItem(position);
        			
        			if (clickedEvent == null) {
        				return;
        			}
        			
        			if (clickedEvent.getEventType() == AkkEventType.Veranstaltungshinweis) {
        				return;
        			}
        			
    				Intent intent = new Intent(AKKtuellMainActivity.this,AKKtuellEventView.class);
    				intent.putExtra("EVENT_NAME", clickedEvent.getEventName());
    				intent.putExtra("EVENT_DATE", "test");
    				intent.putExtra("EVENT_DESCRIPTION", clickedEvent.getEventDescription());
    				startActivity(intent);
        		}
        });
        displayData();
        
    }
    
    protected void displayData() {
    	while (!infoManager.readyToDisplayData()) {
    		//wait for data update
    	}
		View mainView = findViewById(R.id.main_activity_layout);
		TextView listHeaderMonthName = (TextView) mainView.findViewById(R.id.main_activity_list_header);
		
		listHeaderMonthName.setText(infoManager.getCurrentMonthName());
		AkkEventAdapter adapter = new AkkEventAdapter(getApplicationContext(), infoManager.getEvents(), infoManager);
    	elementListView.setAdapter(adapter);    	
    } 
    
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
        if (gestureScanner.onTouchEvent(ev)) {
            return true;
        }
        
	    return super.dispatchTouchEvent(ev);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		//To speed up the app, the activity is not restarted on screen rotation
		setContentView(R.layout.main);
		displayData();
	}
    
	@Override
	public void finish() {
		infoManager.finish();
	}
}