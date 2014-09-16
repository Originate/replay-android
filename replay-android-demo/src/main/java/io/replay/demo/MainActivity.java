package io.replay.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.HashMap;

import io.replay.framework.ReplayIO;
import io.replay.framework.util.ReplayPrefs;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        ReplayIO.init(getApplicationContext(), "51cbeec7-be27-451f-809b-03dbd02dfe5a");
        //replayIO.updateAlias("Alias-"+ new Random().nextInt(9));
        

        Button button1 = (Button) findViewById(R.id.button1);
    	button1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("name", "button1");
				map.put("id", String.valueOf(R.id.button1));
				ReplayIO.track("Button clicked", map);
			}
		});
    	
    	Button button2 = (Button) findViewById(R.id.button2);
    	button2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ReplayIO.dispatch();
			}
		});
        
    	ToggleButton toggleButton = (ToggleButton) findViewById(R.id.toggleButton1);
    	toggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				HashMap<String,String> map = new HashMap<String, String>();
				map.put("toggle", isChecked ? "checked":"unchecked");
				ReplayIO.track("ToggleButton check changed", map);
			}
		});
    	
    	SeekBar seekbar = (SeekBar) findViewById(R.id.seekBar1);
    	seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				HashMap<String,String> map = new HashMap<String, String>();
				map.put("value", String.valueOf(progress));
				map.put("fromUser", String.valueOf(fromUser));
				ReplayIO.track("SeekBar changed", map);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}
    		
    	});

        final EditText identityText = (EditText) findViewById(R.id.editText);
        Button buttonIdentifySet = (Button) findViewById(R.id.button3);
        Button buttonIdentifyClear = (Button) findViewById(R.id.button4);
        View.OnClickListener listener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.button3) {
                    String identity = identityText.getText().toString();
                    ReplayIO.identify(identity);
                    showIdentity();
                }
                if (view.getId() == R.id.button4) {
                    ReplayIO.identify();
                    showIdentity();
                }
            }
        };

        buttonIdentifySet.setOnClickListener(listener);
        buttonIdentifyClear.setOnClickListener(listener);

        showIdentity();
    }

    private void showIdentity() {
        TextView tv = (TextView) findViewById(R.id.textView);
        tv.setText("Current Identity: "+ ReplayPrefs.get(this).getDistinctID());
    }


}
