import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class DiscordBoardsConfig {
    private static final String CONFIG_FILE_NAME = "discordboards.txt";
    public String discordBotToken = "";
    public String discordChannelId = "";

    public void loadConfig() {
        File configDir = new File(FabricLoader.getInstance().getConfigDir().toFile(), "config");
        File configFile = new File(configDir, CONFIG_FILE_NAME);

        try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#") || line.isEmpty()) continue;
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();

                    switch (key) {
                        case "discordbottoken": // Make sure this matches the exact key in your config file
                            discordBotToken = value;
                            break;
                        case "discordchannelid": // Make sure this matches the exact key in your config file
                            discordChannelId = value;
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
        File configDir = new File(FabricLoader.getInstance().getConfigDir().toFile(), "config");
        File configFile = new File(configDir, CONFIG_FILE_NAME);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {
            writer.write("# DiscordBoards Configuration File\n");
            writer.write("discordbottoken=" + discordBotToken + "\n"); //  Make sure this matches the exact key in your config file
            writer.write("discordchannelid=" + discordChannelId + "\n"); //  Make sure this matches the exact key in your config file
        } catch (IOException e) {
            System.err.println("Error saving DiscordBoards config: " + e.getMessage());
        }
    }
}
