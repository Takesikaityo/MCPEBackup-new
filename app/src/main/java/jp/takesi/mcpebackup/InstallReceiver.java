package jp.takesi.mcpebackup;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;

/**
 * Created by takec on 2017/04/29.
 */

public class InstallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String packageName=intent.getData().getEncodedSchemeSpecificPart();
        if(packageName.equals("com.mojang.minecraftpe")){
            intent.setClass(context, MainActivity.class);
            Bundle bandle = new Bundle();
            bandle.putString("type", "backup");
            bandle.putString("package", "com.mojang.minecraftpe");
            bandle.putString("name","MinecraftPE_");
            intent.putExtras(bandle);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
        if(packageName.equals("io.mrarm.mctoolbox")){
            intent.setClass(context, MainActivity.class);
            Bundle bandle = new Bundle();
            bandle.putString("type", "backup");
            bandle.putString("package", "io.mrarm.mctoolbox");
            bandle.putString("name","ToolBox_");
            intent.putExtras(bandle);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
        if(packageName.equals("net.zhuoweizhang.mcpelauncher")){
            intent.setClass(context, MainActivity.class);
            Bundle bandle = new Bundle();
            bandle.putString("type", "backup");
            bandle.putString("package", "net.zhuoweizhang.mcpelauncher");
            bandle.putString("name","BlockLauncher_");
            intent.putExtras(bandle);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
        if(packageName.equals("net.zhuoweizhang.mcpelauncher.pro")){
            intent.setClass(context, MainActivity.class);
            Bundle bandle = new Bundle();
            bandle.putString("type", "backup");
            bandle.putString("package", "net.zhuoweizhang.mcpelauncher.pro");
            bandle.putString("name","BlockLauncherPro_");
            intent.putExtras(bandle);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }

        //Intent intent1 = new Intent(context, InstallActivity.class);
        //intent1.putExtra("text", "Notification Activity");
        // アクティビティが新しい空のタスクで起動するようにフラグを設定
        //intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setAutoCancel(true)
                        .setColor(ContextCompat.getColor(context,R.color.colorAccent))
                        .setContentTitle("一件のアクティビティ")
                        .setContentText(packageName + "のアプリがインストールされました。");
        //Intent resultIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        //stackBuilder.addParentStack(MainActivity.class);
        //stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManagerCompat manager = NotificationManagerCompat.from(context.getApplicationContext());
        manager.notify(1, mBuilder.build());
    }
}
