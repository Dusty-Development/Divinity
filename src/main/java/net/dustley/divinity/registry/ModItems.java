package net.dustley.divinity.registry;


import net.dustley.divinity.Divinity;
import net.dustley.divinity.content.weapon.divina_excidium.DivinaExcidiumItem;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;


public class ModItems {

    // Items //
    public static final Item DIVINA_EXCIDIUM = registerItem("divina_excidium", new DivinaExcidiumItem());

    // Item group //
    public static final RegistryKey<ItemGroup> ITEM_GROUP_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), Divinity.identifier("item_group"));
    public static final ItemGroup ITEM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(DIVINA_EXCIDIUM))
            .displayName(Text.translatable("itemGroup." + Divinity.MOD_ID))
            .build();

    // Utility Functions //
    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Divinity.identifier(name), item);
    }

    // Initializer //
    public static void registerModItems() {
        Divinity.LOGGER.info("Registering Mod Items for " + Divinity.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> entries.add(DIVINA_EXCIDIUM));
    }

    public static void registerClientModItems() {
        Registry.register(Registries.ITEM_GROUP, ITEM_GROUP_KEY, ITEM_GROUP);
    }
}
