package valoeghese.metera;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class EventTriggerItem extends Item {
	public EventTriggerItem(Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		user.getStackInHand(hand).decrement(1);

		if (!world.isClient) {
			WorldData.get(((ServerWorld) world).getServer().getWorld(World.OVERWORLD)).setDaySpeed(7L);
		}

		return TypedActionResult.consume(user.getStackInHand(hand));
	}
}
