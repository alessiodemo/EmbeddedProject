package com.example.sample;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AlarmHandler
{
    private Context context;

    public AlarmHandler(Context context)
    {
        this.context = context;
    }

    //this will activate the alarm
    @SuppressLint("ShortAlarm")
    public void setAlarmManager()
    {
        Intent intent = new Intent(context,ExecutableService.class);
        PendingIntent sender = PendingIntent.getBroadcast(context,2,intent,PendingIntent.FLAG_MUTABLE);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if(current_time.equals("10:09:00")) //il "!" è messo temporaneamente fino a capire come far lavorare l'app in background
        {
            long triggerAfter = 5*1000; //this will trigger the service after 5s
            long triggerEvery = 5*1000; //this will trigger the service every 5s
            //am.setRepeating(AlarmManager.RTC_WAKEUP,triggerAfter,triggerEvery,sender);
            am.setExact(AlarmManager.RTC_WAKEUP,triggerAfter,sender);
        }
    }

    //this will cancel the alarm
    public void cancelAlarmManager()
    {
        Intent intent = new Intent(context,ExecutableService.class);
        PendingIntent sender = PendingIntent.getBroadcast(context,2,intent,PendingIntent.FLAG_MUTABLE);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if(am!=null)
            am.cancel(sender);
    }

    //to obtain current hour of the device
    Date time = Calendar.getInstance().getTime();
    SimpleDateFormat timeformat = new SimpleDateFormat("hh:mm:ss", Locale.getDefault());
    String current_time = timeformat.format(time);
    //------------------------------------------------------------------------------------------------

}

