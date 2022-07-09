package systembot;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.json.JSONObject;
import org.json.JSONTokener;
import systembot.discordcommands.Context;
import systembot.website.entity.Staff;

import java.awt.*;
import java.io.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static arc.util.Log.debug;
import static systembot.SystemBot.staffRepository;

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

    /**
     * Converts a {@link JsonObject} to {@link EmbedBuilder}.
     * Supported Fields: Title, Author, Description, Color, Fields, Thumbnail, Footer.
     *
     * @param json The JsonObject
     * @return The Embed
     */
    public static EmbedBuilder jsonToEmbed(JsonObject json) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        JsonPrimitive titleObj = json.getAsJsonPrimitive("title");
        if (titleObj != null) { // Make sure the object is not null before adding it onto the embed.
            embedBuilder.setTitle(titleObj.getAsString());
        }

        JsonObject authorObj = json.getAsJsonObject("author");
        if (authorObj != null) {
            String authorName = authorObj.get("name").getAsString();
            String authorIconUrl = authorObj.get("icon_url").getAsString();
            String authorUrl = null;
            if (authorObj.get("url") != null)
                authorUrl = authorObj.get("url").getAsString();
            if (authorIconUrl != null) // Make sure the icon_url is not null before adding it onto the embed. If its null then add just the author's name.
                embedBuilder.setAuthor(authorName, (authorUrl != null ? authorUrl : "https://www.youtube.com/watch?v=iik25wqIuFo"), authorIconUrl); // default: little rick roll
            else
                embedBuilder.setAuthor(authorName);
        }

        JsonPrimitive descObj = json.getAsJsonPrimitive("description");
        if (descObj != null) {
            embedBuilder.setDescription(descObj.getAsString());
        }

        JsonPrimitive colorObj = json.getAsJsonPrimitive("color");
        if (colorObj != null) {
            Color color = new Color(colorObj.getAsInt());
            embedBuilder.setColor(color);
        }

        JsonObject imageObj = json.getAsJsonObject("image");
        if (imageObj != null) {
            embedBuilder.setImage(imageObj.get("url").getAsString());
        }

        JsonArray fieldsArray = json.getAsJsonArray("fields");
        if (fieldsArray != null) {
            // Loop over the fields array and add each one by order to the embed.
            fieldsArray.forEach(ele -> {
                debug(ele);
                if (ele != null && !ele.isJsonNull()) {
                    String name = ele.getAsJsonObject().get("name").getAsString();
                    String content = ele.getAsJsonObject().get("value").getAsString();
                    boolean inline = false;
                    if (ele.getAsJsonObject().has("inline")) {
                        inline = ele.getAsJsonObject().get("inline").getAsBoolean();
                    }
                    embedBuilder.addField(name, content, inline);
                }
            });
        }

        JsonObject thumbnailObj = json.getAsJsonObject("thumbnail");
        if (thumbnailObj != null) {
            embedBuilder.setThumbnail(thumbnailObj.get("url").getAsString());
        }

        JsonPrimitive timeStampObj = json.getAsJsonPrimitive("timestamp");
        if (timeStampObj != null) {
            if (timeStampObj.getAsBoolean()) {
                embedBuilder.setTimestampToNow();
            }
        }

        JsonObject footerObj = json.getAsJsonObject("footer");
        if (footerObj != null) {
            String content = footerObj.get("text").getAsString();
            String footerIconUrl = footerObj.get("icon_url").getAsString();

            if (footerIconUrl != null)
                embedBuilder.setFooter(content, footerIconUrl);
            else
                embedBuilder.setFooter(content);
        }

        return embedBuilder;
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

    public static void printResults(Process process, Context ctx) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = "";
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
            ctx.channel.sendMessage(line);
        }
    }

    public static Staff getStaffByString(String name, Context ctx) {
        Staff target;
        if (Pattern.compile("[0-9]+").matcher(name).matches()) { // id
            long id = Long.parseLong(name);
            Optional<Staff> targetOptional = Optional.empty();
            try {
                targetOptional = staffRepository.findById(Math.toIntExact(id));
            } catch (Exception ignored) {
            }
            if (targetOptional.isPresent()) {
                target = targetOptional.get();
            } else { // search for discord id
                targetOptional = staffRepository.findByUserId(id);
                if (targetOptional.isPresent()) {
                    target = targetOptional.get();
                } else { // doesn't exist
                    ctx.channel.sendMessage(new EmbedBuilder()
                            .setTitle("Error")
                            .setDescription("Could not find id `" + id + "`!")
                            .setColor(new Color(0xff0000)));
                    return null;
                }
            }
        } else {
            List<Staff> targetOptional = staffRepository.findByName(name);
            if (targetOptional.isEmpty()) {
                ctx.channel.sendMessage(new EmbedBuilder()
                        .setTitle("Error")
                        .setDescription("Could not find name `" + name + "`!")
                        .setColor(new Color(0xff0000)));
                return null;
            }
            target = targetOptional.get(0);
        }
        if (target == null) {
            ctx.channel.sendMessage(new EmbedBuilder()
                    .setTitle("Error")
                    .setDescription("Could not find staff `" + name + "`!")
                    .setColor(new Color(0xff0000)));
            return null;
        }
        return target;
    }

    // copied and pasted from the internet, hope it works
    public static boolean onlyDigits(String str) {
        // Regex to check string
        // contains only digits
        String regex = "[0-9]+";

        // Compile the ReGex
        Pattern p = Pattern.compile(regex);

        // If the string is empty
        // return false
        if (str == null) {
            return false;
        }

        // Find match between given string
        // and regular expression
        // using Pattern.matcher()
        Matcher m = p.matcher(str);

        // Return if the string
        // matched the ReGex
        return m.matches();
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