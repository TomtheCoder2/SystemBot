package website;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.user.User;
import systembot.SystemBot;

import java.util.HashMap;

public class Administrators {
    public static HashMap<String, User> Admins = new HashMap<>();
    
    public static void init(DiscordApi api) {
        try {
            Admins.put("nautilus", SystemBot.api.getUserById(770240444466069514L).get());
            Admins.put("wmf", SystemBot.api.getUserById(678060551888175145L).get());
            Admins.put("noWords", SystemBot.api.getUserById(687347431548911644L).get());
            Admins.put("zambronix", SystemBot.api.getUserById(398683530588061697L).get());
            Admins.put("etilax", SystemBot.api.getUserById(498158284512034816L).get());
            Admins.put("clashGone", SystemBot.api.getUserById(949658547023650936L).get());
        } catch(Exception e) {
            System.out.println("There was an error while getting avatar images: ");
            e.printStackTrace();
        }
    }
}
