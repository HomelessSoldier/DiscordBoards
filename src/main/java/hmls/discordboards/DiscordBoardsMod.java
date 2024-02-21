import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;

public class DiscordBoardsMod implements ModInitializer {
    private DiscordBoardsConfig config;
    private ScoreboardFetcher scoreboardFetcher;
    private MinecraftServer server;

    private HttpClient httpClient;
    private String webhookMessageID;

    @Override
    public void onInitialize() {
        config = new DiscordBoardsConfig();
        config.loadConfig();

        server = ServerLifecycleEvents.SERVER_STARTED.getInvokers().get(0).getServer();
        scoreboardFetcher = new ScoreboardFetcher(server, config.scoreboardObjective);

        httpClient = HttpClient.newHttpClient();
        webhookMessageID = sendInitialMessage();

        if (config.enableTimerUpdates) {
            startTimerUpdates();
        }

        // Event Listener (if desired)
        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
            if (!config.enableTimerUpdates) {
                updateDiscordMessage();
            }
        });
    }

    private void startTimerUpdates() {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateDiscordMessage();
            }
        }, 0, Duration.ofSeconds(config.timerUpdateInterval).toMillis());
    }

    private String sendInitialMessage() {
        // ... (Logic to format a message, see 'sendDiscordMessage' method below) 
        String initialMessage = formatDiscordMessage(scoreboardFetcher.fetchScoreboardData());
        return sendDiscordMessage(config.discordWebhookUrl, initialMessage);
    }

    private void updateDiscordMessage() {
        String updatedMessage = formatDiscordMessage(scoreboardFetcher.fetchScoreboardData());
        sendDiscordMessage(config.discordWebhookUrl, updatedMessage, webhookMessageID);
    }

    private String formatDiscordMessage(Map<String, Integer> scores) {
        if (scores.isEmpty()) {
            return "No scoreboard data available.";
        }

        // Example Formatting - Customize this! 
        StringBuilder builder = new StringBuilder(config.discordMessageFormat);
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            builder.append("\n").append(entry.getKey()).append(": ").append(entry.getValue());
        }
        return builder.toString();
    }

    private String sendDiscordMessage(String webhookUrl, String content) {
        // ... (Implementation of sending a new message, see below) 
    }

    private String sendDiscordMessage(String webhookUrl, String content, String messageId) {
        // ... (Implementation of editing an existing message, see below)
    }

// ... (Rest of your DiscordBoardsMod class)

    private String sendDiscordMessage(String webhookUrl, String content) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(webhookUrl))
                .POST(HttpRequest.BodyPublishers.ofString("{\"content\": \"" + content + "\"}"))
                .header("Content-Type", "application/json")
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Extract message ID from response (adjust if your API returns it differently)
            return extractMessageIdFromResponse(response);
        } catch (Exception e) {
            System.err.println("Error sending Discord message: " + e.getMessage());
            return null;
        }
    }

    private String sendDiscordMessage(String webhookUrl, String content, String messageId) {
        String patchUrl = webhookUrl + "/messages/" + messageId;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(patchUrl))
                .method("PATCH", HttpRequest.BodyPublishers.ofString("{\"content\": \"" + content + "\"}"))
                .header("Content-Type", "application/json")
                .build();

        try {
            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return messageId; // Success, return the existing message ID
        } catch (Exception e) {
            System.err.println("Error editing Discord message: " + e.getMessage());
            return null;
        }
    }
}