package p455w0rd.sbm.baggablemobs.network;

import java.io.*;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import p455w0rd.sbm.baggablemobs.init.ModConfig.MobListMode;
import p455w0rd.sbm.baggablemobs.init.ModConfig.Options;

/**
 * @author p455w0rd
 *
 */
public class PacketConfigSync implements IMessage {

	public Map<String, Object> values;

	public PacketConfigSync() {
	}

	public PacketConfigSync(Map<String, Object> valuesIn) {
		values = valuesIn;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void fromBytes(ByteBuf buf) {
		short len = buf.readShort();
		byte[] compressedBody = new byte[len];

		for (short i = 0; i < len; i++) {
			compressedBody[i] = buf.readByte();
		}

		try {
			ObjectInputStream obj = new ObjectInputStream(new GZIPInputStream(new ByteArrayInputStream(compressedBody)));
			values = (Map<String, Object>) obj.readObject();
			obj.close();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteArrayOutputStream obj = new ByteArrayOutputStream();

		try {
			GZIPOutputStream gzip = new GZIPOutputStream(obj);
			ObjectOutputStream objStream = new ObjectOutputStream(gzip);
			objStream.writeObject(values);
			objStream.close();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		buf.writeShort(obj.size());
		buf.writeBytes(obj.toByteArray());
	}

	public static class Handler implements IMessageHandler<PacketConfigSync, IMessage> {
		@Override
		public IMessage onMessage(final PacketConfigSync message, final MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				Options.MOB_BAG_MAX_STACKSIZE = (int) message.values.get("MobBagMaxStackSize");
				Options.MOB_LIST_MODE = (boolean) message.values.get("MobListMode") ? MobListMode.BLACKLIST : MobListMode.WHITELIST;
				Options.DISABLE_CAPTURING_HOSTILE_MOBS = (boolean) message.values.get("DisableHostileMobCapture");
				Options.MOB_LIST = (String[]) message.values.get("MobList");
			});
			return null;
		}
	}

}
