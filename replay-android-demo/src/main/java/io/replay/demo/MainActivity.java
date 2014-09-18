package io.replay.demo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.replay.framework.ReplayConfig.RequestType;
import io.replay.framework.ReplayIO;
import io.replay.framework.model.ReplayJob;
import io.replay.framework.model.ReplayRequest;
import io.replay.framework.model.ReplayRequestFactory;
import io.replay.framework.queue.QueueLayer;
import io.replay.framework.queue.ReplayQueue;
import io.replay.framework.util.Config;
import io.replay.framework.util.ReplayParams;


public class MainActivity extends Activity {

    @InjectView(R.id.textView2) TextView textview;
    private ReplayQueue queue;
    private QueueLayer ql;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

     /*   ReplayIO.init(getApplicationContext(), "51cbeec7-be27-451f-809b-03dbd02dfe5a");

        try {
            q = (ReplayQueue) ReplayIO.class.getDeclaredField("replayQueue").get(null);
*/


        Config mConfig = ReplayParams.getOptions(getApplicationContext());
        mConfig.setApiKey("51cbeec7-be27-451f-809b-03dbd02dfe5a");
        mConfig.setDispatchInterval(36000000); // 10 hours == infinity?
        mConfig.setFlushAt(10);
        mConfig.setDebug(true);

        ReplayIO.init(getApplicationContext(), mConfig);

        ReplayRequestFactory.init(getApplicationContext());

        queue = new ReplayQueue(getApplicationContext(), mConfig);
        ql = new QueueLayer(queue);
        ql.start();
        queue.clear();
        queue.start();


    }

    @OnClick(R.id.button2)
    public void button1Click()  {

        int oldCount = queue.count();
        ReplayJob job = null;
        try {
            job = new ReplayJob(new ReplayRequest(RequestType.EVENTS, new JSONObject().put("event_name", "test")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //queue.enqueue(job);
        ql.enqueueJob(job);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(oldCount + 1 == queue.count()) {
            textview.setText(textview.getText()+ "\nAdded " + job);
        }
    }

    @OnClick(R.id.button3)
    public void button2click(){
//        queue.flush();
        ql.sendFlush();
        int i = 0;
        while(queue.count() >0){
          /*  if(queue.count() == prev -1){
                prev = queue.count();
                textview.setText(textview.getText() + "\n Removed job\t count: " + prev);
            }
            if(i%20==0){
                textview.setText(textview.getText() + "\n\tIterationCount: "+i);
            }
            i++;
            */

            if(++i == 3000) break;
        }
        if(i != 3000){
            textview.setText(textview.getText() + "\nFlush complete");
        }
    }

    /*    Button button1 = (Button) findViewById(R.id.button1);
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
*/

}
