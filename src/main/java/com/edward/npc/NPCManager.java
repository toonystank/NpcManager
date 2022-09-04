package com.edward.npc;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NPCManager {

    private final NPCPlugin npcPlugin;
    private final NPCData npcData;
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
            player.sendMessage("NPC already exists");
            return;
        }
        NPC npc = new NPC(npcPlugin, this, name, name, player.getLocation());
        Bukkit.getOnlinePlayers().forEach(npc::showTo);
        npcMap.put(name, npc);
        npcData.addNPC(name);
    }
    public void removeNPC(String name, Player player) {
        if (!npcMap.containsKey(name)) {
            player.sendMessage("NPC does not exist");
            return;
        }
        NPC npc = npcMap.get(name);
        Bukkit.getOnlinePlayers().forEach(npc::hideFrom);
        npcMap.remove(name);
        npcData.removeNPC(name);
    }
    public void showNPCForPlayer(String name, Player player) {
        if (!npcMap.containsKey(name)) {
            player.sendMessage("NPC does not exist");
            return;
        }
        NPC npc = npcMap.get(name);
        npc.showTo(player);
    }
    public void hideNPCForPlayer(String name, Player player) {
        if (!npcMap.containsKey(name)) {
            player.sendMessage("NPC does not exist");
            return;
        }
        NPC npc = npcMap.get(name);
        npc.hideFrom(player);
    }
    public void changeNPCName(String name, String newName, Player player) {
        if (!npcMap.containsKey(name)) {
            player.sendMessage("NPC does not exist");
            return;
        }
        NPC npc = npcMap.get(name);
        npc.updateName(newName);
        npcMap.remove(name);
        npcMap.put(newName, npc);
    }
    public void changeNPCSkin(String name, String skin, Player player) {
        if (!npcMap.containsKey(name)) {
            player.sendMessage("NPC does not exist");
            return;
        }
        NPC npc = npcMap.get(name);
        npc.updateSkin(skin);
        npcData.setNPCSkin(name, skin);
    }
    public void changeNPCLocation(String name, Location location, Player player) {
        if (!npcMap.containsKey(name)) {
            player.sendMessage("NPC does not exist");
            return;
        }
        NPC npc = npcMap.get(name);
        npc.updateLocation(location);
        npcData.setNPCLocation(name, location);
    }
    public void makeNPCRotate(String name, boolean rotate, Player player) {
        if (!npcMap.containsKey(name)) {
            player.sendMessage("NPC does not exist");
            return;
        }
        NPC npc = npcMap.get(name);
        npc.makeNPCRotate(rotate);
        npcData.setNPCRotation(name, rotate);
    }



}
