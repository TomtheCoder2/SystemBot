package systembot.discordcommands.listeners;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.event.message.MessageDeleteEvent;
import org.javacord.api.event.message.MessageEditEvent;
import org.javacord.api.event.server.member.ServerMemberJoinEvent;
import org.javacord.api.event.server.member.ServerMemberLeaveEvent;
import org.javacord.api.listener.message.MessageDeleteListener;
import org.javacord.api.listener.message.MessageEditListener;
import org.javacord.api.listener.server.member.ServerMemberJoinListener;
import org.javacord.api.listener.server.member.ServerMemberLeaveListener;

import static systembot.SystemBot.getTextChannel;
import static systembot.SystemBot.log_channel_id;
import static systembot.discordcommands.listeners.Utility.logAction;

public class ActionListener implements ServerMemberLeaveListener, ServerMemberJoinListener, MessageEditListener, MessageDeleteListener {
    public ActionListener() {
        TextChannel logChannel = getTextChannel(log_channel_id);
        if (logChannel == null) {
            System.out.println("ERROR: Log channel not found!!!");
        }
    }

    @Override
    public void onServerMemberJoin(ServerMemberJoinEvent event) {
        System.out.println("User joined: " + event.getUser().getDiscriminatedName());
        logAction(Action.join, event.getUser(), null, null, null, null);
    }

    @Override
    public void onServerMemberLeave(ServerMemberLeaveEvent event) {
        System.out.println("User left: " + event.getUser().getDiscriminatedName());
        logAction(Action.left, event.getUser(), null, null, null, null);
    }

    @Override
    public void onMessageEdit(MessageEditEvent event) {
        if (event.getMessage().isPresent()) {
            System.out.println("Message edited: " + event.getMessage().get().getLink().toString());
            logAction(Action.edit, event.getMessageAuthor().flatMap(MessageAuthor::asUser).get(), null, event.getOldContent().get(), event.getNewContent(), event.getMessage().get().getLink().toString());
        } else {
            logAction(Action.edit);
        }
    }

    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
        System.out.println("Message deleted: " + event.getMessage());
        logAction(Action.deleted, event.getMessageAuthor().isPresent() ? event.getMessageAuthor().get().asUser().get() : null, null, event.getMessageContent().isPresent() ? event.getMessageContent().get() : "NULL", null, event.getMessage().isPresent() ? event.getMessage().get().getLink().toString() : "NULL");
    }
}
