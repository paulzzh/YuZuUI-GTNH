package com.paulzzh.yuzu.mixins;

import com.paulzzh.yuzu.YuZuUI;
import cpw.mods.fml.relauncher.FMLLaunchHandler;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public enum Mixins {
    VANILLA(new Builder("GUI").addTargetedMod(TargetedMod.VANILLA).setSide(Side.CLIENT)
        .setPhase(Phase.EARLY).addMixinClasses("minecraft.MinecraftMixin")),
    MUSIC_VANILLA(new Builder("MUSIC_VANILLA").addTargetedMod(TargetedMod.VANILLA).setSide(Side.CLIENT)
        .setPhase(Phase.EARLY).addMixinClasses("minecraft.MusicTickerMixin")),
    MUSIC_GALACTICRAFT(new Builder("MUSIC_GALACTICRAFT").addTargetedMod(TargetedMod.GALACTICRAFT).setSide(Side.CLIENT)
        .setPhase(Phase.LATE).addMixinClasses("galacticraft.MusicTickerGCMixin"));

    private final List<String> mixinClasses;
    private final Supplier<Boolean> applyIf;
    private final Phase phase;
    private final Side side;
    private final List<TargetedMod> targetedMods;
    private final List<TargetedMod> excludedMods;

    Mixins(Builder builder) {
        this.mixinClasses = builder.mixinClasses;
        this.applyIf = builder.applyIf;
        this.side = builder.side;
        this.targetedMods = builder.targetedMods;
        this.excludedMods = builder.excludedMods;
        this.phase = builder.phase;
        if (this.targetedMods.isEmpty()) {
            throw new RuntimeException("No targeted mods specified for " + this.name());
        }
        if (this.applyIf == null) {
            throw new RuntimeException("No ApplyIf function specified for " + this.name());
        }
    }

    public static List<String> getEarlyMixins(Set<String> loadedCoreMods) {
        final List<String> mixins = new ArrayList<>();
        final List<String> notLoading = new ArrayList<>();
        for (Mixins mixin : Mixins.values()) {
            if (mixin.phase == Mixins.Phase.EARLY) {
                if (mixin.shouldLoad(loadedCoreMods, Collections.emptySet())) {
                    mixins.addAll(mixin.mixinClasses);
                } else {
                    notLoading.addAll(mixin.mixinClasses);
                }
            }
        }
        YuZuUI.LOG.info("Not loading the following EARLY mixins: {}", notLoading);
        return mixins;
    }

    public static List<String> getLateMixins(Set<String> loadedMods) {
        final List<String> mixins = new ArrayList<>();
        final List<String> notLoading = new ArrayList<>();
        for (Mixins mixin : Mixins.values()) {
            if (mixin.phase == Mixins.Phase.LATE) {
                if (mixin.shouldLoad(Collections.emptySet(), loadedMods)) {
                    mixins.addAll(mixin.mixinClasses);
                } else {
                    notLoading.addAll(mixin.mixinClasses);
                }
            }
        }
        YuZuUI.LOG.info("Not loading the following LATE mixins: {}", notLoading.toString());
        return mixins;
    }

    @SuppressWarnings("SimplifyStreamApiCallChains")
    private static String[] addPrefix(String prefix, String... values) {
        return Arrays.stream(values)
            .map(s -> prefix + s)
            .collect(Collectors.toList())
            .toArray(new String[values.length]);
    }

    private boolean shouldLoadSide() {
        return side == Side.BOTH || (side == Side.SERVER && FMLLaunchHandler.side().isServer())
            || (side == Side.CLIENT && FMLLaunchHandler.side().isClient());
    }

    private boolean allModsLoaded(List<TargetedMod> targetedMods, Set<String> loadedCoreMods, Set<String> loadedMods) {
        if (targetedMods.isEmpty()) return false;

        for (TargetedMod target : targetedMods) {
            if (target == TargetedMod.VANILLA) continue;

            // Check coremod first
            if (!loadedCoreMods.isEmpty() && target.coreModClass != null
                && !loadedCoreMods.contains(target.coreModClass))
                return false;
            else if (!loadedMods.isEmpty() && target.modId != null && !loadedMods.contains(target.modId)) return false;
        }

        return true;
    }

    private boolean noModsLoaded(List<TargetedMod> targetedMods, Set<String> loadedCoreMods, Set<String> loadedMods) {
        if (targetedMods.isEmpty()) return true;

        for (TargetedMod target : targetedMods) {
            if (target == TargetedMod.VANILLA) continue;

            // Check coremod first
            if (!loadedCoreMods.isEmpty() && target.coreModClass != null
                && loadedCoreMods.contains(target.coreModClass))
                return false;
            else if (!loadedMods.isEmpty() && target.modId != null && loadedMods.contains(target.modId)) return false;
        }

        return true;
    }

    private boolean shouldLoad(Set<String> loadedCoreMods, Set<String> loadedMods) {
        return (shouldLoadSide() && applyIf.get()
            && allModsLoaded(targetedMods, loadedCoreMods, loadedMods)
            && noModsLoaded(excludedMods, loadedCoreMods, loadedMods));
    }

    private enum Side {
        BOTH,
        CLIENT,
        SERVER
    }

    private enum Phase {
        EARLY,
        LATE,
    }

    private static class Builder {

        private final List<String> mixinClasses = new ArrayList<>();
        private final List<TargetedMod> targetedMods = new ArrayList<>();
        private final List<TargetedMod> excludedMods = new ArrayList<>();
        private Supplier<Boolean> applyIf = () -> true;
        private Side side = Side.BOTH;
        private Phase phase = Phase.LATE;

        public Builder(@SuppressWarnings("unused") String description) {
        }

        public Builder addMixinClasses(String... mixinClasses) {
            this.mixinClasses.addAll(Arrays.asList(mixinClasses));
            return this;
        }

        public Builder setPhase(Phase phase) {
            this.phase = phase;
            return this;
        }

        public Builder setSide(Side side) {
            this.side = side;
            return this;
        }

        public Builder setApplyIf(Supplier<Boolean> applyIf) {
            this.applyIf = applyIf;
            return this;
        }

        public Builder addTargetedMod(TargetedMod mod) {
            this.targetedMods.add(mod);
            return this;
        }

        public Builder addExcludedMod(TargetedMod mod) {
            this.excludedMods.add(mod);
            return this;
        }
    }
}
