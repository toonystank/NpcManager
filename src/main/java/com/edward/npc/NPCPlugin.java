package com.edward.npc;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.PaperCommandManager;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.edward.npc.commands.CommandManager;
import com.edward.npc.utils.ConfigManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Optional;

public final class NPCPlugin extends JavaPlugin {

    @Getter
    private ProtocolManager protocolManager;

    @Getter
    private static NPCPlugin npcPlugin;

    @Getter
    private ConfigManager defaultConfig;

    @Getter
    private ConfigManager langConfig;

    private NPCManager npcManager;

    private boolean isPaper = false;

    @Override
    public void onEnable() {
        npcPlugin = this;
        protocolManager = ProtocolLibrary.getProtocolManager();
        setDefaultConfig();
        setLangConfig();
        setNpcManager();
        initializeCommand();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        this.getServer().getScheduler().cancelTasks(this);
    }

    void setDefaultConfig() {
        try {
            defaultConfig = new ConfigManager(this, "config.yml", false, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void setLangConfig() {
        Optional.ofNullable(defaultConfig.getString("language")).ifPresent(lang -> {
            try {
                langConfig = new ConfigManager(this, lang, false, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        if (langConfig == null) throw new NullPointerException("Language file not found");
    }

    void setNpcManager() {
        npcManager = new NPCManager(this);
    }

    void initializeCommand() {
        try {
            Class.forName("com.destroystokyo.paper");
            this.isPaper = true;
        } catch (ClassNotFoundException ignored) {
        }

        if (this.isPaper) {

            PaperCommandManager pcm = new PaperCommandManager(this);
            pcm.registerCommand(new CommandManager(this, this.npcManager));

            pcm.getCommandCompletions().registerAsyncCompletion("name", context ->
                   this.npcManager.getNpcData().getNPCs()
            );
        } else {

            BukkitCommandManager bcm = new BukkitCommandManager(this);
            bcm.registerCommand(new CommandManager(this, this.npcManager));

            bcm.getCommandCompletions().registerAsyncCompletion("name", context ->
                    this.npcManager.getNpcData().getNPCs()
            );
        }

    }
}
