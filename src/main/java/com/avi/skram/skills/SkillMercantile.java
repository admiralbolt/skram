package com.avi.skram.skills;

import com.avi.skram.Skram;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;
import net.neoforged.neoforge.event.entity.player.TradeWithVillagerEvent;

public class SkillMercantile extends Skill {

  public static final String NAME = "mercantile";
  public static final StreamCodec<ByteBuf, SkillMercantile> STREAM_CODEC = StreamCodec.ofMember(Skill::encode, SkillMercantile::new);
  public static final String DESCRIPTION = """
    Also it's own skill!
    
    Price reduction is once again logarithmic scaling based on 1 / (1 + 0.02 * level). Level 50 Mercantile = 50% prices, Level 100 Mercantile = 33% prices.
    XP gained from any trade is a base of 400. XP is modified depending on demand + how many times you've traded:
     - 75XP per demand.
     - 10XP per number of trades.
    Minimum XP gained from any trade is 50.
    """;

  public static final int PRICE_REDUCTION_INDEX = 0;

  public SkillMercantile() {
    super(NAME, 1, 0);
    this.enabledAbilities = new boolean[]{true};
  }

  public SkillMercantile(ByteBuf buffer) {
    super(buffer);
  }

  @Override
  public void addXp(Player player, double xp) {
    super.addXp(player, xp);
    player.setData(Skills.MERCANTILE, player.getData(Skills.MERCANTILE));
  }

  public double getPriceReduction() {
    return 1.0 / (1 + 0.02 * this.level);
  }

  public static void handleTrade(TradeWithVillagerEvent event) {
    Player player = event.getEntity();

    SkillMercantile mercantile = player.getData(Skills.MERCANTILE);
    double baseXp = 400;

    // Modify XP based on uses and demand, to mildly encourage not spamming the same trade.
    MerchantOffer offer = event.getMerchantOffer();
    if (offer.getDemand() > 0) {
      baseXp -= 75 * offer.getDemand();
    }
    baseXp -= 10 * offer.getUses();

    mercantile.addXp(player, Math.max(baseXp, 50));

    // Also send a trigger based on the villager profession type.
    AbstractVillager abstractVillager = event.getAbstractVillager();
    if (abstractVillager instanceof WanderingTrader) {
      Skram.ARBITRARY_STRING_TRIGGER.get().trigger((ServerPlayer) player, "traded_with_wandering_trader");
      return;
    }

    if (!(abstractVillager instanceof Villager villager)) return;

    Skram.ARBITRARY_STRING_TRIGGER.get().trigger((ServerPlayer) player, "traded_with_" + villager.getVillagerData().profession().getRegisteredName().split(":")[1]);
  }

  @Override
  public Layout renderSkillAbilities() {
    LinearLayout vertical = new LinearLayout(0, 0, LinearLayout.Orientation.VERTICAL);
    vertical.spacing(1).defaultCellSetting().paddingBottom(2);

    vertical.addChild(
      Checkbox.builder(
          Component.literal("Trade Cost: " + (int) (100 * this.getPriceReduction()) + "%."),
          Minecraft.getInstance().font
        )
        .selected(this.enabledAbilities[PRICE_REDUCTION_INDEX])
        .onValueChange((checkbox, value) -> this.toggleEnabled(PRICE_REDUCTION_INDEX))
        .build()
    );

    return vertical;
  }

  @Override
  public String getDescription() {
    return DESCRIPTION;
  }
}
