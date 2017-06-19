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
 * Modulen f�r att hantera informationen som l�ses av vid skanning av en tag
 * med MIME typen f�r WIFI som programmerades i WriteWifi.java.
 * Modulen l�ser av informationen och g�r om den till en str�ng.
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
		
		//H�mtar informationen fr�n taggen.
		Intent intent = getIntent();
		if(intent.getType() != null && intent.getType().equals("nfcapp/wifi")){
			//G�r om meddelandet fr�n Parcelable till str�ng.
			Parcelable[] rawMsg = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			NdefRecord record = ((NdefMessage)rawMsg[0]).getRecords()[0];
			String data = new String(record.getPayload());
			
			//kapa str�ngen vid // som markerar delningen f�r SSID och Password.
			wifiCredentials = data.split("//");

			connectToWifi(wifiCredentials);
		}
	}

	private void connectToWifi(String[] wifi) {
		//Konfigurerar ett Wifi n�tverk. L�gger till SSID och Password. Informationen skall vara inb�ddad med " vid start och slut.
		WifiConfiguration config = new WifiConfiguration();
		config.SSID = "\"" + wifi[0] + "\"";
		config.preSharedKey = "\"" + wifi[1] + "\"";
		
		//Wifi manager som h�mtar Servicen WIFI, Samt S�tter ig�ng wifi ifall de �r avst�ngt p� telefonen.
		WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		wifiManager.setWifiEnabled(true);
		
		//L�gger till de konfigurerade n�tverket och spara N�tverks id.
		int networkID = wifiManager.addNetwork(config);
		//Kontrollerar s� att n�tverk blir tillagt.
		if(networkID > 0)
			Toast.makeText(getApplicationContext(), "Connected to network: " + wifi[0], Toast.LENGTH_LONG).show();
		else
			Toast.makeText(getApplicationContext(), "Failed to connect to network: " + wifi[0], Toast.LENGTH_LONG).show();
		
		//Avslutar kontakt med annat WIFI n�tverk och ansluter sedan till det konfigurerade.
		wifiManager.disconnect();
		wifiManager.enableNetwork(networkID, true);
		wifiManager.reconnect();
		finish();
		
	}
}
