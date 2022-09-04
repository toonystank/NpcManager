package com.edward.npc;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.scoreboard.CraftScoreboard;
import org.bukkit.craftbukkit.v1_8_R3.scoreboard.CraftScoreboardManager;
import org.bukkit.entity.Player;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class NPC {

    private final NPCPlugin plugin;

    private final UUID uuid = UUID.randomUUID();

    private final String name;

    @Getter @Setter
    private String skin;

    private String texture;

    private String signature;

    @Getter @Setter
    private boolean nameTagVisibility = true;

    @Getter @Setter
    private List<Player> visiblePlayers = new ArrayList<>();

    private EntityPlayer entity;

    private GameProfile profile;

    @Getter @Setter
    private Location location;

    private PacketPlayOutPlayerInfo packetPlayOutPlayerInfo;
    private PacketPlayOutNamedEntitySpawn packetPlayOutNamedEntitySpawn;


    public NPC(NPCPlugin plugin, String name, String skin, Location location) {
        this.plugin = plugin;
        this.name = name;
        this.skin = skin;
        this.location = location;
    }

    /**
     * Create the Fake EntityPlayer
     */
    public void createEntity() {
        MinecraftServer server = ((CraftServer) plugin.getServer()).getServer();
        WorldServer world = ((CraftWorld) this.location.getWorld()).getHandle();
        PlayerInteractManager interactManager = new PlayerInteractManager(world);
        createGameProfile();
        createSkin();
        this.entity = new EntityPlayer(
                server,
                world,
                this.profile,
                interactManager
        );
        this.entity.setLocation(
                this.location.getX(),
                this.location.getY(),
                this.location.getZ(),
                this.location.getYaw(),
                this.location.getPitch()
        );

    }

    /**
     * Create the GameProfile for the Fake EntityPlayer
     */
    public void createGameProfile() {
        this.profile = new GameProfile(uuid, name);
        if (this.texture != null && this.signature != null) {
            this.profile.getProperties().put("textures", new Property(
                    "textures",
                    texture,
                    signature
            ));
        }
    }

    /**
     * Create the Skin for the Fake EntityPlayer
     */
    public void createSkin() {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + skin);
            InputStreamReader reader = new InputStreamReader(url.openStream());
            String uuid = new JsonParser().parse(reader).getAsJsonObject().get("id").getAsString();

            URL url2 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            InputStreamReader reader2 = new InputStreamReader(url2.openStream());
            JsonObject property = new JsonParser().parse(reader2).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();

            this.texture = property.get("value").getAsString();
            this.signature = property.get("signature").getAsString();

        } catch (Exception ex) {
            this.texture = null;
            this.signature = null;
        }
    }

    /**
     * Updates the skin of the Fake EntityPlayer
     *
     * @param skin Name of the skin
     */
    public void updateSkin(String skin) {
        this.skin = skin;
        visiblePlayers.forEach(this::hideFrom);
        visiblePlayers.forEach(this::showTo);
    }

    /**
     * Updates the location of the Fake EntityPlayer
     * @param location New location to update
     */
    public void updateLocation(Location location) {
        this.location = location;
        this.entity.setLocation(
                this.location.getX(),
                this.location.getY(),
                this.location.getZ(),
                this.location.getYaw(),
                this.location.getPitch()
        );
        createPackets();
        visiblePlayers.forEach(this::hideFrom);
        visiblePlayers.forEach(this::showTo);
    }

    /**
     * Hide name tag of the Fake EntityPlayer
     *
     * @param player Player to hide the name tag from
     */
    public void hideNameTagOfEntity(Player player) {
        CraftScoreboardManager scoreboardManager = ((CraftServer) plugin.getServer()).getScoreboardManager();
        assert scoreboardManager != null;
        CraftScoreboard mainScoreboard = scoreboardManager.getMainScoreboard();
        Scoreboard scoreboard = mainScoreboard.getHandle();

        ScoreboardTeam scoreboardTeam = scoreboard.getTeam(this.name);
        if (scoreboardTeam == null) {
            scoreboardTeam = new ScoreboardTeam(scoreboard, this.name);
        }
        if (!this.nameTagVisibility) {
            scoreboardTeam.setNameTagVisibility(ScoreboardTeamBase.EnumNameTagVisibility.NEVER);
            return;
        }
        scoreboardTeam.setNameTagVisibility(ScoreboardTeamBase.EnumNameTagVisibility.ALWAYS);
        sendPacket(player, new PacketPlayOutScoreboardTeam(scoreboardTeam, 1));
        sendPacket(player, new PacketPlayOutScoreboardTeam(scoreboardTeam, 0));
        sendPacket(player, new PacketPlayOutScoreboardTeam(scoreboardTeam, Collections.singletonList(this.name), 3));
    }
    /**
     * Creates the default npc packets
     */
    public void createPackets() {
        this.packetPlayOutPlayerInfo = new PacketPlayOutPlayerInfo(
                PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER,
                this.entity
        );
        this.packetPlayOutNamedEntitySpawn = new PacketPlayOutNamedEntitySpawn(
                this.entity
        );
    }

    /**
     * Show the Fake EntityPlayer to a player
     * @param player Player to show the Fake EntityPlayer to
     */
    public void showTo(Player player) {
        if (this.visiblePlayers.contains(player)) {
            return;
        }
        this.visiblePlayers.add(player);
        if (entity == null) {
            createEntity();
            createPackets();
        }
        sendPacket(player, this.packetPlayOutPlayerInfo);
        sendPacket(player, this.packetPlayOutNamedEntitySpawn);
        hideNameTagOfEntity(player);

        Bukkit.getServer().getScheduler().runTaskTimer(plugin, () -> {
            Player currentlyOnline = Bukkit.getPlayer(player.getUniqueId());
            if (currentlyOnline == null ||
                    !currentlyOnline.isOnline() ||
                    !visiblePlayers.contains(player)) {
                Thread.currentThread().interrupt();
                return;
            }
            sendHeadRotationPacket(player);
        }, 0, 2);

        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> {
            try {
                PacketPlayOutPlayerInfo removeFromTabPacket = new PacketPlayOutPlayerInfo(
                        PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER,
                        this.entity
                );
                sendPacket(player, removeFromTabPacket);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }, 20);

        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> {
            fixSkinLayer(player);
        }, 8);
    }

    /**
     * Hide the Fake EntityPlayer from a player
     * @param player Player to hide the Fake EntityPlayer from
     */
    public void hideFrom(Player player) {
        if (!visiblePlayers.contains(player)) return;
        visiblePlayers.remove(player);

        PacketPlayOutEntityDestroy entityDestroy = new PacketPlayOutEntityDestroy(this.entity.getId());
        sendPacket(player, entityDestroy);
    }

    @SneakyThrows
    void sendHeadRotationPacket(Player player) {
        Location original = getLocation();
        Location location = original.clone().setDirection(player.getLocation().subtract(original.clone()).toVector());

        byte yaw = (byte) (location.getYaw() * 256 / 360);
        byte pitch = (byte) (location.getPitch() * 256 / 360);

        PacketPlayOutEntityHeadRotation headRotationPacket = new PacketPlayOutEntityHeadRotation(
                this.entity,
                yaw
        );
        sendPacket(player, headRotationPacket);

        PacketPlayOutEntity.PacketPlayOutEntityLook lookPacket = new PacketPlayOutEntity.PacketPlayOutEntityLook(
                this.entity.getId(),
                yaw,
                pitch,
                false
        );
        sendPacket(player, lookPacket);
    }

    void fixSkinLayer(Player player) {
        byte skinFixByte = 0x01 | 0x02 | 0x04 | 0x08 | 0x10 | 0x20 | 0x40;
        PacketPlayOutEntityHeadRotation headRotationPacket = new PacketPlayOutEntityHeadRotation(this.entity, skinFixByte);
        sendPacket(player, headRotationPacket);
    }

    @SneakyThrows
    void sendPacket(Player player, Object packet) {
        if (player == null) return;
        PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;
        connection.sendPacket((Packet<?>) packet);
    }
}
