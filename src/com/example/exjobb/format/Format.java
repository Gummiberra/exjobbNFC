package com.example.exjobb.format;

import java.io.IOException;

import com.example.exjobb.nfc.Frontpage;
import com.example.exjobb.nfc.R;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Funktion för att formatera tag till att kunna lagra NDEF formaterade meddelanden.
 * 
 * 
 * @author Simon Larsson
 * @version 1.0
 * @since 2015-05-15
 *
 */

public class Format extends Frontpage {
	private PendingIntent nfcPendingIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_format);
		
	}
	
	protected void onResume(){
		super.onResume();
		
		//Intent som väntar på att lagra infirmation frpn taggen.
		
		nfcPendingIntent = PendingIntent.getActivity(Format.this, 0, 
				new Intent(Format.this,Format.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),0 );
		enableFormat();
	}
	
	protected void onPause(){
		super.onPause();
		disableFormat();
	}
	
	private void enableFormat() {
		
		//Filter som kontrollerar skannad tag.
		
		IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
		IntentFilter techDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
		IntentFilter[] nfcIntentFilter = new IntentFilter[]{tagDetected,techDetected};

		nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, nfcIntentFilter, null);
			
		}

	protected void onNewIntent(Intent intent){
		
		//Sparar ner information från taggen och anropar formatering metod.
		
		if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction()) || NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())){
			Tag myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			formatTag(myTag);
		}
	}

	private void formatTag(Tag tag) {
		
		//Upprättar kontakt med taggen
		
		NdefFormatable format = NdefFormatable.get(tag);
		
		if(format != null){
			try {
				format.connect();
				//Formaterar tag med tomt NDEF meddelande.
				format.format(new NdefMessage(new NdefRecord(NdefRecord.TNF_EMPTY, null, null, null)));
				format.close();
				Toast.makeText(getApplicationContext(), "Tag formated.",	Toast.LENGTH_LONG).show();
				disableFormat();
				
			}catch (IOException e) {
				// TODO Auto-generated catch block
				Toast.makeText(getApplicationContext(), "Failed to connect", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
				
			} catch (FormatException e) {
				// TODO Auto-generated catch block
				Toast.makeText(getApplicationContext(), "Failed Format", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
		}
		else
			Toast.makeText(getApplicationContext(), "Tag unformatable or already formatted to Ndef.", Toast.LENGTH_LONG).show();
		
	}

	private void disableFormat() {
		nfcAdapter.disableForegroundDispatch(this);
		finish();
		
	}
	
}
