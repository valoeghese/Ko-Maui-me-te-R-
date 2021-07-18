package net.minecraft.client.world;

/**
 * Now <b><i>THIS</i></b> is what's called a pro grammer move.
 */
public abstract class WorldType extends GeneratorType {
	protected WorldType(String string) {
		super(string);
		VALUES.add(this);
	}
}
