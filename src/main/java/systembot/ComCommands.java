package systembot;


import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import systembot.discordcommands.Command;
import systembot.discordcommands.Context;
import systembot.discordcommands.DiscordCommands;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.text.NumberFormat;
import java.util.Random;

import static systembot.Pragati.*;
import static systembot.Utils.percentageBar;

public class ComCommands {
    public void registerCommands(DiscordCommands handler) {
        System.out.println("start registerCommands");

        handler.registerCommand(new Command("status") {
            {
                help = "Shows the basic information about the status of the server";
                usage = "[Number of tests (default 3)] [run ram tests (true|false)]";
            }

            @Override
            public void run(Context ctx) {
                new Thread(() -> {
                    EmbedBuilder eb = new EmbedBuilder()
                            .setTitle("Status").setColor(new Color(0x00ffff));
                    int tests = 3;
                    boolean runRamTests = false;
                    if (ctx.args.length > 1) {

                        if (ctx.args.length > 2) {
                            runRamTests = Boolean.parseBoolean(ctx.args[2]);
                        }


                        if (Integer.parseInt(ctx.args[1]) > 10 && Integer.parseInt(ctx.args[1]) <= 25) {
                            ctx.channel.sendMessage(new EmbedBuilder()
                                    .setTitle("Error")
                                    .setDescription("I can only do 10 tests at max")
                                    .setColor(new Color(0xff0000)));
                            return;
                        }

                        if (Integer.parseInt(ctx.args[1]) > 25) {
                            ctx.channel.sendMessage(new EmbedBuilder()
                                    .setTitle("Hello there >:C")
                                    .setColor(new Color(0xff0000))
                                    .setDescription("Do you think that it is funny to tell me to do " + Integer.parseInt(ctx.args[1]) + " tests? It is not. I can do to 10 tests only.\n" +
                                            "<:kekw:897908978368008212>"));
                            return;
                        }

                        tests = Math.min(10, Integer.parseInt(ctx.args[1]));
                    }

                    Message message = ctx.channel.sendMessage(new EmbedBuilder()
                            .setTitle("Starting tests, this should take up to 30 Seconds!")
                            .setDescription("Running " + tests + " tests!")
                            .setColor(new Color(0xffff00))).join();
                    com.sun.management.OperatingSystemMXBean osBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getPlatformMXBean(
                            OperatingSystemMXBean.class);
                    System.out.println(osBean.getProcessCpuLoad());
                    System.out.println(osBean.getCpuLoad());
                    System.out.println(new CpuStats().getUsage());

                    // run linux command to get cpu usage
                    try {
                        int i = 1;
                        float finalres;
                        // execute the linux command
                        Process p = Runtime.getRuntime().exec("mpstat");
                        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
                        String line = null;
                        //read the row corresponding to cpu idle
                        while ((line = in.readLine()) != null && i < 4) {
                            i++;
                        }
                        assert line != null;
                        String res = line.substring(line.length() - 5);
                        finalres = Float.parseFloat(res);
                        //convert the idle to cpuload
                        System.out.println("CPU load:" + (100 - finalres) + "%");
                        eb.addField("CPU Load:", "`" + percentageBar(100 - finalres, 100, 20) + "`", true);
                    } catch (Exception e) {
                        eb.addField("Error", "There was an error with reading cpu usage with mpstat!\n" + e.getMessage());
                    }

                    if (osBean.getProcessCpuLoad() >= 0 && osBean.getCpuLoad() >= 0) {
                        //                eb.addField("Process CPU Load:", new DecimalFormat("##.00").format(osBean.getProcessCpuLoad() * 100) + "%\n`" + percentageBar((float) (osBean.getProcessCpuLoad() * 100), 100, 20) + "`", true);
                        eb.addField("Process CPU Load:", "`" + percentageBar((float) (osBean.getProcessCpuLoad() * 100), 100, 20) + "`", true);
//                eb.addField("CPU Load:", new DecimalFormat("##.00").format(osBean.getCpuLoad() * 100) + "%\n`" + percentageBar((float) (osBean.getCpuLoad() * 100), 100, 20) + "`", true);
                        eb.addField("CPU Load:", "`" + percentageBar((float) (osBean.getCpuLoad() * 100), 100, 20) + "`", true);
                    } else {
//                        eb.addField("Error", "There was an error with reading cpu usage!");
                    }

                    // ram:
                    Runtime runtime = Runtime.getRuntime();

                    NumberFormat format = NumberFormat.getInstance();

                    StringBuilder sb = new StringBuilder();
                    long maxMemory = runtime.maxMemory();
                    long allocatedMemory = runtime.totalMemory();
                    long freeMemory = runtime.freeMemory();

                    sb.append("free memory: ").append(format.format(freeMemory / 1024)).append("MB\n");
                    sb.append("allocated memory: ").append(format.format(allocatedMemory / 1024)).append("MB\n");
                    sb.append("max memory: ").append(format.format(maxMemory / 1024)).append("MB\n");
                    sb.append("total free memory: ").append(format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024)).append("MB\n");

                    eb.addField("RAM", sb.toString());

                    System.out.println(sb);


                    // some other infos ig
                    int mb = 1024 * 1024;
                    int gb = 1024 * 1024 * 1024;

                    com.sun.management.OperatingSystemMXBean os = (com.sun.management.OperatingSystemMXBean)
                            java.lang.management.ManagementFactory.getOperatingSystemMXBean();
                    long physicalMemorySize = os.getTotalMemorySize();
                    System.out.println("PHYSICAL MEMORY DETAILS \n");
                    eb.addField("Total physical memory", physicalMemorySize / mb + "MB ");
                    long physicalfreeMemorySize = os.getFreeMemorySize();
                    eb.addField("Total free physical memory", physicalfreeMemorySize / mb + "MB");
                    /* DISC SPACE DETAILS */
                    File diskPartition = new File("C:");
                    File diskPartition1 = new File("D:");
                    File diskPartition2 = new File("E:");
                    File diskPartition3 = new File("/");
                    long totalCapacity = diskPartition.getTotalSpace() / gb;
                    long totalCapacity1 = diskPartition1.getTotalSpace() / gb;
                    long totalCapacity3 = diskPartition3.getTotalSpace() / gb;
                    double freePartitionSpace = diskPartition.getFreeSpace() / gb;
                    double freePartitionSpace1 = diskPartition1.getFreeSpace() / gb;
                    double freePartitionSpace2 = diskPartition2.getFreeSpace() / gb;
                    double freePartitionSpace3 = diskPartition3.getFreeSpace() / gb;
                    double usablePatitionSpace = diskPartition.getUsableSpace() / gb;
                    double usablePatitionSpace3 = diskPartition3.getUsableSpace() / gb;
                    System.out.println("\n**** Sizes in Giga Bytes ****\n");
                    System.out.println("DISK SPACE DETAILS \n");
                    StringBuilder storage = new StringBuilder();
                    //System.out.println("Total C partition size : " + totalCapacity + "GB");
                    //System.out.println("Usable Space : " + usablePatitionSpace + "GB");
//                storage.append("Free Space in drive C: ").append(freePartitionSpace).append("GB\n`");
//                storage.append(percentageBar((int) usablePatitionSpace, (int) totalCapacity));
//                storage.append("`\nFree Space in drive D: ").append(freePartitionSpace1).append("GB\n");
//                storage.append("Free Space in drive E: ").append(freePartitionSpace2).append("GB\n");
                    storage
//                            .append("Free Space in drive /: ")
                            .append(freePartitionSpace3).append("GB\n`");
                    storage.append(percentageBar((int) ((int) totalCapacity3 - usablePatitionSpace3), (int) totalCapacity3)).append("`");
                    eb.addField("Free space in Master Drive", storage.toString());
                    if (freePartitionSpace <= totalCapacity % 10 || freePartitionSpace1 <= totalCapacity1 % 10) {
                        System.out.println("!!!!Alert!!!!");
                    } else
                        System.out.println("no alert");


                    // idk some testing


                    byte[] bytes;
                    System.out.println("\n \n**MEMORY DETAILS  ** \n");
                    // Print initial memory usage.
                    runtime = Runtime.getRuntime();
                    eb.addField("Initial memory usage", printUsage(runtime, new StringBuilder(), (runRamTests ? 25 : 50)).toString(), true);

                    if (runRamTests) {
                        // Allocate a 1 Megabyte and print memory usage
                        bytes = new byte[1024 * 1024];
//                eb.addField("Memory usage (allocated 1 Megabyte)", printUsage(runtime, new StringBuilder()).toString(), true);

                        bytes = null;
                        // Invoke garbage collector to reclaim the allocated memory.
                        runtime.gc();

                        // Wait 5 seconds to give garbage collector a chance to run
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            return;
                        }

                        // Total memory will probably be the same as the second printUsage call,
                        // but the free memory should be about 1 Megabyte larger if garbage
                        // collection kicked in.
                        printUsage(runtime);
                        eb.addField("Memory usage after allocating 1MB", printUsage(runtime, new StringBuilder(), 25).toString(), true);
                    }
                    StringBuilder cpuUsage = new StringBuilder();
                    float avarage = 0;
                    for (int i = 0; i < tests; i++) {
                        long start = System.nanoTime();
                        // log(start);
                        //number of available processors;
                        int cpuCount = ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();
                        Random random = new Random(start);
                        int seed = Math.abs(random.nextInt());
                        log("\n \n CPU USAGE DETAILS \n\n");
                        cpuUsage.append("Starting Test with ").append(cpuCount).append(" CPUs and random number:").append(seed);
                        int primes = 10000;
                        //
                        long startCPUTime = ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();
                        start = System.nanoTime();
                        while (primes != 0) {
                            if (isPrime(seed)) {
                                primes--;
                            }
                            seed++;

                        }
                        float cpuPercent = calcCPU(startCPUTime, start, cpuCount);
                        avarage += cpuPercent;
                        cpuUsage.append("\nCPU USAGE : ").append(cpuPercent).append(" %\n");


                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                    }
                    cpuUsage.append("Average Usage: ").append(avarage / tests);
                    if (tests > 0) eb.addField("CPU USAGE DETAILS", cpuUsage.toString());

                    ctx.channel.sendMessage(eb);
                    message.delete();
                }).start();
            }
        });

        handler.registerCommand(new Command("help") {
            {
                help = "Display all available commands and their usage.";
                usage = "[command]";
                aliases.add("h");
            }

            public void run(Context ctx) {
                System.out.println("run command help!");
                if (ctx.args.length == 1) {
                    StringBuilder publicCommands = new StringBuilder();
                    StringBuilder management = new StringBuilder();
                    StringBuilder moderation = new StringBuilder();
                    StringBuilder mapReviewer = new StringBuilder();


                    for (Command command : handler.getAllCommands()) {
                        if (command.hidden) continue;
                        switch (command.category) {
                            case "moderation" -> moderation.append("**").append(command.name).append("** ").append("\n");
                            case "management" -> management.append("**").append(command.name).append("** ").append("\n");
                            case "mapReviewer" -> mapReviewer.append("**").append(command.name).append("** ").append("\n");
                            default -> publicCommands.append("**").append(command.name).append("** ").append("\n");
                        }
                    }
//                    EmbedBuilder embed = new EmbedBuilder()
//                            .setTitle("Commands:")
//                            .addField("**__Public:__**", (publicCommands.length() != 0 ? publicCommands.toString() : "No Public commands!"), true)
//                            .addField("**__Moderation:__**", (moderation.length() != 0 ? moderation.toString() : "No Moderation commands!"), true)
//                            .addField("**__Management:__**", (management.length() != 0 ? management.toString() : "No Management commands!"), true)
//                            .addField("**__Map reviewer:__**", (mapReviewer.length() != 0 ? mapReviewer.toString() : "No Management commands!"), true);
                    EmbedBuilder embed = new EmbedBuilder()
                            .setTitle("Commands:");
                    if (!publicCommands.isEmpty()) {
                        embed.addField("**__Public:__**", publicCommands.toString(), true);
                    }
                    if (!moderation.isEmpty()) {
                        embed.addField("**__Moderation:__**", moderation.toString(), true);
                    }
                    if (!management.isEmpty()) {
                        embed.addField("**__Management:__**", management.toString(), true);
                    }

                    ctx.channel.sendMessage(embed);
                } else {
                    EmbedBuilder embed = new EmbedBuilder();
                    for (Command command : handler.getAllCommands()) {
                        if (command.name.equals(ctx.args[1])) {
                            embed.setTitle(command.name)
                                    .setDescription(command.help);
                            if (!command.usage.equals("")) {
                                embed.addField("Usage:", SystemBot.prefix + command.name + " " + command.usage);
                            }
                            embed.addField("Category:", command.category);
                        }
                    }
                    ctx.channel.sendMessage(embed);
                }
            }
        });
    }
}