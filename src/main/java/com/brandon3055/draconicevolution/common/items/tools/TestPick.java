package draconicevolution.common.items.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import com.google.common.collect.Sets;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TestPick extends ItemTool {

	public static Material[] materialsPick = { Material.anvil, Material.circuits, Material.coral, Material.glass, Material.ice, Material.iron, Material.rock };
	private static Set<Block> minableBlocks = Sets.newHashSet();
	public TestPick() {
		super(0, ToolMaterial.EMERALD, minableBlocks);
		this.setUnlocalizedName("testPick");
		this.setCreativeTab(CreativeTabs.tabTools);
		GameRegistry.registerItem(this, "testPick");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(final IIconRegister iconRegister)
	{
		this.itemIcon = iconRegister.registerIcon("minecraft:diamond_pickaxe");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int par1)
	{
		return itemIcon;
	}

	@Override
	public boolean onBlockStartBreak(final ItemStack stack, final int x, final int y, final int z, final EntityPlayer player)
	{
		World world = player.worldObj;
		int fortune = EnchantmentHelper.getFortuneModifier(player);
		boolean silk = EnchantmentHelper.getSilkTouchModifier(player);

		disSquare(x, y, z, player, world, silk, fortune, stack);

		return false;
	}

	public static void disSquare(int x, int y, int z, final EntityPlayer player, final World world, final boolean silk, final int fortune, ItemStack stack)
	{
		int sizeX = 1;
		int sizeY = 1;
		int sizeZ = 1;

		for (int x1 = x - sizeX; x1 <= x + sizeX; x1++) {
			for (int y1 = y - sizeY; y1 <= y + sizeY; y1++) {
				for (int z1 = z - sizeZ; z1 <= z + sizeZ; z1++) {
					mineBlock(x1, y1, z1, player, world, silk, fortune, stack);
				}
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void mineBlock(int x, int y, int z, EntityPlayer player, World world, boolean silk, int fortune, ItemStack stack)
	{
		Block block = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		Material mat = block.getMaterial();

		if ((block != null) && (!block.isAir(world, x, y, z)) && (block.getPlayerRelativeBlockHardness(player, world, x, y, z) != 0.0F)) {
			List<ItemStack> items = new ArrayList();

			if ((!block.canHarvestBlock(player, meta)) || (!isRightMaterial(mat, materialsPick))) {
				return;
			}

			if ((silk) && (block.canSilkHarvest(world, player, x, y, z, meta))) {
				if (block == Blocks.lit_redstone_ore)
					items.add(new ItemStack(Item.getItemFromBlock(Blocks.redstone_ore)));
				else
					items.add(new ItemStack(block.getItem(world, x, y, z), 1, meta));
			} else {
				items.addAll(block.getDrops(world, x, y, z, meta, fortune));
				block.dropXpOnBlockBreak(world, x, y, z, block.getExpDrop(world, meta, fortune));
			}

			world.setBlockToAir(x, y, z);

			if (!world.isRemote) {
				for (final ItemStack item : items) {
					world.spawnEntityInWorld(new EntityItem(world, player.posX, player.posY, player.posZ, item));
				}
			}
		}
	}

	public static boolean isRightMaterial(final Material material, final Material[] materialsListing)
	{
		for (final Material mat : materialsListing) {
			if (material == mat) {
				return true;
			}
		}
		return false;
	}
}