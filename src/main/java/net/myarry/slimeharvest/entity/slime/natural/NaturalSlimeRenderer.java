package net.myarry.slimeharvest.entity.slime.natural;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.myarry.slimeharvest.SlimeHarvest;


public class NaturalSlimeRenderer extends MobRenderer<NaturalSlime, NaturalSlimeModel<NaturalSlime>> {
    public NaturalSlimeRenderer(EntityRendererProvider.Context context) {
        super(context, new NaturalSlimeModel<>(context.bakeLayer(NaturalSlimeModel.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(NaturalSlime entity) {
        return ResourceLocation.fromNamespaceAndPath(SlimeHarvest.MOD_ID, "textures/entity/natural_slime.png");
    }


    @Override
    public RenderType getRenderType(NaturalSlime entity,
                                    boolean bodyVisible,
                                    boolean translucent,
                                    boolean glowing) {
        // Эта строка включает поддержку прозрачности текстуры
        return RenderType.entityTranslucent(getTextureLocation(entity));
    }
}
