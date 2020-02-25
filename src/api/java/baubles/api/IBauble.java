//package baubles.api;
//
//import net.minecraft.entity.LivingEntity;
//import net.minecraft.item.ItemStack;
//
///**
// *
// * This interface should be extended by items that can be worn in bauble slots
// *
// * @author Azanor
// */
//
//public interface IBauble {
//
//	/**
//	 * This method return the type of bauble this is.
//	 * Type is used to determine the slots it can go into.
//	 */
//	public BaubleType getBaubleType(ItemStack itemstack);
//
//	/**
//	 * This method is called once per tick if the bauble is being worn by a player
//	 */
//	public default void onWornTick(ItemStack itemstack, LivingEntity player) {
//	}
//
//	/**
//	 * This method is called when the bauble is equipped by a player
//	 */
//	public default void onEquipped(ItemStack itemstack, LivingEntity player) {
//	}
//
//	/**
//	 * This method is called when the bauble is unequipped by a player
//	 */
//	public default void onUnequipped(ItemStack itemstack, LivingEntity player) {
//	}
//
//	/**
//	 * can this bauble be placed in a bauble slot
//	 */
//	public default boolean canEquip(ItemStack itemstack, LivingEntity player) {
//		return true;
//	}
//
//	/**
//	 * Can this bauble be removed from a bauble slot
//	 */
//	public default boolean canUnequip(ItemStack itemstack, LivingEntity player) {
//		return true;
//	}
//
//	/**
//	 * Will bauble automatically sync to client if a change is detected in its NBT or damage values?
//	 * Default is off, so override and set to true if you want to auto sync.
//	 * This sync is not instant, but occurs every 10 ticks (.5 seconds).
//	 */
//	public default boolean willAutoSync(ItemStack itemstack, LivingEntity player) {
//		return false;
//	}
//}
