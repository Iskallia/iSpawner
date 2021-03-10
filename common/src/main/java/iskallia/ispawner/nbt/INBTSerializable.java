package iskallia.ispawner.nbt;

import net.minecraft.nbt.Tag;

public interface INBTSerializable<T extends Tag> {

	T writeToNBT();

	void readFromNBT(T nbt);

}
