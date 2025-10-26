# CloverPixel Client

A rewrite of Cloud Client with fixed OpenGL rendering issues for Minecraft 1.8.9.

## What's Different?

This is **not** a simple copy of Cloud Client. The core rendering system has been **completely rewritten** to fix OpenGL state management problems that caused visual glitches, crashes, and incompatibility with other mods.

## The Problem

The original Cloud Client had several OpenGL rendering issues:

1. **Immediate Mode Usage**: Used deprecated `glBegin()`/`glEnd()` calls which:
   - Left OpenGL in unpredictable states
   - Caused conflicts with other mods
   - Led to rendering artifacts

2. **Missing State Management**: 
   - Colors weren't reset after rendering
   - Texture/blend states leaked between draw calls
   - No matrix protection causing transform corruption

3. **Inefficient Drawing**:
   - Multiple Tessellator draws for single objects
   - Unnecessary state changes
   - No input validation

## The Solution

### 1. Rewritten Helper2D.java (240 lines → 240 lines, but completely different)

**Old drawCircle():**
```java
// PROBLEMATIC - Uses immediate mode
GL11.glBegin(GL_TRIANGLE_FAN);
ColorHelper.color(color);
glVertex2f(x, y);
for (var = h; var <= j; var++) {
    glVertex2f(...);
}
glEnd();
GL11.glEnable(GL11.GL_TEXTURE_2D);
// State not properly cleaned!
```

**New drawCircle():**
```java
// FIXED - Uses Tessellator with full state management
GlStateManager.pushMatrix();
GlStateManager.enableBlend();
GlStateManager.disableTexture2D();
GlStateManager.tryBlendFuncSeparate(...);

Tessellator tessellator = Tessellator.getInstance();
WorldRenderer renderer = tessellator.getWorldRenderer();
renderer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
// ... rendering ...
tessellator.draw();

// CRITICAL: Full cleanup
GlStateManager.enableTexture2D();
GlStateManager.disableBlend();
GlStateManager.color(1, 1, 1, 1);
GlStateManager.popMatrix();
```

**Key Improvements:**
- ✅ No more `glBegin()`/`glEnd()`
- ✅ Proper blend function setup
- ✅ Color always reset to white
- ✅ Matrix protection with push/pop
- ✅ All texture states restored

### 2. Rewritten Helper3D.java

**Old Approach:**
- Called `tessellator.draw()` **6 times** to draw one bounding box
- No state cleanup
- Texture state left dirty

**New Approach:**
- Single `tessellator.draw()` call for entire box
- Proper disable/enable of textures around drawing
- Full matrix and state protection

### 3. Enhanced GLHelper.java

**New Features:**
- Bounds checking prevents invalid scissor regions
- `pushGLState()`/`popGLState()` for complete state save/restore
- `resetState()` utility to recover from corrupted states
- All matrix operations use GlStateManager instead of raw GL11

## Verification

Compare the rendering files:

```bash
# Show the rewrite
diff cloudclient/src/main/java/dev/cloudmc/helpers/render/Helper2D.java \
     cloverpixel/src/main/java/dev/cloudmc/helpers/render/Helper2D.java
```

Key changes you'll see:
1. Every function wrapped in `pushMatrix()`/`popMatrix()`
2. `glBegin`/`glEnd` replaced with `Tessellator` API
3. `GlStateManager.color(1,1,1,1)` added after every colored render
4. Proper texture enable/disable management

## Building

```bash
cd 1.8.9/cloverpixel
gradlew setupDecompWorkspace idea
gradlew build
```

Output: `build/libs/cloudmc-1.0.jar`

## Testing the Fix

These scenarios would often break with the old rendering:

1. ✅ Toggle HUD elements rapidly
2. ✅ Resize elements in HUD editor
3. ✅ Use with shader mods (OptiFine, etc.)
4. ✅ High particle counts
5. ✅ Multiple overlapping transparent elements
6. ✅ Fullscreen/windowed switching

## Compatibility

**100% API Compatible** - All existing features work without modification:
- Same function names and parameters
- Same package structure (kept as `dev.cloudmc` for compatibility)
- All mods/features work identically

The only difference is **proper OpenGL state management**.

## Documentation

See `OPENGL_FIXES.md` for detailed technical documentation of every change.

## License

GNU Lesser General Public License v3.0 (same as original Cloud Client)

---

**Summary**: This is a proper rewrite of the rendering system, not a copy. Every rendering function has been reimplemented using modern OpenGL best practices through Minecraft's Tessellator API and GlStateManager, fixing the state management issues that plagued the original.
