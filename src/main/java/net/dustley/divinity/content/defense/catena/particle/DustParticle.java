package net.dustley.divinity.content.defense.catena.particle;

import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.world.ClientWorld;

public class DustParticle extends BillboardParticle {
    protected DustParticle(ClientWorld clientWorld, double d, double e, double f) {
        super(clientWorld, d, e, f);
    }

    @Override
    protected float getMinU() {
        return 0;
    }

    @Override
    protected float getMaxU() {
        return 0;
    }

    @Override
    protected float getMinV() {
        return 0;
    }

    @Override
    protected float getMaxV() {
        return 0;
    }

    @Override
    public ParticleTextureSheet getType() {
        return null;
    }
}
