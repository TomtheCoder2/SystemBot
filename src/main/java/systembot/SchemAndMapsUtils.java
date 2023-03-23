package systembot;

import arc.files.Fi;
import arc.struct.Seq;
import arc.struct.StringMap;
import arc.util.Strings;
import arc.util.io.CounterInputStream;
import mindustry.game.Schematic;
import mindustry.game.Schematics;
import mindustry.io.SaveIO;
import mindustry.io.SaveVersion;
import mindustry.type.ItemSeq;
import mindustry.type.ItemStack;
import org.javacord.api.entity.emoji.KnownCustomEmoji;
import org.javacord.api.entity.message.MessageAttachment;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import systembot.discordcommands.Context;
import systembot.discordcommands.DiscordCommands;
import systembot.discordcommands.MessageCreatedListener;
import systembot.mindServ.ContentHandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.zip.InflaterInputStream;

import static arc.util.Log.debug;
import static systembot.SystemBot.*;
import static systembot.mindServ.MindServ.contentHandler;

public class SchemAndMapsUtils {
    /**
     * Send a schematic to channel of the ctx
     */
    public static void sendSchem(Schematic schem, Context ctx) {
        ItemSeq req = schem.requirements();
        EmbedBuilder eb = new EmbedBuilder()
                .setColor(new Color(0x00ffff))
                .setAuthor(ctx.author)
                .setTitle(schem.name());
        StringBuilder sb = new StringBuilder("");
        for (ItemStack item : req) {
            Collection<KnownCustomEmoji> emojis = api.getCustomEmojisByNameIgnoreCase(item.item.name.replaceAll("-", ""));
//            eb.addField(emoijs.iterator().next().getMentionTag(), String.valueOf(item.amount), true);
            sb.append(emojis.iterator().next().getMentionTag()).append(item.amount).append("    ");
        }
        eb.setDescription(schem.description());
        eb.addField("**Requirements:**", sb.toString());
        // power emojis
        String powerPos = api.getCustomEmojisByNameIgnoreCase("power_pos").iterator().next().getMentionTag();
        String powerNeg = api.getCustomEmojisByNameIgnoreCase("power_neg").iterator().next().getMentionTag();
        eb.addField("**Power:**", powerPos + "+" + schem.powerProduction() + "    " +
                powerNeg + "-" + schem.powerConsumption() + "     \n" +
                powerPos + "-" + powerNeg + (schem.powerProduction() - schem.powerConsumption()));

        // preview schem
        BufferedImage visualSchem;
        File imageFile;
        Fi schemFile;
        try {
            visualSchem = contentHandler.previewSchematic(schem);
            imageFile = new File("temp/" + "image_" + schem.name().replaceAll("[^a-zA-Z0-9\\.\\-]", "_") + ".png");
            ImageIO.write(visualSchem, "png", imageFile);
            // crate the .msch file
            schemFile = new Fi("temp/" + "file_" + schem.name().replaceAll("[^a-zA-Z0-9\\.\\-]", "_") + ".msch");
            Schematics.write(schem, schemFile);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        eb.setImage("attachment://" + imageFile.getName());
        MessageBuilder mb = new MessageBuilder();
        mb.addEmbed(eb);
        mb.addAttachment(imageFile);
        mb.addAttachment(schemFile.file());
        mb.send(ctx.channel);
    }

    /**
     * Send a map to channel of the ctx
     */
    public static void sendMap(ContentHandler.Map map, Context ctx, InputStream data) {
        EmbedBuilder eb = new EmbedBuilder()
                .setColor(new Color(0x00ffff))
                .setAuthor(ctx.author)
                .setTitle(map.name);
        eb.setDescription(map.description);

        // preview map
        File imageFile;
        Fi schemFile;
        try {
            imageFile = new File("temp/" + "image_" + map.name.replaceAll("[^a-zA-Z0-9\\.\\-]", "_") + ".png");
            ImageIO.write(map.image, "png", imageFile);
            // crate the .msch file
            schemFile = new Fi("temp/" + "file_" + map.name.replaceAll("[^a-zA-Z0-9\\.\\-]", "_") + ".msch");
            schemFile.writeBytes(data.readAllBytes());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
//        eb.setImage(ContentServer.renderMap(map));
        MessageBuilder mb = new MessageBuilder();
        mb.addEmbed(eb);
        mb.addAttachment(imageFile);
        mb.addAttachment(schemFile.file());
        mb.send(ctx.channel);
    }


    /**
     * check if the message starts with the schematic prefix
     */
    public static boolean checkIfSchem(MessageCreateEvent event) {
        // check if it's a schem encoded in base64
        String message = event.getMessageContent();
        if (event.getMessageContent().startsWith("bXNjaA")) {
            try {
                debug("send schem");
                sendSchem(contentHandler.parseSchematic(message), new Context(event, null, null));
                event.deleteMessage();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // ========= check if it's a schem file ============
        // download all files
        Seq<MessageAttachment> ml = new Seq<>();
        Seq<MessageAttachment> txtData = new Seq<>();
        for (MessageAttachment ma : event.getMessageAttachments()) {
            if ((ma.getFileName().split("\\.", 2)[1].trim().equals("msch")) && !event.getMessageAuthor().isBotUser()) { // check if its a .msch file
                ml.add(ma);
            }
            if ((ma.getFileName().split("\\.", 2)[1].trim().equals("txt")) && !event.getMessageAuthor().isBotUser()) { // check if its a .txt file
                txtData.add(ma);
            }
        }

        if (ml.size > 0) {
            CompletableFuture<byte[]> cf = ml.get(0).downloadAsByteArray();
            try {
                byte[] data = cf.get();
                Schematic schem = Schematics.read(new ByteArrayInputStream(data));
                sendSchem(schem, new Context(event, null, null));
                return true;
            } catch (Exception e) {
                event.getChannel().sendMessage(new EmbedBuilder().setTitle(e.getMessage()).setColor(new Color(0xff0000)));
                e.printStackTrace();
            }
        }

        if (txtData.size > 0) {
            CompletableFuture<byte[]> cf = txtData.get(0).downloadAsByteArray();
            try {
                byte[] data = cf.get();
                String base64Encoded = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data)))
                        .lines().parallel().collect(Collectors.joining("\n"));
                Schematic schem = contentHandler.parseSchematic(base64Encoded);
                sendSchem(schem, new Context(event, null, null));
                event.deleteMessage();
                return true;
            } catch (Exception e) {
                event.getChannel().sendMessage(new EmbedBuilder().setTitle(e.getMessage()).setColor(new Color(0xff0000)));
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean checkIfMap(MessageCreateEvent event) {
        // ========= check if it's a map file ============
        // download all files
        Seq<MessageAttachment> ml = new Seq<>();
        Seq<MessageAttachment> txtData = new Seq<>();
        for (MessageAttachment ma : event.getMessageAttachments()) {
            if (ma.getFileName().split("\\.", 2)[1].trim().equals("msav") && !event.getMessageAuthor().isBotUser()) { // check if its a .msav file
                ml.add(ma);
            }
        }


        if (ml.size > 0) {
            for (MessageAttachment ma : ml) {
                byte[] data = ma.downloadAsByteArray().join();
                if (!SaveIO.isSaveValid(new DataInputStream(new InflaterInputStream(new ByteArrayInputStream(data))))) {
                    continue;
                }
//            try {
//                InputStream data =  ml.get(0).downloadAsInputStream();
////                Log.info("file size: @", data.readAllBytes().length);
//                ContentHandler.Map map = contentHandler.readMap(data);
//                sendMap(map, new Context(event, null, null), data);
//                return true;
//            } catch (Exception e) {
//                event.getChannel().sendMessage(new EmbedBuilder().setTitle(e.getMessage()).setColor(new Color(0xff0000)));
//                e.printStackTrace();
//            }
                StringMap meta;
                try {
                    CounterInputStream counter = new CounterInputStream(new InflaterInputStream(new ByteArrayInputStream(data)));
                    DataInput stream = new DataInputStream(counter);
                    SaveIO.readHeader(stream);

                    int version = stream.readInt();
                    SaveVersion ver = SaveIO.getSaveWriter(version);
                    StringMap[] metaOut = {null};
                    ver.region("meta", stream, counter, in -> metaOut[0] = ver.readStringMap(in));
                    meta = metaOut[0];
                } catch (IOException e) {
                    e.printStackTrace();
                    event.getChannel().sendMessage(new EmbedBuilder().setTitle("Internal Error Try uploading again."));
                    return false;
                }
                EmbedBuilder eb = new EmbedBuilder()
                        .setTitle(Strings.stripColors(meta.get("name")))
                        .setDescription(meta.get("description"))
                        .setAuthor(event.getMessageAuthor().getDisplayName(), event.getMessageAuthor().getAvatar().getUrl().toString(), event.getMessageAuthor().getAvatar().getUrl().toString())
                        .setImage(ContentServer.renderRaw(data))
                        .addInlineField("Size", meta.getInt("width") + "x" + meta.getInt("height"));
                event.getChannel().sendMessage(eb);
            }
        }

        if (txtData.size > 0) {
            CompletableFuture<byte[]> cf = txtData.get(0).downloadAsByteArray();
            try {
                byte[] data = cf.get();
                String base64Encoded = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data)))
                        .lines().parallel().collect(Collectors.joining("\n"));
                Schematic schem = contentHandler.parseSchematic(base64Encoded);
                sendSchem(schem, new Context(event, null, null));
                return true;
            } catch (Exception e) {
                event.getChannel().sendMessage(new EmbedBuilder().setTitle(e.getMessage()).setColor(new Color(0xff0000)));
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public void registerListeners(DiscordCommands handler) {
        if (previewSchems) {
            handler.registerOnMessage(new MessageCreatedListener() {
                @Override
                public boolean run(MessageCreateEvent messageCreateEvent) {
                    return checkIfSchem(messageCreateEvent);
                }
            });
        }
        if (previewMaps) {
            handler.registerOnMessage(new MessageCreatedListener() {
                @Override
                public boolean run(MessageCreateEvent messageCreateEvent) {
                    return checkIfMap(messageCreateEvent);
                }
            });
        }
    }
}
