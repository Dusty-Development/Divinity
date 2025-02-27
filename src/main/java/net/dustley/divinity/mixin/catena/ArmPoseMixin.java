package net.dustley.divinity.mixin.catena;

import net.dustley.divinity.content.defense.catena.CatenaEntity;
import net.dustley.divinity.content.defense.catena.render.CatenaArmPosing;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BipedEntityModel.class)
public class ArmPoseMixin {

    @Shadow @Final public ModelPart rightArm;

    @Shadow @Final public ModelPart leftArm;

    @Shadow @Final public ModelPart head;

    @Inject(method = "positionRightArm", at = @At("HEAD"), cancellable = true)
    private void positionCustomRightArm(LivingEntity entity, CallbackInfo ci) {
        for (Entity e : entity.getPassengerList()) {
            if (e instanceof CatenaEntity) {
                CatenaArmPosing.hold(this.rightArm, this.leftArm, this.head, true);
                ci.cancel();
                break;
            }
        }
    }

    @Inject(method = "positionLeftArm", at = @At("HEAD"), cancellable = true)
    private void positionCustomLeftArm(LivingEntity entity, CallbackInfo ci) {
        for (Entity e : entity.getPassengerList()) {
            if (e instanceof CatenaEntity) {
                CatenaArmPosing.hold(this.rightArm, this.leftArm, this.head, false);
                ci.cancel();
                break;
            }
        }
    }
}
