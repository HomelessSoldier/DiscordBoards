public void saveConfig() {
    File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), CONFIG_FILE_NAME);

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {
        writer.write("# DiscordBoards Configuration File\n");
        writer.write("discordMessageFormat=" + discordMessageFormat + "\n");
        writer.write("discordChannelId=" + discordChannelId + "\n");
        writer.write("botWebhookUrl=" + botWebhookUrl + "\n");
        writer.write("scoreboardObjective=" + scoreboardObjective + "\n");

        // ... add lines for other config options
    } catch (IOException e) {
        System.err.println("Error saving DiscordBoards config: " + e.getMessage());
    }
}
