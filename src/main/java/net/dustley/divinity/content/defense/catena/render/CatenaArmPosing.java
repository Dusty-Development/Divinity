package net.dustley.divinity.content.defense.catena.render;

import net.minecraft.client.model.ModelPart;

public class CatenaArmPosing {
    public static void hold(ModelPart holdingArm, ModelPart otherArm, ModelPart head, boolean rightArmed) {
        ModelPart modelPart = rightArmed ? holdingArm : otherArm;
        ModelPart modelPart2 = rightArmed ? otherArm : holdingArm;

        modelPart.roll = (float) Math.toRadians(-15f);
        modelPart2.roll = (float) Math.toRadians(15f);

        modelPart.pitch = (float) Math.toRadians(180f);
        modelPart2.pitch = (float) Math.toRadians(180f);
    }
}
