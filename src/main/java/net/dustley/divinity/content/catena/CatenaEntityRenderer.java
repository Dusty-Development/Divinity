package net.dustley.divinity.content.catena;

import net.dustley.divinity.Divinity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import org.joml.Vector3d;

public class CatenaEntityRenderer extends LivingEntityRenderer<CatenaEntity, CatenaEntityModel> {

    public CatenaEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new CatenaEntityModel(ctx.getPart(CatenaEntityModel.modelLayer)), 0.25f);
    }

    @Override
    public Identifier getTexture(CatenaEntity entity) { return Divinity.identifier("textures/entity/catena/catena_ball.png"); }
    public Identifier getChainTexture() { return Divinity.identifier("textures/entity/catena/catena_chain.png"); }

    @Override
    protected void renderLabelIfPresent(CatenaEntity entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float tickDelta) { }

    @Override
    public void render(CatenaEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        drawChain(entity, matrices, tickDelta, vertexConsumers);
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    public void drawChain(CatenaEntity entity, MatrixStack matrices, float delta, VertexConsumerProvider vertexConsumers) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(getChainTexture()));
        World world = entity.getWorld();
        LivingEntity owner = entity.owner;

        if(owner != null) {
            double catenaHeight = 0.875;
            double ownerHeight = entity.getEyeHeight(entity.getPose()) * 0.5;
            Vec3d entityPos = entity.getLerpedPos(delta).add(0.0, catenaHeight, 0.0);
            Vec3d ownerPos = owner.getLerpedPos(delta).add(0.0, ownerHeight, 0.0);

            Vec3d direction = ownerPos.subtract(entityPos).normalize();
            double distance = ownerPos.subtract(entityPos).length();

            double rotationDegrees = Math.toRadians(90.0);
            Vec3d upVector = new Vec3d(0.0, 1.0, 0.0);
            Vector3d rightDirection = new Vector3d(upVector.x, upVector.y, upVector.z).rotateAxis(rotationDegrees, direction.x, direction.y, direction.z).normalize();
            Vec3d rightVector = new Vec3d(rightDirection.x, rightDirection.y, rightDirection.z);

            double max = Math.ceil(distance);
            for (int i = 0; i < max; i++) {
                Vec3d forwardOffset = direction;

                if (i >= max - 1) {
                    double length = distance - i;
                    forwardOffset = ownerPos.subtract(entityPos).normalize().multiply(length);
                }

                drawSegment(new Matrix4f(), vertexConsumer, entityPos.add(direction.multiply(i)), forwardOffset, rightVector, world.getLightLevel(entity.getBlockPos()), world.getLightLevel(owner.getBlockPos()));
                drawSegment(new Matrix4f(), vertexConsumer, entityPos.add(direction.multiply(i)), forwardOffset, rightVector.multiply(-1.0), world.getLightLevel(entity.getBlockPos()), world.getLightLevel(owner.getBlockPos()));
            }
        }
    }

    public void drawSegment(Matrix4f matrix, VertexConsumer vertexConsumer, Vec3d pos, Vec3d forwardOffset, Vec3d rightVector, int lightA, int lightB) {
        double width = 0.0625 * 2;

        Vec3d a = pos.add(rightVector.multiply(width));
        Vec3d b = pos.add(rightVector.multiply(-width));

        Vec3d c = a.add(forwardOffset);
        Vec3d d = b.add(forwardOffset);

        float segment = ((float) c.subtract(a).length());

        var cam = MinecraftClient.getInstance().gameRenderer.getCamera().getPos().toVector3f();
        vertexConsumer.vertex(matrix, (float) a.x - cam.x, (float) a.y - cam.y, (float) a.z - cam.z).overlay(OverlayTexture.DEFAULT_UV).light(lightA*17).color(255, 255, 255, 255).normal(0f,1f,0f).texture(0, 0);
        vertexConsumer.vertex(matrix, (float) b.x - cam.x, (float) b.y - cam.y, (float) b.z - cam.z).overlay(OverlayTexture.DEFAULT_UV).light(lightA*17).color(255, 255, 255, 255).normal(0f,1f,0f).texture(1, 0);
        vertexConsumer.vertex(matrix, (float) c.x - cam.x, (float) c.y - cam.y, (float) c.z - cam.z).overlay(OverlayTexture.DEFAULT_UV).light(lightB*17).color(255, 255, 255, 255).normal(0f,1f,0f).texture(0, segment);
        vertexConsumer.vertex(matrix, (float) d.x - cam.x, (float) d.y - cam.y, (float) d.z - cam.z).overlay(OverlayTexture.DEFAULT_UV).light(lightB*17).color(255, 255, 255, 255).normal(0f,1f,0f).texture(1, segment);

    }

}
