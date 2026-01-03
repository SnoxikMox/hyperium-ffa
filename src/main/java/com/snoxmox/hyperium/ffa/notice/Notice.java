package com.snoxmox.hyperium.ffa.notice;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.command.CommandSender;

public class Notice extends OkaeriConfig {

    private final String text;
    private final NotificationType type;

    private String subtitle;
    private int duration = 5;
    private BossBar.Color color = BossBar.Color.WHITE;
    private BossBar.Overlay style = BossBar.Overlay.PROGRESS;

    @Exclude
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public Notice(final String text, final NotificationType type) {
        this.text = text;
        this.type = type;
    }

    public Notice(final String text, final NotificationType type, final String subtitle, final int duration, final BossBar.Color color, final BossBar.Overlay style) {
        this.text = text;
        this.type = type;
        this.subtitle = subtitle;
        this.duration = duration;
        if (color != null) this.color = color;
        if (style != null) this.style = style;
    }

    public void send(final Audience audience) {
        this.send(audience, null);
    }

    public void send(final Audience audience, final String key, final String value) {
        this.send(audience, Collections.singletonMap(key, value));
    }

    public void send(final Audience audience, final Map<String, String> placeholders) {
        final LegacyComponentSerializer legacy = LegacyComponentSerializer.legacyAmpersand();
        final MiniMessage mini = MiniMessage.miniMessage();

        final String rawText = applyPlaceholders(this.text, placeholders);
        final String rawSub = applyPlaceholders(this.subtitle, placeholders);

        final Component content = (rawText != null)
                ? (rawText.contains("&") ? legacy.deserialize(rawText) : mini.deserialize(rawText))
                : Component.empty();

        switch (this.type) {
            case CHAT:
                audience.sendMessage(content);
                break;

            case ACTIONBAR:
                audience.sendActionBar(content);
                break;

            case TITLE:
                final Component sub = (rawSub != null)
                        ? (rawSub.contains("&") ? legacy.deserialize(rawSub) : mini.deserialize(rawSub))
                        : Component.empty();

                final Title.Times times = Title.Times.times(
                        Duration.ofMillis(500),
                        Duration.ofSeconds(this.duration),
                        Duration.ofMillis(1000)
                );
                audience.showTitle(Title.title(content, sub, times));
                break;

            case BOSSBAR:
                final BossBar bar = BossBar.bossBar(content, 1.0f, this.color, this.style);
                audience.showBossBar(bar);
                scheduler.schedule(() -> audience.hideBossBar(bar), this.duration, TimeUnit.SECONDS);
                break;
        }
    }

    public void send(final CommandSender sender) {
        this.send(sender, (Map<String, String>) null);
    }

    public void send(final CommandSender sender, final String key, final String value) {
        this.send(sender, Collections.singletonMap(key, value));
    }

    public void send(final CommandSender sender, final Map<String, String> placeholders) {

        if (sender instanceof Audience audience) {
            this.send(audience, placeholders);
            return;
        }

        final LegacyComponentSerializer legacy = LegacyComponentSerializer.legacyAmpersand();
        final MiniMessage mini = MiniMessage.miniMessage();

        final String rawText = applyPlaceholders(this.text, placeholders);
        final Component content = (rawText != null)
                ? (rawText.contains("&") ? legacy.deserialize(rawText) : mini.deserialize(rawText))
                : Component.empty();

        sender.sendMessage(content);
    }

    private static String applyPlaceholders(final String text, final Map<String, String> placeholders) {
        if (text == null) return null;
        if (placeholders == null || placeholders.isEmpty()) return text;

        String out = text;
        for (final Map.Entry<String, String> e : placeholders.entrySet()) {
            out = out.replace("{" + e.getKey() + "}", e.getValue());
        }
        return out;
    }

    public String getText() { return this.text; }
    public NotificationType getType() { return this.type; }
    public String getSubtitle() { return this.subtitle; }
    public int getDuration() { return this.duration; }
    public BossBar.Color getColor() { return this.color; }
    public BossBar.Overlay getStyle() { return this.style; }
}