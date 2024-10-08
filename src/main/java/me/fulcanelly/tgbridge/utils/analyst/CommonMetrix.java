package me.fulcanelly.tgbridge.utils.analyst;

import java.lang.management.ManagementFactory;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;

import me.fulcanelly.tgbridge.utils.UsefulStuff;

public class CommonMetrix {
    
    MemoryUsageDiagramDrawer drawer = new MemoryUsageDiagramDrawer(40, 20);

    public CommonMetrix() {
        drawer.start();
    }
    
    public String getMemoryUsage() {
        return drawer.toString();
    }

    public String getUptime() {
        long jvmUpTime = ManagementFactory
            .getRuntimeMXBean()
            .getUptime();

        Calendar calendar = GregorianCalendar   
            .getInstance();

        calendar.setTime(new Date(jvmUpTime));
        
        int day = calendar.get(Calendar.DAY_OF_YEAR) - 1;
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);

        return String.format("Uptime : %dd %dh %dm %ds", day,  hours, minutes, seconds);
    }

    public List<String> getOnlineList() {
        return Bukkit.getOnlinePlayers().stream()
            .map(player -> switch ((int) player.getHealth()) {
                case 0 -> String.format("* %s  ⚰️",  player.getName());
                default -> String.format("* %s   ♥️ %.2f",  player.getName(), player.getHealth());
            })
            .map(username -> UsefulStuff.escapeMarkdown(username))
            .collect(Collectors.toList());
    }

}