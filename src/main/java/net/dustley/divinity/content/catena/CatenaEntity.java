package net.dustley.divinity.content.catena;

import net.dustley.divinity.Divinity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.List;

public class CatenaEntity extends LivingEntity {

    public LivingEntity owner;

    public CatenaEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        Divinity.log(String.valueOf(player.getWorld().isClient));
        owner = player;
        return super.interact(player, hand);
    }

    @Override
    public boolean damage(DamageSource source, float amount) { return false; }

    @Override
    protected boolean shouldSwimInFluids() { return false; }

    @Override
    protected boolean isImmobile() { return true; }

    @Override
    public boolean isFallFlying() { return false; }

    @Override
    public Iterable<ItemStack> getArmorItems() { return List.of(); }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) { return ItemStack.EMPTY; }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) { }

    @Override
    public Arm getMainArm() { return Arm.RIGHT; }

}
