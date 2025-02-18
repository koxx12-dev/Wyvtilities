/*
 * Wyvtilities - Utilities for Hypixel 1.8.9.
 * Copyright (C) 2021 Wyvtilities
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.wyvest.wyvtilities.mixin;

import net.minecraft.client.audio.*;
import net.wyvest.wyvtilities.Wyvtilities;
import net.wyvest.wyvtilities.config.WyvtilsConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundManager.class)
public class MixinSoundManager {
    @Inject(method = "getNormalizedVolume", at = @At("HEAD"), cancellable = true)
    public void getVolume(ISound sound, SoundPoolEntry entry, SoundCategory category, CallbackInfoReturnable<Float> cir) {
        if (WyvtilsConfig.INSTANCE.getSoundBoost()) {
            if (sound instanceof PositionedSound) {
                PositionedSound positionedSound = (PositionedSound) sound;
                if (Wyvtilities.INSTANCE.checkSound(sound.getSoundLocation().getResourcePath())) {
                    if (positionedSound.getVolume() * WyvtilsConfig.INSTANCE.getSoundMultiplier() <= 1) {
                        cir.setReturnValue(positionedSound.getVolume() * WyvtilsConfig.INSTANCE.getSoundMultiplier());
                    } else {
                        cir.setReturnValue(Float.parseFloat("1"));
                    }
                }
            }
        }
    }
}
