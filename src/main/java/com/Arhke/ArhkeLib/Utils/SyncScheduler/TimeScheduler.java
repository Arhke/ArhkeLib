package com.Arhke.ArhkeLib.Utils.SyncScheduler;

import com.Arhke.ArhkeLib.Base.MainBase;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class TimeScheduler<T extends JavaPlugin> extends MainBase<T> {
    public TimeScheduler(T instance) {
        super(instance);
    }

    public void hourlyUpdateAsync(boolean sync, BukkitRunnable br) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Calendar nextTask = new GregorianCalendar();

                nextTask.set(Calendar.MILLISECOND, 0);
                nextTask.set(Calendar.SECOND, 0);
                nextTask.set(Calendar.MINUTE, 0);
                nextTask.set(Calendar.HOUR_OF_DAY, 0);
                nextTask.add(Calendar.DATE, 1);
                long till;
                if ((till = nextTask.getTimeInMillis() - System.currentTimeMillis()) < 3600000) {
                    if(sync) br.runTaskLater(getPlugin(), till / 50L);
                    else br.runTaskLaterAsynchronously(getPlugin(), till / 50L);

                }

            }

        }.runTaskTimerAsynchronously(getPlugin(), 0, 20 * 59 * 10);
    }
}
