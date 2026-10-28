
package com.fonlisstars.game

import android.app.Activity
import android.os.Bundle
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import android.view.View
import kotlin.math.hypot
import kotlin.random.Random

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation =
            android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        )
        setContentView(FonlisGame(this))
    }
}

private class Fighter(
    var x: Float,
    var y: Float,
    var hp: Int = 100,
    val enemy: Boolean = false
)

class FonlisGame(context: android.content.Context) : View(context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val random = Random.Default

    private var player = Fighter(300f, 300f)
    private val enemies = mutableListOf<Fighter>()
    private val obstacles = mutableListOf<RectF>()

    private var moveX = 0f
    private var moveY = 0f
    private var attacking = false
    private var superReady = true
    private var gameOver = false
    private var wins = 0
    private var lastAttack = 0L

    init {
        resetGame()
        isFocusable = true
    }

    private fun resetGame() {
        player = Fighter(width.coerceAtLeast(800) * 0.25f, 300f)
        enemies.clear()
        repeat(3) {
            enemies.add(
                Fighter(
                    random.nextInt(400, 900).toFloat(),
                    random.nextInt(100, 600).toFloat(),
                    enemy = true
                )
            )
        }
        obstacles.clear()
        gameOver = false
        superReady = true
    }

    private fun text(
        c: Canvas, value: String, x: Float, y: Float, size: Float,
        color: Int = Color.WHITE
    ) {
        paint.color = color
        paint.textSize = size
        paint.style = Paint.Style.FILL
        c.drawText(value, x, y, paint)
    }

    override fun onDraw(c: Canvas) {
        super.onDraw(c)
        if (width == 0 || height == 0) return

        c.drawColor(Color.rgb(73, 164, 91))

        paint.color = Color.rgb(65, 150, 80)
        for (x in 0 until width step 45) {
            for (y in 0 until height step 45) {
                c.drawCircle(x.toFloat(), y.toFloat(), 2f, paint)
            }
        }

        paint.color = Color.rgb(125, 87, 52)
        c.drawRoundRect(
            width * .36f, height * .25f,
            width * .43f, height * .48f, 8f, 8f, paint
        )
        c.drawRoundRect(
            width * .58f, height * .55f,
            width * .67f, height * .73f, 8f, 8f, paint
        )

        if (enemies.isEmpty() && !gameOver) {
            wins++
            gameOver = true
        }

        if (!gameOver) updateEnemies()

        drawFighter(c, player, Color.rgb(45, 115, 255))

        enemies.forEach {
            drawFighter(c, it, Color.rgb(235, 65, 65))
        }

        text(c, "FONLIS STARS", 22f, 36f, 26f)
        text(c, "HP: ${player.hp}", 22f, 68f, 22f)
        text(c, "Врагов: ${enemies.size}", 22f, 98f, 20f)
        text(c, "Победы: $wins", 22f, 127f, 18f)

        paint.color = Color.argb(110, 255, 255, 255)
        c.drawCircle(90f, height - 100f, 58f, paint)
        c.drawCircle(width - 130f, height - 100f, 55f, paint)
        c.drawCircle(width - 260f, height - 100f, 45f, paint)

        text(c, "ДВИЖЕНИЕ", 43f, height - 95f, 14f, Color.BLACK)
        text(c, "АТАКА", width - 165f, height - 95f, 18f, Color.BLACK)
        text(
            c, "СУПЕР", width - 292f, height - 95f, 18f,
            if (superReady) Color.BLUE else Color.DKGRAY
        )

        if (gameOver) {
            paint.color = Color.argb(210, 0, 0, 0)
            c.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            text(c, "ПОБЕДА!", width * .4f, height * .4f, 42f)
            text(c, "Нажми, чтобы играть снова", width * .32f,
                height * .55f, 24f)
        } else if (player.hp <= 0) {
            gameOver = true
            text(c, "ТЫ ПРОИГРАЛ", width * .38f, height * .4f, 38f)
            text(c, "Нажми для рестарта", width * .37f,
                height * .55f, 24f)
        }

        postInvalidateDelayed(33)
    }

    private fun drawFighter(c: Canvas, f: Fighter, color: Int) {
        paint.color = Color.argb(65, 0, 0, 0)
        c.drawCircle(f.x + 4f, f.y + 5f, 23f, paint)

        paint.color = color
        c.drawCircle(f.x, f.y, 22f, paint)

        paint.color = Color.WHITE
        c.drawCircle(f.x - 6f, f.y - 4f, 4f, paint)
        c.drawCircle(f.x + 6f, f.y - 4f, 4f, paint)

        paint.color = Color.DKGRAY
        c.drawRect(f.x - 24f, f.y - 34f, f.x + 24f, f.y - 29f, paint)

        paint.color = Color.GREEN
        c.drawRect(
            f.x - 24f, f.y - 34f,
            f.x - 24f + 48f * f.hp.coerceAtLeast(0) / 100f,
            f.y - 29f, paint
        )
    }

    private fun updateEnemies() {
        val now = System.currentTimeMillis()

        for (enemy in enemies.toList()) {
            val dx = player.x - enemy.x
            val dy = player.y - enemy.y
            val distance = hypot(dx, dy).coerceAtLeast(1f)

            if (distance > 65f) {
                enemy.x += dx / distance * 1.3f
                enemy.y += dy / distance * 1.3f
            } else if (now - lastAttack > 900L) {
                player.hp -= 5
                lastAttack = now
            }

            enemy.x = enemy.x.coerceIn(30f, width - 30f)
            enemy.y = enemy.y.coerceIn(40f, height - 35f)
        }

        player.x += moveX * 5f
        player.y += moveY * 5f
        player.x = player.x.coerceIn(30f, width - 30f)
        player.y = player.y.coerceIn(40f, height - 35f)
    }

    private fun attack(superAttack: Boolean) {
        if (gameOver) {
            resetGame()
            return
        }

        if (superAttack && !superReady) return
        if (superAttack) superReady = false

        val damage = if (superAttack) 100 else 35
        val range = if (superAttack) 260f else 190f

        val target = enemies.minByOrNull {
            hypot(it.x - player.x, it.y - player.y)
        } ?: return

        if (hypot(target.x - player.x, target.y - player.y) <= range) {
            target.hp -= damage
            if (target.hp <= 0) enemies.remove(target)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_MOVE -> {
                if (gameOver) {
                    resetGame()
                } else if (x < width * .35f) {
                    moveX = ((x - width * .15f) / 55f).coerceIn(-1f, 1f)
                    moveY = ((y - height * .75f) / 55f).coerceIn(-1f, 1f)
                } else if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                    when {
                        x > width * .82f -> attack(false)
                        x > width * .65f && y > height * .65f ->
                            attack(true)
                    }
                }
                invalidate()
            }

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                moveX = 0f
                moveY = 0f
                invalidate()
            }
        }
        return true
    }
}
