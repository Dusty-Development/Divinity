package net.dustley.divinity.content.defense.catena.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.dustley.divinity.Divinity;
import net.dustley.lemon.modules.camera_effects.freeze_frames.FreezeFrameManager;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

public class CatenaSpawnOverlay implements HudRenderCallback {
    private static final Identifier OVERLAY = Divinity.identifier("textures/entity/catena/catena_spawn_overlay.png");

    float fade = 0f;

    public CatenaSpawnOverlay() {
    }

    public void onHudRender(DrawContext context, RenderTickCounter counter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null && client.player != null) {
            RenderSystem.enableBlend();
            int screenWidth = client.getWindow().getScaledWidth();
            int screenHeight = client.getWindow().getScaledHeight();

            fade += counter.getTickDelta(false) * 0.1f;
            fade = Math.min(fade, 1f); // Clamp to 1 to prevent overflow

            if (FreezeFrameManager.getOverlayTime()) {
                fade = 0f; // Instantly show full visibility
            }

            if (fade < 1f) { // Only render if not fully faded
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                RenderSystem.setShaderColor(1.0f + fade, 1.0f + fade, 1.0f + fade, 1.0f - fade); // Apply fade effect

                context.drawTexture(OVERLAY, 0, 0, 0.0F, 0.0F, screenWidth, screenHeight, screenWidth, screenHeight);

                RenderSystem.disableBlend();
            }
        }
    }
}
