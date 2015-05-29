package cz.sognus.mineauction.utils;

import java.util.Arrays;

import org.bukkit.Bukkit;

public class ItemUtils {

	public static final int[] canHaveDamage = { 256, 257, 258, 259, 261, 267,
			268, 269, 270, 271, 272, 273, 274, 275, 276, 277, 278, 279, 283,
			284, 285, 286, 290, 291, 292, 293, 294, 298, 299, 300, 301, 302,
			303, 304, 305, 306, 307, 308, 309, 310, 311, 312, 313, 314, 315,
			316, 317, 346, 359, 398 };
	
	public static boolean canHaveDamage(int typeId)
	{
		for(int test : canHaveDamage)
		{
			if(test == typeId)
			{
				return true;
			}
			
		}
		return false;
	}

}
