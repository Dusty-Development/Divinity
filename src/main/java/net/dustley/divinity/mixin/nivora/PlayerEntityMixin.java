package net.dustley.divinity.mixin.nivora;

import net.dustley.divinity.content.nivora.NivoraInstance;
import net.dustley.divinity.mixin_duck.INivora;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements INivora {

    @Unique private NivoraInstance instance;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) { super(entityType, world); }

    // Mixin your custom method for, say, a double jump
    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        // Create nivora
        if(instance == null) { instance = new NivoraInstance((PlayerEntity) (Object) this); }
        instance.tick();
    }

    @Override
    public NivoraInstance getNivora() { return instance; }
}