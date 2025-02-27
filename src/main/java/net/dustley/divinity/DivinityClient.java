package net.dustley.divinity;

import net.dustley.divinity.registry.ModEntities;
import net.dustley.divinity.registry.ModItems;
import net.dustley.divinity.registry.ModNetworking;
import net.fabricmc.api.ClientModInitializer;

public class DivinityClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        ModEntities.onInitClient();
        ModItems.registerClientModItems();
        ModNetworking.onInitClient();

    }
}
