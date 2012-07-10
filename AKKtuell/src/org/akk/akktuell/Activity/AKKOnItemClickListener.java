package org.akk.akktuell.Activity;

import org.akk.akktuell.Model.AkkEvent;
import org.akk.akktuell.Model.AkkEvent.AkkEventType;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class AKKOnItemClickListener implements OnItemClickListener {
	
	private AKKtuellMainActivity main;
	private ListView listView;
	
	public AKKOnItemClickListener(AKKtuellMainActivity main, ListView listView) {
		this.main = main;
		this.listView = listView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		AkkEvent clickedEvent = (AkkEvent) listView.getAdapter().getItem(position);
		
		if (clickedEvent == null) {
			return;
		}
		
		if (clickedEvent.getEventType() == AkkEventType.Veranstaltungshinweis) {
			return;
		}
		
		Intent intent = new Intent(main,AKKtuellEventView.class);
		intent.putExtra("EVENT_NAME", clickedEvent.getEventName());
		intent.putExtra("EVENT_DATE", "test");
		intent.putExtra("EVENT_DESCRIPTION", clickedEvent.getEventDescription());
		main.startActivity(intent);
	}

}
