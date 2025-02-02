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

package ca.tweetzy.vouchers.settings;

import ca.tweetzy.feather.config.tweetzy.ConfigEntry;
import ca.tweetzy.feather.config.tweetzy.TweetzyYamlConfig;
import ca.tweetzy.vouchers.Vouchers;

import java.util.List;

public final class Locale {

	static final TweetzyYamlConfig config = Vouchers.getLangConfig();

	public static final ConfigEntry VOUCHER_EXISTS_ALREADY = new ConfigEntry(config, "Voucher Already Exists", "&cA voucher with that id already exists");
	public static final ConfigEntry NOT_A_NUMBER = new ConfigEntry(config, "Not A Number", "&cThat is not a valid number!");
	public static final ConfigEntry PLAYER_OFFLINE = new ConfigEntry(config, "Player Offline", "&cThat player is not currently online!");
	public static final ConfigEntry REDEEM_LIMIT_REACHED = new ConfigEntry(config, "Redeem Limit Reached", "&cYou cannot redeem that voucher anymore!");
	public static final ConfigEntry NOT_ALLOWED_TO_USE = new ConfigEntry(config, "Not Allowed To Use", "&cYou are not allowed to use that voucher");
	public static final ConfigEntry WAIT_FOR_COOLDOWN = new ConfigEntry(config, "Wait For Cooldown", "&cYou can redeem that voucher in &4%cooldown_time% &cseconds");


	public static final ConfigEntry GUI_CONFIRM_TITLE = new ConfigEntry(config, "Gui.Confirm.Title", "&bVouchers &8> &7Confirm Action");
	public static final ConfigEntry GUI_CONFIRM_ITEM_YES_NAME = new ConfigEntry(config, "Gui.Confirm.Items.Yes.Name", "&a&lConfirm");
	public static final ConfigEntry GUI_CONFIRM_ITEM_YES_LORE = new ConfigEntry(config, "Gui.Confirm.Items.Yes.Lore", List.of("&b&lClick &8» &7To confirm action"));
	public static final ConfigEntry GUI_CONFIRM_ITEM_NO_NAME = new ConfigEntry(config, "Gui.Confirm.Items.No.Name", "&c&lCancel");
	public static final ConfigEntry GUI_CONFIRM_ITEM_NO_LORE = new ConfigEntry(config, "Gui.Confirm.Items.No.Lore", List.of("&b&lClick &8» &7To cancel action"));

	public static final ConfigEntry GUI_REWARD_SELECT_TITLE = new ConfigEntry(config, "Gui.Select Reward.Title", "&bVouchers &8> &7Select Reward");
	public static final ConfigEntry GUI_REWARD_SELECT_CMD_NAME = new ConfigEntry(config, "Gui.Select Reward.Items.Command.Name", "&B&lCommand Reward");
	public static final ConfigEntry GUI_REWARD_SELECT_CMD_LORE = new ConfigEntry(config, "Gui.Select Reward.Items.Command.Lore", List.of(
			"&7Command&f: &b%reward_command%",
			"&7Chance&F: &b%reward_chance%",
			"",
			"&b&lClick &8» &7To select this reward"
	));

	public static final ConfigEntry GUI_REWARD_SELECT_ITEM_LORE = new ConfigEntry(config, "Gui.Select Reward.Items.Item.Lore", List.of(
			"&7Chance&F: &b%reward_chance%",
			"",
			"&b&lClick &8» &7To select this reward"
	));

	public static boolean setup() {
		return config.init();
	}
}
