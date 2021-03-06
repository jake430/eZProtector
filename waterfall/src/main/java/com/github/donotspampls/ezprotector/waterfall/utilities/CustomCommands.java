/*
 * eZProtector - Copyright (C) 2018-2019 DoNotSpamPls
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.donotspampls.ezprotector.waterfall.utilities;

import com.github.donotspampls.ezprotector.waterfall.Main;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.config.Configuration;

public class CustomCommands {

    /**
     * Intercepts a command if it's found to be blocked by the server admin.
     *
     * @param event The command event from which other information is gathered.
     */
    public static void execute(ChatEvent event) {
        if (!(event.getSender() instanceof ProxiedPlayer)) return;

        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        String command = event.getMessage();
        Configuration config = Main.getConfig();

        for (String message : config.getStringList("custom-commands.commands")) {
            // Replace placeholder with the command executed by the player
            if (command.split(" ")[0].equalsIgnoreCase("/" + message) && !player.hasPermission("ezprotector.bypass.command.custom")) {
                event.setCancelled(true);
                // Replace placeholder with the error message in the config
                String errorMessage = config.getString("custom-commands.error-message");

                if (!errorMessage.trim().isEmpty())
                    player.sendMessage(MessageUtil.placeholders(errorMessage, player, null, "/" + message));

                if (config.getBoolean("custom-commands.punish-player.enabled")) {
                    String punishCommand = config.getString("custom-commands.punish-player.command");
                    ExecutionUtil.executeConsoleCommand(MessageUtil.placeholders(punishCommand, player, errorMessage, "/" + message));
                }

                if (config.getBoolean("custom-commands.notify-admins.enabled")) {
                    String notifyMessage = MessageUtil.placeholders(config.getString("custom-commands.notify-admins.message"), player, null, command);
                    ExecutionUtil.notifyAdmins(notifyMessage, "ezprotector.notify.command.custom");
                }
            }
        }
    }
}
