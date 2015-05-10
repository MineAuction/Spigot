package cz.sognus.mineauction.utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PlayerUtils {

	public static boolean hasEnoughSpace(Player p, ItemStack istack, int qty) {
		Inventory inv = p.getInventory();

		for (ItemStack stack : inv.getContents()) {
			// Return true if there is free slot for item or actual plus
			// withdrawing qty is lesser than max size
			if (stack == null
					|| stack.getAmount() + qty <= istack.getMaxStackSize())
				return true;
		}
		return false;

	}

}
