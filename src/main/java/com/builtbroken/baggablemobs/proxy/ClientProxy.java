package com.builtbroken.baggablemobs.proxy;

import net.minecraft.client.Minecraft;

public class ClientProxy extends CommonProxy
{
    @Override
    public void refreshResources()
    {
        Minecraft.getMinecraft().refreshResources();
    }
}
