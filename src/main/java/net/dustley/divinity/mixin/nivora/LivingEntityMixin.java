package net.dustley.divinity.mixin.nivora;

import net.dustley.divinity.mixin_duck.INivora;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements INivora {

    @Shadow public abstract void playSound(@Nullable SoundEvent sound);

    protected LivingEntityMixin(EntityType<? extends LivingEntity> entityType, World world) { super(entityType, world); }

    @Inject(method = "getScale", at = @At("HEAD"), cancellable = true)
    private void scale(CallbackInfoReturnable<Float> cir) {
        if(((LivingEntity) (Object) this) instanceof PlayerEntity player) {
            if(player.isSneaking()) {
//                cir.setReturnValue(2f);
//                cir.cancel();
            }
        }
    }

}