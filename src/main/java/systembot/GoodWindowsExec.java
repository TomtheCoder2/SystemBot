package systembot;

import systembot.discordcommands.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class StreamGobbler extends Thread {
    InputStream is;
    String type;
    Context ctx;

    StreamGobbler(InputStream is, String type, Context ctx) {
        this.is = is;
        this.type = type;
        this.ctx = ctx;
    }

    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                if (line.equals("")) continue;
                if (2000 < sb.length() + type.length() + 10 + line.length()) {
                    sb.append("`");
                    ctx.channel.sendMessage("`" + sb);
                    System.out.println("sent message. length: " + sb.length());
                    sb = new StringBuilder();
                }
                sb.append("\n").append(type).append(">").append(line);
            }
            if (sb.length() > 0) {
                sb.append("`");
                ctx.channel.sendMessage("`" + sb);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}

public class GoodWindowsExec {
    public static void execute(String[] args, Context ctx) {
        if (args.length < 1) {
            ctx.channel.sendMessage("`USAGE: java GoodWindowsExec <cmd>`");
        }

        Runtime rt = Runtime.getRuntime();
        for (String command : args) {
            try {
                String osName = System.getProperty("os.name");
                ctx.channel.sendMessage("`OS NAME IS " + osName + "`");
                String[] cmd = new String[3];
                if (osName.equals("Windows NT")) {
                    cmd[0] = "cmd.exe";
                    cmd[1] = "/C";
                    cmd[2] = command;
                } else if (osName.equals("Windows 95")) {
                    cmd[0] = "command.com";
                    cmd[1] = "/C";
                    cmd[2] = command;
                } else if (osName.toUpperCase().trim().contains("WINDOWS")) {
                    cmd[0] = "cmd.exe";
                    cmd[1] = "/C";
                    cmd[2] = command;
                } else {
                    cmd[0] = "/bin/sh";
                    cmd[1] = "-c";
                    cmd[2] = command.replaceAll("&&", ";");
                }

                ctx.channel.sendMessage("`Executing " + cmd[0] + " " + cmd[1]
                        + " " + cmd[2] + "`");
                Process proc = rt.exec(cmd);
                // any error message?
                StreamGobbler errorGobbler = new
                        StreamGobbler(proc.getErrorStream(), "ERROR", ctx);

                // any output?
                StreamGobbler outputGobbler = new
                        StreamGobbler(proc.getInputStream(), "OUTPUT", ctx);

                // kick them off
                errorGobbler.start();
                outputGobbler.start();

                // any error???
                int exitVal = proc.waitFor();
                ctx.channel.sendMessage("`ExitValue: " + exitVal + "`");
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }
}
