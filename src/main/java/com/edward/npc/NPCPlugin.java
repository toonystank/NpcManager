package com.edward.npc;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class NPCPlugin extends JavaPlugin {


    private ProtocolManager protocolManager;
    private NPCPlugin npcManager;

    @Override
    public void onEnable() {
        npcManager = this;
        protocolManager = ProtocolLibrary.getProtocolManager();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
