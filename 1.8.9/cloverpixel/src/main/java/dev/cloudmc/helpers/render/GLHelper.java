/*
 * CloverPixel Client - Rewritten OpenGL helper utilities
 * Enhanced with proper state management and error prevention
 */

package dev.cloudmc.helpers.render;

import dev.cloudmc.helpers.ResolutionHelper;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class GLHelper {

    /**
     * Scissors out everything outside a rectangle using GL_SCISSOR_TEST
     * REWRITTEN: Added bounds checking and proper state preservation
     */
    public static void startScissor(int x, int y, int width, int height) {
        if (width <= 0 || height <= 0) {
            return;
        }
        
        int scaleFactor = ResolutionHelper.getFactor();
        int screenHeight = ResolutionHelper.getHeight();
        
        int scissorX = Math.max(0, x * scaleFactor);
        int scissorY = Math.max(0, (screenHeight - y - height) * scaleFactor);
        int scissorWidth = Math.max(0, width * scaleFactor);
        int scissorHeight = Math.max(0, height * scaleFactor);
        
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(scissorX, scissorY, scissorWidth, scissorHeight);
    }

    /**
     * Stops the scissors
     * REWRITTEN: Ensures scissor test is always disabled
     */
    public static void endScissor() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    /**
     * Scales everything with the given scale amount
     * REWRITTEN: Uses GlStateManager for better compatibility and state tracking
     */
    public static void startScale(int x, int y, float scale) {
        if (scale <= 0) {
            scale = 1.0f;
        }
        
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(scale, scale, 1.0f);
        GlStateManager.translate(-x, -y, 0);
    }

    /**
     * Stops scaling
     * REWRITTEN: Uses GlStateManager
     */
    public static void endScale() {
        GlStateManager.popMatrix();
    }
    
    /**
     * Saves the current OpenGL state
     * NEW: Utility method for preserving state before rendering
     */
    public static void pushGLState() {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GlStateManager.pushMatrix();
    }
    
    /**
     * Restores the previously saved OpenGL state
     * NEW: Utility method for restoring state after rendering
     */
    public static void popGLState() {
        GlStateManager.popMatrix();
        GL11.glPopAttrib();
    }
    
    /**
     * Resets common OpenGL states to a sane default
     * NEW: Utility to fix corrupted state
     */
    public static void resetState() {
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }
}
