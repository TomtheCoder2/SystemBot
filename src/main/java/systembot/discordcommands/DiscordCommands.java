package systembot.discordcommands;

import java.awt.*;
import java.util.*;

import systembot.SystemBot;
import systembot.Utils;
import systembot.SystemBot;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import static systembot.SystemBot.*;

/**
 * Represents a registry of commands
 */
public class DiscordCommands implements MessageCreateListener {
    private HashMap<String, Command> registry = new HashMap<>();
    private final Set<MessageCreatedListener> messageCreatedListenerRegistry = new HashSet<>();
    private final TextChannel admin_bot_channel = getTextChannel(admin_bot_channel_id);
    private final TextChannel staff_bot_channel = getTextChannel(staff_bot_channel_id);
    private final TextChannel bot_channel = getTextChannel(bot_channel_id);
    private final TextChannel error_log_channel = getTextChannel("891677596117504020");


    public DiscordCommands() {
        System.out.println("Init DiscordCommands...");
        // stuff
    }

    /**
     * Register a command in the CommandRegistry
     *
     * @param c The command
     */
    public void registerCommand(Command c) {
        registry.put(c.name.toLowerCase(), c);
    }
    // you can override the name of the command manually, for example for aliases

    /**
     * Register a command in the CommandRegistry
     *
     * @param forcedName Register the command under another name
     * @param c          The command to register
     */
    public void registerCommand(String forcedName, Command c) {
        registry.put(forcedName.toLowerCase(), c);
    }

    /**
     * Register a method to be run when a message is created.
     *
     * @param listener MessageCreatedListener to be run when a message is created.
     */
    public void registerOnMessage(MessageCreatedListener listener) {
        messageCreatedListenerRegistry.add(listener);
    }

    /**
     * Parse and run a command
     *
     * @param event Source event associated with the message
     */
    public void onMessageCreate(MessageCreateEvent event) {
//        System.out.printf("%s: %s\n", event.getMessageAuthor(), event.getMessageContent());
        for (MessageCreatedListener listener : messageCreatedListenerRegistry) listener.run(event);

        String message = event.getMessageContent();
        // check if it's a command
        if (!message.startsWith(SystemBot.prefix)) return;
        // get the arguments for the command
        String[] args = message.split(" ");
        int commandLength = args[0].length();
        args[0] = args[0].substring(SystemBot.prefix.length());
        // command name
        String name = args[0];
        if (!isCommand(name)) return;

        // the message without the command name and the prefix
        String newMessage = null;
        if (args.length > 1) newMessage = message.substring(commandLength + 1);

        if (event.getChannel().getId() == 897837888736219167L) {
            if (name.equals("accept")) {
                runCommand(name, new Context(event, args, newMessage));
                return;
            }
        }

        // get the command to check the category
        Command command = registry.get(name.toLowerCase());
        // you can only run not public commands in #staff-bot and #admin-bot
        if (!Objects.equals(command.category, "public")) {
            if (event.getChannel().getId() != Long.parseLong(staff_bot_channel_id)
                    && event.getChannel().getId() != Long.parseLong(admin_bot_channel_id)) {
                EmbedBuilder eb = new EmbedBuilder()
                        .setTitle("Wrong Channel!")
                        .setDescription("Please use <#" + staff_bot_channel.getIdAsString() + "> or <#" + admin_bot_channel.getIdAsString() + ">! ")
                        .setColor(Utils.Pals.error);
                event.getChannel().sendMessage(eb);
                return;
            }
        }
        // check if the command gets executed in the #bot channel
        if (event.getChannel().getId() != Long.parseLong(bot_channel_id)
                && event.getChannel().getId() != Long.parseLong(staff_bot_channel_id)
                && event.getChannel().getId() != Long.parseLong(admin_bot_channel_id)) {
            EmbedBuilder eb = new EmbedBuilder()
                    .setTitle("Wrong Channel!")
                    .setDescription("Please use <#" + bot_channel.getIdAsString() + ">! ")
                    .setColor(Utils.Pals.error);
            event.getChannel().sendMessage(eb);
            return;
        }

        // run the command
        runCommand(name, new Context(event, args, newMessage));
    }

    /**
     * Run a command
     *
     * @param name the name of the command
     * @param ctx  the context of the command
     */
    public void runCommand(String name, Context ctx) {
        Command command = registry.get(name.toLowerCase());
        if (command == null) {
            return;
        }
        if (!command.hasPermission(ctx)) {
            EmbedBuilder eb = new EmbedBuilder()
                    .setTitle("No permissions!")
                    .setDescription("You need higher permissions to execute this command.");
            ctx.channel.sendMessage(eb);
            return;
        }
        try {
            command.run(ctx);
        } catch (Exception error) {
            System.out.println(Arrays.toString(error.getStackTrace()));
            System.out.println(error.getMessage());
            try {
                EmbedBuilder eb = new EmbedBuilder()
                        .setTitle("There was an error executing this command: " + name + "!")
                        .setDescription(error.getStackTrace()[0].toString())
                        .setColor(Color.decode("#ff0000"));
                assert error_log_channel != null;
                error_log_channel.sendMessage(eb);
            } catch (Exception error2) {
                System.out.println("There was an error at outputting the error!!!");
                System.out.println(error2.toString());
            }
        }
    }

    /**
     * Get a command by name
     *
     * @param name the requested command name
     * @return the command
     */
    public Command getCommand(String name) {
        return registry.get(name.toLowerCase());
    }

    /**
     * Get all commands in the registry
     *
     * @return all commands
     */
    public Collection<Command> getAllCommands() {
        return registry.values();
    }

    /**
     * Check if a command exists in the registry
     *
     * @param name command name
     * @return return true if there is a command, else return false
     */
    public boolean isCommand(String name) {
        return registry.containsKey(name.toLowerCase());
    }
}