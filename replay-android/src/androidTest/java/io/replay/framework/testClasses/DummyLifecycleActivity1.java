package io.replay.framework.testClasses;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import io.replay.framework.ReplayActivity;
import io.replay.framework.tests.R;

/**
 * Created by parthpadgaonkar on 11/18/14.
 */
public class DummyLifecycleActivity1 extends ReplayActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dummy_lifecycle_layout);
        setTitle("Dummy Activity 1");

        Button button = new Button(this);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DummyLifecycleActivity1.this, DummyLifecycleActivity2.class));
            }
        });

        ((LinearLayout)findViewById(R.id.ll_top)).addView(button);
    }
}
