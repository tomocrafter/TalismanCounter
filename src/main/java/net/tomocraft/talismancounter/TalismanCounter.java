package net.tomocraft.talismancounter;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// This mod just need mixin, so this class is for register the mod. Maybe we don't need this class
@Mod(modid = "talismancounter", name = "TalismanCounter", version = "1.0", useMetadata = true, clientSideOnly = true, acceptedMinecraftVersions = "[1.8.9]")
public class TalismanCounter {

    private static final Logger LOGGER = LogManager.getLogger("RankedRate");

    @Mod.EventHandler
    public void init(final FMLInitializationEvent event) {
        LOGGER.info("Loading the TalismanCounter...");
    }
}
