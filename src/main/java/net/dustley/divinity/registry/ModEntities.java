package net.dustley.divinity.registry;

import net.dustley.divinity.Divinity;
import net.dustley.divinity.content.catena.CatenaEntity;
import net.dustley.divinity.content.catena.CatenaEntityModel;
import net.dustley.divinity.content.catena.CatenaEntityRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModEntities {

    public static final EntityType<CatenaEntity> CATENA = Registry.register(Registries.ENTITY_TYPE,
            Divinity.identifier("catena"),
            EntityType.Builder.create(CatenaEntity::new, SpawnGroup.MISC)
                    .dimensions(0.75f, 0.75f).build());

    public static void onInit() {
        FabricDefaultAttributeRegistry.register(CATENA, CatenaEntity.createLivingAttributes());
    }

    public static void onInitClient() {
        EntityRendererRegistry.register(CATENA, CatenaEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(CatenaEntityModel.modelLayer, CatenaEntityModel::getTexturedModelData);
    }

}
