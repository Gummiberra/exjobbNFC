package com.example.exjobb.format;

import com.example.exjobb.nfc.Frontpage;
import com.example.exjobb.nfc.R;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Funktion f�r att ta bort information fr�n taggen och skapa ett tomt NDEF meddelande p� taggen.
 * 
 * @author Simon Larsson
 * @version 1.0
 * @since 2015-05-15
 *
 */

public class Erase extends Frontpage {
	private PendingIntent nfcPendingIntent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_erase);
	}

	protected void onResume(){
		super.onResume();
		
		//Intent som v�ntar p� att h�mta information fr�n taggen som blir skannad.
		
		nfcPendingIntent = PendingIntent.getActivity(Erase.this, 0, 
				new Intent(Erase.this,Erase.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),0 );
		WriteModeOn();
	}
	
	protected void onPause(){
		super.onPause();
		WriteModeOff();
	}
	
	private void WriteModeOn() {
		
		//Filter som kontrollerar ifall en tag �r skannade och tar endast emot och �tg�rdar tag ifall ett av kraven �r uppfyllda.
		
		IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
		IntentFilter techDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
		IntentFilter[] nfcIntentFilter = new IntentFilter[]{tagDetected,techDetected};

		nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, nfcIntentFilter, null);
	}
	
	private void WriteModeOff() {
		nfcAdapter.disableForegroundDispatch(this);
		finish();
	}
	
	protected void onNewIntent(Intent intent){
		
		//H�mtar information ifr�n taggen och skickar vidare f�r borttagning.
		
		if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
			Tag myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			write(myTag);
		}
	}
	
	public void write(Tag t){
		
		//Uppkoppling mot taggen
		
		Ndef tag = Ndef.get(t);
		try{
			if(tag != null){
				if(tag.isWritable()){
					tag.connect();
					//Skapar ett tomt NDEF meddelande och ers�tter den befinntliga informationen p� taggen.
					tag.writeNdefMessage(new NdefMessage(new NdefRecord(NdefRecord.TNF_EMPTY, null, null, null)));
					tag.close();
					Toast.makeText(getApplicationContext(), "Content on tag erased.", Toast.LENGTH_LONG).show();
					WriteModeOff();
				}
			}
			else
				Toast.makeText(getApplicationContext(), "tag null.", Toast.LENGTH_LONG).show();
		}catch (Exception e){
			
		}
	}

}
