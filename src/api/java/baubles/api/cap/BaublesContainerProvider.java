//package baubles.api.cap;
//
//import net.minecraft.nbt.CompoundNBT;
//import net.minecraft.util.Direction;
//import net.minecraftforge.common.capabilities.Capability;
//import net.minecraftforge.common.capabilities.ICapabilityProvider;
//import net.minecraftforge.common.util.INBTSerializable;
//
//public class BaublesContainerProvider implements INBTSerializable<CompoundNBT>, ICapabilityProvider {
//
//	private final BaublesContainer container;
//
//	public BaublesContainerProvider(BaublesContainer container) {
//		this.container = container;
//	}
//
//	@Override
//	public boolean hasCapability (Capability<?> capability, Direction facing) {
//		return capability == BaublesCapabilities.CAPABILITY_BAUBLES;
//	}
//
//	@Override
//	@SuppressWarnings("unchecked")
//	public <T> T getCapability (Capability<T> capability, Direction facing) {
//		if (capability == BaublesCapabilities.CAPABILITY_BAUBLES) return (T) this.container;
//		return null;
//	}
//
//	@Override
//	public CompoundNBT serializeNBT () {
//		return this.container.serializeNBT();
//	}
//
//	@Override
//	public void deserializeNBT (CompoundNBT nbt) {
//		this.container.deserializeNBT(nbt);
//	}
//}
