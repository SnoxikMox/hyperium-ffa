package com.snoxmox.hyperium.ffa.command;

import com.snoxmox.hyperium.ffa.configuration.MenuConfiguration;
import com.snoxmox.hyperium.ffa.configuration.MessageConfiguration;
import com.snoxmox.hyperium.ffa.gui.AdminKitEditorGui;
import com.snoxmox.hyperium.ffa.gui.PlayerKitEditorGui;
import com.snoxmox.hyperium.ffa.gui.PlayerKitSelectorGui;
import com.snoxmox.hyperium.ffa.kit.Kit;
import com.snoxmox.hyperium.ffa.kit.KitManager;
import com.snoxmox.hyperium.ffa.kit.item.KitItem;
import com.snoxmox.hyperium.ffa.user.User;
import com.snoxmox.hyperium.ffa.user.UserService;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import java.util.HashMap;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;

@Command(name = "kit", aliases = {"kits", "zestaw"})
@Permission("hyperium.command.kit")
public record KitCommand(UserService userService, KitManager kitManager, MessageConfiguration messageConfiguration, MenuConfiguration menuConfiguration) {

    @Execute(name = "edit")
    public void editKit(@Context final Player player) {
        final User user = this.userService.getUser(player.getUniqueId());

        new PlayerKitSelectorGui(this.kitManager, user, this.messageConfiguration, this.menuConfiguration).open(player);
    }

    @Execute(name = "create")
    public void createKit(@Context final Player player, @Arg final String name) {
        if (this.kitManager.getKit(name) != null) {
            this.messageConfiguration.kitAlreadyExists.send(player);
            return;
        }

        final Kit kit = new Kit(name);
        this.kitManager.addKit(kit);
        this.messageConfiguration.kitCreated.send(player, "name", name);
    }

    @Execute(name = "delete")
    public void deleteKit(@Context final Player player, @Arg final Kit kit) {
        this.kitManager.removeKit(kit);
        this.messageConfiguration.kitDeleted.send(player, "name", kit.getName());
    }

    @Execute(name = "list")
    public void listKits(@Context final CommandSender sender) {
        final MessageConfiguration msg = this.messageConfiguration;
        msg.kitListHeader.send(sender);
        for (final Kit kit : this.kitManager.getKits()) {
            msg.kitListEntry.send(sender, java.util.Map.of(
                    "name", kit.getName(),
                    "display", kit.getDisplayName()
            ));
        }
    }

    @Execute(name = "setitems")
    public void setItems(@Context final Player player, @Arg final Kit kit) {
        new AdminKitEditorGui(kit, this.messageConfiguration, this.menuConfiguration).open(player);
    }

    @Execute(name = "seticon")
    public void setIcon(@Context final Player player, @Arg final Kit kit) {
        final ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (itemInHand.getType() == Material.AIR) {
            this.messageConfiguration.kitSetIconNoItem.send(player);
            return;
        }

        kit.setDisplayItem(itemInHand.clone());
        this.messageConfiguration.kitIconSet.send(player, "name", kit.getName());
    }

    @Execute(name = "setdisplayname")
    public void setDisplayName(@Context final CommandSender sender, @Arg final Kit kit, @Arg final List<String> displayName) {
        final String name = String.join(" ", displayName);
        kit.setDisplayName(name);
        this.messageConfiguration.kitSetDisplayName.send(sender, "name", name);
    }

    @Execute(name = "setbuild")
    public void setBuild(@Context final CommandSender sender, @Arg final Kit kit, @Arg final boolean build) {
        kit.setBuild(build);
        this.messageConfiguration.kitSetBuild.send(sender, java.util.Map.of(
                "name", kit.getName(),
                "build", String.valueOf(build)
        ));
    }

    @Execute(name = "info")
    public void info(@Context final CommandSender sender, @Arg final Kit kit) {
        final MessageConfiguration msg = this.messageConfiguration;
        msg.kitInfoHeader.send(sender);
        msg.kitInfoId.send(sender, "id", kit.getName());
        msg.kitInfoDisplayName.send(sender, "display", kit.getDisplayName());
        msg.kitInfoItems.send(sender, "count", String.valueOf(kit.getItems().size()));
        msg.kitInfoBuild.send(sender, "build", String.valueOf(kit.isBuild()));

        if (kit.getDisplayItem() != null) {
            msg.kitInfoIconPresent.send(sender, "material", kit.getDisplayItem().getType().name());
        } else {
            msg.kitInfoIconAbsent.send(sender);
        }
    }
}