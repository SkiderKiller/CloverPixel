/*
 * CloverPixel Client - Rewritten rendering utilities with proper OpenGL state management
 * This fixes rendering issues by ensuring proper state push/pop and cleanup
 */

package dev.cloudmc.helpers.render;

import dev.cloudmc.Cloud;
import dev.cloudmc.helpers.ColorHelper;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class Helper2D {

    /**
     * Draws a rounded Rectangle on the HUD using quarter circles and rectangles
     * Rewritten with proper OpenGL state management
     */
    public static void drawRoundedRectangle(int x, int y, int w, int h, int radius, int color, int index) {
        if (index == -1) {
            drawRectangle(x, y, w, h, color);
        } else if (index == 0) {
            drawRectangle(x + radius, y + radius, w - radius * 2, h - radius * 2, color);
            drawRectangle(x + radius, y, w - radius * 2, radius, color);
            drawRectangle(x + w - radius, y + radius, radius, h - radius * 2, color);
            drawRectangle(x + radius, y + h - radius, w - radius * 2, radius, color);
            drawRectangle(x, y + radius, radius, h - radius * 2, color);
            drawCircle(x + radius, y + radius, radius, 180, 270, color);
            drawCircle(x + w - radius, y + radius, radius, 270, 360, color);
            drawCircle(x + radius, y + h - radius, radius, 90, 180, color);
            drawCircle(x + w - radius, y + h - radius, radius, 0, 90, color);
        } else if (index == 1) {
            drawRectangle(x + radius, y, w - radius * 2, radius, color);
            drawRectangle(x, y + radius, w, h - radius, color);
            drawCircle(x + radius, y + radius, radius, 180, 270, color);
            drawCircle(x + w - radius, y + radius, radius, 270, 360, color);
        } else if (index == 2) {
            drawRectangle(x, y, w, h - radius, color);
            drawRectangle(x + radius, y + h - radius, w - radius * 2, radius, color);
            drawCircle(x + radius, y + h - radius, radius, 90, 180, color);
            drawCircle(x + w - radius, y + h - radius, radius, 0, 90, color);
        }
    }

    /**
     * Draws a rectangle using width and height
     */
    public static void drawRectangle(int x, int y, int w, int h, int color) {
        Gui.drawRect(x, y, x + w, y + h, color);
    }

    /**
     * Draws a picture with proper OpenGL state management
     * REWRITTEN: Ensures texture state is properly restored
     */
    public static void drawPicture(int x, int y, int w, int h, int color, String location) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        
        if (color == 0) {
            GlStateManager.color(1, 1, 1, 1);
        } else {
            ColorHelper.color(color);
        }
        
        ResourceLocation resourceLocation = new ResourceLocation(Cloud.modID, location);
        Cloud.INSTANCE.mc.getTextureManager().bindTexture(resourceLocation);
        
        GlStateManager.enableTexture2D();
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, w, h, w, h);
        
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    /**
     * Draws an outline of a rectangle
     */
    public static void drawOutlinedRectangle(int x, int y, int w, int h, int t, int color) {
        drawRectangle(x, y, w, t, color);
        drawRectangle(x + w - t, y, t, h, color);
        drawRectangle(x, y + h - t, w, t, color);
        drawRectangle(x, y, t, h, color);
    }

    /**
     * Draws a vertical gradient rectangle
     * REWRITTEN: Properly manages OpenGL states with push/pop
     */
    public static void drawGradientRectangle(float x, float y, float w, float h, int startColor, int endColor) {
        GlStateManager.pushMatrix();
        
        float startAlpha = (float) (startColor >> 24 & 255) / 255.0F;
        float startRed = (float) (startColor >> 16 & 255) / 255.0F;
        float startGreen = (float) (startColor >> 8 & 255) / 255.0F;
        float startBlue = (float) (startColor & 255) / 255.0F;
        
        float endAlpha = (float) (endColor >> 24 & 255) / 255.0F;
        float endRed = (float) (endColor >> 16 & 255) / 255.0F;
        float endGreen = (float) (endColor >> 8 & 255) / 255.0F;
        float endBlue = (float) (endColor & 255) / 255.0F;
        
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.pos(x + w, y, 0).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        worldRenderer.pos(x, y, 0).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        worldRenderer.pos(x, y + h, 0).color(endRed, endGreen, endBlue, endAlpha).endVertex();
        worldRenderer.pos(x + w, y + h, 0).color(endRed, endGreen, endBlue, endAlpha).endVertex();
        tessellator.draw();
        
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.color(1, 1, 1, 1);
        
        GlStateManager.popMatrix();
    }

    /**
     * Draws a horizontal gradient rectangle
     * REWRITTEN: Properly manages OpenGL states
     */
    public static void drawHorizontalGradientRectangle(float x, float y, float w, float h, int startColor, int endColor) {
        GlStateManager.pushMatrix();
        
        float startAlpha = (float) (startColor >> 24 & 255) / 255.0F;
        float startRed = (float) (startColor >> 16 & 255) / 255.0F;
        float startGreen = (float) (startColor >> 8 & 255) / 255.0F;
        float startBlue = (float) (startColor & 255) / 255.0F;
        
        float endAlpha = (float) (endColor >> 24 & 255) / 255.0F;
        float endRed = (float) (endColor >> 16 & 255) / 255.0F;
        float endGreen = (float) (endColor >> 8 & 255) / 255.0F;
        float endBlue = (float) (endColor & 255) / 255.0F;
        
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.pos(x, y, 0).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        worldRenderer.pos(x, y + h, 0).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        worldRenderer.pos(x + w, y + h, 0).color(endRed, endGreen, endBlue, endAlpha).endVertex();
        worldRenderer.pos(x + w, y, 0).color(endRed, endGreen, endBlue, endAlpha).endVertex();
        tessellator.draw();
        
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.color(1, 1, 1, 1);
        
        GlStateManager.popMatrix();
    }

    /**
     * Draws a textured modal rectangle
     * REWRITTEN: Uses Tessellator properly
     */
    public static void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x, y + height, 0).tex((float) (textureX) * f, (float) (textureY + height) * f1).endVertex();
        worldrenderer.pos(x + width, y + height, 0).tex((float) (textureX + width) * f, (float) (textureY + height) * f1).endVertex();
        worldrenderer.pos(x + width, y, 0).tex((float) (textureX + width) * f, (float) (textureY) * f1).endVertex();
        worldrenderer.pos(x, y, 0).tex((float) (textureX) * f, (float) (textureY) * f1).endVertex();
        tessellator.draw();
    }

    /**
     * Draws a circle/arc
     * COMPLETELY REWRITTEN: Uses Tessellator instead of glBegin/glEnd to avoid state issues
     */
    public static void drawCircle(float x, float y, float r, int startAngle, int endAngle, int color) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.disableCull();
        
        float alpha = (color >> 24 & 255) / 255.0F;
        float red = (color >> 16 & 255) / 255.0F;
        float green = (color >> 8 & 255) / 255.0F;
        float blue = (color & 255) / 255.0F;
        
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        
        worldRenderer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.pos(x, y, 0).color(red, green, blue, alpha).endVertex();
        
        for (int angle = startAngle; angle <= endAngle; angle++) {
            double radians = Math.toRadians(angle);
            double posX = x + r * Math.cos(radians);
            double posY = y + r * Math.sin(radians);
            worldRenderer.pos(posX, posY, 0).color(red, green, blue, alpha).endVertex();
        }
        
        tessellator.draw();
        
        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.color(1, 1, 1, 1);
        
        GlStateManager.popMatrix();
    }
}
