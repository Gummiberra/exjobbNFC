package com.example.exjobb.Wifi;

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
 * Funktionen för att programmera taggar med WIFI information, Funktionen kräver att
 * applikationen är installerad vid avläsning. Vid användande av Nyare versioner Android 5.0
 * och högre går det att programmera taggar med wifi från Nätverksinställningar.
 * Fälten som fylls i är SSID och Password.
 * Anslutningen till wifi funkar ifall man använder sig utav WPA som krypering på nätverket.
 *  
 * @author Simon Larsson
 * @version 1.0
 * @since 2015-05-15
 *
 */

public class WriteWifi extends Frontpage {
	private  Button write;
	private EditText wifiSSID;
	private EditText wifiPassword;
	private String SSID;
	private String password;
	private boolean writeMode;
	private PendingIntent nfcPengingIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_write_wifi);
		
		writeMode = false;
		
		write = (Button) findViewById(R.id.writeWifi);
		wifiSSID = (EditText) findViewById(R.id.wifiSSID);
		wifiPassword = (EditText) findViewById(R.id.wifiPassword);
		
		write.setOnClickListener(new OnClick());
		 
	}
	
	private class OnClick implements OnClickListener{
		public void onClick(View v) {
			
			// Hämtar infromation från fälten
			SSID = wifiSSID.getText().toString();
			password =wifiPassword.getText().toString();
			
			//Intent som väntar på att lagra information från skannad tag.
			nfcPengingIntent = PendingIntent.getActivity(WriteWifi.this, 0, 
					new Intent(WriteWifi.this,WriteWifi.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),0 );
			
			WriteModeOn();
		}
	}
	
	private void WriteModeOn() {
		writeMode=true;
		
		//Filter som kollar NFC taggens skanning
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
		if(writeMode && NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
			//Hämtar tag information
			Tag myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			
			//Test för NFC app, länk till applikationen då den inte finns på Googleplay.
			NdefRecord aarRecord = NdefRecord.createUri("https://www.dropbox.com/s/t2fuhq89ke7e4ye/exjobb%20Nfc.apk?dl=0");
			
			/*Nedanstående aaRecord används när applikationen är lanserad på Google play.
			 *När taggen skannas av en telefon som ej har applikationen så måste applikationen installeras för att
			 *informationen på taggen skall gå att använda.
			 */
			//NdefRecord aarRecord = NdefRecord.createApplicationRecord(Frontpage.class.getPackage().getName());
			
			//Ndefrecord för informationen om WIFI med en egen MIME Typ som gör att inga andra applikationen bör ta emot taggen när den skannas.
			NdefRecord record = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,new String("nfcApp/wifi").getBytes(),null, new String(SSID + "//"+ password).getBytes());
			
			//Båda recordsen läggs i meddelandet som skall programmeras. aarRecord läggs till sist för att inte avläsas ifall applikationen är installerad.
			NdefMessage messageTag = new NdefMessage(new NdefRecord[]{record,aarRecord});
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
				//Ifall taggen är oformaterad för NDEF formateras taggen och programmeras med WIFI meddelandet.
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

