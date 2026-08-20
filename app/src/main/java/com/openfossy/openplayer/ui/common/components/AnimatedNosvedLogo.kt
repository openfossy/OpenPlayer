package com.openfossy.openplayer.ui.common.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.tan

// Outer hexagon contour: pointy-top, regular hexagon with rounded corners
private val OpenPlayerHexagonContour = listOf(
    50.00f to 6.50f,   // Top vertex
    87.67f to 28.25f,  // Top-Right vertex
    87.67f to 71.75f,  // Bottom-Right vertex
    50.00f to 93.50f,  // Bottom vertex
    12.33f to 71.75f,  // Bottom-Left vertex
    12.33f to 28.25f   // Top-Left vertex
)

// Inner left bracket: concentric hexagon segment with open top and bottom gaps
private val OpenPlayerLeftBracketPoints = listOf(
    41.50f to 25.91f,  // Upper-left diagonal end
    24.89f to 35.50f,  // Top-Left corner
    24.89f to 64.50f,  // Bottom-Left corner
    41.50f to 74.09f   // Lower-left diagonal end
)

// Inner right bracket: mirror concentric hexagon segment
private val OpenPlayerRightBracketPoints = listOf(
    58.50f to 25.91f,  // Upper-right diagonal end
    75.11f to 35.50f,  // Top-Right corner
    75.11f to 64.50f,  // Bottom-Right corner
    58.50f to 74.09f   // Lower-right diagonal end
)

// Center play button triangle: equilateral play icon with rounded corners, optically centered
private val OpenPlayerTriangleContour = listOf(
    42.00f to 36.50f,  // Top-Left vertex
    42.00f to 63.50f,  // Bottom-Left vertex
    65.00f to 50.00f   // Right tip vertex
)

/**
 * Builds a closed Path with smooth circular arc corner fillets using cubic Bezier curves.
 */
private fun buildSmoothRoundedContour(
    pointsPct: List<Pair<Float, Float>>,
    w: Float,
    h: Float,
    cornerRadius: Float
): Path {
    val n = pointsPct.size
    val pts = pointsPct.map { Offset(it.first / 100f * w, it.second / 100f * h) }
    val path = Path()
    if (n < 3) return path

    val startPoints = ArrayList<Offset>(n)
    val endPoints = ArrayList<Offset>(n)
    val ctrl1Points = ArrayList<Offset>(n)
    val ctrl2Points = ArrayList<Offset>(n)

    for (i in 0 until n) {
        val curr = pts[i]
        val prev = pts[(i - 1 + n) % n]
        val next = pts[(i + 1) % n]

        val toPrev = prev - curr
        val toNext = next - curr
        val prevLen = toPrev.getDistance()
        val nextLen = toNext.getDistance()

        if (prevLen == 0f || nextLen == 0f) {
            startPoints.add(curr)
            endPoints.add(curr)
            ctrl1Points.add(curr)
            ctrl2Points.add(curr)
            continue
        }

        val uPrev = toPrev / prevLen
        val uNext = toNext / nextLen

        val dot = (uPrev.x * uNext.x + uPrev.y * uNext.y).coerceIn(-1f, 1f)
        val alpha = acos(dot)
        val halfAlpha = alpha / 2f
        val tanHalfAlpha = tan(halfAlpha)

        val beta = PI.toFloat() - alpha
        val tanQuarterBeta = tan(beta / 4f)

        val desiredD = if (tanHalfAlpha > 0.001f) cornerRadius / tanHalfAlpha else 0f
        val maxD = minOf(prevLen * 0.48f, nextLen * 0.48f)
        val d = minOf(desiredD, maxD)
        val rEff = d * tanHalfAlpha

        val pStart = curr + uPrev * d
        val pEnd = curr + uNext * d

        val lCtrl = (4f / 3f) * tanQuarterBeta * rEff
        val c1 = pStart - uPrev * lCtrl
        val c2 = pEnd - uNext * lCtrl

        startPoints.add(pStart)
        endPoints.add(pEnd)
        ctrl1Points.add(c1)
        ctrl2Points.add(c2)
    }

    path.moveTo(startPoints[0].x, startPoints[0].y)
    for (i in 0 until n) {
        if (i > 0) {
            path.lineTo(startPoints[i].x, startPoints[i].y)
        }
        path.cubicTo(
            ctrl1Points[i].x, ctrl1Points[i].y,
            ctrl2Points[i].x, ctrl2Points[i].y,
            endPoints[i].x, endPoints[i].y
        )
    }
    path.close()
    return path
}

