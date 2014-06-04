package io.replay.demo;

import io.replay.framework.ReplayIO;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        ReplayIO.init(getApplicationContext(), "51cbeec7-be27-451f-809b-03dbd02dfe5a");
        //replayIO.updateAlias("Alias-"+ new Random().nextInt(9));
        
        ReplayIO.setDispatchInterval(5);
        
        Button button1 = (Button) findViewById(R.id.button1);
    	button1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("name", "button1");
				map.put("id", String.valueOf(R.id.button1));
				ReplayIO.trackEvent("Button clicked", map);
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
				ReplayIO.trackEvent("ToggleButton check changed", map);
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
				ReplayIO.trackEvent("SeekBar changed", map);
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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
        	startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
