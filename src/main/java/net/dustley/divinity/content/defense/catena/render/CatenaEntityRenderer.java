package net.dustley.divinity.content.defense.catena.render;

import net.dustley.divinity.Divinity;
import net.dustley.divinity.content.defense.catena.CatenaEntity;
import net.dustley.lemon.modules.citrus_physics.component.ActorComponent;
import net.dustley.lemon.modules.math.conversion.JomlToMC;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import org.joml.Vector3d;

import static java.lang.Math.toRadians;

public class CatenaEntityRenderer extends LivingEntityRenderer<CatenaEntity, CatenaEntityModel> {

    public Vec2f chainTextureSize() { return new Vec2f((1/16f) * 6, (1/16f) * 9); }

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
        if(entity.ownerEntity != null) {
            entity.startEntityConstraint.position = entity.getChainStartLerpedPos(tickDelta);
            entity.endEntityConstraint.position = entity.getChainEndLerpedPos(tickDelta);

            drawChain(entity, matrices, tickDelta, vertexConsumers);
        }
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    public boolean isEven(int num) {
        return num % 2 == 0;
    }

    public void drawChain(CatenaEntity entity, MatrixStack matrices, float delta, VertexConsumerProvider vertexConsumers) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(getChainTexture()));
        World world = entity.getWorld();
        LivingEntity owner = entity.ownerEntity;

        for (int segmentID = 0; segmentID < entity.shownSegments - 1; segmentID++) {
            var segment = entity.chainSegments[segmentID];
            var actor = segment.get(ActorComponent.class);
            var lightA = world.getLightLevel(BlockPos.ofFloored(JomlToMC.fromVector3d(actor.position)));

            var nextSegment = entity.chainSegments[segmentID+1];
            var nextActor = nextSegment.get(ActorComponent.class);
            var lightB = world.getLightLevel(BlockPos.ofFloored(JomlToMC.fromVector3d(nextActor.position)));

            var forwardVector = nextActor.position.sub(actor.position, new Vector3d()).normalize();
            var horizontalVector = nextActor.position.sub(actor.position, new Vector3d()).mul(1.0,0.0,1.0).normalize();

            var rightVector = horizontalVector.rotateY(toRadians(90.0), new Vector3d());
            if(!isEven(segmentID)) rightVector.rotateAxis(toRadians(90.0), forwardVector.x, forwardVector.y, forwardVector.z);
            rightVector.rotateAxis(toRadians(45.0), forwardVector.x, forwardVector.y, forwardVector.z);

            var nextPos = actor.position.add(forwardVector.mul((chainTextureSize().y + (1/16f)), new Vector3d()), new Vector3d());
            drawSegment(vertexConsumer, JomlToMC.fromVector3d(actor.position), JomlToMC.fromVector3d(nextPos), JomlToMC.fromVector3d(rightVector), lightA, lightB);
        }
    }


    public void drawSegment(VertexConsumer vertexConsumer, Vec3d pos, Vec3d nextPos, Vec3d rightVector, int lightA, int lightB) {
        var cam = MinecraftClient.getInstance().gameRenderer.getCamera().getPos().toVector3f();
        double width = chainTextureSize().x * 0.5;

        // Draw 2 triangles
        Vec3d a = pos.add(rightVector.multiply(width));
        Vec3d b = pos.add(rightVector.multiply(-width));
        Vec3d c = nextPos.add(rightVector.multiply(width));
        Vec3d d = nextPos.add(rightVector.multiply(-width));
        vertexConsumer.vertex(new Matrix4f(), (float) a.x - cam.x, (float) a.y - cam.y, (float) a.z - cam.z).overlay(OverlayTexture.DEFAULT_UV).light(lightA*16).color(255, 255, 255, 255).normal(0f,1f,0f).texture(0, 0);
        vertexConsumer.vertex(new Matrix4f(), (float) b.x - cam.x, (float) b.y - cam.y, (float) b.z - cam.z).overlay(OverlayTexture.DEFAULT_UV).light(lightA*16).color(255, 255, 255, 255).normal(0f,1f,0f).texture(1, 0);
        vertexConsumer.vertex(new Matrix4f(), (float) c.x - cam.x, (float) c.y - cam.y, (float) c.z - cam.z).overlay(OverlayTexture.DEFAULT_UV).light(lightB*16).color(255, 255, 255, 255).normal(0f,1f,0f).texture(0, 1);
        vertexConsumer.vertex(new Matrix4f(), (float) d.x - cam.x, (float) d.y - cam.y, (float) d.z - cam.z).overlay(OverlayTexture.DEFAULT_UV).light(lightB*16).color(255, 255, 255, 255).normal(0f,1f,0f).texture(1, 1);

        a = pos.add(rightVector.multiply(-width));
        b = pos.add(rightVector.multiply(width));
        c = nextPos.add(rightVector.multiply(-width));
        d = nextPos.add(rightVector.multiply(width));
        vertexConsumer.vertex(new Matrix4f(), (float) a.x - cam.x, (float) a.y - cam.y, (float) a.z - cam.z).overlay(OverlayTexture.DEFAULT_UV).light(lightA*16).color(255, 255, 255, 255).normal(0f,1f,0f).texture(1, 0);
        vertexConsumer.vertex(new Matrix4f(), (float) b.x - cam.x, (float) b.y - cam.y, (float) b.z - cam.z).overlay(OverlayTexture.DEFAULT_UV).light(lightA*16).color(255, 255, 255, 255).normal(0f,1f,0f).texture(0, 0);
        vertexConsumer.vertex(new Matrix4f(), (float) c.x - cam.x, (float) c.y - cam.y, (float) c.z - cam.z).overlay(OverlayTexture.DEFAULT_UV).light(lightB*16).color(255, 255, 255, 255).normal(0f,1f,0f).texture(1, 1);
        vertexConsumer.vertex(new Matrix4f(), (float) d.x - cam.x, (float) d.y - cam.y, (float) d.z - cam.z).overlay(OverlayTexture.DEFAULT_UV).light(lightB*16).color(255, 255, 255, 255).normal(0f,1f,0f).texture(0, 1);

    }

}
