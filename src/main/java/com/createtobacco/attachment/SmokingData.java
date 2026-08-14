package com.createtobacco.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;
import com.createtobacco.smoking.SmokingBalance;

/** Persistent, server-owned smoking state attached to each player. */
public final class SmokingData {
    public static final Codec<SmokingData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("dependence", 0.0F).forGetter(SmokingData::dependence),
            Codec.LONG.optionalFieldOf("active_ticks_since_satisfied", 0L)
                    .forGetter(SmokingData::activeTicksSinceSatisfied),
            Codec.LONG.optionalFieldOf("dependence_decay_accumulator", 0L)
                    .forGetter(SmokingData::dependenceDecayAccumulator),
            Codec.LONG.optionalFieldOf("ticks_until_next_withdrawal_episode", -1L)
                    .forGetter(SmokingData::ticksUntilNextWithdrawalEpisode),
            Codec.INT.optionalFieldOf("withdrawal_relief_puffs", 0)
                    .forGetter(SmokingData::withdrawalReliefPuffs),
            Codec.LONG.optionalFieldOf("ticks_until_next_cough_check", -1L)
                    .forGetter(SmokingData::ticksUntilNextCoughCheck)
    ).apply(instance, SmokingData::new));

    private float dependence;
    private long activeTicksSinceSatisfied;
    private long dependenceDecayAccumulator;
    private long ticksUntilNextWithdrawalEpisode;
    private int withdrawalReliefPuffs;
    private long ticksUntilNextCoughCheck;

    public SmokingData() {
        this(0.0F, 0L, 0L, -1L, 0, -1L);
    }

    public SmokingData(
            float dependence,
            long activeTicksSinceSatisfied,
            long dependenceDecayAccumulator,
            long ticksUntilNextWithdrawalEpisode,
            int withdrawalReliefPuffs,
            long ticksUntilNextCoughCheck
    ) {
        this.dependence = Mth.clamp(dependence, SmokingBalance.MIN_DEPENDENCE, SmokingBalance.MAX_DEPENDENCE);
        this.activeTicksSinceSatisfied = Math.max(0L, activeTicksSinceSatisfied);
        this.dependenceDecayAccumulator = Math.max(0L, dependenceDecayAccumulator);
        // Phase 9 stored zero while this field was reserved. Treat loaded zero as
        // unscheduled so upgraded worlds still randomize their first episode.
        this.ticksUntilNextWithdrawalEpisode = ticksUntilNextWithdrawalEpisode <= 0L
                ? -1L
                : ticksUntilNextWithdrawalEpisode;
        this.withdrawalReliefPuffs = Math.max(0, withdrawalReliefPuffs);
        this.ticksUntilNextCoughCheck = ticksUntilNextCoughCheck <= 0L ? -1L : ticksUntilNextCoughCheck;
    }

    public void tickActive() {
        activeTicksSinceSatisfied = saturatedAdd(activeTicksSinceSatisfied, 1L);
        if (ticksUntilNextWithdrawalEpisode > 0L) {
            ticksUntilNextWithdrawalEpisode--;
        }
        if (ticksUntilNextCoughCheck > 0L) {
            ticksUntilNextCoughCheck--;
        }

        if (dependence <= 0.0F) {
            dependenceDecayAccumulator = 0L;
            return;
        }

        dependenceDecayAccumulator = saturatedAdd(dependenceDecayAccumulator, 1L);
        long completedIntervals = dependenceDecayAccumulator / SmokingBalance.DEPENDENCE_DECAY_INTERVAL_TICKS;
        if (completedIntervals > 0L) {
            double decay = completedIntervals * (double) SmokingBalance.DEPENDENCE_DECAY_PER_INTERVAL;
            dependence = (float) Math.max(0.0D, dependence - decay);
            dependenceDecayAccumulator %= SmokingBalance.DEPENDENCE_DECAY_INTERVAL_TICKS;
            if (dependence == 0.0F) {
                dependenceDecayAccumulator = 0L;
            }
        }
    }

    public void addDependence(float amount) {
        dependence = Mth.clamp(dependence + amount, SmokingBalance.MIN_DEPENDENCE, SmokingBalance.MAX_DEPENDENCE);
    }

    public void setDependence(float value) {
        dependence = Mth.clamp(value, SmokingBalance.MIN_DEPENDENCE, SmokingBalance.MAX_DEPENDENCE);
    }

    public void setActiveTicksSinceSatisfied(long ticks) {
        activeTicksSinceSatisfied = Math.max(0L, ticks);
        clearWithdrawalSchedule();
    }

    public void markSatisfied() {
        activeTicksSinceSatisfied = 0L;
        clearWithdrawalSchedule();
    }

    public void clearWithdrawalSchedule() {
        ticksUntilNextWithdrawalEpisode = -1L;
        withdrawalReliefPuffs = 0;
    }

    public boolean withdrawalEpisodeIsScheduled() {
        return ticksUntilNextWithdrawalEpisode >= 0L;
    }

    public boolean withdrawalEpisodeIsDue() {
        return ticksUntilNextWithdrawalEpisode == 0L;
    }

    public void scheduleWithdrawalEpisode(long ticks) {
        ticksUntilNextWithdrawalEpisode = Math.max(1L, ticks);
    }

    public void beginWithdrawalEpisode() {
        withdrawalReliefPuffs = 0;
    }

    public void recordWithdrawalReliefPuff() {
        withdrawalReliefPuffs++;
    }

    public void resetWithdrawalReliefPuffs() {
        withdrawalReliefPuffs = 0;
    }

    public float dependence() {
        return dependence;
    }

    public long activeTicksSinceSatisfied() {
        return activeTicksSinceSatisfied;
    }

    public long dependenceDecayAccumulator() {
        return dependenceDecayAccumulator;
    }

    public long ticksUntilNextWithdrawalEpisode() {
        return ticksUntilNextWithdrawalEpisode;
    }

    public int withdrawalReliefPuffs() {
        return withdrawalReliefPuffs;
    }

    public long ticksUntilNextCoughCheck() {
        return ticksUntilNextCoughCheck;
    }

    public boolean coughCheckIsScheduled() {
        return ticksUntilNextCoughCheck >= 0L;
    }

    public boolean coughCheckIsDue() {
        return ticksUntilNextCoughCheck == 0L;
    }

    public void scheduleCoughCheck(long ticks) {
        ticksUntilNextCoughCheck = Math.max(1L, ticks);
    }

    public void clearCoughSchedule() {
        ticksUntilNextCoughCheck = -1L;
    }

    public void reset() {
        dependence = 0.0F;
        activeTicksSinceSatisfied = 0L;
        dependenceDecayAccumulator = 0L;
        clearWithdrawalSchedule();
        clearCoughSchedule();
    }

    private static long saturatedAdd(long value, long increment) {
        return value > Long.MAX_VALUE - increment ? Long.MAX_VALUE : value + increment;
    }
}
