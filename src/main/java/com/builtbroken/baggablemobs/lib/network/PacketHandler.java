package com.builtbroken.baggablemobs.lib.network;

import com.builtbroken.baggablemobs.BaggableMobs;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author p455w0rd
 */
public class PacketHandler
{
    private static SimpleNetworkWrapper INSTANCE = null;

    public static SimpleNetworkWrapper getInstance()
    {
        return INSTANCE;
    }

    public static void init()
    {
        if (INSTANCE == null)
        {
            INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(BaggableMobs.MODID);
        }
        getInstance().registerMessage(PacketConfigSync.Handler.class, PacketConfigSync.class, 0, Side.CLIENT);
    }
}
