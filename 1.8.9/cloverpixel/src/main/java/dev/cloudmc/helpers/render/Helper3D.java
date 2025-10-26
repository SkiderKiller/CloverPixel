/*
 * CloverPixel Client - Rewritten 3D rendering utilities
 * Properly manages OpenGL states for 3D rendering
 */

package dev.cloudmc.helpers.render;

import dev.cloudmc.Cloud;
import dev.cloudmc.feature.mod.impl.FreelookMod;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

public class Helper3D {

    /**
     * Draws a filled box over a given Axis Aligned Bounding Box in the world
     * REWRITTEN: Uses single Tessellator session with proper state management
     */
    public static void drawFilledBoundingBox(AxisAlignedBB box) {
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer renderer = tessellator.getWorldRenderer();
        
        renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        
        // Top face
        renderer.pos(box.minX, box.maxY, box.maxZ).endVertex();
        renderer.pos(box.maxX, box.maxY, box.maxZ).endVertex();
        renderer.pos(box.maxX, box.maxY, box.minZ).endVertex();
        renderer.pos(box.minX, box.maxY, box.minZ).endVertex();
        
        // Bottom face
        renderer.pos(box.maxX, box.minY, box.maxZ).endVertex();
        renderer.pos(box.minX, box.minY, box.maxZ).endVertex();
        renderer.pos(box.minX, box.minY, box.minZ).endVertex();
        renderer.pos(box.maxX, box.minY, box.minZ).endVertex();
        
        // Front face (minZ)
        renderer.pos(box.minX, box.maxY, box.minZ).endVertex();
        renderer.pos(box.maxX, box.maxY, box.minZ).endVertex();
        renderer.pos(box.maxX, box.minY, box.minZ).endVertex();
        renderer.pos(box.minX, box.minY, box.minZ).endVertex();
        
        // Back face (maxZ)
        renderer.pos(box.maxX, box.maxY, box.maxZ).endVertex();
        renderer.pos(box.minX, box.maxY, box.maxZ).endVertex();
        renderer.pos(box.minX, box.minY, box.maxZ).endVertex();
        renderer.pos(box.maxX, box.minY, box.maxZ).endVertex();
        
        // Left face (minX)
        renderer.pos(box.minX, box.minY, box.maxZ).endVertex();
        renderer.pos(box.minX, box.maxY, box.maxZ).endVertex();
        renderer.pos(box.minX, box.maxY, box.minZ).endVertex();
        renderer.pos(box.minX, box.minY, box.minZ).endVertex();
        
        // Right face (maxX)
        renderer.pos(box.maxX, box.maxY, box.maxZ).endVertex();
        renderer.pos(box.maxX, box.minY, box.maxZ).endVertex();
        renderer.pos(box.maxX, box.minY, box.minZ).endVertex();
        renderer.pos(box.maxX, box.maxY, box.minZ).endVertex();
        
        tessellator.draw();
        
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    /**
     * Calculates camera distance for freelook
     * Unchanged as it doesn't involve rendering
     */
    public static double calculateCameraDistance(double d0, double d1, double d2, double d3) {
        float f1 = FreelookMod.cameraYaw;
        float f2 = FreelookMod.cameraPitch;

        if (Cloud.INSTANCE.mc.gameSettings.thirdPersonView == 2) {
            f2 += 180.0F;
        }

        double d4 = (double) (MathHelper.sin(f1 / 180.0F * (float) Math.PI) * MathHelper.cos(f2 / 180.0F * (float) Math.PI)) * d3;
        double d5 = (double) (-MathHelper.cos(f1 / 180.0F * (float) Math.PI) * MathHelper.cos(f2 / 180.0F * (float) Math.PI)) * d3;
        double d6 = (double) (-MathHelper.sin(f2 / 180.0F * (float) Math.PI)) * d3;

        for (int i = 0; i < 8; ++i) {
            float f3 = (float) ((i & 1) * 2 - 1);
            float f4 = (float) ((i >> 1 & 1) * 2 - 1);
            float f5 = (float) ((i >> 2 & 1) * 2 - 1);
            f3 = f3 * 0.1F;
            f4 = f4 * 0.1F;
            f5 = f5 * 0.1F;
            MovingObjectPosition movingobjectposition = Cloud.INSTANCE.mc.theWorld.rayTraceBlocks(
                    new Vec3(d0 + (double) f3, d1 + (double) f4, d2 + (double) f5),
                    new Vec3(d0 - d4 + (double) f3 + (double) f5, d1 - d6 + (double) f4, d2 - d5 + (double) f5)
            );

            if (movingobjectposition != null) {
                double d7 = movingobjectposition.hitVec.distanceTo(new Vec3(d0, d1, d2));

                if (d7 < d3) {
                    d3 = d7;
                }
            }
        }
        return d3;
    }
}
