package io.neskdev.api.utils.discord;

import lombok.SneakyThrows;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

/**
 * Class used to execute Discord Webhooks with low effort
 * Source: <a href="https://gist.github.com/k3kdude/fba6f6b37594eae3d6f9475330733bdb?permalink_comment_id=3528910">...</a>
 * Mini optimisation from iammehdib
 */
public class DiscordWebhook {

    private final String url;
    private String content;
    private String username;
    private String avatarUrl;

    /**
     * Constructs a new DiscordEmbedWebhook instance
     *
     * @param url The webhook URL obtained in Discord
     */
    public DiscordWebhook(String url) {
        this.url = url;
    }

    public DiscordWebhook setContent(String content) {
        this.content = content;
        return this;
    }

    public DiscordWebhook setUsername(String username) {
        this.username = username;
        return this;
    }

    public DiscordWebhook setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        return this;
    }

    @SneakyThrows
    public void sendAsync() {
        CompletableFuture.runAsync(this::send);
    }

    @SneakyThrows
    @Deprecated
    public void send() {
        execute();
    }

    private void execute() throws IOException {
        if (this.content == null) {
            throw new IllegalArgumentException("Set content or add at least one EmbedBuilder");
        }

        JSONObject json = new JSONObject();

        json.put("content", this.content);
        json.put("username", this.username);
        json.put("avatar_url", this.avatarUrl);
        json.put("tts", false);


        URL url = new URL(this.url);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.addRequestProperty("Content-Type", "application/json");
        connection.addRequestProperty("User-Agent", "Java-DiscordEmbedWebhook-BY-Gelox_");
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setConnectTimeout(2000);
        connection.setReadTimeout(2000);

        OutputStream stream = connection.getOutputStream();
        stream.write(json.toString().getBytes(StandardCharsets.UTF_8));
        stream.flush();
        stream.close();

        connection.getInputStream().close(); //I'm not sure why, but it doesn't work without getting the InputStream
        connection.disconnect();
    }

}