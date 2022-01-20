package systembot;


import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.json.JSONObject;
import systembot.discordcommands.Command;
import systembot.discordcommands.Context;
import systembot.discordcommands.DiscordCommands;
import systembot.discordcommands.RoleRestrictedCommand;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

import static systembot.SystemBot.getTextChannel;
import static systembot.SystemBot.suggestion_channel_id;


public class ServerCommands {
    private final TextChannel error_log_channel = getTextChannel("891677596117504020");
    private final JSONObject data;

    public ServerCommands(JSONObject data) {
        this.data = data;
    }

    public void registerCommands(DiscordCommands handler) {
        if (data.has("administrator_roleid")) {
            String adminRole = data.getString("administrator_roleid");
            String devRole = data.getString("dev_roleid");

            handler.registerCommand(new RoleRestrictedCommand("accept") {
                {
                    help = "Accept a form and send the congrats message";
                    role = adminRole;
                    usage = "<id>";
                    category = "management";
                }

                @Override
                public void run(Context ctx) {
                    System.out.println(SystemBot.api.getUserById(ctx.args[1]));
                    SystemBot.api.getUserById(ctx.args[1]).join().sendMessage(new EmbedBuilder()
                            .setTitle("Congratulations!")
                            .setDescription("Your Moderator application got accepted.\n" +
                                    "Please ping or dm a Marshal (Admin) on the Phoenix-Network discord server")
                            .setColor(new Color(0x00ff00)));
                    ctx.channel.sendMessage(new EmbedBuilder()
                            .setTitle("Successfully send message!")
                            .setColor(new Color(0x00ff00)));
                }
            });

            handler.registerCommand(new RoleRestrictedCommand("reject") {
                {
                    help = "Reject a form and send the reject message";
                    role = adminRole;
                    usage = "<id> <reason>";
                    category = "management";
                }

                @Override
                public void run(Context ctx) {
                    if (ctx.args.length < 2) {
                        ctx.channel.sendMessage(new EmbedBuilder()
                                .setTitle("Not enough Arguments")
                                .setColor(new Color(0xff0000)));
                        return;
                    }
                    System.out.println(SystemBot.api.getUserById(ctx.args[1]));
                    SystemBot.api.getUserById(ctx.args[1]).join().sendMessage(new EmbedBuilder()
                            .setTitle("Mod application denied!")
                            .setDescription("Your Moderator application got denied.\n")
                            .addField("Reason", ctx.message.split(" ", 2)[1])
                            .setColor(new Color(0xff0000)));
                    ctx.channel.sendMessage(new EmbedBuilder()
                            .setTitle("Successfully send message!")
                            .setColor(new Color(0x00ff00)));
                }
            });

            handler.registerCommand(new RoleRestrictedCommand("start") {
                {
                    help = "Start the v7-bot, v7-mod-bot or the buggy music bot";
                    role = devRole;
                    usage = "<v7, v7-mod, music> [map name] [mode]";
                    category = "management";
                }

                public void run(Context ctx) {
                    String name = "";
                    String mode = "";
                    if (ctx.args.length < 2) {
                        EmbedBuilder eb = new EmbedBuilder()
                                .setTitle("Please select a bot first!")
                                .setColor(Color.decode("#00ffff"));
                        ctx.channel.sendMessage(eb);
                        return;
                    }
                    if (ctx.args.length >= 3) {
                        name = ctx.args[2];
                    }
                    if (ctx.args.length == 4) {
                        mode = ctx.args[3];
                    }
                    try {
                        String command = "";
                        switch (ctx.args[1]) {
                            case "v7" -> {
//                                command = "bash -c \"screen -dmr v7 -X stuff $'java -jar server-release.jar\\nhost\\n'\"";
                                command = "sh shellScripts/start/v7.sh " + name + " " + mode;
                            }
                            case "v7-mod" -> {
//                                command = "bash -c \"screen -dmr v7-mod -X stuff $'java -jar server-release.jar\\nhost\\n'\"";
                                command = "sh shellScripts/start/v7-mod.sh " + name + " " + mode;
                            }
                            case "music" -> {
//                                command = "bash -c \"screen -dmr MusicBot -X stuff $'node index.js\\n'\"";
                                command = "sh shellScripts/start/MusicBot.sh";
                            }
                            default -> {
                                EmbedBuilder eb = new EmbedBuilder()
                                        .setTitle("Please select a bot first!")
                                        .setColor(Color.decode("#00ffff"));
                                ctx.channel.sendMessage(eb);
                                return;
                            }
                        }
                        execute(ctx, command, "Started");
                    } catch (Exception error) {
                        EmbedBuilder eb = new EmbedBuilder()
                                .setTitle("There was an error executing this command: " + name + "!")
                                .setDescription(error.getStackTrace()[0].toString())
                                .setColor(Color.decode("#ff0000"));
                        ctx.channel.sendMessage(eb);
                        assert error_log_channel != null;
                        error_log_channel.sendMessage(eb);
                    }
                }
            });

            handler.registerCommand(new RoleRestrictedCommand("stop") {
                {
                    help = "Stop the v7-bot, v7-mod-bot or the buggy music bot";
                    role = devRole;
                    usage = "<v7, v7-mod, music>";
                    category = "management";
                }

                public void run(Context ctx) {
                    try {
                        String command = "";
                        switch (ctx.args[1]) {
                            case "v7" -> {
                                command = "sh shellScripts/stop/v7.sh";
                            }
                            case "v7-mod" -> {
                                command = "sh shellScripts/stop/v7-mod.sh";
                            }
                            case "music" -> {
                                command = "sh shellScripts/stop/MusicBot.sh";
                            }
                            default -> {
                                EmbedBuilder eb = new EmbedBuilder()
                                        .setTitle("Please select a bot first!")
                                        .setColor(Color.decode("#00ffff"));
                                ctx.channel.sendMessage(eb);
                                return;
                            }
                        }
                        execute(ctx, command, "Stopped");
                    } catch (Exception error) {
                        EmbedBuilder eb = new EmbedBuilder()
                                .setTitle("There was an error executing this command: " + name + "!")
                                .setDescription(error.getStackTrace()[0].toString())
                                .setColor(Color.decode("#ff0000"));
                        ctx.channel.sendMessage(eb);
                        assert error_log_channel != null;
                        error_log_channel.sendMessage(eb);
                    }
                }
            });

            handler.registerCommand(new RoleRestrictedCommand("restart") {
                {
                    help = "Restart the v7-bot, v7-mod-bot or the buggy music bot";
                    role = devRole;
                    usage = "<v7, v7-mod, music> [warn players (true|false)]";
                    category = "management";
                }

                public void run(Context ctx) {
                    try {
                        String command = "";
                        switch (ctx.args[1]) {
                            case "v7" -> {
                                command = "sh shellScripts/restart/v7.sh";
                                if (ctx.args.length > 2) {
                                    if (Objects.equals(ctx.args[2], "true")) {
                                        command = "sh shellScripts/restart/v7.alert.sh";
                                    }
                                }
                            }
                            case "v7-mod" -> {
                                command = "sh shellScripts/restart/v7-mod.sh";
                                if (ctx.args.length > 2) {
                                    if (Objects.equals(ctx.args[2], "true")) {
                                        command = "sh shellScripts/restart/v7-mod.alert.sh";
                                    }
                                }
                            }
                            case "music" -> {
                                command = "sh shellScripts/restart/MusicBot.sh";
                            }
                            default -> {
                                EmbedBuilder eb = new EmbedBuilder()
                                        .setTitle("Please select a bot first!")
                                        .setColor(Color.decode("#00ffff"));
                                ctx.channel.sendMessage(eb);
                                return;
                            }
                        }
                        execute(ctx, command, "Restarted");
                    } catch (Exception error) {
                        EmbedBuilder eb = new EmbedBuilder()
                                .setTitle("There was an error executing this command: " + name + "!")
                                .setDescription(error.getStackTrace()[0].toString())
                                .setColor(Color.decode("#ff0000"));
                        ctx.channel.sendMessage(eb);
                        assert error_log_channel != null;
                        error_log_channel.sendMessage(eb);
                    }
                }
            });
        }


        handler.registerCommand(new Command("ping") {
            {
                help = "Reply with Pong!";
                hidden = true;
            }

            public void run(Context ctx) {
                EmbedBuilder eb = new EmbedBuilder()
                        .setTitle("Pong!")
                        .setColor(Color.decode("#00ff00"));
                ctx.channel.sendMessage(eb);
            }
        });
        handler.registerCommand(new Command("shell") {
            {
                help = "execute a command in the sell";
                hidden = true;
            }

            public void run(Context ctx) {
                // screen -r v7 -X stuff $'java -jar server-release.jar\nhost\n'
                // screen -r v7 -X stuff $'^C'
                try {
                    Process process = Runtime.getRuntime().exec("ping www.stackabuse.com");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (Exception error) {
                    EmbedBuilder eb = new EmbedBuilder()
                            .setTitle("There was an error executing this command: " + name + "!")
                            .setDescription(error.getStackTrace()[0].toString())
                            .setColor(Color.decode("#ff0000"));
                    assert error_log_channel != null;
                    error_log_channel.sendMessage(eb);
                }
            }
        });

        handler.registerCommand(new Command("suggest") {
            {
                help = "Suggest a feature or something else for this server.";
            }

            @Override
            public void run(Context ctx) {
                EmbedBuilder eb = new EmbedBuilder();
                if (ctx.args.length > 1) {
                    eb.setTitle("New Suggestion!");
                    eb.setDescription(ctx.message.split(" ", 1)[0]);
                    eb.addField("by", "<@" + ctx.author.getIdAsString() + ">");
                    eb.setColor(Color.decode("#ffff00"));
                    TextChannel suggestion_channel = getTextChannel(suggestion_channel_id);
                    assert suggestion_channel != null;
                    suggestion_channel.sendMessage(eb);
                    EmbedBuilder respond = new EmbedBuilder()
                            .setTitle("Sent your suggestion in " + suggestion_channel.getType().name())
                            .setColor(Color.decode("#00ff00"));
                    ctx.channel.sendMessage(respond);
                } else {
                    eb.setTitle("Please suggest something.");
                    eb.setColor(Color.decode("#ff0000"));
                    ctx.channel.sendMessage(eb);
                }
            }
        });

    }

    private void execute(Context ctx, String command, String commandName) throws IOException {
        Process process = Runtime.getRuntime().exec(command);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = "";
//        StringBuilder output = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
//            output.append(line);
        }
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle(commandName + " the Bot " + ctx.args[1] + " successfully!")
                .setColor(Color.decode("#00ff00"));
        ctx.channel.sendMessage(eb);
    }
}