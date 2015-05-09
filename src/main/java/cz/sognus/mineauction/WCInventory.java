package cz.sognus.mineauction;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * 
 * @author SiDzej (_CJ_)
 * 
 */

public class WCInventory {
	private Player p;
	private Inventory i;

	public WCInventory(Player p) {
		this.p = p;
		i = p.getInventory();
	}

	private void updateInv() {
		p.updateInventory();
	}

	public boolean hasEmptySpaceFor(ItemStack item) {
		for (ItemStack is : i.getContents()) {
			// empty space
			if (is == null)
				return true;
			// partial empty
			if (is.isSimilar(item) && is.getAmount() < is.getMaxStackSize())
				return true;
		}
		return false;
	}

	public boolean hasItemStack(ItemStack item) {
		for (ItemStack is : i.getContents()) {
			if (is == null)
				continue;
			if (is.isSimilar(item))
				return true;
		}
		return false;
	}

	public int removeItems(ItemStack item, int count) {
		item.setAmount(count);
		HashMap<Integer, ItemStack> tmp = i.removeItem(item);
		updateInv();
		if (tmp.isEmpty())
			return 0; // if all good return 0
		return tmp.get(0).getAmount();
	}

	public int addItems(ItemStack item, int count) {
		item.setAmount(count);
		HashMap<Integer, ItemStack> tmp = i.addItem(item);
		updateInv();
		if (tmp.isEmpty())
			return 0; // if all good return 0
		return tmp.get(0).getAmount();
	}

}
