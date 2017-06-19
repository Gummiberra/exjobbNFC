package com.example.exjobb.format;

import com.example.exjobb.nfc.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * Informations sida för formatering med val mellan att formatera eller
 * rensa tag från information då det inte går att formatera en redan 
 * formaterad tag.
 * 
 * @author Simon Larsson
 * @version 1.0
 * @since 2015-05-15
 *
 */

public class FormatTag extends Activity {

	private Button format,erase;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_format_tag);
		
		
		//Knappar för val av funktion.
		format = (Button) findViewById(R.id.formatButton);
		format.setOnClickListener(new OnClickFormat());
		
		erase = (Button) findViewById(R.id.eraseButton);
		erase.setOnClickListener(new OnClickErase());
		
	}
	
	private class OnClickFormat implements OnClickListener{
		public void onClick(View v) {
			Intent formatIntent = new Intent(FormatTag.this, Format.class);
			FormatTag.this.startActivity(formatIntent);
			
			
		}
	}
	
	private class OnClickErase implements OnClickListener{
		public void onClick(View v) {
			Intent eraseIntent = new Intent(FormatTag.this, Erase.class);
			FormatTag.this.startActivity(eraseIntent);
			
		}
	}
	
	
}
