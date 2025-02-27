package net.dustley.divinity.content.critter.nivora;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class NivoraInstance {

    public World world;
    public PlayerEntity player;
    public NivoraRenderer renderer; // Is null on server

    public NivoraInstance(PlayerEntity player) {
        this.player = player;
        world = player.getWorld();
        if(world.isClient) renderer = new NivoraRenderer((ClientWorld) world, this);
    }

    public void tick() {

    }

}
