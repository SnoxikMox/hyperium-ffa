package com.snoxmox.hyperium.ffa.command.resolver;

import com.snoxmox.hyperium.ffa.kit.Kit;
import com.snoxmox.hyperium.ffa.kit.KitManager;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.command.CommandSender;

public class KitArgumentResolver extends ArgumentResolver<CommandSender, Kit> {

    private final KitManager kitManager;

    public KitArgumentResolver(final KitManager kitManager) {
        this.kitManager = kitManager;
    }

    @Override
    protected ParseResult<Kit> parse(final Invocation<CommandSender> invocation, final Argument<Kit> argument, final String text) {
        final Kit kit = this.kitManager.getKit(text);

        if (kit == null) {
            return ParseResult.failure("Nie znaleziono kit o nazwie: " + text);
        }

        return ParseResult.success(kit);
    }

    @Override
    public SuggestionResult suggest(final Invocation<CommandSender> invocation, final Argument<Kit> argument, final SuggestionContext context) {
        return SuggestionResult.of(this.kitManager.getKits().stream()
                .map(Kit::getName)
                .toList());
    }
}