# CloverPixel Client - OpenGL Rendering Fixes

This is a rewrite of the Cloud Client with fixes for OpenGL rendering issues.

## Major Changes to Fix OpenGL Problems

### 1. Helper2D.java - Complete Rewrite of 2D Rendering
**Problems Fixed:**
- **State Leakage**: Original code used raw `glBegin()`/`glEnd()` calls which could leave OpenGL in inconsistent states
- **Missing State Management**: Color, blend, and texture states were not properly restored
- **No Matrix Protection**: Transformations could corrupt the matrix stack

**Solutions Implemented:**
- ✅ All rendering methods now use `GlStateManager.pushMatrix()`/`popMatrix()`
- ✅ Replaced `glBegin()`/`glEnd()` with modern `Tessellator`/`WorldRenderer` API
- ✅ Proper blend function setup with `tryBlendFuncSeparate()`
- ✅ Always restore color state to `(1, 1, 1, 1)` after rendering
- ✅ Texture states are properly enabled/disabled
- ✅ `drawCircle()` method completely rewritten using `GL_TRIANGLE_FAN` with Tessellator

### 2. Helper3D.java - 3D Bounding Box Rendering
**Problems Fixed:**
- **Multiple Draw Calls**: Original code called `tessellator.draw()` 6 times for one box
- **No State Cleanup**: Blend and texture states were left dirty

**Solutions Implemented:**
- ✅ Single Tessellator session draws all 6 faces
- ✅ Proper state setup with `disableTexture2D()` and `enableBlend()`
- ✅ Full state restoration after drawing
- ✅ Matrix push/pop protection

### 3. GLHelper.java - Complete Overhaul
**Problems Fixed:**
- **Direct GL Calls**: Mixed GL11 and GlStateManager usage
- **No Safety Checks**: Scissor could be set with invalid dimensions
- **Missing State Utilities**: No way to save/restore full GL state

**Solutions Implemented:**
- ✅ Bounds checking for scissor regions
- ✅ Replaced GL11 matrix ops with `GlStateManager` equivalents
- ✅ **NEW**: `pushGLState()` / `popGLState()` methods for complete state preservation
- ✅ **NEW**: `resetState()` utility to recover from corrupted states
- ✅ Safety check for scale <= 0

## Key Principles Applied

1. **Always Push/Pop Matrix**: Every rendering operation is wrapped in push/pop
2. **Clean State on Exit**: Colors reset to white, blend disabled, textures enabled
3. **Use Modern APIs**: Tessellator instead of immediate mode
4. **Defensive Programming**: Bounds checks and validation
5. **State Encapsulation**: Each function manages its own state completely

## Testing Recommendations

Test these scenarios that commonly triggered the original OpenGL bugs:

1. ✅ Rapidly toggling HUD elements on/off
2. ✅ Resizing HUD components in the editor
3. ✅ Rendering overlapping transparent elements
4. ✅ Switching between fullscreen and windowed mode
5. ✅ Using shader mods alongside CloverPixel
6. ✅ High particle counts with rendering enabled
7. ✅ Multiple HUD elements with rounded corners

## Compatibility

The rewritten rendering system maintains full API compatibility with the original:
- All function signatures remain the same
- All parameters have the same meaning
- Existing mods/features work without modification

## Performance

The new implementation may be slightly faster due to:
- Fewer state changes (batched operations)
- Single Tessellator draw calls instead of multiple
- Better use of GlStateManager caching

## Build Instructions

Same as original Cloud Client:
```bash
cd cloverpixel
gradlew build
```

The output JAR will have proper OpenGL state management throughout.
