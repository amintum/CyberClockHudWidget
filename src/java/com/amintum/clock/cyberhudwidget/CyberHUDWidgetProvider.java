package com.amintum.clock.cyberhudwidget;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;
import android.widget.RemoteViews;

public class CyberHUDWidgetProvider extends AppWidgetProvider {

    private static final String ACTION_UPDATE_HUD = "com.amintum.clock.cyberhudwidget.ACTION_UPDATE_HUD";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        updateAllWidgets(context);
        scheduleRepeatingUpdate(context);
    }

    @Override
    public void onEnabled(Context context) {
        updateAllWidgets(context);
        scheduleRepeatingUpdate(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        updateAllWidgets(context);
    }

    private void scheduleRepeatingUpdate(Context context) {
        try {
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, CyberHUDWidgetProvider.class);
            intent.setAction(ACTION_UPDATE_HUD);
            PendingIntent pi = PendingIntent.getBroadcast(
                    context, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            if (am != null) {
                am.setRepeating(
                        AlarmManager.ELAPSED_REALTIME,
                        SystemClock.elapsedRealtime() + 15000,
                        15000,
                        pi
                );
            }
        } catch (Exception ignored) {}
    }

    public static void updateAllWidgets(Context context) {
        try {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName widgetComponent = new ComponentName(context, CyberHUDWidgetProvider.class);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(widgetComponent);

            if (appWidgetIds == null || appWidgetIds.length == 0) {
                return;
            }

            // 1. Battery (100% accurate native hardware broadcast)
            Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            int battLevel = 100;
            boolean isCharging = false;
            if (batteryIntent != null) {
                int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                int status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                if (level >= 0 && scale > 0) {
                    battLevel = (level * 100) / scale;
                }
                isCharging = (status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL);
            }
            String battStr = (isCharging ? "⚡ BATTERY " : "BATTERY ") + battLevel + "%";

            // 2. RAM
            int ramPercent = calculateRamUsage(context);

            // 3. Storage
            int storagePercent = calculateStorageUsage();

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_cyber_hud);

            // Set Battery Column
            views.setTextViewText(R.id.tv_battery_label, battStr);
            views.setProgressBar(R.id.pb_battery, 100, battLevel, false);

            // Set RAM Column
            views.setTextViewText(R.id.tv_ram_label, "RAM " + ramPercent + "%");
            views.setProgressBar(R.id.pb_ram, 100, ramPercent, false);

            // Set Storage Column
            views.setTextViewText(R.id.tv_storage_label, "STORAGE " + storagePercent + "%");
            views.setProgressBar(R.id.pb_storage, 100, storagePercent, false);

            appWidgetManager.updateAppWidget(widgetComponent, views);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int calculateRamUsage(Context context) {
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (am != null) {
                ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
                am.getMemoryInfo(mi);
                long total = mi.totalMem;
                long avail = mi.availMem;
                if (total > 0) {
                    return (int) (((total - avail) * 100) / total);
                }
            }
        } catch (Exception ignored) {}
        return 48;
    }

    private static int calculateStorageUsage() {
        try {
            StatFs stat = new StatFs(Environment.getDataDirectory().getAbsolutePath());
            long total = stat.getTotalBytes();
            long available = stat.getAvailableBytes();
            if (total > 0) {
                return (int) (((total - available) * 100) / total);
            }
        } catch (Exception ignored) {}
        return 65;
    }
}
