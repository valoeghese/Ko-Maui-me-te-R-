package valoeghese.metera;

import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;

public class MeteraModels {
	static void init() {
		EntityRendererRegistry.INSTANCE.register(Metera.TE_RAA, TeRaaRenderer::new);
	}
}
