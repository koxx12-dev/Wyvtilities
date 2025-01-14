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

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.GuiIngameForge;
import net.wyvest.wyvtilities.config.WyvtilsConfig;
import net.wyvest.wyvtilities.gui.ActionBarGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("DefaultAnnotationParam")
@Mixin(value = GuiIngameForge.class, remap = false)
public class MixinGuiIngameForge {

    @Inject(method = "renderRecordOverlay", at = @At("HEAD"), cancellable = true)
    private void removeActionBar(int width, int height, float partialTicks, CallbackInfo ci) {
        if ((WyvtilsConfig.INSTANCE.getActionBarCustomization() && !WyvtilsConfig.INSTANCE.getActionBar()) || Minecraft.getMinecraft().currentScreen instanceof ActionBarGui) {
            ci.cancel();
        }
    }

    @Redirect(method = "renderRecordOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;translate(FFF)V"), remap = true)
    private void removeTranslation(float x, float y, float z) {
        if ((!WyvtilsConfig.INSTANCE.getActionBarPosition() && WyvtilsConfig.INSTANCE.getActionBarCustomization()) || !WyvtilsConfig.INSTANCE.getActionBarCustomization()) {
            GlStateManager.translate(x, y, z);
        }
    }

    @Redirect(method = "renderRecordOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;III)I"), remap = true)
    private int modifyDrawString(FontRenderer fontRenderer, String text, int x, int y, int color) {
        if (!WyvtilsConfig.INSTANCE.getActionBarCustomization()) {
            return fontRenderer.drawString(text, x, y, color);
        } else {
            int newX;
            int newY;
            if (WyvtilsConfig.INSTANCE.getActionBarPosition()) {
                newX = WyvtilsConfig.INSTANCE.getActionBarX();
                newY = WyvtilsConfig.INSTANCE.getActionBarY();
            } else {
                newX = x;
                newY = y;
            }
            return fontRenderer.drawString(
                    text,
                    newX,
                    newY,
                    color,
                    WyvtilsConfig.INSTANCE.getActionBarShadow()
            );
        }
    }

    @Inject(method = "renderTitle", at = @At("HEAD"), cancellable = true)
    private void removeTitle(int width, int height, float partialTicks, CallbackInfo ci) {
        if (!WyvtilsConfig.INSTANCE.getTitle()) {
            ci.cancel();
        }
    }

    @Redirect(method = "renderTitle", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;scale(FFF)V"), remap = true)
    private void modifyTitleScale1(float x, float y, float z) {
        if (WyvtilsConfig.INSTANCE.getTitleScale() == 1.0F && WyvtilsConfig.INSTANCE.getSubtitleScale() == 1.0F) {
            GlStateManager.scale(x, y, z);
        } else {
            if (x == 4.0F) {
                //Title
                GlStateManager.scale(x * WyvtilsConfig.INSTANCE.getTitleScale(), y * WyvtilsConfig.INSTANCE.getTitleScale(), z * WyvtilsConfig.INSTANCE.getTitleScale());
                return;
            }
            if (x == 2.0F) {
                //Subtitle
                GlStateManager.scale(x * WyvtilsConfig.INSTANCE.getSubtitleScale(), y * WyvtilsConfig.INSTANCE.getSubtitleScale(), z * WyvtilsConfig.INSTANCE.getSubtitleScale());
            }
        }
    }

}
