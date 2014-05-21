package io.replay.demo;

import io.replay.framework.ReplayIO;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

public class SettingsActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_settings);
		
		ToggleButton btnReplayIO = (ToggleButton) findViewById(R.id.toggleButton1);
		btnReplayIO.setChecked(ReplayIO.isEnabled());
		
		ToggleButton btnDebugMode = (ToggleButton) findViewById(R.id.toggleButton2);
		btnDebugMode.setChecked(ReplayIO.isDebugMode());
		
		CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(buttonView.getId() == R.id.toggleButton1) {
					if (isChecked) {
						ReplayIO.enable();
					} else {
						ReplayIO.disable();
					}
				} else if (buttonView.getId() == R.id.toggleButton2) {
					ReplayIO.setDebugMode(isChecked);
				}
			}
		};
		
		btnReplayIO.setOnCheckedChangeListener(listener);
		btnDebugMode.setOnCheckedChangeListener(listener);
		
		EditText etInterval = (EditText) findViewById(R.id.editText1);
		etInterval.setText(String.valueOf(ReplayIO.getDispatchInterval()));
		etInterval.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if (s.length() != 0) {
					int value = Integer.valueOf(s.toString());
					Log.d("Demo", "new value: "+value);
					ReplayIO.setDispatchInterval(value);
				}
			}
			
		});
		
	}
}
