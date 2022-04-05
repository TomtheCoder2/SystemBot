package systembot.discordcommands;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/** Represents a command */
public abstract class Command {
    public String name;
    /** Help for this command, shown by the help command */
    public String help = ":v no information was provided for this command";
    public String usage = "";
    public String category = "public";
    public boolean hidden = false;
    public List<String> aliases = new ArrayList<>();

    public Command(String name) {
        // ALWAYS -> always lowercase command names
        this.name = name.toLowerCase();
    }

    /**
     * This method is called when the command is run
     * @param ctx
     * Context
     */
    public abstract void run(Context ctx) throws ExecutionException, InterruptedException, IOException;

    public boolean hasPermission(Context ctx) {
        return true;
    }
}