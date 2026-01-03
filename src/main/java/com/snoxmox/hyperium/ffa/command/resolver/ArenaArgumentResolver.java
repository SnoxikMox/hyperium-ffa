package com.snoxmox.hyperium.ffa.command.resolver;

import com.snoxmox.hyperium.ffa.arena.Arena;
import com.snoxmox.hyperium.ffa.arena.ArenaManager;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.command.CommandSender;

import java.util.stream.Collectors;

public class ArenaArgumentResolver extends ArgumentResolver<CommandSender, Arena> {

    private final ArenaManager arenaManager;

    public ArenaArgumentResolver(final ArenaManager arenaManager) {
        this.arenaManager = arenaManager;
    }

    @Override
    protected ParseResult<Arena> parse(final Invocation<CommandSender> invocation, final Argument<Arena> argument, final String text) {
        final Arena arena = this.arenaManager.getArena(text);

        if (arena == null) {
            return ParseResult.failure("Nie znaleziono areny o nazwie: " + text);
        }

        return ParseResult.success(arena);
    }

    @Override
    public SuggestionResult suggest(final Invocation<CommandSender> invocation, final Argument<Arena> argument, final SuggestionContext context) {
        return SuggestionResult.of(this.arenaManager.getArenas().stream()
                .map(Arena::getName)
                .collect(Collectors.toList()));
    }
}