package com.snoxmox.hyperium.ffa.command.resolver;

import com.snoxmox.hyperium.ffa.clan.Clan;
import com.snoxmox.hyperium.ffa.clan.ClanService;
import com.snoxmox.hyperium.ffa.kit.Kit;
import com.snoxmox.hyperium.ffa.kit.KitManager;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import java.util.Optional;
import org.bukkit.command.CommandSender;

public class ClanResolver extends ArgumentResolver<CommandSender, Clan> {

    private final ClanService clanService;

    public ClanResolver(final ClanService clanService) {
        this.clanService = clanService;
    }

    @Override
    protected ParseResult<Clan> parse(final Invocation<CommandSender> invocation, final Argument<Clan> argument, final String text) {
        final Optional<Clan> kit = this.clanService.getClan(text);
        return kit.<ParseResult<Clan>>map(ParseResult::success).orElseGet(() -> ParseResult.failure("Nie znaleziono kit o nazwie: " + text));

    }

    @Override
    public SuggestionResult suggest(final Invocation<CommandSender> invocation, final Argument<Clan> argument, final SuggestionContext context) {
        return SuggestionResult.of(this.clanService.getClansByTag().keySet().stream().toList());
    }
}