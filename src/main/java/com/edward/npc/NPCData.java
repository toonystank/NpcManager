package com.edward.npc;

import com.edward.npc.utils.ConfigManager;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.io.IOException;

public class NPCData extends ConfigManager {


    public NPCData(Plugin plugin) throws IOException {
        super(plugin, "data.yml", false, false);
    }

    public void addNPC(String name) {
        this.getConfig().getConfigurationSection("npc").createSection(name);
    }
    public void removeNPC(String name) {
        this.getConfig().getConfigurationSection("npc").set(name, null);
    }
    public void setNPCLocation(String name, Location location) {
        this.set("npc." + name + ".location", location);
    }
    public Location getNPCLocation(String name) {
        return (Location) this.get("npc." + name + ".location");
    }
    public void setNPCSkin(String name, String skin) {
        this.set("npc." + name + ".skin", skin);
    }
    public String getNPCSkin(String name) {
        return this.getString("npc." + name + ".skin");
    }
    public void setNPCRotation(String name, boolean rotation) {
        this.set("npc." + name + ".rotation", rotation);
    }
    public boolean getNPCRotation(String name) {
        return this.getBoolean("npc." + name + ".rotation");
    }
    public void setNPOCONameVisibility(String name, boolean visibility) {
        this.set("npc." + name + ".oNameVisibility", visibility);
    }
    public boolean getNPCONameVisibility(String name) {
        return this.getBoolean("npc." + name + ".oNameVisibility");
    }
}