/**
 * Builds an open Path with smooth circular arc corner fillets on interior vertices.
 */
private fun buildSmoothRoundedOpenPath(
    pointsPct: List<Pair<Float, Float>>,
    w: Float,
    h: Float,
    cornerRadius: Float
): Path {
    val pts = pointsPct.map { Offset(it.first / 100f * w, it.second / 100f * h) }
    val path = Path()
    if (pts.isEmpty()) return path
    if (pts.size == 1) {
        path.moveTo(pts[0].x, pts[0].y)
        return path
    }

    path.moveTo(pts[0].x, pts[0].y)

    for (i in 1 until pts.size - 1) {
        val curr = pts[i]
        val prev = pts[i - 1]
        val next = pts[i + 1]

        val toPrev = prev - curr
        val toNext = next - curr
        val prevLen = toPrev.getDistance()
        val nextLen = toNext.getDistance()

        if (prevLen == 0f || nextLen == 0f) {
            path.lineTo(curr.x, curr.y)
            continue
        }

        val uPrev = toPrev / prevLen
        val uNext = toNext / nextLen

        val dot = (uPrev.x * uNext.x + uPrev.y * uNext.y).coerceIn(-1f, 1f)
        val alpha = acos(dot)
        val halfAlpha = alpha / 2f
        val tanHalfAlpha = tan(halfAlpha)

        val beta = PI.toFloat() - alpha
        val tanQuarterBeta = tan(beta / 4f)

        val desiredD = if (tanHalfAlpha > 0.001f) cornerRadius / tanHalfAlpha else 0f
        val maxD = minOf(prevLen * 0.48f, nextLen * 0.48f)
        val d = minOf(desiredD, maxD)
        val rEff = d * tanHalfAlpha

        val pStart = curr + uPrev * d
        val pEnd = curr + uNext * d

        val lCtrl = (4f / 3f) * tanQuarterBeta * rEff
        val c1 = pStart - uPrev * lCtrl
        val c2 = pEnd - uNext * lCtrl

        path.lineTo(pStart.x, pStart.y)
        path.cubicTo(c1.x, c1.y, c2.x, c2.y, pEnd.x, pEnd.y)
    }

    path.lineTo(pts.last().x, pts.last().y)
    return path
}

/** Returns the first `progress` fraction (by length) of `path` for a progressive reveal. */
private fun trimmedSegment(path: Path, progress: Float): Path {
    val out = Path()
    if (progress <= 0f) return out
    val measure = PathMeasure().apply { setPath(path, false) }
    measure.getSegment(0f, measure.length * progress.coerceIn(0f, 1f), out, true)
    return out
}

/** Point along `path` at fractional `progress` (0f..1f) to lead stroke with a glow tip. */
private fun tipPosition(path: Path, progress: Float): Offset? {
    if (progress <= 0f || progress >= 1f) return null
    val measure = PathMeasure().apply { setPath(path, false) }
    return measure.getPosition(measure.length * progress)
}

private fun stageProgress(overall: Float, start: Float, end: Float): Float =
    ((overall - start) / (end - start)).coerceIn(0f, 1f)

@Composable
fun AnimatedNosvedLogo() {
    AnimatedNosvedLogo(
        modifier = Modifier,
        color = MaterialTheme.colorScheme.primary,
        animateOnEntry = true
    )
}

@Composable
fun AnimatedNosvedLogo(modifier: Modifier) {
    AnimatedNosvedLogo(
        modifier = modifier,
        color = MaterialTheme.colorScheme.primary,
        animateOnEntry = true
    )
}

