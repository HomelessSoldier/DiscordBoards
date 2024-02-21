import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class DiscordBoardsConfig {
    private static final String CONFIG_FILE_NAME = "discordboards.txt";
    public String discordWebhookUrl = "";
    public String discordMessageFormat = "`Header1, player 1, player 2, player 3, etc.`";
    public String scoreboardObjective = "score";
    public boolean enableTimerUpdates = true;
    public int timerUpdateInterval = 60;
    public String updateEvent = "minecraft.server.player_logged_in";

    public void loadConfig() {
        File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), CONFIG_FILE_NAME);

        try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#") || line.isEmpty()) continue;
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();

                    switch (key) {
                        case "discordwebhook":
                            discordWebhookUrl = value;
                            break;
                        case "discordmessageformat":
                            discordMessageFormat = value;
                            break;
                        case "scoreboardname":
                            scoreboardObjective = value;
                            break;
                        case "enabletimerupdates":
                            enableTimerUpdates = Boolean.parseBoolean(value);
                            break;
                        case "timerupdateinterval":
                            try {
                                timerUpdateInterval = Integer.parseInt(value);
                            } catch (NumberFormatException e) {
                                System.err.println("Invalid timerUpdateInterval in config. Using default value.");
                            }
                            break;
                        case "updateevent":
                            updateEvent = value;
                            break;
                        default:
                            System.err.println("Unknown config option: " + key);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading DiscordBoards config: " + e.getMessage());
        }
    }

    public void saveConfig() {
        File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), CONFIG_FILE_NAME);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {
            writer.write("# DiscordBoards Configuration File\n");
            writer.write("discordwebhook=" + discordWebhookUrl + "\n");
            writer.write("discordmessageformat=" + discordMessageFormat + "\n");
            writer.write("scoreboardname=" + scoreboardObjective + "\n");
            writer.write("enabletimerupdates=" + enableTimerUpdates + "\n");
            writer.write("timerupdateinterval=" + timerUpdateInterval + "\n");
            writer.write("updateevent=" + updateEvent + "\n");
        } catch (IOException e) {
            System.err.println("Error saving DiscordBoards config: " + e.getMessage());
        }
    }
}
