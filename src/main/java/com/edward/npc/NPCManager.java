package com.edward.npc;


import java.util.ArrayList;
import java.util.List;

public class NPCManager {

    private final NPCPlugin npcPlugin;
    private final List<NPC> NPCs = new ArrayList<>();

    public NPCManager(NPCPlugin npcPlugin) {
        this.npcPlugin = npcPlugin;
    }

}
