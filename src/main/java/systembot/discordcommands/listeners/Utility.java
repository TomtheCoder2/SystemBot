package systembot.discordcommands.listeners;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;

import java.awt.*;

import static systembot.SystemBot.getTextChannel;
import static systembot.SystemBot.log_channel_id;

public class Utility {
    public static void logAction(Action action, User user1, User user2, String message1, String message2, String trivia) {
        TextChannel logChannel = getTextChannel(log_channel_id);
        if (logChannel == null) {
            System.out.println("ERROR: Log channel not found!!!");
            return;
        }
        switch (action) {
            case left -> {
                logChannel.sendMessage(new EmbedBuilder()
                        .setTitle(user1.getDiscriminatedName() + " left the server")
                        .addField("Id", user1.getIdAsString())
                        .setColor(new Color(0xff0000)));
            }
            case edit -> {
                logChannel.sendMessage(new EmbedBuilder()
                        .setTitle("Message edited")
                        .addField("old Message", message1)
                        .addField("new message", message2)
                        .addField("edited by", "<@" + user1.getIdAsString() + ">\n" + user1.getIdAsString())
                        .addField("link to message", trivia));
            }
            case deleted -> {
                logChannel.sendMessage(new EmbedBuilder()
                        .setTitle("Message deleted")
                        .addField("old message", message1)
                        .addField("deleted by", "<@" + (user1 == null ? "null" : user1.getIdAsString()) + ">\n" + (user1 == null ? "null" : user1.getIdAsString()))
                        .addField("link to message", trivia)
                        .setColor(new Color(0xff0000)));
            }
            case join -> {
                logChannel.sendMessage(new EmbedBuilder()
                        .setTitle("<@" + user1.getIdAsString() + "> joined the server.")
                        .addField("id", user1.getIdAsString())
                        .setColor(new Color(0x00ff00)));
            }
        }
    }

    public static void logAction(Action action) {
        TextChannel logChannel = getTextChannel(log_channel_id);
        if (logChannel == null) {
            System.out.println("ERROR: Log channel not found!!!");
            return;
        }
        switch (action) {
            case left -> {
                logChannel.sendMessage(new EmbedBuilder()
                        .setTitle("null left the server")
                        .addField("Id", "null")
                        .setColor(new Color(0xff0000)));
            }
            case edit -> {
                logChannel.sendMessage(new EmbedBuilder()
                        .setTitle("Message edited")
                        .addField("old Message", "null")
                        .addField("new message", "null")
                        .addField("edited by", "<@null>\nnull")
                        .addField("link to message", "null"));
            }
            case deleted -> {
                logChannel.sendMessage(new EmbedBuilder()
                        .setTitle("Message deleted")
                        .addField("old message", "null")
                        .addField("deleted by", "null")
                        .addField("link to message", "null")
                        .setColor(new Color(0xff0000)));
            }
            case join -> {
                logChannel.sendMessage(new EmbedBuilder()
                        .setTitle("<@null> joined the server.")
                        .addField("id", "null")
                        .setColor(new Color(0x00ff00)));
            }
        }
    }
}
