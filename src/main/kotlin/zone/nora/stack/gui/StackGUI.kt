package zone.nora.stack.gui

import club.sk1er.elementa.WindowScreen
import club.sk1er.elementa.components.UIBlock
import club.sk1er.elementa.components.UIContainer
import club.sk1er.elementa.components.UIText
import club.sk1er.elementa.components.UIWrappedText
import club.sk1er.elementa.constraints.CenterConstraint
import club.sk1er.elementa.constraints.SiblingConstraint
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.dsl.*
import club.sk1er.mods.core.universal.UKeyboard
import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.util.ResourceLocation
import org.lwjgl.input.Keyboard
import zone.nora.stack.util.DelayedTask
import java.awt.Color

class StackGUI : WindowScreen() {
    private val board = UIContainer().constrain {
        x = CenterConstraint()
        y = CenterConstraint()
        width = 216.pixels()
        height = 288.pixels()
    } childOf window

    private val squares: ArrayList<Square> = ArrayList()
    private val accent: Color = Color(0xc50ed2)
    private val instructions: UIWrappedText
    private val resetButton: UIText

    private var started: Boolean = false
        set(value) {
            field = value
            if (value) {
                move()
            }
        }
    private var cancelAction: Boolean = false
    private var currentRow: Int = 11
    private var currentDirection: Direction = Direction.LEFT
    private var activeSquares: IntRange = (102..104)

    init {
        var row = 0
        for (i in 0..107) {
            val j = i % 9
            val square = Square(i).constrain {
                x = (j * 24).pixels()
                y = (24 * row).pixels()
            } childOf board
            squares.add(square)
            if (j == 8) row++
        }

        UIText("\u00a7nStack!", shadow = false).constrain {
            x = CenterConstraint()
            y = SiblingConstraint(8f, alignOpposite = true)
            color = accent.asConstraint()
            textScale = 2.5f.pixels()
        } childOf window

        instructions = UIWrappedText("Stack the blocks by pressing SPACE when they are on top of each-other! Press SPACE to start!!", shadow = false, centered = true).constrain {
            width = 100.pixels()
            x = (window.getWidth() / 2 + 118).pixels()
            y = CenterConstraint()
        } childOf window

        resetButton = UIText("\u00a7cReset").constrain {
            x = CenterConstraint()
            y = (window.getHeight() / 2 + 160).pixels()
            textScale = 1.5f.pixels()
        }.onMouseEnter {
            (this as UIText).setText("\u00a7c\u00a7nReset")
        }.onMouseLeave {
            (this as UIText).setText("\u00a7cReset")
        }.onMouseClick {
            mc.soundHandler.playSound(PositionedSoundRecord.create(ResourceLocation("gui.button.press"), 1.0f))
            window.clearChildren()
            mc.displayGuiScreen(StackGUI())
        } as UIText childOf window

        val copyright = "(C) 2021 Nora Cos."

        UIText(copyright, shadow = false).constrain {
            x = (-"(C)".width(.7f)).pixels(alignOpposite = true, alignOutside = true)
            y = 0.pixels(true)
            textScale = .7f.pixels()
        }.onMouseEnter {
            animate {
                setXAnimation(Animations.OUT_BOUNCE, .5f, (-copyright.width(.7f)).pixels(alignOpposite = true, alignOutside = true))
            }
        }.onMouseLeave {
            animate {
                setXAnimation(Animations.OUT_BOUNCE, .5f, (-"(C)".width(.7f)).pixels(alignOpposite = true, alignOutside = true))
            }
        } childOf window
    }

    override fun onKeyPressed(keyCode: Int, typedChar: Char, modifiers: UKeyboard.Modifiers?) {
        if (keyCode == Keyboard.KEY_SPACE) {
            if (started) {
                cancelAction = true
            } else {
                val starting = (102..104)
                for (i in starting) {
                    squares[i].active = true
                }
                instructions.animate {
                    setColorAnimation(Animations.LINEAR, .5f, Color(255, 255, 255, 0).asConstraint())
                }
                started = true
                activeSquares = starting
            }
        } else {
            super.onKeyPressed(keyCode, typedChar, modifiers)
        }
    }

    private fun move() {
        val newMin: Int
        val newMax: Int
        if (currentDirection == Direction.LEFT) {
            for (i in activeSquares) {
                squares[i].active = false
                squares[i - 1].active = true
            }
            newMin = activeSquares.first - 1
            newMax = activeSquares.last - 1
        } else {
            for (i in activeSquares.reversed()) {
                squares[i].active = false
                squares[i + 1].active = true
            }
            newMin = activeSquares.first + 1
            newMax = activeSquares.last + 1
        }
        activeSquares = newMin..newMax
        if (newMin % 9 == 0) currentDirection = Direction.RIGHT else if (newMax % 9 == 8) currentDirection = Direction.LEFT
        DelayedTask({ if (cancelAction) handlePress() else move() }, currentRow / 2)
    }

    private fun handlePress() {
        currentRow--
        when {
            currentRow < 0 -> { } // atm nothing but thihs is victory
            currentRow == 10 -> {
                activeSquares = activeSquares.first - 9..activeSquares.last - 9
                cancelAction = false
                move()
            }
            else -> {
                var blocksLost = activeSquares.last - activeSquares.first + 1
                for (i in activeSquares) {
                    if (squares[i + 9].active) blocksLost-- else squares[i].active = false
                }
                activeSquares = if (blocksLost >= 0) {
                    activeSquares.first - 9..activeSquares.last - 9 - blocksLost
                } else {
                    activeSquares.first - 9..activeSquares.last - 9
                }
                cancelAction = false
                move()
            }
        }
    }

    private class Square(id: Int) : UIBlock(Color(0, 0, 0, 100)) {
        var active: Boolean = false
            set(value) {
                field = value
                setColor(if (value) Color(0xc50ed2).asConstraint() else Color(0, 0, 0, 100).asConstraint())
            }

        init {
            constrain {
                width = 20.pixels()
                height = 20.pixels()
            }

            if (Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) {
                UIText(id.toString()).constrain {
                    x = CenterConstraint()
                    y = CenterConstraint()
                    textScale = .5f.pixels()
                } childOf this
            }
        }
    }

    enum class Direction {
        LEFT,
        RIGHT
    }
}