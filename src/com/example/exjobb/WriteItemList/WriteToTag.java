package com.example.exjobb.WriteItemList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.example.exjobb.Mail.WriteEmail;
import com.example.exjobb.Sms.WriteSMS;
import com.example.exjobb.Wifi.WriteWifi;
import com.example.exjobb.listAdapter.MyAdapter;
import com.example.exjobb.nfc.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Lista med det olika funktionerna som går att programmera till taggen.
 * 
 * @author Simon Larsson
 * @version 1.0
 * @since 2015-05-15
 *
 */

public class WriteToTag extends Activity {
	private ListView list;
	private MyAdapter adapter;
	private List<String> items;
	private Map<String,Class> name2class;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_write_to_tag);
		
		items = new ArrayList<String>();
		
		name2class = new HashMap<String,Class>();
		
		setup();
		
		list = (ListView) findViewById(R.id.list);
		list.setOnItemClickListener(new ItemClick());
		
		adapter = new MyAdapter(this,R.layout.listlayout_readtag);
		
		//Add on list
		adapter.addAll(items);
		list.setAdapter(adapter);
	}
	
	private void setup(){
		
		//Mappning mellan klass och Text i lista.
		
		addActivity("SMS", WriteSMS.class);
		addActivity("Email",WriteEmail.class);
		addActivity("Wifi", WriteWifi.class);
	}
	
	private void addActivity(String name, Class activity) {
		items.add(name);
    	name2class.put(name, activity); 
	}

	private class ItemClick implements OnItemClickListener {
    	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    		/* Find selected activity */
    		String activity_name = items.get(position);
    		Class activity_class = name2class.get(activity_name);

    		/* Start new Activity */
    		Intent intent = new Intent(WriteToTag.this,activity_class);
    		WriteToTag.this.startActivity(intent);
    	}   	
    }
}
