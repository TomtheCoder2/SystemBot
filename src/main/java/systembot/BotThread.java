package systembot;

import org.javacord.api.DiscordApi;
import org.json.JSONObject;
import systembot.discordcommands.DiscordCommands;

public class BotThread extends Thread {
    public DiscordApi api;
    private final Thread mt;
    private JSONObject data;
    public DiscordCommands commandHandler = new DiscordCommands();
    /**
     * start the bot thread
     * @param api the discordApi to operate with
     * @param mt the main Thread
     * @param data the data from settings.json
     * */
    public BotThread(DiscordApi api, Thread mt, JSONObject data) {
        this.api = api; //new DiscordApiBuilder().setToken(data.get(0)).login().join();
        this.mt = mt;
        this.data = data;

        // register commands
        this.api.addMessageCreateListener(commandHandler);
        new ComCommands().registerCommands(commandHandler);
        new ServerCommands(data).registerCommands(commandHandler);
        //new MessageCreatedListeners(data).registerListeners(commandHandler);
    }

    public void run(){
        while (this.mt.isAlive()){}
//        api.disconnect();
    }
}
