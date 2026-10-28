package com.fonlisstars.game

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.MotionEvent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.content.Context

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(28, 36, 75)
        window.navigationBarColor = Color.rgb(28, 36, 75)
        setContentView(FonlisView(this))
    }
}

private class FonlisView(context: Context) : View(context) {
    private val p = Paint(Paint.ANTI_ALIAS_FLAG)
    private val prefs = context.getSharedPreferences("fonlis", Context.MODE_PRIVATE)

    private var trophies = prefs.getInt("trophies", 0)
    private var coins = prefs.getInt("coins", 500)
    private var gems = prefs.getInt("gems", 30)
    private var pass = prefs.getInt("pass", 0)
    private var claimed = prefs.getInt("claimed", 0)
    private var enemyHp = 3
    private var screen = "home"
    private var message = ""

    private data class Hit(val rect: RectF, val action: String)
    private val hits = mutableListOf<Hit>()

    private val navy = Color.rgb(28, 36, 75)
    private val blue = Color.rgb(56, 111, 225)
    private val yellow = Color.rgb(255, 202, 45)
    private val pink = Color.rgb(238, 68, 153)
    private val white = Color.WHITE

    private fun save() {
        prefs.edit()
            .putInt("trophies", trophies)
            .putInt("coins", coins)
            .putInt("gems", gems)
            .putInt("pass", pass)
            .putInt("claimed", claimed)
            .apply()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        canvas.scale(width / 1000f, height / 600f)
        hits.clear()

        p.style = Paint.Style.FILL
        canvas.drawColor(Color.rgb(75, 165, 239))

        when (screen) {
            "home" -> drawHome(canvas)
            "fighters" -> drawFighters(canvas)
            "pass" -> drawPass(canvas)
            "road" -> drawRoad(canvas)
            "shop" -> drawShop(canvas)
            "battle" -> drawBattle(canvas)
            "profile" -> drawProfile(canvas)
            "settings" -> drawSettings(canvas)
        }

        if (message.isNotEmpty()) {
            box(canvas, 300f, 515f, 700f, 560f, navy)
            text(canvas, message, 500f, 544f, 17f, yellow)
        }

        canvas.restore()
    }

    private fun box(c: Canvas, x: Float, y: Float, r: Float, b: Float, color: Int) {
        p.color = color
        p.style = Paint.Style.FILL
        c.drawRoundRect(RectF(x, y, r, b), 14f, 14f, p)
        p.color = Color.rgb(20, 26, 55)
        p.style = Paint.Style.STROKE
        p.strokeWidth = 3f
        c.drawRoundRect(RectF(x, y, r, b), 14f, 14f, p)
        p.style = Paint.Style.FILL
    }

    private fun text(
        c: Canvas, s: String, x: Float, y: Float,
        size: Float, color: Int = white
    ) {
        p.color = color
        p.textSize = size
        p.typeface = android.graphics.Typeface.create(
            "sans-serif-condensed", android.graphics.Typeface.BOLD
        )
        p.textAlign = Paint.Align.CENTER
        p.style = Paint.Style.FILL
        c.drawText(s, x, y, p)
    }

    private fun button(
        c: Canvas, label: String, x: Float, y: Float,
        r: Float, b: Float, color: Int, action: String
    ) {
        box(c, x, y, r, b, color)
        text(c, label, (x + r) / 2f, (y + b) / 2f + 7f, 20f)
        hits.add(Hit(RectF(x, y, r, b), action))
    }

    private fun header(c: Canvas, title: String) {
        box(c, 18f, 12f, 982f, 65f, navy)
        text(c, title, 500f, 46f, 28f, yellow)
        text(c, "🏆 $trophies", 110f, 48f, 19f)
        text(c, "● $coins", 800f, 48f, 19f, yellow)
    }

