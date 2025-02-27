package net.dustley.divinity.registry;

import net.dustley.divinity.Divinity;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public class ModDamageTypes {

    public static final RegistryKey<DamageType> EXCIDIUM = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Divinity.identifier("excidium"));

}
