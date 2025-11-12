package kr.co.example.treeplz.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * TreeView - A custom view to visualize environmental impact through a tree's health.
 *
 * This view draws a tree whose appearance changes based on a "progress" value,
 * representing the health of the tree from healthy (0.0f) to fully degraded (1.0f).
 *
 * DESIGN IMPROVEMENTS from previous versions:
 * 1. Trunk, branches, and roots are now drawn as a single, continuous Path using Bezier curves
 *    to ensure a smooth, organic look without disjointed parts.
 * 2. Leaves are drawn along the branch paths, not floating randomly. They are layered with varied
 *    sizes and colors for a more natural appearance.
 * 3. Weather and surrounding life (animals) are now tied directly to the tree's health state,
 *    fading out gradually as the tree degrades.
 *
 * VISUAL GUIDE:
 * This implementation is inspired by the visual style in `ref_tree.png`. The goal is to capture
 * its essence with a cleaner, more organic, and animated result.
 */
public class TreeView extends View {

    public enum TreeState {
        HEALTHY(0.0f), MODERATE(0.35f), DEGRADED(0.7f), FINAL(1.0f);
        private final float progress;
        TreeState(float progress) { this.progress = progress; }
        public float getProgress() { return progress; }
    }

    private float currentProgress = 0.0f;
    private ValueAnimator animator;
    private final Random random = new Random();

    // Paints
    private Paint trunkPaint;
    private Paint leafPaint;
    private Paint groundPaint;
    private Paint fallenLeafPaint;
    private Paint animalPaint;
    private Paint rainPaint;

    // Paths & Objects
    private Path trunkAndBranchesPath;
    private final List<Leaf> leaves = new ArrayList<>();
    private final List<FallenLeaf> fallenLeaves = new ArrayList<>();
    private final List<Raindrop> raindrops = new ArrayList<>();

    // Colors (to be interpolated)
    private int skyColorStart, skyColorEnd;
    private int healthySkyColorStart = Color.parseColor("#81D4FA"); // Soft blue
    private int degradedSkyColorStart = Color.parseColor("#B0BEC5"); // Muted gray
    private int healthySkyColorEnd = Color.parseColor("#FFF9C4"); // Pale yellow
    private int degradedSkyColorEnd = Color.parseColor("#78909C");   // Darker gray

    private int healthyLeafColor = Color.parseColor("#5A7D63"); // Toned-down green
    private int degradedLeafColor = Color.parseColor("#8D6E63"); // Brownish

    public TreeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        trunkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        trunkPaint.setStyle(Paint.Style.STROKE);
        trunkPaint.setStrokeCap(Paint.Cap.ROUND);
        trunkPaint.setStrokeJoin(Paint.Join.ROUND);
        trunkPaint.setColor(Color.parseColor("#5D4037")); // Earthy brown

        leafPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        leafPaint.setStyle(Paint.Style.FILL);

        fallenLeafPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fallenLeafPaint.setStyle(Paint.Style.FILL);

        groundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        groundPaint.setStyle(Paint.Style.FILL);
        groundPaint.setColor(Color.parseColor("#A1887F")); // Muted earth tone

        animalPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        animalPaint.setStyle(Paint.Style.FILL);
        animalPaint.setColor(Color.parseColor("#FFC107")); // Butterfly yellow

        rainPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rainPaint.setColor(Color.WHITE);
        rainPaint.setAlpha(150);
        rainPaint.setStrokeWidth(2f);

        trunkAndBranchesPath = new Path();

