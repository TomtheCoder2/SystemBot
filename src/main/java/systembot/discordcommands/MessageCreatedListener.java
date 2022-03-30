package systembot.discordcommands;

import org.javacord.api.event.message.MessageCreateEvent;


public abstract class MessageCreatedListener {
    /**
     * @return true -> cancel*/
    public boolean run(MessageCreateEvent messageCreateEvent){
        return false;
    }
}