package net.dustley.divinity.content.defense.catena.network;

import net.dustley.divinity.Divinity;
import net.dustley.divinity.content.defense.catena.CatenaEntity;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import java.util.UUID;

public class ConnectCatenaS2CPacketPayload implements CustomPayload {

    private final int catena;
    private final UUID player;

    public ConnectCatenaS2CPacketPayload(int catena, UUID player) {
        this.catena = catena;
        this.player = player;
    }

    public ConnectCatenaS2CPacketPayload(RegistryByteBuf buf) {
        this.catena = buf.readInt();
        this.player = buf.readUuid();
    }

    public void write(RegistryByteBuf buf) {
        buf.writeInt(catena);
        buf.writeUuid(player);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return CATENA_CLIENT_PACKET_ID;
    }

    public static final Id<ConnectCatenaS2CPacketPayload> CATENA_CLIENT_PACKET_ID = new Id<>(Divinity.identifier("catena_client_packet"));

    public static final PacketCodec<RegistryByteBuf, ConnectCatenaS2CPacketPayload> CODEC = PacketCodec.of(ConnectCatenaS2CPacketPayload::write, ConnectCatenaS2CPacketPayload::new);

    public static void receive(ConnectCatenaS2CPacketPayload payload, ClientPlayNetworking.Context context) {
        UUID player = payload.player;

        context.client().execute(() -> {
            var world = context.client().world;
            CatenaEntity entity = null;
            if (world != null) entity = (CatenaEntity) world.getEntityById(payload.catena);
            if (entity != null) entity.connectToPlayer(world.getPlayerByUuid(player), true);
        });
    }

}