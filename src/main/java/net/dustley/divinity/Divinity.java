package net.dustley.divinity;

import net.dustley.divinity.registry.ModEntities;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

import java.util.logging.Logger;

public class Divinity implements ModInitializer {
    public static String MOD_ID = "divinity";
    public static Logger LOGGER = Logger.getLogger(MOD_ID);

    public static Identifier identifier(String path) { return Identifier.of(MOD_ID, path); }
    public static void log(String msg) { LOGGER.info(msg); }

    @Override
    public void onInitialize() {

        ModEntities.onInit();

    }
}
