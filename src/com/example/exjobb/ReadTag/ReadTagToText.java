package com.example.exjobb.ReadTag;

import com.example.exjobb.listAdapter.MyAdapter;
import com.example.exjobb.nfc.Frontpage;
import com.example.exjobb.nfc.R;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.ListView;

/**
 * Modul för att läsa av information från en skannad tag och visa upp informationen
 * samt kort information om taggen.
 * 
 * @author Simon Larsson
 * @version 1.0
 * @since 2015-05-15
 *
 */

public class ReadTagToText extends Frontpage {
	private IntentFilter[] nfcIntentFilter;
	private PendingIntent nfcPendingIntent;
	private Tag myTag;
	private ListView list;
	private MyAdapter adapter;
	private String tagString;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_read_tag_to_text);
		
		list = (ListView) findViewById(R.id.list);
		
		adapter = new MyAdapter(this,R.layout.listlayout_readtag);
	}

	protected void onResume() {
		super.onResume();
		
		NfcAdapter.getDefaultAdapter(this);
		
		//Intent som väntar på att lagra information från skannad tag.
		
		nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
				| Intent.FLAG_ACTIVITY_CLEAR_TOP), 0);
		
		nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, nfcIntentFilter, null);
				
	}
	
	protected void onPause(){
		super.onPause();
		if(nfcAdapter != null)
			nfcAdapter.disableForegroundDispatch(this);
	}
	
	protected void onNewIntent(Intent intent){
		super.onNewIntent(intent);
		if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())||NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())||NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
			
			//Hämtar information från tag.
			myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			NdefMessage[] messages = null;
			Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			
			//Omvandlar från parcelable till NDEF meddelande.
			if (rawMsgs != null) {
				messages = new NdefMessage[rawMsgs.length];
				for (int i = 0; i < rawMsgs.length; i++) {
					messages[i] = (NdefMessage) rawMsgs[i];
				}
			}	
			
			
			if(messages[0] != null) {
				
				//Omvandlar NDEF meddelandes payload från bitar till läsbar text
				String result="";
				byte[] payload = messages[0].getRecords()[0].getPayload();
				for (int b = 0; b<payload.length; b++) {
					result += (char) payload[b];
				}
				
				//Skriver ut den hämtade informationen samt meddelandet.
				if(result.length() == 0)
					tagString = myTag.toString() + "\n--------------Content--------------\nTag is empty";
				else
					tagString = myTag.toString() + "\n--------------Content--------------\nTag Contains " + result;
				
				adapter.add(new String(tagString));//Add on list
				list.setAdapter(adapter);
			}
		}
	}

	
}
