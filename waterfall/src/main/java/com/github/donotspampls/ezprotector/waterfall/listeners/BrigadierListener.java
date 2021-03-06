/*
 * eZProtector - Copyright (C) 2018-2019 DoNotSpamPls
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.donotspampls.ezprotector.waterfall.listeners;

import com.github.donotspampls.ezprotector.waterfall.Main;
import io.github.waterfallmc.waterfall.event.ProxyDefineCommandsEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;

public class BrigadierListener implements Listener {

    /**
     * Removes forbidden commands from Brigadier's command tree (1.13)
     *
     * @param event The event which removes the tab completions from the client.
     */
    @EventHandler
    public void onCommandSend(ProxyDefineCommandsEvent event) {
        if (!(event.getReceiver() instanceof ProxiedPlayer)) return;

        ProxiedPlayer player = (ProxiedPlayer) event.getReceiver();
        Configuration config = Main.getConfig();

        if (config.getBoolean("tab-completion.blocked") && !player.hasPermission("ezprotector.bypass.command.tabcomplete")) {
            for (String cmd : config.getStringList("tab-completion.blacklisted")) {
                event.getCommands().remove(cmd);
            }
        }
    }

}
