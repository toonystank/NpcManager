package com.edward.npc;

import com.edward.npc.utils.Language;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NPCManager {

    private final NPCPlugin npcPlugin;

    @Getter
    private final NPCData npcData;

    @Getter
    private final Map<String, NPC> npcMap = new HashMap<>();

    public NPCManager(NPCPlugin npcPlugin) {
        this.npcPlugin = npcPlugin;
        try {
            npcData = new NPCData(npcPlugin);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load NPC data");
        }
    }
    void registerEvents() {

    }
    void registerCommands() {

    }
    public void createNPC(String name, Player player) {
        if (npcMap.containsKey(name)) {
            sendMessage(player, Language.COMMAND_NPC_ALREADY_EXISTS);
            return;
        }
        NPC npc = new NPC(npcPlugin, this, name, name, player.getLocation());
        Bukkit.getOnlinePlayers().forEach(npc::showTo);
        npcMap.put(name, npc);
        npcData.addNPC(name);

        sendMessage(player, Language.COMMAND_NPC_CREATED);
    }
    public void removeNPC(String name, Player player) {
        if (!npcMap.containsKey(name)) {
            sendMessage(player, Language.COMMAND_NPC_NOT_FOUND);
            return;
        }
        NPC npc = npcMap.get(name);
        Bukkit.getOnlinePlayers().forEach(npc::hideFrom);
        npcMap.remove(name);
        npcData.removeNPC(name);
        sendMessage(player, Language.COMMAND_NPC_DELETED);
    }
    public void showNPCForPlayer(CommandSender sender, String name, Player player) {
        if (!npcMap.containsKey(name)) {
            sendMessage(sender, Language.COMMAND_NPC_NOT_FOUND);
            return;
        }
        NPC npc = npcMap.get(name);
        npc.showTo(player);
        sendMessage(sender, Language.NPC_VISIBILITY_CHANGED);
    }
    public void hideNPCForPlayer(CommandSender sender, String name, Player player) {
        if (!npcMap.containsKey(name)) {
            sendMessage(sender, Language.COMMAND_NPC_NOT_FOUND);
            return;
        }
        NPC npc = npcMap.get(name);
        npc.hideFrom(player);
        sendMessage(sender, Language.NPC_VISIBILITY_CHANGED);
    }
    public void changeNPCName(String name, String newName, CommandSender sender) {
        if (!npcMap.containsKey(name)) {
            sendMessage(sender, Language.COMMAND_NPC_NOT_FOUND);
            return;
        }
        NPC npc = npcMap.get(name);
        npc.updateName(newName);
        npcMap.remove(name);
        npcMap.put(newName, npc);
        sendMessage(sender, Language.COMMAND_NPC_RENAMED);
    }
    public void changeNPCSkin(String name, String skin, CommandSender player) {
        if (!npcMap.containsKey(name)) {
            sendMessage(player, Language.COMMAND_NPC_NOT_FOUND);
            return;
        }
        NPC npc = npcMap.get(name);
        npc.updateSkin(skin);
        npcData.setNPCSkin(name, skin);
        sendMessage(player, Language.COMMAND_NPC_SET_SKIN);
    }
    public void changeNPCLocation(String name, Location location, Player player) {
        if (!npcMap.containsKey(name)) {
            sendMessage(player, Language.COMMAND_NPC_NOT_FOUND);
            return;
        }
        NPC npc = npcMap.get(name);
        npc.updateLocation(location);
        npcData.setNPCLocation(name, location);
        sendMessage(player, Language.COMMAND_NPC_SET_LOCATION);
    }
    public void makeNPCRotate(String name, boolean rotate, CommandSender sender) {
        if (!npcMap.containsKey(name)) {
            sendMessage(sender, Language.COMMAND_NPC_NOT_FOUND);
            return;
        }
        NPC npc = npcMap.get(name);
        npc.makeNPCRotate(rotate);
        npcData.setNPCRotation(name, rotate);
        sendMessage(sender, Language.COMMAND_NPC_SET_ROTATION);
    }

   public void sendMessage(CommandSender sender, Language message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes(
                '&',
                Language.PREFIX.getMessage() + message.getMessage()));
    }


}
