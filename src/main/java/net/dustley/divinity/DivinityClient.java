package net.dustley.divinity;

import net.dustley.divinity.registry.ModEntities;
import net.fabricmc.api.ClientModInitializer;

public class DivinityClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        ModEntities.onInitClient();

    }
}
