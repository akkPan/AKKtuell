package org.akk.akktuell.Activity;

import org.akk.akktuell.R;
import org.akk.akktuell.Model.AkkEvent;
import org.akk.akktuell.Model.InfoManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class AKKtuellMainActivity extends Activity {
	
	private InfoManager infoManager;
	private ListView elementListView;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        infoManager = new InfoManager(getApplicationContext());
        
        setContentView(R.layout.main);
        elementListView = (ListView) findViewById(R.id.main_element_listview);
        //elementListView.addHeaderView(view, null, false);
        elementListView.setOnItemClickListener(new OnItemClickListener() {  
        	@Override
        	public void onItemClick(AdapterView<?> parent, View view,
        			int position, long id) {
        			AkkEvent clickedEvent = (AkkEvent) AKKtuellMainActivity.this.elementListView.getAdapter().getItem(position);
        			if (clickedEvent != null) {
        				Intent intent = new Intent(AKKtuellMainActivity.this,AKKtuellEventView.class);
        				intent.putExtra("EVENT_NAME", clickedEvent.getEventName());
        				intent.putExtra("EVENT_DATE", "test");
        				intent.putExtra("EVENT_DESCRIPTION", clickedEvent.getEventDescription());
        				startActivity(intent);
        			}
        		}
        });
        displayData();
        
    }
    
    private void displayData() {
    	if (!infoManager.readyToDisplayData()) {
    		//wait for data update
    	} 
    	AkkEventAdapter adapter = new AkkEventAdapter(getApplicationContext(), infoManager.getEvents(), infoManager);
    	elementListView.setAdapter(adapter);
    	
    }
    
    
}