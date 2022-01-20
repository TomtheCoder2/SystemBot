package systembot;

import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

//import java.sql.*;

public class Utils {
    public static void init() {
    }

    /**
     * Convert a long to formatted time.
     *
     * @param epoch the time in long.
     * @return formatted time
     */
    public static String epochToString(long epoch) {
        Date date = new Date(epoch * 1000L);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        return format.format(date) + " UTC";
    }

    public static JSONObject readJsonFile(String fileName) throws IOException {
        File initialFile = new File(fileName);
        InputStream is = new FileInputStream(initialFile);
//        InputStream is = Utils.class.getResourceAsStream(fileName);
        if (is == null) {
            throw new NullPointerException("Cannot find resource file " + fileName);
        }

        JSONTokener tokener = new JSONTokener(is);
//        System.out.println(new JSONObject(tokener));
        return new JSONObject(tokener);
    }

    public static String percentageBar(int remain, int total) {
        return percentageBar(remain, total, 50);
    }

    public static String percentageBar(int remain, int total, int maxBareSize) {
        return percentageBar((float) remain, (float) total, maxBareSize);
    }

    public static String percentageBar(float remain, float total, int maxBareSize) {
        if (remain > total) {
            throw new IllegalArgumentException();
        }
//        long maxBareSize = 50;
//        int remainProcent = ((100 * remain) / total)
//                / maxBareSize;
        float remainProcent = (remain / total) * maxBareSize;
        System.out.println(remainProcent);
        char defaultChar = '-';
        String icon = "#";
        String bare = new String(new char[(int) maxBareSize]).replace('\0', defaultChar) + "]";
        StringBuilder bareDone = new StringBuilder();
        bareDone.append("[");
        for (int i = 0; i < remainProcent; i++) {
            bareDone.append(icon);
        }
        String bareRemain = bare.substring((int) remainProcent);
        return bareDone + bareRemain + " " + new DecimalFormat("##.00").format(remain / total * 100.0) + "%";
    }


    public static String getKeyByValue(HashMap<String, Integer> map, Integer value) {
        for (java.util.Map.Entry<String, Integer> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Send message without response handling
     *
     * @param user User to dm
     * @param eb   Embed
     */
    public static void sendMessage(org.javacord.api.entity.user.User user, EmbedBuilder eb) {
        user.openPrivateChannel().join().sendMessage(eb);
    }

    // colors for errors, info, warning etc messages
    public static class Pals {
        public static Color warning = (Color.getHSBColor(5, 85, 95));
        public static Color info = (Color.getHSBColor(45, 85, 95));
        public static Color error = (Color.getHSBColor(3, 78, 91));
        public static Color success = (Color.getHSBColor(108, 80, 100));
    }


}