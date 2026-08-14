package com.createtobacco.command;

import com.createtobacco.CreateTobacco;
import com.createtobacco.attachment.SmokingData;
import com.createtobacco.attachment.WithdrawalTier;
import com.createtobacco.registry.ModAttachments;
import com.createtobacco.registry.ModEffects;
import com.createtobacco.smoking.CoughingSystem;
import com.createtobacco.smoking.EnderRoulette;
import com.createtobacco.smoking.SmokingEffects;
import com.createtobacco.smoking.SmokingBalance;
import com.createtobacco.smoking.SmokingProduct;
import com.createtobacco.smoking.WithdrawalSystem;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Locale;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = CreateTobacco.MOD_ID)
public final class CreateTobaccoCommands {
    private static final int OPERATOR_PERMISSION_LEVEL = 2;
    private static final java.util.Set<SmokingProduct> DEBUG_PUFF_PRODUCTS = java.util.Set.of(
            SmokingProduct.MARLBORE_RED,
            SmokingProduct.WINSTONE_BLUE,
            SmokingProduct.CREPERFIELD,
            SmokingProduct.CHUNKMAN,
            SmokingProduct.PIGLIAMENT,
            SmokingProduct.BEDROMORKANAL,
            SmokingProduct.STONEO_Y_GLOWLIETA
    );

    private CreateTobaccoCommands() {
    }

    @SubscribeEvent
    private static void registerCommands(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }

