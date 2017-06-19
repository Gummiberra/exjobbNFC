package com.example.exjobb.listAdapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Egen gjord adapter för att kunna lägga till text/Object till en lista.
 * Texten agerar även som en knapp.
 * 
 * @author Simon Larsson
 * @version 1.0
 * @since 2015-05-15
 *
 */

public class MyAdapter extends ArrayAdapter<Object>{
	public MyAdapter(Context c, int textViewId)
	{
		super(c, textViewId);
	}
	
	public View getView(int pos, View convertView, ViewGroup partent){
		TextView t = (TextView)super.getView(pos, convertView, partent);
		Object o = getItem(pos);
		t.setText(o.toString());
		t.setBackgroundColor(0xffffffff);
		t.setTextColor(0xff000000);
		return t;
	}   
	
}