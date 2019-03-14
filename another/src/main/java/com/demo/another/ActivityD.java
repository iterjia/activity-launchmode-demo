package com.demo.another;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

// DABC=standard
// [D->B->C->A->B1->C1] 同一任务栈，不同实例，逐个返回
public class ActivityD extends AppCompatActivity {

    protected Context mContext;
    protected TextView mTagView;
    protected Button mBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        String tag = getIntent().getStringExtra("tag");
        Log.i("xwj", "onCreate " + getTag() + ", task id is " + getTaskId() + ", process id is " + Process.myPid() + ", from " + tag);
        mBtn = (Button) findViewById(R.id.btn_open);
        mBtn.setText("Launch " + getTargetTag());
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNextActivity();
            }
        });

        mTagView = (TextView) findViewById(R.id.tv_tag);
        mTagView.setText("I'm Activity " + getTag());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String tag = intent.getStringExtra("tag");
        Log.i("xwj", "onNewIntent " + getTag() + ", task id is " + getTaskId() + ", process id is " + Process.myPid() + ", from " + tag);
    }

    protected String getTag() {
        return "D";
    }

    protected String getTargetTag() {
        return "C";
    }

    protected void startNextActivity() {
        Intent intent = new Intent();
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("demo.intent.action." + getTargetTag());
        intent.putExtra("tag", getTag());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("xwj", getTag() + " onDestroy");
    }
}