    private static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("createtobacco")
                .requires(source -> source.hasPermission(OPERATOR_PERMISSION_LEVEL))
                .then(Commands.literal("status")
                        .executes(context -> status(context.getSource(), context.getSource().getPlayerOrException()))
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(context -> status(context.getSource(), EntityArgument.getPlayer(context, "player")))))
                .then(Commands.literal("dependence")
                        .then(Commands.literal("get")
                                .executes(context -> dependenceGet(context.getSource(), context.getSource().getPlayerOrException()))
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(context -> dependenceGet(context.getSource(), EntityArgument.getPlayer(context, "player")))))
                        .then(Commands.literal("set")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("value", FloatArgumentType.floatArg(0.0F, 100.0F))
                                                .executes(CreateTobaccoCommands::dependenceSet))))
                        .then(Commands.literal("add")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("value", FloatArgumentType.floatArg())
                                                .executes(CreateTobaccoCommands::dependenceAdd)))))
                .then(Commands.literal("craving")
                        .then(Commands.literal("setelapsed")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("minutes", IntegerArgumentType.integer(0))
                                                .executes(CreateTobaccoCommands::cravingSetElapsed))))
                        .then(Commands.literal("reset")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(CreateTobaccoCommands::cravingReset))))
                .then(Commands.literal("withdrawal")
                        .then(Commands.literal("trigger")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("tier", StringArgumentType.word())
                                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                                                        new String[]{"mild", "moderate", "high", "severe"}, builder))
                                                .executes(CreateTobaccoCommands::withdrawalTrigger))))
                        .then(Commands.literal("clear")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(CreateTobaccoCommands::withdrawalClear))))
                .then(Commands.literal("cough")
                        .then(Commands.literal("trigger")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(CreateTobaccoCommands::coughTrigger))))
                .then(Commands.literal("effect")
                        .then(Commands.literal("trigger")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(productArgument()
                                                .executes(CreateTobaccoCommands::effectTrigger)))))
                .then(Commands.literal("kend")
                        .then(Commands.literal("trigger")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("outcome", StringArgumentType.word())
                                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                                                        new String[]{"random", "teleport", "slow_falling", "jump_boost", "invisibility", "levitation"}, builder))
                                                .executes(CreateTobaccoCommands::kendTrigger)))))
                .then(Commands.literal("completion")
                        .then(Commands.literal("trigger")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("product", StringArgumentType.word())
                                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                                                        Arrays.stream(SmokingProduct.values())
                                                                .map(product -> product.itemId().getPath()), builder))
                                                .executes(CreateTobaccoCommands::completionTrigger)))))
                .then(Commands.literal("reset")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(CreateTobaccoCommands::reset))));
    }

    private static com.mojang.brigadier.builder.RequiredArgumentBuilder<CommandSourceStack, String> productArgument() {
        String[] products = {
                "marlbore_red", "winstone_blue", "creperfield", "chunkman",
                "pigliament", "bedromorkanal", "stoneo_y_glowlieta"
        };
        return Commands.argument("product", StringArgumentType.word())
                .suggests((context, builder) -> SharedSuggestionProvider.suggest(products, builder));
    }

    private static int status(CommandSourceStack source, ServerPlayer player) {
        SmokingData data = data(player);
        WithdrawalTier tier = WithdrawalTier.fromDependence(data.dependence());
        long safeTicks = tier.safeIntervalTicks();
        long safeRemaining = Math.max(0L, safeTicks - data.activeTicksSinceSatisfied());
        boolean craving = tier != WithdrawalTier.NONE && safeRemaining == 0L;
        MobEffectInstance withdrawal = player.getEffect(ModEffects.WITHDRAWAL);
        WithdrawalTier activeTier = withdrawal == null
                ? WithdrawalTier.NONE
                : WithdrawalTier.fromAmplifier(withdrawal.getAmplifier());

        send(source, "Smoking status: " + player.getGameProfile().getName());
        send(source, String.format(Locale.ROOT, "Dependence: %.3f / 100 (%s)", data.dependence(), tier));
        send(source, "Active since satisfied: " + formatTicks(data.activeTicksSinceSatisfied()));
        send(source, "Safe interval: " + formatTicks(safeTicks) + "; remaining: " + formatTicks(safeRemaining));
        send(source, "Craving state: " + craving);
        send(source, "Withdrawal: " + activeTier
                + (withdrawal == null ? "" : " (" + formatTicks(withdrawal.getDuration()) + ")"));
        send(source, "Next episode: " + (data.withdrawalEpisodeIsScheduled()
                ? formatTicks(data.ticksUntilNextWithdrawalEpisode()) : "not scheduled"));
        send(source, "Relief puffs this episode: " + data.withdrawalReliefPuffs()
                + (withdrawal == null ? "" : " (each successful puff lowers Withdrawal by one tier)"));
        send(source, "Cough check: " + (data.coughCheckIsScheduled()
                ? formatTicks(data.ticksUntilNextCoughCheck()) : "not scheduled"));
        return 1;
    }

    private static int dependenceGet(CommandSourceStack source, ServerPlayer player) {
        send(source, String.format(Locale.ROOT, "%s dependence: %.3f",
                player.getGameProfile().getName(), data(player).dependence()));
        return 1;
    }

    private static int dependenceSet(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        data(player).setDependence(FloatArgumentType.getFloat(context, "value"));
        return dependenceGet(context.getSource(), player);
    }

    private static int dependenceAdd(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        data(player).addDependence(FloatArgumentType.getFloat(context, "value"));
        return dependenceGet(context.getSource(), player);
    }

    private static int cravingSetElapsed(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        long ticks = (long) IntegerArgumentType.getInteger(context, "minutes") * 60L * 20L;
        data(player).setActiveTicksSinceSatisfied(ticks);
        send(context.getSource(), "Active craving elapsed set to " + formatTicks(ticks));
        return 1;
    }

    private static int cravingReset(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        data(player).setActiveTicksSinceSatisfied(0L);
        WithdrawalSystem.clear(player, data(player));
        send(context.getSource(), "Craving timer reset for " + player.getGameProfile().getName());
        return 1;
    }

    private static int withdrawalTrigger(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        WithdrawalTier tier;
        try {
            tier = WithdrawalTier.valueOf(StringArgumentType.getString(context, "tier").toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            context.getSource().sendFailure(Component.literal("Unknown Withdrawal tier"));
            return 0;
        }
        if (tier == WithdrawalTier.NONE) return 0;

        // Debug trigger prepares a production-valid craving state first. Without
        // this, the normal server tick correctly clears a manually injected
        // Withdrawal effect immediately when dependence/safe time are too low.
        SmokingData data = data(player);
        float minimumDependence = switch (tier) {
            case MILD -> SmokingBalance.MILD_DEPENDENCE;
            case MODERATE -> SmokingBalance.MODERATE_DEPENDENCE;
            case HIGH -> SmokingBalance.HIGH_DEPENDENCE;
            case SEVERE -> SmokingBalance.SEVERE_DEPENDENCE;
            case NONE -> 0.0F;
        };
        if (data.dependence() < minimumDependence) {
            data.setDependence(minimumDependence);
        }
        if (data.activeTicksSinceSatisfied() < tier.safeIntervalTicks()) {
            data.setActiveTicksSinceSatisfied(tier.safeIntervalTicks());
        }

        WithdrawalSystem.trigger(player, data, tier, false);
        send(context.getSource(), "Triggered persistent " + tier + " Withdrawal for "
                + player.getGameProfile().getName() + " (debug state prepared)");
        return 1;
    }

    private static int withdrawalClear(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        WithdrawalSystem.clear(player, data(player));
        send(context.getSource(), "Withdrawal cleared for " + player.getGameProfile().getName());
        return 1;
    }

    private static int coughTrigger(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        CoughingSystem.trigger(player);
        send(context.getSource(), "Cough triggered for " + player.getGameProfile().getName());
        return 1;
    }

    private static int effectTrigger(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        SmokingProduct product = parseProduct(context, "product");
        if (product == null || !DEBUG_PUFF_PRODUCTS.contains(product)) {
            context.getSource().sendFailure(Component.literal("Unknown product or product has no special effect"));
            return 0;
        }
        SmokingEffects.triggerPuffEffect(player, product);
        send(context.getSource(), "Triggered product effect: " + product.itemId().getPath());
        return 1;
    }

    private static int kendTrigger(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        String value = StringArgumentType.getString(context, "outcome");
        EnderRoulette.Outcome outcome;
        if (value.equals("random")) {
            outcome = EnderRoulette.randomOutcome(player);
        } else {
            try {
                outcome = EnderRoulette.Outcome.valueOf(value.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException exception) {
                context.getSource().sendFailure(Component.literal("Unknown KEnd outcome"));
                return 0;
            }
        }
        EnderRoulette.trigger(player, outcome);
        send(context.getSource(), "Triggered KEnd outcome: " + outcome);
        return 1;
    }

    private static int completionTrigger(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        SmokingProduct product = parseProduct(context, "product");
        if (product == null) {
            context.getSource().sendFailure(Component.literal("Unknown smoking product"));
            return 0;
        }
        SmokingEffects.onFullyConsumed(player, product);
        send(context.getSource(), "Triggered completion profile: " + product.itemId().getPath());
        return 1;
    }

    private static int reset(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        data(player).reset();
        player.removeEffect(ModEffects.WITHDRAWAL);
        player.removeEffect(ModEffects.NICOTINE_RUSH);
        send(context.getSource(), "Create Tobacco state reset for " + player.getGameProfile().getName());
        return 1;
    }

    private static SmokingProduct parseProduct(CommandContext<CommandSourceStack> context, String argument) {
        String value = StringArgumentType.getString(context, argument);
        return Arrays.stream(SmokingProduct.values())
                .filter(product -> product.itemId().getPath().equals(value))
                .findFirst()
                .orElse(null);
    }

    private static SmokingData data(ServerPlayer player) {
        return player.getData(ModAttachments.SMOKING_DATA);
    }

    private static void send(CommandSourceStack source, String message) {
        source.sendSuccess(() -> Component.literal(message), false);
    }

    private static String formatTicks(long ticks) {
        if (ticks < 0L) return "n/a";
        long totalSeconds = ticks / 20L;
        long hours = totalSeconds / 3_600L;
        long minutes = totalSeconds % 3_600L / 60L;
        long seconds = totalSeconds % 60L;
        return hours > 0L
                ? String.format(Locale.ROOT, "%dh %02dm %02ds", hours, minutes, seconds)
                : String.format(Locale.ROOT, "%dm %02ds", minutes, seconds);
    }
}
