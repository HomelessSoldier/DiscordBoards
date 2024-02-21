import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.MinecraftServer;
import java.util.Map;
import java.util.HashMap;

public class ScoreboardFetcher {
    private final MinecraftServer server;
    private final String objectiveName;

    public ScoreboardFetcher(MinecraftServer server, String objectiveName) {
        this.server = server;
        this.objectiveName = objectiveName;
    }

    public Map<String, Integer> fetchScoreboardData() {
        Scoreboard scoreboard = server.getWorld(World.OVERWORLD).getScoreboard();
        ScoreboardObjective objective = scoreboard.getObjective(objectiveName);
        Map<String, Integer> scores = new HashMap<>();

        if (objective != null) {
            for (ScoreboardEntry entry : scoreboard.getAllPlayerScores(objective)) {
                scores.put(entry.getPlayerName(), entry.getScore());
            }
        }

        return scores;
    }
}
