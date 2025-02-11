package net.dustley.divinity.content.nivora;

import net.minecraft.block.Blocks;
import net.minecraft.block.HeavyCoreBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;

public class NivoraRenderer {

    public ClientWorld world;
    public NivoraInstance instance;

    public NivoraRenderer(ClientWorld clientWorld, NivoraInstance nivoraInstance) {
        world = clientWorld;
        instance = nivoraInstance;
    }

    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, float f, float g, int i) {
        matrixStack.push();

        matrixStack.pop();
    }
}
