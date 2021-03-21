package iskallia.ispawner.net.packet;

import net.minecraft.network.PacketByteBuf;

public interface IByteSerializable<T> {

	T writeToBuf(PacketByteBuf buf);

	T readFromBuf(PacketByteBuf buf);

}