    private fun drawHome(c: Canvas) {
        // Stylized arena background
        p.color = Color.rgb(244, 133, 101)
        c.drawRect(0f, 100f, 1000f, 600f, p)
        p.color = Color.rgb(255, 177, 105)
        c.drawRect(0f, 350f, 1000f, 600f, p)

        for (i in 0..8) {
            val x = i * 125f
            box(c, x, 390f + (i % 3) * 28f, x + 80f, 465f + (i % 3) * 28f,
                if (i % 2 == 0) blue else Color.rgb(213, 80, 99))
        }

        box(c, 20f, 82f, 255f, 143f, navy)
        text(c, "FONLIS", 137f, 108f, 28f, yellow)
        text(c, "STARS", 137f, 134f, 27f, pink)

        // Character illustration
        p.color = Color.rgb(67, 49, 150)
        c.drawRoundRect(RectF(410f, 355f, 590f, 505f), 28f, 28f, p)
        p.color = Color.rgb(117, 68, 225)
        c.drawCircle(500f, 270f, 88f, p)
        p.color = Color.rgb(255, 202, 164)
        c.drawCircle(500f, 285f, 58f, p)
        p.color = Color.rgb(47, 38, 77)
        c.drawCircle(480f, 278f, 7f, p)
        c.drawCircle(520f, 278f, 7f, p)
        p.color = yellow
        c.drawRoundRect(RectF(453f, 316f, 547f, 338f), 8f, 8f, p)
        box(c, 558f, 286f, 605f, 350f, navy)

        text(c, "ТВОЙ БОЕЦ", 500f, 390f, 18f)
        text(c, "РАНГ ${trophies / 25 + 1}", 500f, 420f, 22f, yellow)

        button(c, "БОЙЦЫ", 20f, 165f, 190f, 220f, blue, "fighters")
        button(c, "НАГРАДЫ", 20f, 232f, 190f, 287f, pink, "road")
        button(c, "МАГАЗИН", 20f, 299f, 190f, 354f, blue, "shop")
        button(c, "ПРОФИЛЬ", 20f, 366f, 190f, 421f, navy, "profile")

        box(c, 20f, 475f, 430f, 580f, yellow)
        text(c, "FONLIS PASS", 225f, 506f, 25f, navy)
        text(c, "Прогресс: $pass / 400", 225f, 537f, 18f, navy)
        hits.add(Hit(RectF(20f, 475f, 430f, 580f), "pass"))

        box(c, 450f, 475f, 980f, 580f, navy)
        text(c, "ВЫБРАТЬ РЕЖИМ", 715f, 505f, 20f, yellow)
        text(c, "Сражайся и получай кубки!", 715f, 532f, 17f)
        button(c, "ИГРАТЬ", 740f, 540f, 960f, 577f, yellow, "battle")

        text(c, "💎 $gems", 870f, 95f, 20f)
        button(c, "⚙", 920f, 80f, 980f, 125f, navy, "settings")
    }

    private fun drawFighters(c: Canvas) {
        header(c, "ТВОИ БОЙЦЫ")
        text(c, "КОЛЛЕКЦИЯ", 500f, 110f, 23f)
        val names = listOf("ФОНЛИ", "ИСКРА", "ТЕНЬ", "РОКИ")
        for (i in names.indices) {
            val x = 45f + i * 240f
            box(c, x, 145f, x + 205f, 365f,
                listOf(pink, blue, navy, Color.rgb(55, 155, 100))[i])
            p.color = listOf(
                Color.rgb(117, 68, 225), yellow,
                Color.rgb(60, 60, 95), Color.rgb(70, 180, 120)
            )[i]
            c.drawCircle(x + 102f, 215f, 48f, p)
            p.color = Color.rgb(255, 202, 164)
            c.drawCircle(x + 102f, 225f, 30f, p)
            text(c, names[i], x + 102f, 292f, 20f)
            text(c, if (i == 0) "ВЫБРАН" else "СКОРО", x + 102f, 330f, 16f, yellow)
            hits.add(Hit(RectF(x, 145f, x + 205f, 365f), "fighter$i"))
        }
        text(c, "Выбирай бойца перед сражением", 500f, 415f, 20f)
        button(c, "НАЗАД", 25f, 525f, 200f, 580f, navy, "home")
    }

