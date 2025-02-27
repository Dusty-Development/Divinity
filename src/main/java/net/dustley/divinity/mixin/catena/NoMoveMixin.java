package net.dustley.divinity.mixin.catena;

import net.dustley.divinity.content.defense.catena.CatenaEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class NoMoveMixin extends LivingEntity {

    @Shadow protected boolean isSubmergedInWater;

    protected NoMoveMixin(EntityType<? extends LivingEntity> entityType, World world) { super(entityType, world); }


    @Inject(method = "getMovementSpeed", at = @At("HEAD"), cancellable = true)
    private void freezeMovement(CallbackInfoReturnable<Float> cir) {
        // Stop the player's movement
        for (Entity entity : getPassengerList()) {
            if(entity instanceof CatenaEntity) {
                cir.setReturnValue(0.025f);
                cir.cancel();
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        // Stop the player's movement
        for (Entity entity : getPassengerList()) {
            if(entity instanceof CatenaEntity) {
                if(isSubmergedInWater) setVelocity(0.0, -0.5, 0.0);
            }
        }
    }

    @Inject(method = "isSwimming", at = @At("HEAD"), cancellable = true)
    private void swimming(CallbackInfoReturnable<Boolean> cir) {
        // Stop the player's movement
        for (Entity entity : getPassengerList()) {
            if(entity instanceof CatenaEntity) {
                cir.setReturnValue(false);
                cir.cancel();
            }
        }
    }

    @Inject(method = "jump", at = @At("HEAD"), cancellable = true)
    private void stopJumping(CallbackInfo ci) {
        // Prevent jumping
        for (Entity entity : getPassengerList()) {
            if(entity instanceof CatenaEntity) {
                ci.cancel();
            }
        }
    }
}
