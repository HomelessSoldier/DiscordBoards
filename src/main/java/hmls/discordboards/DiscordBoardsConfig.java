import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class DiscordBoardsConfig {
    private static final String CONFIG_FILE_NAME = "discordboards.txt";

    public String discordMessageFormat = "[{server} Scoreboard] {objective}: {value}";
    public String discordChannelId = "";
    public String botWebhookUrl = "";
    public String scoreboardObjective = "score";
    public int refreshInterval = 60; // Example: Refresh scoreboard every 60 seconds

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
                        case "discordMessageFormat":
                            discordMessageFormat = value;
                            break;
                        case "discordChannelId":
                            discordChannelId = value;
                            break;
                        case "botWebhookUrl":
                            botWebhookUrl = value;
                            break;
                        case "scoreboardObjective":
                            scoreboardObjective = value;
                            break;
                        case "refreshInterval":
                            try {
                                refreshInterval = Integer.parseInt(value);
                            } catch (NumberFormatException e) {
                                System.err.println("Invalid refreshInterval in config. Using default value.");
                            }
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
            writer.write("discordMessageFormat=" + discordMessageFormat + "\n");
            writer.write("discordChannelId=" + discordChannelId + "\n");
            writer.write("botWebhookUrl=" + botWebhookUrl + "\n");
            writer.write("scoreboardObjective=" + scoreboardObjective + "\n");
            writer.write("refreshInterval=" + refreshInterval + "\n"); // Example
        } catch (IOException e) {
            System.err.println("Error saving DiscordBoards config: " + e.getMessage());
        }
    }
}
