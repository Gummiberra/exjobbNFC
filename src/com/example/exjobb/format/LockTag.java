package com.example.exjobb.format;

import com.example.exjobb.nfc.Frontpage;
import com.example.exjobb.nfc.R;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Funktion för att låsa taggen till den programmerade informationen.
 * Funktionen är en One way operation.
 * 
 * @author Simon Larsson
 * @version 1.0
 * @since 2015-05-15
 *
 */

public class LockTag extends Frontpage {
	
	private PendingIntent nfcPendingIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock_tag);
	}

	protected void onResume(){
		super.onResume();
		
		//Intent som väntar på att lagra information från tag.
		
		nfcPendingIntent = PendingIntent.getActivity(LockTag.this, 0, 
				new Intent(LockTag.this,LockTag.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),0 );
		LockTagOn();
	}
	
	protected void onPause(){
		super.onPause();
		LockTagOff();
	}
	
	private void LockTagOn() {
		
		//Filter som kontrollerar taggen. 
		
		IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
		IntentFilter techDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
		IntentFilter[] nfcIntentFilter = new IntentFilter[]{tagDetected,techDetected};

		nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, nfcIntentFilter, null);
	}
	
	private void LockTagOff() {
		nfcAdapter.disableForegroundDispatch(this);
		finish();
	}
	
	protected void onNewIntent(Intent intent){
		
		//Lagrar information från taggen.
		
		if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
			Tag myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			Lock(myTag);
		}
	}
	
	public void Lock(Tag t){
		
		// upprätta kontakt med tag.
		
		Ndef tag = Ndef.get(t);
		try{
			if(tag != null){
				tag.connect();
				if(tag.canMakeReadOnly()){
					//lås taggen med den programmerade informationen.
					tag.makeReadOnly();
					tag.close();
					Toast.makeText(getApplicationContext(), "Tag is locked", Toast.LENGTH_LONG).show();
				}
				else
					Toast.makeText(getApplicationContext(), "Tag can't be locked", Toast.LENGTH_LONG).show();
			}
			else
				Toast.makeText(getApplicationContext(), "Tag null.", Toast.LENGTH_LONG).show();
		}catch (Exception e){
			
		}
	}
}
