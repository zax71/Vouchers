/*
 * Vouchers
 * Copyright 2022 Kiran Hart
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ca.tweetzy.vouchers.commands;

import ca.tweetzy.feather.utils.Common;
import ca.tweetzy.vouchers.Vouchers;
import ca.tweetzy.vouchers.api.voucher.Voucher;
import ca.tweetzy.vouchers.gui.GUIVouchersAdmin;
import ca.tweetzy.vouchers.impl.importer.VouchersImporter;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandAlias("vouchers")
public class VouchersCommand extends BaseCommand {

	@Default
	@CommandPermission("vouchers.admin")
	public static void onAdmin(Player sender, String[] args) {
		Vouchers.getGuiManager().showGUI(sender, new GUIVouchersAdmin());
	}

	@Subcommand("import")
	@CommandPermission("vouchers.command.import")
	public static void onImport(Player sender, String[] args) {
		new VouchersImporter().load();

		Common.tell(sender, "&aImported any vouchers found within the exported v2 file. /vouchers to view");
		Common.tell(sender, "&cWhile the importer shouldn't miss anything, it's always recommended to go back");
		Common.tell(sender, "&cinto the /vouchers list and check if everything is correct!");
	}

	@Subcommand("give")
	@CommandPermission("vouchers.command.give")
	@CommandCompletion("@players @vouchers @range:1-10")
	public static void onGive(Player sender, String[] args) {

		// Get values from command and put them in to the right type
		final boolean isGivingAll = args[0].equals("*");
		final Player target = Bukkit.getPlayerExact(args[0]);
		final int amount = Integer.parseInt(args[2]);
		final Voucher voucherFound = Vouchers.getVoucherManager().find(args[1]);

		if (isGivingAll)
			for (Player player : Bukkit.getOnlinePlayers()) {
				for (int i = 0; i < amount; i++)
					player.getInventory().addItem(voucherFound.buildItem());
			}
		else {
			for (int i = 0; i < amount; i++)
				target.getInventory().addItem(voucherFound.buildItem());
		}
	}

	@Subcommand("help")
	@HelpCommand
	public static void onHelp(Player sender, String[] args) {
		Common.tell(sender, "/vouchers usage");
		Common.tell(sender, "&l/vouchers give <player/*> <voucher> [count] &r- gives a player the specified number of vouchers or * for all players");
		Common.tell(sender, "&l/vouchers import &r- Imports V2 vouchers in to V3");
		Common.tell(sender, "&l/vouchers &r- opens the voucher editing menu");
		Common.tell(sender, "&l/vouchers help&r - opens this menu");
	}
}