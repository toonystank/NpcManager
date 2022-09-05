package com.edward.npc.utils;

import com.edward.npc.NPCPlugin;

public enum Language {
    PREFIX("Prefix"),
    DEFAULT_NPC_NAME("Default_npc_name"),
    COMMAND_ERROR("Command_error"),
    COMMAND_NO_PERMISSION("Command_no_permission"),
    COMMAND_NO_CONSOLE("Command_no_console"),
    COMMAND_NPC_NOT_FOUND("Command_npc_not_found"),
    COMMAND_NPC_ALREADY_EXISTS("Command_npc_already_exists"),
    COMMAND_NPC_CREATED("Command_npc_created"),
    COMMAND_NPC_DELETED("Command_npc_deleted"),
    COMMAND_NPC_RENAMED("Command_npc_renamed"),
    COMMAND_NPC_SET_LOCATION("Command_npc_set_location"),
    COMMAND_NPC_SET_SKIN("Command_npc_set_skin"),
    COMMAND_NPC_SET_NAME("Command_npc_set_name"),
    COMMAND_NPC_SET_ROTATION("Command_npc_set_rotation"),
    NPC_VISIBILITY_CHANGED("Npc_visibility")
    ;

    private final String message;
    Language(String message) {
        this.message = message;
    }
    public String getMessage() {
        return NPCPlugin.getNpcPlugin().getLangConfig().getString(message);
    }
    public String getValue() {
        return message;
    }
}
