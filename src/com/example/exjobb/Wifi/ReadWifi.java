package com.example.exjobb.Wifi;

import com.example.exjobb.nfc.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.Toast;

/**
 * Modulen för att hantera informationen som läses av vid skanning av en tag
 * med MIME typen för WIFI som programmerades i WriteWifi.java.
 * Modulen läser av informationen och gör om den till en sträng.
 *  
 * @author Simon Larsson
 * @version 1.0
 * @since 2015-05-15
 *
 */

public class ReadWifi extends Activity {
	private String[] wifiCredentials;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_read_wifi);
		
		//Hämtar informationen från taggen.
		Intent intent = getIntent();
		if(intent.getType() != null && intent.getType().equals("nfcapp/wifi")){
			//Gör om meddelandet från Parcelable till sträng.
			Parcelable[] rawMsg = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			NdefRecord record = ((NdefMessage)rawMsg[0]).getRecords()[0];
			String data = new String(record.getPayload());
			
			//kapa strängen vid // som markerar delningen för SSID och Password.
			wifiCredentials = data.split("//");

			connectToWifi(wifiCredentials);
		}
	}

	private void connectToWifi(String[] wifi) {
		//Konfigurerar ett Wifi nätverk. Lägger till SSID och Password. Informationen skall vara inbäddad med " vid start och slut.
		WifiConfiguration config = new WifiConfiguration();
		config.SSID = "\"" + wifi[0] + "\"";
		config.preSharedKey = "\"" + wifi[1] + "\"";
		
		//Wifi manager som hämtar Servicen WIFI, Samt Sätter igång wifi ifall de är avstängt på telefonen.
		WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		wifiManager.setWifiEnabled(true);
		
		//Lägger till de konfigurerade nätverket och spara Nätverks id.
		int networkID = wifiManager.addNetwork(config);
		//Kontrollerar så att nätverk blir tillagt.
		if(networkID > 0)
			Toast.makeText(getApplicationContext(), "Connected to network: " + wifi[0], Toast.LENGTH_LONG).show();
		else
			Toast.makeText(getApplicationContext(), "Failed to connect to network: " + wifi[0], Toast.LENGTH_LONG).show();
		
		//Avslutar kontakt med annat WIFI nätverk och ansluter sedan till det konfigurerade.
		wifiManager.disconnect();
		wifiManager.enableNetwork(networkID, true);
		wifiManager.reconnect();
		finish();
		
	}
}
