package net.dustley.divinity.content.nivora;

import net.minecraft.block.BlockState;
import net.minecraft.block.HeavyCoreBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
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
