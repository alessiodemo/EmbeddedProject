package com.example.sample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ExecutableService extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        //this will be executed at selected interval
        Toast.makeText(context,"azione ripetuta",Toast.LENGTH_SHORT).show();
    }
}