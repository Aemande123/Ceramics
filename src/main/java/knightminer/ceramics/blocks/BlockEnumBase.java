package knightminer.ceramics.blocks;

import knightminer.ceramics.Ceramics;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;

public abstract class BlockEnumBase<T extends Enum<T> & IStringSerializable & BlockEnumBase.IEnumMeta> extends Block implements IBlockEnum<T> {

	private PropertyEnum<T> prop;
	private T[] values;

	public BlockEnumBase(Material material, PropertyEnum<T> prop) {
		super(setTemp(material, prop));
		this.prop = prop;

		this.setCreativeTab(Ceramics.tab);
		values = prop.getValueClass().getEnumConstants();
	}

	// works around the property not being defined for the blockstate during construction
	private static PropertyEnum<?> temp;
	private static Material setTemp(Material material, PropertyEnum<?> prop) {
		temp = prop;
		return material;
	}

	@Override
	public PropertyEnum<T> getMappingProperty() {
		return prop;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		if(prop == null) {
			return new BlockStateContainer(this, new IProperty[] {temp});
		}
		return new BlockStateContainer(this, new IProperty[] {prop});
	}

	public T fromMeta(int meta) {
		if(meta >= values.length || meta < 0) {
			meta = 0;
		}

		return values[meta];
	}

	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(prop, fromMeta(meta));
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	@Override
	public int getMetaFromState(IBlockState state) {
		return (state.getValue(prop)).getMeta();
	}

	/**
	 * Gets the metadata of the item this Block can drop. This method is called when the block gets destroyed. It
	 * returns the metadata of the dropped item based on the old metadata of the block.
	 */
	@Override
	public int damageDropped(IBlockState state) {
		return getMetaFromState(state);
	}

	/**
	 * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
	 */
	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
		for(T type : values) {
			if(type.shouldDisplay()) {
				list.add(new ItemStack(this, 1, type.getMeta()));
			}
		}
	}

	public interface IEnumMeta {
		public int getMeta();
		public boolean shouldDisplay();
	}
}
