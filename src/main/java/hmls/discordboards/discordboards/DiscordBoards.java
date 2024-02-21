import com.example.discordboardsmod.DiscordBoardsConfig;
import io.github.prospector.clothconfig.api.ClothConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class DiscordBoardsMod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("DiscordBoards");
    public DiscordBoardsConfig config;
    private Timer updateTimer;
    private ScoreboardFetcher scoreboardFetcher;
    private OkHttpClient httpClient;
    private static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");

    @Override
    public void onInitialize() {
        config = loadConfig();
        scoreboardFetcher = new ScoreboardFetcher(FabricLoader.getInstance().getGameInstance().getServer(), config.scoreboardObjective);
        httpClient = new OkHttpClient();

        updateTimer = new Timer();
        updateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sendScoreboardUpdate();
            }
        }, 0, config.refreshInterval * 1000L);
    }

    private DiscordBoardsConfig loadConfig() {
        try {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("discordboardsmod.json"))
                    .setDefaultValue(new DiscordBoardsConfig());
            DiscordBoardsConfig config = builder.build().load();
            builder.save(); // Save immediately on first creation
            return config;
        } catch (Exception e) {
            LOGGER.error("Error loading DiscordBoards config.", e);
            return new DiscordBoardsConfig(); // Return default on failure 
        }
    }

    private void sendScoreboardUpdate() {
        try {
            String message = formatScoreboardMessage();
            RequestBody body = RequestBody.create(message, JSON_MEDIA_TYPE);
            Request request = new Request.Builder().url(config.botWebhookUrl).post(body).build();
            Response response = httpClient.newCall(request).execute();

            if (!response.isSuccessful()) {
                LOGGER.error("Failed to send Discord scoreboard update. Response code: {}", response.code());
            }
        } catch (IOException e) {
            LOGGER.error("Error sending Discord scoreboard update.", e);
        }
    }

    private String formatScoreboardMessage() {
        Map<String, Integer> scores = scoreboardFetcher.fetchScoreboardData();

        // Limit the top X entries based on config 
        scores = limitEntries(scores, config.displayTopEntries);

        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append(config.discordMessageFormat.replace("{server}", "Minecraft Server")); // Replace with actual server name later 

        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            messageBuilder.append(config.discordMessageFormat
                            .replace("{objective}", config.scoreboardObjective)
                            .replace("{player}", entry.getKey())
                            .replace("{value}", entry.getValue().toString()))
                    .append("\n");
        }

        return messageBuilder.toString();
    }

    private Map<String, Integer> limitEntries(Map<String, Integer> scores, int maxEntries) {
        Map<String, Integer> topScores = new HashMap<>();
        int count = 0;
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            topScores.put(entry.getKey(), entry.getValue());
            count++;
            if (count >= maxEntries) {
                break;
            }
        }
        return topScores;
    }
}