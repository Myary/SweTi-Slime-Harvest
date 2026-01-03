package net.myarry.slimeharvest.entity.slime.mine;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.myarry.slimeharvest.SlimeHarvest;


public class MineSlimeRenderer extends MobRenderer<MineSlime, MineSlimeModel<MineSlime>> {
    public MineSlimeRenderer(EntityRendererProvider.Context context) {
        super(context, new MineSlimeModel<>(context.bakeLayer(MineSlimeModel.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(MineSlime entity) {
        return ResourceLocation.fromNamespaceAndPath(SlimeHarvest.MOD_ID, "textures/entity/mine_slime.png");
    }


    @Override
    public RenderType getRenderType(MineSlime entity,
                                    boolean bodyVisible,
                                    boolean translucent,
                                    boolean glowing) {
        // Эта строка включает поддержку прозрачности текстуры
        return RenderType.entityTranslucent(getTextureLocation(entity));
    }
}
