package net.adventurez.item;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.adventurez.entity.nonliving.GildedStoneEntity;
import net.adventurez.init.ConfigInit;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;

public class GildedStoneItem extends Item {

    private final Supplier<EntityType<GildedStoneEntity>> typeSupplier;
    private EntityType<GildedStoneEntity> cachedType = null;

    public GildedStoneItem(Settings settings, Supplier<EntityType<GildedStoneEntity>> typeSupplier) {
        super(settings);
        this.typeSupplier = typeSupplier;
    }

    public EntityType<GildedStoneEntity> getType() {
        if (cachedType == null) {
            cachedType = typeSupplier.get();
        }
        return cachedType;
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        super.appendTooltip(itemStack, world, tooltip, tooltipContext);
        if (ConfigInit.CONFIG.allow_extra_tooltips) {
            tooltip.add(new TranslatableText("item.adventurez.moreinfo.tooltip"));
            if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 340)) {
                tooltip.remove(new TranslatableText("item.adventurez.moreinfo.tooltip"));
                tooltip.add(new TranslatableText("item.adventurez.gilded_stone.tooltip"));
            }
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (!user.isSneaking()) {
            world.playSound((PlayerEntity) null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_EGG_THROW, SoundCategory.PLAYERS, 0.5F, 0.4F / (world.random.nextFloat() * 0.4F + 0.8F));
            if (!world.isClient) {
                GildedStoneEntity gildedStoneEntity = new GildedStoneEntity(world, user.getX(), user.getY() + 1.6D, user.getZ());
                gildedStoneEntity.setItem(itemStack);
                gildedStoneEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.4F, 1.0F);
                world.spawnEntity(gildedStoneEntity);
            }
            if (!user.isCreative()) {
                itemStack.decrement(1);
            }

            return TypedActionResult.success(itemStack, world.isClient());
        }
        return TypedActionResult.pass(itemStack);
    }
}