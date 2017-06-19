package com.example.exjobb.Sms;

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
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class WriteSMS extends Frontpage {
	private  Button write;
	private EditText phoneNumber;
	private EditText phoneMessage;
	private String number;
	private String message;
	private boolean writeMode;
	private PendingIntent nfcPengingIntent;
	
	/**
	 * Funktion för att programmera tag med ett definierat SMS meddelande.
	 * Fält kan lämnas toma, de fält som kan programmeras är Telefon nummer samt meddelande.
	 * 
	 * @author Simon Larsson
	 * @version 1.0
	 * @since 2015-05-15
	 *
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_write_sms);
		
		writeMode = false;
		
		write = (Button) findViewById(R.id.WriteSMS);
		phoneNumber = (EditText) findViewById(R.id.editPhoneSMS);
		phoneMessage = (EditText) findViewById(R.id.editTextSMS);
		
		write.setOnClickListener(new OnClick());
		 
	}
	
	private class OnClick implements OnClickListener{
		public void onClick(View v) {
			
			//Hämtar information från fälten.
			
			number = phoneNumber.getText().toString();
			message =phoneMessage.getText().toString();
			
			//intent som väntar på att hämta infromation från skannad tag.
			nfcPengingIntent = PendingIntent.getActivity(WriteSMS.this, 0, 
					new Intent(WriteSMS.this,WriteSMS.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),0 );
			
			WriteModeOn();
		}
	}
	
	private void WriteModeOn() {
		writeMode=true;
		
		//Filter för att behandla tag.
		
		IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
		IntentFilter[] WriteTagFilter = new IntentFilter[]{tagDetected};
		nfcAdapter.enableForegroundDispatch(this, nfcPengingIntent, WriteTagFilter, null);
		Toast.makeText(getApplicationContext(), "Put tag on the device's NFC thouchpoint.", Toast.LENGTH_SHORT).show();
	}
	
	private void WriteModeOff() {
		writeMode=false;
		nfcAdapter.disableForegroundDispatch(this);
	}
	
	protected void onNewIntent(Intent intent){
		
		//Hämtar information om tag samt skapar ett NDEF meddelande med informationen som hämtats innan.
		
		if(writeMode && NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
			Tag myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			NdefRecord record = NdefRecord.createUri("sms:" + number + "?body=" + message);
			NdefMessage messageTag = new NdefMessage(record);
			write(messageTag,myTag);
		}
	}
	
	public void write(NdefMessage m, Tag t){
		int size = m.toByteArray().length;
		
		//Upprätta en kontakt
		
		Ndef tag = Ndef.get(t);
		try{
			if(tag != null){
				tag.connect();
				if(tag.getMaxSize() < size){
					Toast.makeText(getApplicationContext(), "Not enough room on tag!",
							Toast.LENGTH_LONG).show();	
					tag.close();
					WriteModeOff();
				}
				else if(tag.isWritable()){
					
					//Programmera taggen.
					
					tag.writeNdefMessage(m);
					tag.close();
					Toast.makeText(getApplicationContext(), "Tag programmed.", Toast.LENGTH_LONG).show();
					finish();
				}
			}
			else{
				//Ifall taggen är oformaterad för NDEF formateras taggen och programmeras med SMS meddelandet.
				NdefFormatable format = NdefFormatable.get(t);
				if(format != null){
					try{
						format.connect();
						format.format(m);
						format.close();
						Toast.makeText(getApplicationContext(), "Tag formated and programmed.",	Toast.LENGTH_LONG).show();
						finish();
					}catch(Exception e){
						
					}
				}
			
			}
		}catch (Exception e){
			
		}
	}

}
