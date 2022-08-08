package systembot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.ServerThreadChannelBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.json.JSONObject;
import systembot.discordcommands.Command;
import systembot.discordcommands.Context;
import systembot.discordcommands.DiscordCommands;
import systembot.discordcommands.RoleRestrictedCommand;
import systembot.website.entity.Staff;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import static arc.util.Log.debug;
import static systembot.SystemBot.*;
import static systembot.Utils.*;

public class ServerCommands {
    private final TextChannel error_log_channel = getTextChannel("891677596117504020");
    private final JSONObject data;

    public ServerCommands(JSONObject data) {
        this.data = data;
    }

    public void registerCommands(DiscordCommands handler) {
        if (data.has("administrator_roleid")) {
            String adminRole = data.getString("administrator_roleid");
            String devRole = dev_roleId;

            handler.registerCommand(new RoleRestrictedCommand("staff") {
                {
                    role = adminRole;
                    help = "Manage staff members on the website.";
                    usage = "<new|update|delete|list> [name, id, userId (discordId) or discord name (with tag eg Nautilus#0100, currently doesnt work with spaces)] [rank] [description...]";
                    category = "management";
                }

                @Override
                public void run(Context ctx) {
                    String op = ctx.args[1];
                    EmbedBuilder eb = new EmbedBuilder();
                    switch (op.toLowerCase().trim()) {
                        case "list" -> {
                            for (Staff staff : staffRepository.findAll()) {
                                eb.addField(staff.getName(), staff.getDescription() +
                                        "\n\nRank: " + staff.getRank() + "\nid: `" + staff.getId() + "`\nUser Id: `" + staff.getUserId() + "`", true);
                            }
                            eb.setTitle("All current staff members:");
                        }
                        case "delete" -> {
                            Staff target = getStaffByString(ctx.args[2], ctx);
                            if (target != null) {
                                staffRepository.delete(target);
                            } else {
                                return;
                            }
                            eb.setTitle("Successfully deleted staff " + target.getName() + "!")
                                    .setColor(new Color(0x00ff00));
                        }
                        case "update" -> {
                            Staff target = getStaffByString(ctx.args[2], ctx);
                            if (target == null) return;
                            String rank = ctx.args[3];
                            String desc = ctx.message.split(" ", 4)[3];
                            eb.setTitle("Successfully updated " + target.getName() + "!")
                                    .setColor(new Color(0x00ff00))
                                    .addField("Old data", "**Description:** \n" + target.getDescription() + "\n\n**Rank:**\n" + target.getRank(), true);
                            target.setRank(rank);
                            target.setDescription(desc);
                            staffRepository.save(target);
                            eb.addField("New data", "**Description:** \n" + target.getDescription() + "\n\n**Rank:**\n" + target.getRank(), true);
                        }
                        case "new" -> {
                            String id = ctx.args[2];
                            String rank = ctx.args[3];
                            String desc = ctx.message.split(" ", 4)[3];
                            Staff newStaff;
                            try {
                                newStaff = new Staff(api.getUserById(id).get(), desc, rank);
                            } catch (Exception e) {
                                ctx.channel.sendMessage(new EmbedBuilder().setTitle("Could not find user with id `" + id + "`!")
                                        .setColor(new Color(0xff0000)));
                                return;
                            }
                            staffRepository.save(newStaff);
                            eb.setTitle("Successfully saved " + newStaff.getName() + "!")
                                    .setColor(new Color(0x00ff00))
                                    .addField("Data", "**Description:** \n" + newStaff.getDescription() + "\n\n**Rank:**\n" + newStaff.getRank(), true);
                        }
                    }
                    ctx.channel.sendMessage(eb);
                }
            });

            handler.registerCommand(new RoleRestrictedCommand("alloc") {
                {
                    role = adminRole;
                    hidden = true;
                }

                @Override
                public void run(Context ctx) {
                    ArrayList<byte[]> memList = new ArrayList<>();
                    for (int j = 0; j < 1024; j++) {
                        byte[] mem = new byte[16 * 1024 * 1204 * 64];
                        memList.add(mem);
                        for (int i = 0; i < mem.length; i++) {
//                        System.out.print(m);
                            mem[i]++;
                        }
                        System.out.println(j);
                    }
                    for (byte[] mem : memList) {
                        for (int i = 0; i < mem.length; i++) {
                            mem[i]++;
                        }
//                        System.out.println(j);
                    }
                    System.out.println("finished");

                }
            });

            handler.registerCommand(new RoleRestrictedCommand("accept") {
                {
                    help = "Accept a form and send the congrats message";
                    role = adminRole;
                    usage = "<mod|map> <id>";
                    category = "management";
                    aliases.add("a");
                }

                @Override
                public void run(Context ctx) {
                    String app = "";
                    if (ctx.args[1].equalsIgnoreCase("mod")) {
                        app = "Moderator";
                    } else {
                        app = "Map Reviewer";
                    }
                    SystemBot.api.getUserById(ctx.args[2]).join().sendMessage(new EmbedBuilder()
                            .setTitle("Congratulations!")
                            .setDescription("Your **" + app + "** application got accepted.\n" +
                                    "Please ping or dm a Marshal (Admin) on the Phoenix-Network discord server")
                            .setColor(new Color(0x00ff00))).join();
                    ctx.channel.sendMessage(new EmbedBuilder()
                            .setTitle("Successfully sent message!")
                            .setColor(new Color(0x00ff00)));
                }
            });

            handler.registerCommand(new RoleRestrictedCommand("reject") {
                {
                    help = "Reject a form and send the reject message";
                    role = adminRole;
                    usage = "<mod|map> <id> <reason>";
                    category = "management";
                    aliases.add("r");
                }

                @Override
                public void run(Context ctx) {
                    if (ctx.args.length < 3) {
                        ctx.channel.sendMessage(new EmbedBuilder()
                                .setTitle("Not enough Arguments")
                                .setColor(new Color(0xff0000)));
                        return;
                    }
                    String app = "";
                    if (ctx.args[1].equalsIgnoreCase("mod")) {
                        app = "Moderator";
                    } else {
                        app = "Map Reviewer";
                    }
                    System.out.println(SystemBot.api.getUserById(ctx.args[2]));
                    SystemBot.api.getUserById(ctx.args[2]).join().sendMessage(new EmbedBuilder()
                            .setTitle(app + " application denied!")
                            .setDescription("Your " + app + " application got denied.\n")
                            .addField("Reason", ctx.message.split(" ").length > 2 ? ctx.message.split(" ", 3)[2] :
                                    "No reason given", false)
                            .setColor(new Color(0xff0000))).join();
                    ctx.channel.sendMessage(new EmbedBuilder()
                            .setTitle("Successfully sent message!")
                            .setColor(new Color(0x00ff00)));
                }
            });

            // server name, screen name
//            636385.TheRealFast      (06/04/2022 10:19:48 AM)        (Detached)
//                    2185902.be      (05/15/2022 04:56:43 PM)        (Detached)
//                    196729.mod      (04/06/2022 05:40:32 PM)        (Detached)
//                    2203.pvp-v7     (04/03/2022 08:22:12 PM)        (Detached)
//                    2115.v7         (04/03/2022 08:21:09 PM)        (Detached)
//                    1906.SystemBot  (04/03/2022 08:16:31 PM)        (Detached)
            HashMap<String, String> servers = new HashMap();
            servers.put("v7", "v7");
            servers.put("mod", "mod");
            servers.put("TheRealFast", "TheRealFast");
            servers.put("be", "be");
            servers.put("pvp", "pvp-v7");
            StringBuilder allServerNames = new StringBuilder();
            for (Map.Entry<String, String> s : servers.entrySet()) {
                allServerNames.append(", ").append(s.getKey());
            }
            String allServers = allServerNames.toString().replaceFirst(", ", "");

            handler.registerCommand(new RoleRestrictedCommand("stop") {
                {
                    help = "Stop a server";
                    role = restart_roleId;
                    usage = "<" + allServers + ">";
                    category = "management";
                }

                public void run(Context ctx) {
                    if (ctx.args.length < 2) {
                        EmbedBuilder eb = new EmbedBuilder()
                                .setTitle("Please select a bot first!")
                                .setColor(Color.decode("#00ffff"));
                        ctx.channel.sendMessage(eb);
                        return;
                    }
                    name = ctx.args[1];
                    try {
                        if (checkIfServerExists(ctx, name, servers, allServers)) return;
                        String command = "screen -S " + servers.get(name) + " -X stuff '^C\\r';";
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
                    help = "Restart a server.";
                    role = restart_roleId;
                    usage = "<" + allServers + "> [map name] [mode]";
                    category = "management";
                    aliases.add("s");
                    aliases.add("r");
                }

                public void run(Context ctx) {
                    String name = "";
                    if (ctx.args.length < 2) {
                        EmbedBuilder eb = new EmbedBuilder()
                                .setTitle("Please select a bot first!")
                                .setColor(Color.decode("#00ffff"));
                        ctx.channel.sendMessage(eb);
                        return;
                    }
                    name = ctx.args[1];
                    try {
                        String command = "";
                        if (checkIfServerExists(ctx, name, servers, allServers)) return;
                        command = "screen -S " + servers.get(name) + " -X stuff 'say [scarlet]Server restart in 10 Seconds! All progress will be saved.\\rsave 1\\r';";
                        command += "sleep 10;";
                        command += "screen -S " + servers.get(name) + " -X stuff '^C\\r';";
                        command += "screen -S " + servers.get(name) + " -X stuff 'ls\\rsh autoRestart.sh\\rstop\\rload 1\\r';";
                        ctx.channel.sendMessage(new EmbedBuilder().setTitle("Restarting...").setDescription("**Command: **\n" + command));
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

            handler.registerCommand(new RoleRestrictedCommand("reload") {
                {
                    help = "Reload all contents for the website.";
                    role = devRole;
                    usage = "";
                    category = "management";
                    aliases.add("r");
                }

                public void run(Context ctx) {
                    try {
                        for (Staff staff : staffRepository.findAll()) {
                            staff.setAvatarUrl(SystemBot.api.getUserById(staff.getUserId()).get().getAvatar().getUrl().toString());
                            staffRepository.save(staff);
                        }
                    } catch (Exception e) {
                        ctx.channel.sendMessage(new EmbedBuilder()
                                .setTitle("Error")
                                .setDescription("There was an error while updating the profile pictures: " + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()))
                                .setColor(new Color(0xff0000)));
                    }
                    GoodWindowsExec.execute(new String[]{"cd " + webRoot + "&& git pull"}, ctx);
                    ctx.channel.sendMessage(new EmbedBuilder()
                            .setTitle("Success")
                            .setDescription("Successfully updated content of the website.")
                            .setColor(new Color(0x00ff00)));
                    GoodWindowsExec.execute(new String[]{"cd ~/logs/phoenix_logs && sh saveLogs.sh"}, ctx);
                    ctx.channel.sendMessage(new EmbedBuilder()
                            .setTitle("Success")
                            .setDescription("Successfully pushed all logs to the github repository.")
                            .setColor(new Color(0x00ff00)));
                }
            });
        }

        if (moderator_roleId != null) {
            handler.registerCommand(new RoleRestrictedCommand("say") {
                {
                    help = "Say something as the bot.";
                    usage = "<channel> <message>";
                    role = moderator_roleId;
                    category = "management";
                }

                @Override
                public void run(Context ctx) {
                    if (!Objects.equals(ctx.args[1], "edit")) {
                        String channelName = ctx.args[1].replaceAll("<#", "").replaceAll(">", "");
                        String message = ctx.message.split(" ", 2)[1];
                        TextChannel channel = null;
                        if (onlyDigits(channelName)) {
                            channel = getTextChannel(channelName);
                        } else {
                            Collection<Channel> channels = api.getChannelsByName(channelName);
                            if (!channels.isEmpty()) {
                                channel = getTextChannel(String.valueOf(channels.stream().toList().get(0).getId()));
                            }
                        }
                        if (channel == null) {
                            ctx.channel.sendMessage(new EmbedBuilder()
                                    .setTitle("Error")
                                    .setDescription("Could not find text channel " + channelName)
                                    .setColor(new Color(0xff0000)));
                            return;
                        }
                        if (!channel.canSee(ctx.author.asUser().get()) || !channel.canWrite(ctx.author.asUser().get())) {
                            ctx.channel.sendMessage(new EmbedBuilder()
                                    .setTitle("Error")
                                    .setDescription("You cant sent messages in <#" + channel.getIdAsString() + ">")
                                    .setColor(new Color(0xff0000)));
                            return;
                        }
                        if (!message.startsWith("```") && !message.startsWith("```json")) {
                            channel.sendMessage(new EmbedBuilder().setTitle(message));
                        } else {
                            EmbedBuilder eb;
                            try {
                                message = message.replaceAll("```json", "").replaceAll("```", "");
                                Gson gson = new GsonBuilder()
                                        .setLenient()
                                        .create();
//                            debug(message);
                                JsonElement element = gson.fromJson(message, JsonElement.class);
                                JsonObject jsonObj = element.getAsJsonObject();

                                eb = jsonToEmbed(jsonObj);
                            } catch (Exception e) {
                                e.printStackTrace();
                                ctx.channel.sendMessage(new EmbedBuilder()
                                        .setTitle("Error")
                                        .setColor(new Color(0xff0000))
                                        .setDescription("There was an error while parsing the json object: \n" + e.getMessage()));
                                return;
                            }
                            debug(eb.toString());
                            try {
                                channel.sendMessage(eb).get();
                            } catch (Exception e) {
                                e.printStackTrace();
                                ctx.channel.sendMessage(new EmbedBuilder()
                                        .setTitle("Error")
                                        .setColor(new Color(0xff0000))
                                        .setDescription("There was an error while sending the message: \n" + e.getMessage()));
                                return;
                            }
                        }
                        ctx.channel.sendMessage(new EmbedBuilder()
                                .setTitle("Successfully sent Message")
                                .setDescription("Successfully sent message in channel: <#" + channel.getId() + ">")
                                .setColor(new Color(0x00ff00)));
                    } else {
                        try {
                            Message msg = api.getMessageById(ctx.args[2], getTextChannel(ctx.args[3].replaceAll("<#", "").replaceAll(">", ""))).get();
                        } catch (Exception e) {
                            ctx.channel.sendMessage(new EmbedBuilder().setTitle("Couldn't find this message " + ctx.args[2]).setColor(new Color(0xff0000)));
                        }
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
        handler.registerCommand(new RoleRestrictedCommand("shell") {
            {
                help = "Execute a command in the sell";
                hidden = true;
                role = moderator_roleId;
            }

            public void run(Context ctx) {
                // screen -r v7 -X stuff $'sh autoRestart.sh\nhost\n'
                // screen -r v7 -X stuff $'^C'
                String command = ctx.message;
//                command = "screen -S v7 -X stuff 'echo Hello\\r'";
                ctx.channel.sendMessage(new EmbedBuilder().setTitle("Executing Command:").setDescription("**Command(s):** \n" + command));
                try {
                    ProcessBuilder processBuilder = new ProcessBuilder();
                    processBuilder.command("bash", "-c", command);
//                    Process process = Runtime.getRuntime().exec(command);
                    Process process = processBuilder.start();
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
                    ctx.channel.sendMessage(eb);
                    error.printStackTrace();
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
                    try {
                        Message msg = suggestion_channel.sendMessage(eb).get();
                        msg.addReactions("\u2705", "\u274C").get();
                        new ServerThreadChannelBuilder(msg, ctx.author.getDisplayName() + "#" + ctx.author.getDiscriminator().get()).create().join();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    EmbedBuilder respond = new EmbedBuilder()
                            .setTitle("New Suggestion!")
                            .setDescription("<#" + suggestion_channel.getIdAsString() + ">")
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

    private boolean checkIfServerExists(Context ctx, String name, HashMap<String, String> servers, String allServers) {
        if (Objects.equals(name, "")) {
            ctx.channel.sendMessage(new EmbedBuilder()
                    .setTitle("Error").setDescription("Please provide a server")
                    .setColor(new Color(0xff0000)));
            return true;
        }
        if (!servers.containsKey(name)) {
            ctx.channel.sendMessage(new EmbedBuilder()
                    .setTitle("Error").setDescription("Please provide a valid server\nValid Servers are: " + allServers)
                    .setColor(new Color(0xff0000)));
            return true;
        }
        return false;
    }

    private void execute(Context ctx, String command, String commandName) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bash", "-c", command);
        Process process = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = "";
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle(commandName + " the server " + ctx.args[1] + " successfully!")
                .setColor(Color.decode("#00ff00"));
        ctx.channel.sendMessage(eb);
    }
}