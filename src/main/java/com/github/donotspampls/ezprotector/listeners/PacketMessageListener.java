/*
 * eZProtector - Copyright (C) 2018 DoNotSpamPls
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.donotspampls.ezprotector.listeners;

import com.github.donotspampls.ezprotector.Main;
import com.github.donotspampls.ezprotector.utilities.ExecutionUtil;
import com.github.donotspampls.ezprotector.utilities.MessageUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class PacketMessageListener implements PluginMessageListener {

    private final Main plugin;
    public PacketMessageListener(Main plugin) {
        this.plugin = plugin;
    }

    /**
     * Listen for plugin messages by various mods
     *
     * @param channel The plugin channel used by the mod.
     * @param player The player whose client sent the plugin message.
     * @param value The bytes included in the plugin message.
     */
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] value) {
        FileConfiguration config = plugin.getConfig();

        if (config.getBoolean("mods.5zig.block")) block5Zig(player, channel);
        if (config.getBoolean("mods.bettersprinting.block")) blockBSM(player, channel);

        if (config.getBoolean("mods.schematica.block") && !player.hasPermission("ezprotector.bypass.mod.schematica"))
            player.sendPluginMessage(plugin, Main.SCHEMATICA, getSchematicaPayload());

        if (channel.equalsIgnoreCase(Main.MCBRAND)) {
            // Converts the byte array to a string called "brand"
            String brand = new String(value, StandardCharsets.UTF_8);

            if (config.getBoolean("mods.forge.block")) blockForge(player, brand, config);
            if (config.getBoolean("mods.liteloader.block")) blockLiteLoader(player, brand, config);
        }
    }

    /**
     * Blocks the 5-Zig mod for a certain player.
     *
     * @param player The player to execute the block on.
     * @param channel The channel where the byte array should be sent.
     */
    private void block5Zig(Player player, String channel) {
        if ((channel.equalsIgnoreCase(Main.ZIG)) || (channel.contains("5zig")) && !player.hasPermission("ezprotector.bypass.mod.5zig")) {
            // Send the data output as a byte array to the player
            player.sendPluginMessage(plugin, channel, new byte[] {0x1|0x2|0x4|0x8|0x10|0x20});
        }
    }

    /**
     * Blocks the BetterSprinting mod for a certain player.
     *
     * @param player The player to execute the block on.
     * @param channel The channel where the byte array should be sent.
     */
    private void blockBSM(Player player, String channel) {
        if (channel.equalsIgnoreCase(Main.BSM) && !player.hasPermission("ezprotector.bypass.mod.bettersprinting")) {
            // Send the data output as a byte array to the player
            player.sendPluginMessage(plugin, channel, new byte[] {1});
        }
    }

    /**
     * Kicks a certain player if Forge is found on the client
     *
     * @param player The player to execute the block on.
     * @param brand The brand name recieved in the "MC|Brand" plugin message.
     * @param config The plugin configuration on the particular server.
     */
    private void blockForge(Player player, String brand, FileConfiguration config) {
        if ((brand.equalsIgnoreCase("fml,forge")) || (brand.contains("fml")) || (brand.contains("forge")) && !player.hasPermission("ezprotector.bypass.mod.forge")) {
            String punishCommand = config.getString("mods.forge.punish-command");
            ExecutionUtil.executeConsoleCommand(MessageUtil.placeholders(punishCommand, player, null, null));

            String notifyMessage = MessageUtil.placeholders(config.getString("mods.forge.warning-message"), player, null, null);
            ExecutionUtil.notifyAdmins(notifyMessage, "ezprotector.notify.mod.forge");
        }
    }

    /**
     * Kicks a certain player if LiteLoader is found on the client
     *
     * @param player The player to execute the block on.
     * @param brand The brand name recieved in the "MC|Brand" plugin message.
     * @param config The plugin configuration on the particular server.
     */
    private void blockLiteLoader(Player player, String brand, FileConfiguration config) {
        if ((brand.contains("Lite")) || (brand.equalsIgnoreCase("LiteLoader")) && !player.hasPermission("ezprotector.bypass.mod.liteloader")) {
            String punishCommand = config.getString("mods.liteloader.punish-command");
            ExecutionUtil.executeConsoleCommand(MessageUtil.placeholders(punishCommand, player, null, null));

            String notifyMessage = MessageUtil.placeholders(config.getString("mods.liteloader.warning-message"), player, null, null);
            ExecutionUtil.notifyAdmins(notifyMessage, "ezprotector.notify.mod.liteloader");
        }
    }

    /**
     * Creates a byte array with options that tell Schematica which features to block (used for 1.8+)
     *
     * @return The byte array containing the Schematica block configuration.
     */
    private static byte[] getSchematicaPayload() {
        // Create the byte array and data stream
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        try {
            // Add bytes to data stream
            dataOutputStream.writeByte(0);
            dataOutputStream.writeBoolean(false);
            dataOutputStream.writeBoolean(false);
            dataOutputStream.writeBoolean(false);

            // Convert the data stream to a byte array and return it
            return byteArrayOutputStream.toByteArray();
        } catch (IOException ignored) {
            return null;
        }
    }

}
