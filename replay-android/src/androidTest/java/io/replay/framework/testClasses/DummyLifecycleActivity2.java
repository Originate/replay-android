package io.replay.framework.testClasses;

import android.os.Bundle;

import io.replay.framework.ReplayActivity;
import io.replay.framework.tests.R;

/**
 * Created by parthpadgaonkar on 11/18/14.
 */
public class DummyLifecycleActivity2 extends ReplayActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dummy_lifecycle_layout2);
        setTitle("Dummy Activity 2");
    }
}
