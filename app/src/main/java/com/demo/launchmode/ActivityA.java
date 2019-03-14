package com.demo.launchmode;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Process;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

// ABC在一个进程，DEF在另一个进程

// ABC=standard
// [A->B->C->A1->B1->C1] 同一任务栈，不同实例，逐个返回

// ABC=standard, B.taskAffinity单独设置
// [A->B->C->A1->B1->C1] 同一任务栈，不同实例，逐个返回。standard时taskAffinity设置无效

// AC=standard, B=singleTask
// [A->B->C->A1->B] 同一任务栈，从A1启动B时，B到栈顶，A1和C被销毁，变为[A->B]

// AC=standard, B=singleTask && taskAffinity单独设置
// [A]->[B->C->A1->B] 不同任务栈，从A1启动B时，B到栈顶，A1和C被销毁，变为[A]->[B]

// AC=standard, B=singleTask && taskAffinity单独设置, E=singleTask
// [A]->[B->C]->[E]->[B] 不同任务栈，EB不同进程；从E启动B时，B到栈顶，C被销毁，变为[A]->[E]->[B]

// A=singleInstance, CD=standard || A=singleInstance, C=singleTop, D=standard
// [A]->[C->D]->[A]->C 第二次从A启动C时，CD任务栈到顶部，显示D（此时没有触发onNewIntent事件）

// A=singleInstance, C=singleTask, D=standard
// [A]->[C->D]->[A]->C 第二次从A启动C时，D被销毁，C到栈顶

// 总结：
// launchMode=singleInstance时系统中只有一个实例，由此启动的Activity都会创建新的任务栈

// launchMode=singleTask时系统中只有一个实例，该特性决定了在启动该Activity时，在其顶部的Activity会被销毁，
// 任务栈的情况配合taskAffinity属性有不同的结果，taskAffinity属性值默认是包名
// 如果启动它的Activity与其具有相同的taskAffinity属性，则在同一个任务栈中，否则会创建新的任务栈
public class ActivityA extends AppCompatActivity {

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
        return "A";
    }

    protected String getTargetTag() {
        return "D";
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
