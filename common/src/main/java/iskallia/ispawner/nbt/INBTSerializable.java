package iskallia.ispawner.nbt;

import net.minecraft.nbt.NbtElement;

public interface INBTSerializable<T extends NbtElement> {

	T writeToNBT();

	void readFromNBT(T nbt);

}
