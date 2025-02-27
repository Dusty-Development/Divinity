package net.dustley.divinity.content.weapon.divina_excidium;

import net.dustley.divinity.registry.ModDamageTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;

public class DivinaExcidiumItem extends Item {

    public DivinaExcidiumItem() { super(new Settings().attributeModifiers(getAttributes())); }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);

        if(entity instanceof LivingEntity living) {
            if(living.getHealth() > 1) {
                DamageSource damageSource = new DamageSource(
                        world.getRegistryManager()
                                .get(RegistryKeys.DAMAGE_TYPE)
                                .getEntry(ModDamageTypes.EXCIDIUM.getValue()).get()
                );

                living.damage(damageSource, 0.5f);
            }

            living.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 2, 1, true, false, false));
        }
    }

    private static AttributeModifiersComponent getAttributes() {
        var builder = AttributeModifiersComponent.builder();

        builder.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID, 24.0 - 1.0, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND);
        builder.add(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, -3.7, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND);

        return builder.build();
    }
}
