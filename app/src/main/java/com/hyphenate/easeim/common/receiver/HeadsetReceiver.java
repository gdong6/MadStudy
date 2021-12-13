package com.hyphenate.easeim.common.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.hyphenate.util.EMLog;

public class HeadsetReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean state = intent.getIntExtra("state", 0) == 0 ? false : true;
        String name = intent.getStringExtra("name");

    }
}