@Composable
fun AnimatedNosvedLogo(modifier: Modifier, color: Color) {
    AnimatedNosvedLogo(
        modifier = modifier,
        color = color,
        animateOnEntry = true
    )
}

/**
 * Animated Open Player logo: a hollow rounded hexagon, two concentric bracket accents,
 * and a center play triangle. Draws smoothly on entry with glowing pen tips,
 * settles with a gentle spring pop, and enters a subtle breathing idle loop.
 */
@Composable
fun AnimatedNosvedLogo(
    modifier: Modifier,
    color: Color,
    animateOnEntry: Boolean
) {
    var hasAnimated by rememberSaveable { mutableStateOf(!animateOnEntry) }

    val containerAlpha = remember { Animatable(if (hasAnimated) 1f else 0f) }
    val containerScale = remember { Animatable(if (hasAnimated) 1f else 0.72f) }
    val drawProgress = remember { Animatable(if (hasAnimated) 1f else 0f) }

    LaunchedEffect(hasAnimated) {
        if (!hasAnimated) {
            launch { containerAlpha.animateTo(1f, tween(260, easing = FastOutSlowInEasing)) }
            launch {
                containerScale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
            delay(90)
            drawProgress.animateTo(1f, tween(durationMillis = 1150, easing = FastOutSlowInEasing))
            // subtle settle pop on completion
            containerScale.animateTo(1.04f, tween(120, easing = FastOutSlowInEasing))
            containerScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
            hasAnimated = true
        }
    }

    val idleTransition = rememberInfiniteTransition(label = "nosved_idle")
    val breathe by idleTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.025f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe"
    )

    val strokeWidthPx = with(LocalDensity.current) { 3.6.dp.toPx() }
    val glowRadiusPx = with(LocalDensity.current) { 5.dp.toPx() }

    Box(
        modifier = modifier
            .scale(containerScale.value * breathe)
            .alpha(containerAlpha.value),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            val hexPath = buildSmoothRoundedContour(OpenPlayerHexagonContour, w, h, w * 0.11f)
            val leftBracketPath = buildSmoothRoundedOpenPath(OpenPlayerLeftBracketPoints, w, h, w * 0.065f)
            val rightBracketPath = buildSmoothRoundedOpenPath(OpenPlayerRightBracketPoints, w, h, w * 0.065f)
            val triPath = buildSmoothRoundedContour(OpenPlayerTriangleContour, w, h, w * 0.048f)

            val hexP = stageProgress(drawProgress.value, 0f, 0.44f)
            val leftBracketP = stageProgress(drawProgress.value, 0.32f, 0.72f)
            val rightBracketP = stageProgress(drawProgress.value, 0.32f, 0.72f)
            val triP = stageProgress(drawProgress.value, 0.60f, 1f)

            val strokeStyle = Stroke(strokeWidthPx, cap = StrokeCap.Round, join = StrokeJoin.Round)
            drawPath(trimmedSegment(hexPath, hexP), color = color, style = strokeStyle)
            drawPath(trimmedSegment(leftBracketPath, leftBracketP), color = color, style = strokeStyle)
            drawPath(trimmedSegment(rightBracketPath, rightBracketP), color = color, style = strokeStyle)
            drawPath(trimmedSegment(triPath, triP), color = color, style = strokeStyle)

            // Leading glow tips following the active strokes
            if (drawProgress.value < 1f) {
                val tips = buildList {
                    tipPosition(hexPath, hexP)?.let { add(it) }
                    tipPosition(leftBracketPath, leftBracketP)?.let { add(it) }
                    tipPosition(rightBracketPath, rightBracketP)?.let { add(it) }
                    tipPosition(triPath, triP)?.let { add(it) }
                }
                tips.forEach { p ->
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(color.copy(alpha = 0.85f), Color.Transparent),
                            center = p,
                            radius = glowRadiusPx * 2.4f
                        ),
                        radius = glowRadiusPx * 2.4f,
                        center = p
                    )
                    drawCircle(color = color, radius = glowRadiusPx * 0.55f, center = p)
                }
            }
        }
    }
}