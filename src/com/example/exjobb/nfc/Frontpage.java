package com.example.exjobb.nfc;

import com.example.exjobb.ReadTag.ReadTagToText;
import com.example.exjobb.WriteItemList.WriteToTag;
import com.example.exjobb.about.AboutNfc;
import com.example.exjobb.format.FormatTag;
import com.example.exjobb.format.LockTag;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * Frontpage �r huvudsidan f�r applikationen, d�r aktiveras nfcAdaptern som anv�nds
 * f�r alla funktioner i detta program.
 * Huvudsidan h�nvisar sedan vidare till de olika funktionerna i programmet.
 * 
 * @author Simon Larsson
 * @version 1.0
 * @since 2015-05-15
 *
 */


public class Frontpage extends Activity {

	protected NfcAdapter nfcAdapter;
	private Button readTag,writeTag,formatTag,lockTag,about;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frontpage);
        
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        
        // Knappar som skickar vidare anv�ndaren till n�sta aktivitet f�r att utf�ra funktionen.
        readTag = (Button) findViewById(R.id.readButton);
        readTag.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent readTagIntent = new Intent(Frontpage.this, 
						ReadTagToText.class);
				Frontpage.this.startActivity(readTagIntent);
				
			}
		});
        writeTag = (Button) findViewById(R.id.writeButton);
        writeTag.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent writeTagIntent = new Intent(Frontpage.this, 
						WriteToTag.class);
				startActivity(writeTagIntent);
				
			}
		});
        
        formatTag = (Button) findViewById(R.id.formatButton);
        formatTag.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent formatTagIntent = new Intent(Frontpage.this, 
						FormatTag.class);
				startActivity(formatTagIntent);
				
			}
		});
        
        lockTag = (Button) findViewById(R.id.lockButton);
        lockTag.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent lockTagIntent = new Intent(Frontpage.this, 
						LockTag.class);
				startActivity(lockTagIntent);
				
			}
		});
        
        about = (Button) findViewById(R.id.aboutButton);
        about.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent lockTagIntent = new Intent(Frontpage.this, 
						AboutNfc.class);
				startActivity(lockTagIntent);
				
			}
		});
    }
    
    protected void onResume(){
    	super.onResume();
    	
    	// Kontroll att nfcAdaptern inte �r null.
    	
    	if(nfcAdapter != null){ 
    		
    		/*Ifall NFC inte �r aktiverat p� telefonen m�ste detta aktiveras innan man kan forts�tta att anv�nda applikationen,
    		Om nfc inte �r aktiverat �ppnas ett f�nster med information om att antingen aktivera eller avsluta. Aktiverar man s� 
    		skickas man vidare till nfc inst�llningar d�r man kan aktivera NFC. Finns inte h�rdvara f�r NFCeller att NFC itne 
    		aktiveras avslutas applikationen.*/
    		
    		if(!nfcAdapter.isEnabled()){  
    			LayoutInflater inflater = getLayoutInflater();
    			View nfc_Settings_layout = inflater.inflate(R.layout.nfc_settings_layout,(ViewGroup) findViewById(R.id.nfc_settings_layout));
	            new AlertDialog.Builder(this).setView(nfc_Settings_layout)
	            	    .setPositiveButton("Activate NFC", new DialogInterface.OnClickListener() {
			                public void onClick(DialogInterface arg0, int arg1) {
			    				Intent setnfc = new Intent(Settings.ACTION_NFC_SETTINGS);
			    				startActivity(setnfc);
			                }
		                })
	                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
	                        
	                    	public void onCancel(DialogInterface dialog) {
	                    		finish(); // Avslutar applikationen d� NFC inte aktiveras d� applikationen �r beroende av NFC.
	                        }	                    	
	                    }).create().show();
    		}
    		
    	}
    	else{
    		Toast.makeText(getApplicationContext(), "NFC is not available on this phone", Toast.LENGTH_LONG).show();
    		finish();
    	}
    }
}