    private fun drawPass(c: Canvas) {
        header(c, "FONLIS PASS")
        text(c, "ЗАРАБАТЫВАЙ ОЧКИ В БОЯХ", 500f, 110f, 22f)
        box(c, 60f, 150f, 940f, 205f, navy)
        box(c, 75f, 165f, 75f + pass * 2.1f, 190f, yellow)
        text(c, "$pass / 400", 500f, 245f, 26f, yellow)

        for (i in 0..3) {
            val x = 55f + i * 230f
            box(c, x, 285f, x + 195f, 415f, if (pass >= (i + 1) * 100) blue else navy)
            text(c, "УРОВЕНЬ ${(i + 1) * 100}", x + 97f, 320f, 17f)
            text(c, if (pass >= (i + 1) * 100) "НАГРАДА" else "ЗАКРЫТО",
                x + 97f, 360f, 18f, yellow)
            text(c, "+100 МОНЕТ", x + 97f, 390f, 16f)
        }
        text(c, "Очки пропуска выдаются за победы", 500f, 460f, 18f)
        button(c, "НАЗАД", 25f, 525f, 200f, 580f, navy, "home")
        button(c, "В БОЙ", 760f, 525f, 970f, 580f, yellow, "battle")
    }

    private fun drawRoad(c: Canvas) {
        header(c, "ДОРОГА КУБКОВ")
        text(c, "Следующая награда каждые 100 кубков", 500f, 110f, 21f)
        for (i in 1..6) {
            val x = 35f + ((i - 1) % 3) * 320f
            val y = if (i <= 3) 150f else 330f
            val target = i * 100
            box(c, x, y, x + 285f, y + 135f,
                if (trophies >= target && claimed < i) blue else navy)
            text(c, "$target КУБКОВ", x + 142f, y + 35f, 22f, yellow)
            text(c, if (trophies >= target && claimed < i) "ЗАБРАТЬ" else
                if (claimed >= i) "ПОЛУЧЕНО" else "ЗАКРЫТО",
                x + 142f, y + 77f, 20f)
            text(c, "+100 МОНЕТ", x + 142f, y + 108f, 17f)
            hits.add(Hit(RectF(x, y, x + 285f, y + 135f), "reward$i"))
        }
        button(c, "НАЗАД", 25f, 525f, 200f, 580f, navy, "home")
    }

    private fun drawShop(c: Canvas) {
        header(c, "МАГАЗИН")
        text(c, "ЕЖЕДНЕВНЫЕ ПРЕДЛОЖЕНИЯ", 500f, 110f, 23f)
        val labels = listOf("100 МОНЕТ", "5 КРИСТАЛЛОВ", "ПОДАРОК")
        val prices = listOf("Бесплатно", "20 монет", "Бесплатно")
        for (i in 0..2) {
            val x = 70f + i * 310f
            box(c, x, 155f, x + 250f, 380f, navy)
            text(c, labels[i], x + 125f, 220f, 21f, yellow)
            text(c, prices[i], x + 125f, 275f, 19f)
            button(c, "ЗАБРАТЬ", x + 20f, 315f, x + 230f, 365f,
                blue, "buy$i")
        }
        text(c, "Монеты и кристаллы сохраняются на устройстве", 500f, 440f, 18f)
        button(c, "НАЗАД", 25f, 525f, 200f, 580f, navy, "home")
    }

    private fun drawBattle(c: Canvas) {
        header(c, "АРЕНА FONLIS")
        text(c, "ПОПАДИ ПО ПРОТИВНИКУ 3 РАЗА!", 500f, 105f, 22f, yellow)

        // Arena walls
        box(c, 80f, 155f, 210f, 210f, Color.rgb(45, 145, 75))
        box(c, 785f, 160f, 915f, 215f, Color.rgb(45, 145, 75))
        box(c, 430f, 350f, 565f, 390f, Color.rgb(45, 145, 75))

        // Player
        p.color = Color.rgb(117, 68, 225)
        c.drawCircle(310f, 295f, 42f, p)
        p.color = Color.rgb(255, 202, 164)
        c.drawCircle(310f, 300f, 25f, p)
        p.color = navy
        c.drawCircle(302f, 297f, 4f, p)
        c.drawCircle(318f, 297f, 4f, p)
        text(c, "ТЫ", 310f, 360f, 18f)

        // Enemy
        p.color = Color.rgb(205, 60, 90)
        c.drawCircle(680f, 295f, 43f, p)
        p.color = Color.rgb(255, 202, 164)
        c.drawCircle(680f, 300f, 25f, p)
        p.color = navy
        c.drawCircle(672f, 297f, 4f, p)
        c.drawCircle(688f, 297f, 4f, p)
        text(c, "ВРАГ", 680f, 360f, 18f)
        text(c, "Здоровье врага: $enemyHp", 680f, 395f, 18f, yellow)

        button(c, "АТАКА", 610f, 430f, 820f, 490f, pink, "attack")
        button(c, "В МЕНЮ", 25f, 525f, 230f, 580f, navy, "home")
        button(c, "СДАТЬСЯ", 760f, 525f, 970f, 580f, navy, "home")
    }

