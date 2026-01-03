package net.myarry.slimeharvest.entity.slime.coal;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.myarry.slimeharvest.SlimeHarvest;


public class CoalSlimeRenderer extends MobRenderer<CoalSlime, CoalSlimeModel<CoalSlime>> {
    public CoalSlimeRenderer(EntityRendererProvider.Context context) {
        super(context, new CoalSlimeModel<>(context.bakeLayer(CoalSlimeModel.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(CoalSlime entity) {
        return ResourceLocation.fromNamespaceAndPath(SlimeHarvest.MOD_ID, "textures/entity/coal_slime.png");
    }


    @Override
    public RenderType getRenderType(CoalSlime entity,
                                    boolean bodyVisible,
                                    boolean translucent,
                                    boolean glowing) {
        // Эта строка включает поддержку прозрачности текстуры
        return RenderType.entityTranslucent(getTextureLocation(entity));
    }
}
