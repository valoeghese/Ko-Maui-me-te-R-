package valoeghese.metera;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class WhakamaatakitakiOTeRaa extends MobEntityRenderer<TeRaa, AahuahangaOTeRaa> {
	public static final Identifier TE_RAA = new Identifier("metera:textures/entity/te_raa.png");

	public WhakamaatakitakiOTeRaa(EntityRendererFactory.Context context) {
		super(context, new AahuahangaOTeRaa(), 1.0f);
	}

	@Override
	public void render(TeRaa yes, float something, float someOtherThing, MatrixStack m,
			VertexConsumerProvider uselessWrapper, int probablyLightIdk) {
		m.push();
		final float s = 2.0f;
		m.scale(s, s, s);
		m.translate(0, -0.5, 0);// using gl to fix my crap model code instead of fixing the model
		super.render(yes, something, someOtherThing, m, uselessWrapper, probablyLightIdk);
		m.pop();
	}

	public int getBlockLight(TeRaa closeEnoughToGlowSquid, BlockPos blockPos) {
		int i = (int)MathHelper.method_37166(0.0F, 15.0F, 1.0F);
		return i == 15 ? 15 : Math.max(i, super.getBlockLight(closeEnoughToGlowSquid, blockPos));
	}

	@Override
	public Identifier getTexture(TeRaa entity) {
		return TE_RAA;
	}
}
