package com.example.exjobb.Mail;

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

/**
 * Funktion för att programmera tag med Email meddelande,
 * Går att lämna fält toma för att programmera tag.
 * Address,Ämne och Meddelande är de tre fälten som går att definiera.
 * 
 * @author Simon Larsson
 * @version 1.0
 * @since 2015-05-15
 *
 */

public class WriteEmail extends Frontpage {
	private Button write;
	private EditText emailAddress;
	private EditText emailSubject;
	private EditText emailMessage;
	private String address;
	private String subject;
	private String message;
	private boolean writeMode;
	private PendingIntent nfcPengingIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_write_email);
		
		writeMode = false;
		
		
		write = (Button) findViewById(R.id.WriteEmail);
		emailAddress = (EditText) findViewById(R.id.editEmailAddress);
		emailSubject = (EditText) findViewById(R.id.editSubjectEmail);
		emailMessage = (EditText) findViewById(R.id.editEmailText);
		
		write.setOnClickListener(new OnClick());
		 
	}
	
	private class OnClick implements OnClickListener{
		public void onClick(View v) {
			
			//Hämta information från fälten.
			
			address= emailAddress.getText().toString();
			subject= emailSubject.getText().toString();
			message =emailMessage.getText().toString();
			
			//Intent som väntar på att lagra taggens information.
			
			nfcPengingIntent = PendingIntent.getActivity(WriteEmail.this, 0, 
					new Intent(WriteEmail.this,WriteEmail.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),0 );
			
			WriteModeOn();
		}
	}
	
	private void WriteModeOn() {
		writeMode=true;
		
		//Filter som letar efter en skannad Tag.
		
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
		
		//Hämtar information från taggen, Skapa ett NDEF meddelande med den information som angets i fälten.
		
		if(writeMode && NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
			Tag myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			NdefRecord record = NdefRecord.createUri("mailto:" + address +"?subject="+ subject + "&body=" + message);
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
				//Ifall taggen är oformaterad för NDEF formateras taggen och programmeras med Mailet.
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