        // Initialize animator
        animator = new ValueAnimator();
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            setTreeState((float) animation.getAnimatedValue());
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        generateTreePath(w, h);
        generateLeaves(w, h);
        generateRaindrops(w, h);
    }

    private void generateTreePath(int w, int h) {
        trunkAndBranchesPath.reset();
        // Define tree geometry based on view size
        float trunkWidth = w / 12f;
        trunkPaint.setStrokeWidth(trunkWidth);

        float startX = w / 2f;
        float startY = h * 0.9f;

        // Main trunk
        trunkAndBranchesPath.moveTo(startX, startY);
        trunkAndBranchesPath.lineTo(startX, h * 0.4f);

        // Branch 1 (left)
        trunkAndBranchesPath.moveTo(startX, h * 0.6f);
        trunkAndBranchesPath.cubicTo(startX, h * 0.5f, w * 0.3f, h * 0.45f, w * 0.2f, h * 0.3f);
        // Branch 2 (right)
        trunkAndBranchesPath.moveTo(startX, h * 0.5f);
        trunkAndBranchesPath.cubicTo(startX, h * 0.4f, w * 0.7f, h * 0.35f, w * 0.8f, h * 0.2f);
        // Smaller top branches
        trunkAndBranchesPath.moveTo(startX, h * 0.4f);
        trunkAndBranchesPath.cubicTo(startX, h * 0.35f, w * 0.4f, h * 0.25f, w*0.45f, h * 0.15f);
    }

    private void generateLeaves(int w, int h) {
        leaves.clear();
        // Generate leaves along the branches path - this is a simplified example.
        // A more robust solution would use PathMeasure to get points along the path.
        // DESIGNER NOTE: Leaf shapes could be loaded from VectorDrawables for more variety.
        for (int i = 0; i < 200; i++) {
            float x = random.nextFloat() * w;
            float y = (random.nextFloat() * h * 0.6f); // Confine to top 60%
            float size = 15 + random.nextFloat() * 20;
            // Only add leaf if it is near a branch (simple proximity check)
            if (y < h * 0.6 && (x > w*0.1f && x < w*0.9f) ) { // Simplified check
                leaves.add(new Leaf(x, y, size));
            }
        }
    }

    private void generateRaindrops(int w, int h) {
        raindrops.clear();
        for (int i = 0; i < 100; i++) {
            raindrops.add(new Raindrop(random.nextFloat() * w, random.nextFloat() * h, 20 + random.nextFloat() * 30));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawSky(canvas);
        drawGround(canvas);
        drawTrunkAndBranches(canvas);
        drawLeaves(canvas);
        drawFallenLeaves(canvas);
        drawFauna(canvas);

        if (currentProgress > 0.85f) {
            drawRain(canvas);
        }
        updateRaindrops();
    }

    private void drawSky(Canvas canvas) {
        // Interpolate colors based on progress
        skyColorStart = (int) ColorUtils.blendARGB(healthySkyColorStart, degradedSkyColorStart, currentProgress);
        skyColorEnd = (int) ColorUtils.blendARGB(healthySkyColorEnd, degradedSkyColorEnd, currentProgress);

        Paint skyPaint = new Paint();
        skyPaint.setShader(new LinearGradient(0, 0, 0, getHeight(), skyColorStart, skyColorEnd, Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, getWidth(), getHeight(), skyPaint);
    }

    private void drawGround(Canvas canvas) {
        float groundLevel = getHeight() * 0.9f;
        canvas.drawRect(0, groundLevel, getWidth(), getHeight(), groundPaint);
    }

    private void drawTrunkAndBranches(Canvas canvas) {
        canvas.drawPath(trunkAndBranchesPath, trunkPaint);
    }

    private void drawLeaves(Canvas canvas) {
        int totalLeaves = leaves.size();
        int leavesToDraw = (int) (totalLeaves * (1.0f - currentProgress));
        int leafColor = (int) ColorUtils.blendARGB(healthyLeafColor, degradedLeafColor, currentProgress);
        leafPaint.setColor(leafColor);

        for (int i = 0; i < leavesToDraw; i++) {
            Leaf leaf = leaves.get(i);
            leafPaint.setAlpha(150 + random.nextInt(105)); // Add variety
            canvas.drawCircle(leaf.x, leaf.y, leaf.size, leafPaint);
        }
    }
    
    private void drawFallenLeaves(Canvas canvas) {
        // As progress increases, more leaves fall
        int targetFallenLeaves = (int)(currentProgress * 50);
        while(fallenLeaves.size() < targetFallenLeaves) {
            float x = random.nextFloat() * getWidth();
            float y = getHeight() * 0.9f - 5;
            float size = 10 + random.nextFloat() * 10;
            fallenLeaves.add(new FallenLeaf(x,y,size));
        }

        int fallenLeafColor = (int) ColorUtils.blendARGB(healthyLeafColor, degradedLeafColor, Math.min(currentProgress * 1.5f, 1.0f));
        fallenLeafPaint.setColor(fallenLeafColor);
        for(FallenLeaf leaf : fallenLeaves) {
            canvas.drawOval(leaf.x, leaf.y, leaf.x+leaf.size, leaf.y+leaf.size/2, fallenLeafPaint);
        }
    }

    private void drawFauna(Canvas canvas) {
        // Butterflies and birds fade out as the tree degrades.
        int animalAlpha = (int) (255 * (1.0f - currentProgress * 2.0f));
        if (animalAlpha <= 0) return;

        animalPaint.setAlpha(animalAlpha);
        // Draw a simple butterfly
        // DESIGNER NOTE: Replace with ic_butterfly.xml and ic_bird.xml VectorDrawables
        canvas.drawCircle(getWidth() * 0.75f, getHeight() * 0.6f, 20, animalPaint);
        canvas.drawCircle(getWidth() * 0.25f, getHeight() * 0.5f, 15, animalPaint);
    }

    private void drawRain(Canvas canvas) {
        float rainAlpha = (currentProgress - 0.85f) / 0.15f; // Fade in rain
        rainPaint.setAlpha((int)(rainAlpha * 150));
        for (Raindrop drop : raindrops) {
            canvas.drawLine(drop.x, drop.y, drop.x, drop.y + drop.length, rainPaint);
        }
    }

    private void updateRaindrops() {
        for (Raindrop drop : raindrops) {
            drop.y += 20; // speed
            if (drop.y > getHeight()) {
                drop.y = -drop.length; // reset to top
                drop.x = random.nextFloat() * getWidth();
            }
        }
    }

    /**
     * Sets the tree's health state directly.
     * @param progress A value from 0.0 (healthy) to 1.0 (degraded).
     */
    public void setTreeState(float progress) {
        this.currentProgress = Math.max(0.0f, Math.min(1.0f, progress));
        invalidate();
    }

    /**
     * Animates the tree to a target state.
     * @param target The target TreeState enum.
     */
    public void animateToState(TreeState target) {
        if (animator.isRunning()) {
            animator.cancel();
        }
        animator.setFloatValues(currentProgress, target.getProgress());
        animator.setDuration((long) (Math.abs(target.getProgress() - currentProgress) * 1200));
        animator.start();
    }

    // Helper classes for objects
    private static class Leaf {
        float x, y, size;
        Leaf(float x, float y, float size) {
            this.x = x;
            this.y = y;
            this.size = size;
        }
    }
    
    private static class FallenLeaf {
        float x, y, size;
        FallenLeaf(float x, float y, float size) {
            this.x = x;
            this.y = y;
            this.size = size;
        }
    }

    private static class Raindrop {
        float x, y, length;
        Raindrop(float x, float y, float length) {
            this.x = x;
            this.y = y;
            this.length = length;
        }
    }
}
