package net.dustley.divinity.registry;

import net.dustley.divinity.content.defense.catena.network.ConnectCatenaS2CPacketPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

import static net.dustley.divinity.content.defense.catena.network.ConnectCatenaS2CPacketPayload.CATENA_CLIENT_PACKET_ID;

public class ModNetworking {

    public static void onInit() {
        PayloadTypeRegistry.playS2C().register(CATENA_CLIENT_PACKET_ID, ConnectCatenaS2CPacketPayload.CODEC);
    }

    public static void onInitClient() {
        ClientPlayNetworking.registerGlobalReceiver(CATENA_CLIENT_PACKET_ID, ConnectCatenaS2CPacketPayload::receive);
    }

}