    private fun drawProfile(c: Canvas) {
        header(c, "ПРОФИЛЬ ИГРОКА")
        text(c, "FONLIS", 500f, 170f, 42f, yellow)
        text(c, "Кубки: $trophies", 500f, 240f, 28f)
        text(c, "Монеты: $coins", 500f, 290f, 24f, yellow)
        text(c, "Кристаллы: $gems", 500f, 335f, 24f, pink)
        text(c, "Очки пропуска: $pass", 500f, 380f, 24f)
        button(c, "НАЗАД", 25f, 525f, 200f, 580f, navy, "home")
    }

    private fun drawSettings(c: Canvas) {
        header(c, "НАСТРОЙКИ")
        text(c, "FONLIS STARS — версия 1.0", 500f, 200f, 25f)
        text(c, "Прогресс хранится на этом устройстве", 500f, 250f, 20f)
        button(c, "СБРОСИТЬ ПРОГРЕСС", 310f, 320f, 690f, 380f, pink, "reset")
        button(c, "НАЗАД", 25f, 525f, 200f, 580f, navy, "home")
    }

    private fun act(a: String) {
        message = ""
        when {
            a == "home" -> screen = "home"
            a == "fighters" -> screen = "fighters"
            a == "pass" -> screen = "pass"
            a == "road" -> screen = "road"
            a == "shop" -> screen = "shop"
            a == "battle" -> {
                enemyHp = 3
                screen = "battle"
            }
            a == "profile" -> screen = "profile"
            a == "settings" -> screen = "settings"

            a == "attack" -> {
                if (enemyHp > 0) enemyHp--
                if (enemyHp == 0) {
                    trophies += 8
                    coins += 30
                    pass = (pass + 20).coerceAtMost(400)
                    save()
                    message = "ПОБЕДА! +8 КУБКОВ, +30 МОНЕТ"
                    screen = "home"
                } else {
                    message = "ПОПАДАНИЕ! Осталось: $enemyHp"
                }
            }

            a.startsWith("reward") -> {
                val n = a.removePrefix("reward").toIntOrNull() ?: 0
                if (trophies >= n * 100 && claimed < n) {
                    claimed = n
                    coins += 100
                    save()
                    message = "НАГРАДА ПОЛУЧЕНА: +100 МОНЕТ"
                } else {
                    message = "Сначала набери ${n * 100} кубков!"
                }
            }

            a.startsWith("buy") -> {
                when (a) {
                    "buy0" -> {
                        coins += 100
                        message = "ПОЛУЧЕНО +100 МОНЕТ"
                    }
                    "buy1" -> {
                        if (coins >= 20) {
                            coins -= 20
                            gems += 5
                            message = "ПОЛУЧЕНО +5 КРИСТАЛЛОВ"
                        } else message = "НЕ ХВАТАЕТ МОНЕТ"
                    }
                    "buy2" -> {
                        coins += 50
                        message = "ПОДАРОК: +50 МОНЕТ"
                    }
                }
                save()
            }

            a.startsWith("fighter") -> {
                message = if (a == "fighter0") "ФОНЛИ ВЫБРАН!" else
                    "ЭТОТ БОЕЦ ПОЯВИТСЯ В БУДУЩЕМ ОБНОВЛЕНИИ"
            }

            a == "reset" -> {
                trophies = 0
                coins = 500
                gems = 30
                pass = 0
                claimed = 0
                save()
                message = "ПРОГРЕСС СБРОШЕН"
            }
        }
        save()
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            val x = event.x * 1000f / width
            val y = event.y * 600f / height
            val hit = hits.lastOrNull { it.rect.contains(x, y) }
            if (hit != null) act(hit.action)
            performClick()
            return true
        }
        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }
}
