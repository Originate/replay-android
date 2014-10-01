package io.replay.demo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.replay.demo.model.TestReplayJob;
import io.replay.framework.ReplayIO;
import io.replay.framework.model.ReplayRequestFactory;
import io.replay.framework.queue.QueueLayer;
import io.replay.framework.queue.ReplayQueue;
import io.replay.framework.util.Config;
import io.replay.framework.util.ReplayParams;
import io.replay.framework.util.Util;

public class MainActivity extends Activity {

    public static final String API_KEY = "2bbf36db-ed6f-4944-92ab-11639c2b74f2";
    @InjectView(R.id.customEventText) TextView customText;
    @InjectView(R.id.identifyText) TextView identifyText;
    @InjectView(R.id.flushCountTV) TextView count;
    @InjectView(R.id.log) TextView log;


    private ReplayQueue queue;
    private QueueLayer ql;
    private Handler mHandler;


    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            count.setText(String.valueOf(queue.count()));
            mHandler.postDelayed(mStatusChecker, 333);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

        ButterKnife.inject(this);

        ReplayIO.init(getApplicationContext(), API_KEY);


        Config mConfig = ReplayParams.getOptions(getApplicationContext());
        mConfig.setApiKey(API_KEY);
        mConfig.setDispatchInterval(1000*60*60); // 10 hours == infinity?
        mConfig.setFlushAt(10);
        mConfig.setMaxQueue(50);
        mConfig.setDebug(true);

        ReplayIO.init(getApplicationContext(), mConfig);

        ReplayRequestFactory.init(getApplicationContext());

        queue = new ReplayQueue(getApplicationContext(), mConfig);
        ql = new QueueLayer(queue);
        ql.start();
        queue.clear();
        queue.start();

        mHandler = new Handler();
        mHandler.postDelayed(mStatusChecker, 333);
    }

    @OnClick(R.id.button_a)
    public void AButtonClick() {
        TestReplayJob job = new TestReplayJob(ReplayRequestFactory.requestForEvent("Clicked Button A", null));
        ql.enqueueJob(job);
        log.setText(log.getText() + "\nAdded Button_A Job #"+job.idStr);
    }

    @OnClick(R.id.button_b)
    public void BButtonClick(){
        TestReplayJob job = new TestReplayJob(ReplayRequestFactory.requestForEvent("Clicked Button B", null));
        ql.enqueueJob(job);
        log.setText(log.getText() + "\nAdded Button_B Job #"+job.idStr);
    }

    @OnClick(R.id.button_customEvent)
    public void customButtonClick(){
        String custom = customText.getText().toString();
        if(!Util.isNullOrEmpty(custom)){
            TestReplayJob job = new TestReplayJob(ReplayRequestFactory.requestForEvent("Clicked " + custom, null));
            ql.enqueueJob(job);
            log.setText(log.getText() + "\nAdded Custom Event Job#"+job.idStr);
        }else{
            Toast.makeText(this, "Please type some text first",Toast.LENGTH_SHORT).show();
        }

    }

    @OnClick(R.id.button_identify)
    public void identifyButtonClick(){
        String alias = identifyText.getText().toString();
        if(!Util.isNullOrEmpty(alias)){
            TestReplayJob job = new TestReplayJob(ReplayRequestFactory.requestForEvent("Clicked " + alias, null));
            ql.enqueueJob(job);
            log.setText(log.getText() + "\nAdded Alias Job #"+job.idStr);
        }else{
            Toast.makeText(this, "Please type some text first",Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.flush)
    public void flushButtonClick(){
        ql.sendFlush();
        mHandler.postDelayed(flushCheck, 1000);
    }

    private Runnable flushCheck = new Runnable(){
        @Override
        public void run() {
            if(queue.count() == 0){
                log.setText("\nSuccessfully flushed queue!");
                TestReplayJob.id.set(0);
            }
        }
    };

}
