package io.neskdev.api.utils.discord;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Class used to execute Discord Webhooks with low effort
 * Source: <a href="https://gist.github.com/k3kdude/fba6f6b37594eae3d6f9475330733bdb?permalink_comment_id=3528910">...</a>
 * Mini optimisation from iammehdib
 */
public class DiscordEmbedWebhook {

    private final String url;
    private final List<EmbedBuilder> embeds = new ArrayList<>();
    private String content;
    private String username;
    private String avatarUrl;
    private boolean tts;

    /**
     * Constructs a new DiscordEmbedWebhook instance
     *
     * @param url The webhook URL obtained in Discord
     */
    public DiscordEmbedWebhook(String url) {
        this.url = url;
    }

    public DiscordEmbedWebhook setContent(String content) {
        this.content = content;
        return this;
    }

    public DiscordEmbedWebhook setUsername(String username) {
        this.username = username;
        return this;
    }

    public DiscordEmbedWebhook setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        return this;
    }

    public DiscordEmbedWebhook setColor(Color color) {
        if (this.embeds.isEmpty()) {
            this.embeds.add(new EmbedBuilder());
        }
        this.embeds.get(0).setColor(color);
        return this;
    }

    public DiscordEmbedWebhook setDescription(String description) {
        if (this.embeds.isEmpty()) {
            this.embeds.add(new EmbedBuilder());
        }
        this.embeds.get(0).setDescription(description);
        return this;
    }

    public DiscordEmbedWebhook addField(String name, String value, boolean inline) {
        if (this.embeds.isEmpty()) {
            this.embeds.add(new EmbedBuilder());
        }
        this.embeds.get(0).addField(name, value, inline);
        return this;
    }

    public DiscordEmbedWebhook setFooter(String text, String iconUrl) {
        if (this.embeds.isEmpty()) {
            this.embeds.add(new EmbedBuilder());
        }
        this.embeds.get(0).setFooter(text, iconUrl);
        return this;
    }

    public DiscordEmbedWebhook setTimestamp() {
        if (this.embeds.isEmpty()) {
            this.embeds.add(new EmbedBuilder());
        }
        // Discord does not support setting a timestamp directly in the embed builder,
        // so we will not implement this method.
        return this;
    }


    public DiscordEmbedWebhook setTts(boolean tts) {
        this.tts = tts;
        return this;
    }

    public void addEmbed(EmbedBuilder embed) {
        this.embeds.add(embed);
    }

    public void addEmbed(EmbedBuilder... embeds) {
        Collections.addAll(this.embeds, embeds);
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
        if (this.content == null && this.embeds.isEmpty()) {
            throw new IllegalArgumentException("Set content or add at least one EmbedBuilder");
        }

        JSONObject json = new JSONObject();

        json.put("content", this.content);
        json.put("username", this.username);
        json.put("avatar_url", this.avatarUrl);
        json.put("tts", this.tts);

        if (!this.embeds.isEmpty()) {
            List<JSONObject> embedObjects = new ArrayList<>();

            for (EmbedBuilder embed : this.embeds) {
                JSONObject jsonEmbed = new JSONObject();

                jsonEmbed.put("title", embed.getTitle());
                jsonEmbed.put("description", embed.getDescription());
                jsonEmbed.put("url", embed.getUrl());

                if (embed.getColor() != null) {
                    Color color = embed.getColor();
                    int rgb = color.getRed();
                    rgb = (rgb << 8) + color.getGreen();
                    rgb = (rgb << 8) + color.getBlue();

                    jsonEmbed.put("color", rgb);
                }

                EmbedBuilder.Footer footer = embed.getFooter();
                EmbedBuilder.Image image = embed.getImage();
                EmbedBuilder.Thumbnail thumbnail = embed.getThumbnail();
                EmbedBuilder.Author author = embed.getAuthor();
                List<EmbedBuilder.Field> fields = embed.getFields();

                if (footer != null) {
                    JSONObject jsonFooter = new JSONObject();

                    jsonFooter.put("text", footer.getText());
                    jsonFooter.put("icon_url", footer.getIconUrl());
                    jsonEmbed.put("footer", jsonFooter);
                }

                if (image != null) {
                    JSONObject jsonImage = new JSONObject();

                    jsonImage.put("url", image.getUrl());
                    jsonEmbed.put("image", jsonImage);
                }

                if (thumbnail != null) {
                    JSONObject jsonThumbnail = new JSONObject();

                    jsonThumbnail.put("url", thumbnail.getUrl());
                    jsonEmbed.put("thumbnail", jsonThumbnail);
                }

                if (author != null) {
                    JSONObject jsonAuthor = new JSONObject();

                    jsonAuthor.put("name", author.getName());
                    jsonAuthor.put("url", author.getUrl());
                    jsonAuthor.put("icon_url", author.getIconUrl());
                    jsonEmbed.put("author", jsonAuthor);
                }

                List<JSONObject> jsonFields = new ArrayList<>();
                for (EmbedBuilder.Field field : fields) {
                    JSONObject jsonField = new JSONObject();

                    jsonField.put("name", field.getName());
                    jsonField.put("value", field.getValue());
                    jsonField.put("inline", field.isInline());

                    jsonFields.add(jsonField);
                }

                jsonEmbed.put("fields", jsonFields.toArray());
                embedObjects.add(jsonEmbed);
            }

            json.put("embeds", embedObjects.toArray());
        }

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

    @Getter
    public static class EmbedBuilder {
        private final List<Field> fields = new ArrayList<>();
        private String title;
        private String description;
        private String url;
        private Color color;
        private Footer footer;
        private Thumbnail thumbnail;
        private Image image;
        private Author author;

        public EmbedBuilder setTitle(String title) {
            this.title = title;
            return this;
        }

        public EmbedBuilder setDescription(String description) {
            this.description = description;
            return this;
        }

        public EmbedBuilder setUrl(String url) {
            this.url = url;
            return this;
        }

        public EmbedBuilder setColor(Color color) {
            this.color = color;
            return this;
        }

        public EmbedBuilder setFooter(String text, String icon) {
            this.footer = new Footer(text, icon);
            return this;
        }

        public EmbedBuilder setThumbnail(String url) {
            this.thumbnail = new Thumbnail(url);
            return this;
        }

        public EmbedBuilder setImage(String url) {
            this.image = new Image(url);
            return this;
        }

        public EmbedBuilder setAuthor(String name, String url, String icon) {
            this.author = new Author(name, url, icon);
            return this;
        }

        public EmbedBuilder addField(String name, String value, boolean inline) {
            this.fields.add(new Field(name, value, inline));
            return this;
        }

        @AllArgsConstructor
        @Getter
        private static class Footer {
            private final String text;
            private final String iconUrl;
        }

        @AllArgsConstructor
        @Getter
        private static class Thumbnail {
            private final String url;
        }

        @AllArgsConstructor
        @Getter
        private static class Image {
            private final String url;
        }

        @AllArgsConstructor
        @Getter
        private static class Author {
            private final String name;
            private final String url;
            private final String iconUrl;
        }

        @AllArgsConstructor
        @Getter
        private static class Field {
            private String name;
            private String value;
            private boolean inline;
        }
    }
}