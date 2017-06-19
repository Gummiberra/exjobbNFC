package com.example.exjobb.about;

import com.example.exjobb.nfc.R;
import android.app.Activity;
import android.os.Bundle;

/**
 * AboutNfc är endast en informationssida och utför inga exekveringar, utan
 * har bara en layout med information om NFC och de olika funktionerna i
 * programmet. Finns även en hänvisning för kompabilitet för olika nfc taggar.
 * 
 * @author Simon Larsson
 * @version 1.0
 * @since 2015-05-15
 *
 */

public class AboutNfc extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_nfc);
	}

}
