package valoeghese.metera;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPart.Cuboid;
import net.minecraft.client.render.entity.model.CompositeEntityModel;

public class AahuahangaOTeRaa extends CompositeEntityModel<TeRaa> {
	private static final ModelPart model;
	private static final int textureWidth = 32;
	private static final int textureHeight = 32;

	static {
		Cuboid cub = new Cuboid(0, 0, -16.0F, -16.0F, -16.0F, 32.0F, 32.0F, 32.0F, 0.0F, 0.0F, 0.0F, false, textureWidth, textureHeight);

		model = new ModelPart(List.of(cub), Map.of());
		model.setPivot(0.0F, 0.0F, 0.0F);
	}

	@Override
	public void setAngles(TeRaa entity, float limbAngle, float limbDistance, float customAngle, float headYaw, float headPitch) {
		model.yaw = headYaw / 100;
		model.pitch = headPitch / 100;
	}

	@Override
	public Iterable<ModelPart> getParts() {
		return ImmutableList.of(model);
	}
}
