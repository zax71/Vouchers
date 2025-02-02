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

package ca.tweetzy.vouchers.model.manager;

import ca.tweetzy.feather.collection.ProbabilityCollection;
import ca.tweetzy.feather.comp.Titles;
import ca.tweetzy.feather.comp.enums.CompMaterial;
import ca.tweetzy.feather.utils.Common;
import ca.tweetzy.feather.utils.PlayerUtil;
import ca.tweetzy.feather.utils.Replacer;
import ca.tweetzy.vouchers.Vouchers;
import ca.tweetzy.vouchers.api.voucher.*;
import ca.tweetzy.vouchers.gui.GUIRewardSelection;
import ca.tweetzy.vouchers.impl.VoucherRedeem;
import ca.tweetzy.vouchers.settings.Locale;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public final class RedeemManager extends Manager<UUID, Redeem> {

	@Override
	public List<Redeem> getAll() {
		return List.copyOf(this.contents.values());
	}

	@Override
	public Redeem find(@NonNull UUID uuid) {
		return this.contents.getOrDefault(uuid, null);
	}

	@Override
	public void add(@NonNull Redeem redeem) {
		this.contents.put(redeem.getId(), redeem);
	}

	@Override
	public void remove(@NonNull UUID uuid) {
		this.contents.remove(uuid);
	}

	public int getTotalRedeems(@NonNull final UUID playerUUID, @NonNull final String voucherId) {
		return (int) this.contents.values().stream().filter(redeem -> redeem.getUser().equals(playerUUID) && redeem.getVoucherId().equalsIgnoreCase(voucherId)).count();
	}

	public int getTotalRedeems(@NonNull final Player player, @NonNull final Voucher voucher) {
		return getTotalRedeems(player.getUniqueId(), voucher.getId());
	}

	public boolean isAtRedeemLimit(@NonNull final Player player, @NonNull final Voucher voucher) {
		int maxVoucherUses = voucher.getOptions().getMaxUses();
		if (maxVoucherUses <= -1) return false;

		return getTotalRedeems(player, voucher) >= maxVoucherUses;
	}

	public void redeemVoucher(@NonNull final Player player, @NonNull final Voucher voucher, final boolean ignoreRedeemLimit, final boolean ignoreCooldown) {
		// check permission
		if (voucher.getOptions().isRequiresPermission() && !player.hasPermission(voucher.getOptions().getPermission())) {
			Common.tell(player, Locale.NOT_ALLOWED_TO_USE.getString());
			return;
		}

		if (isAtRedeemLimit(player, voucher) && !ignoreRedeemLimit) {
			Common.tell(player, Locale.REDEEM_LIMIT_REACHED.getString());
			return;
		}

		// check cooldown
		if (!ignoreCooldown)
			if (Vouchers.getCooldownManager().isPlayerInCooldown(player.getUniqueId()) && Vouchers.getCooldownManager().isPlayerInCooldownForVoucher(player.getUniqueId(), voucher)) {
				long cooldownTime = Vouchers.getCooldownManager().getCooldownTime(player.getUniqueId(), voucher);

				if (System.currentTimeMillis() < cooldownTime) {
					Common.tell(player, Replacer.replaceVariables(Locale.WAIT_FOR_COOLDOWN.getString(), "cooldown_time", String.format("%,.2f", (cooldownTime - System.currentTimeMillis()) / 1000F)));
					return;
				}
			}

		// collect titles
		if (!voucher.getOptions().getMessages().isEmpty()) {
			final Message titleMessage = voucher.getOptions().getMessages().stream().filter(msg -> msg.getMessageType() == MessageType.TITLE).findFirst().orElse(null);
			final Message subtitleMessage = voucher.getOptions().getMessages().stream().filter(msg -> msg.getMessageType() == MessageType.SUBTITLE).findFirst().orElse(null);

			int fadeIn = 20;
			int fadeOut = 20;
			int stay = 20;

			if (titleMessage != null) {
				fadeIn = titleMessage.getFadeInDuration();
				fadeOut = titleMessage.getFadeOutDuration();
				stay = titleMessage.getStayDuration();
			}

			if (subtitleMessage != null) {
				fadeIn = Math.max(subtitleMessage.getFadeInDuration(), fadeIn);
				fadeOut = Math.max(subtitleMessage.getFadeOutDuration(), fadeOut);
				stay = Math.max(subtitleMessage.getStayDuration(), stay);
			}

			if (!(titleMessage == null && subtitleMessage == null)) {
				Titles.sendTitle(
						player,
						fadeIn,
						stay,
						fadeOut,
						Common.colorize(titleMessage != null ? titleMessage.getColouredAndReplaced(player, voucher) : ""),
						Common.colorize(subtitleMessage != null ? subtitleMessage.getColouredAndReplaced(player, voucher) : "")
				);
			}

			// the other message types
			voucher.getOptions().getMessages().stream().filter(msg -> msg.getMessageType() != MessageType.TITLE && msg.getMessageType() != MessageType.SUBTITLE).collect(Collectors.toList()).forEach(msg -> {
				msg.send(player, voucher);
			});
		}

		// rewards

		switch (voucher.getRewardMode()) {
			case AUTOMATIC -> {
				// automatic means it will give them every reward added to the voucher
				voucher.getRewards().forEach(reward -> reward.execute(player, false));
				takeHand(player, voucher);
				if (!ignoreCooldown)
					Vouchers.getCooldownManager().addPlayerToCooldown(player.getUniqueId(), voucher);
				registerRedeemIfApplicable(player, voucher);
			}
			case REWARD_SELECT -> Vouchers.getGuiManager().showGUI(player, new GUIRewardSelection(voucher, selected -> {
				takeHand(player, voucher);
				player.closeInventory();
				if (!ignoreCooldown)
					Vouchers.getCooldownManager().addPlayerToCooldown(player.getUniqueId(), voucher);
				registerRedeemIfApplicable(player, voucher);
			}));
			case RANDOM -> {
				final ProbabilityCollection<Reward> rewardProbabilityCollection = new ProbabilityCollection<>();
				voucher.getRewards().forEach(reward -> rewardProbabilityCollection.add(reward, (int) reward.getChance()));

				final Reward selectedReward = rewardProbabilityCollection.get();
				selectedReward.execute(player, false);
				takeHand(player, voucher);
				if (!ignoreCooldown)
					Vouchers.getCooldownManager().addPlayerToCooldown(player.getUniqueId(), voucher);
				registerRedeemIfApplicable(player, voucher);
			}
		}
	}

	public void registerRedeemIfApplicable(@NonNull final Player player, @NonNull final Voucher voucher) {
		Vouchers.getDataManager().createVoucherRedeem(new VoucherRedeem(UUID.randomUUID(), player.getUniqueId(), voucher.getId(), System.currentTimeMillis()), (error, createdRedeem) -> {
			if (error == null)
				this.add(createdRedeem);
			else
				error.printStackTrace();
		});
	}

	private void takeHand(@NonNull final Player player, @NonNull final Voucher voucher) {
		if (voucher.getOptions().isRemoveOnUse()) {
			if (PlayerUtil.getHand(player).getAmount() >= 2) {
				PlayerUtil.getHand(player).setAmount(PlayerUtil.getHand(player).getAmount() - 1);
			} else {
				player.getInventory().setItemInMainHand(CompMaterial.AIR.parseItem());
			}

			player.updateInventory();
		}
	}

	@Override
	public void load() {
		this.contents.clear();

		Vouchers.getDataManager().getVoucherRedeems((error, all) -> {
			if (error == null)
				all.forEach(this::add);
		});
	}
}
