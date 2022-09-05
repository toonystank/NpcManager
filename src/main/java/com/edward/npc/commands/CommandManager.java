package com.edward.npc.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.edward.npc.NPCManager;
import com.edward.npc.NPCPlugin;
import com.edward.npc.utils.Language;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("NPC")
public class CommandManager extends BaseCommand {

    private final NPCManager npcManager;

    public CommandManager(NPCPlugin plugin, NPCManager npcManager) {
        this.npcManager = npcManager;
    }

    @Subcommand("create")
    @CommandPermission("npcplugin.create")
    @CommandCompletion("@name")
    @Description("Create an NPC")
    public void onCreate(CommandSender sender, String name) {
        if (isConsole(sender)) return;
        npcManager.createNPC(name, (Player) sender);
    }

    @Subcommand("remove")
    @CommandPermission("npcplugin.remove")
    @CommandCompletion("@name")
    @Description("Remove an NPC")
    public void onRemove(CommandSender sender, String name) {
        npcManager.removeNPC(name, sender);
    }

    @Subcommand("show")
    @CommandPermission("npcplugin.show")
    @CommandCompletion("@name @players")
    @Description("Show an NPC to a player")
    public void onShow(CommandSender sender, String name, Player player) {
        npcManager.showNPCForPlayer(sender, name, player);
    }

    @Subcommand("hide")
    @CommandPermission("npcplugin.hide")
    @CommandCompletion("@name")
    @Description("Hide an NPC from a player")
    public void onHide(CommandSender sender, String name, Player player) {
        npcManager.hideNPCForPlayer(sender,name, player);
    }

    @Subcommand("change name")
    @CommandPermission("npcplugin.changename")
    @CommandCompletion("@name")
    @Description("Change the name of an NPC")
    public void onChangeName(CommandSender sender, String name, String newName) {
        npcManager.changeNPCName(name, newName, sender);
    }

    @Subcommand("change skin")
    @CommandPermission("npcplugin.changeskin")
    @CommandCompletion("@name")
    @Description("Change the skin of an NPC")
    public void onChangeSkin(CommandSender sender, String name, String skinName) {
        npcManager.changeNPCSkin(name, skinName, sender);
    }

    @Subcommand("change location")
    @CommandPermission("npcplugin.changelocation")
    @CommandCompletion("@name")
    @Description("Change the location of an NPC")
    public void onChangeLocation(CommandSender sender, String name) {
        if (isConsole(sender)) return;
        npcManager.changeNPCLocation(name, ((Player) sender).getLocation(), (Player) sender);
    }

    @Subcommand("rotate")
    @CommandPermission("npcplugin.changerotation")
    @CommandCompletion("@name true|false")
    @Description("Make NPC look at players")
    public void onRotate(CommandSender sender, String name, boolean rotate) {
        npcManager.makeNPCRotate(name, rotate, sender);
    }

    boolean isConsole(CommandSender sender) {
        if (!(sender instanceof Player)) {
            npcManager.sendMessage(sender, Language.COMMAND_NO_CONSOLE);
            return true;
        }
        return false;
    }

}
