package com.example.model

enum class Direction(val dx: Int, val dy: Int) {
    UP(0, -1),
    RIGHT(1, 0),
    DOWN(0, 1),
    LEFT(-1, 0);

    fun opposite(): Direction = when (this) {
        UP -> DOWN
        DOWN -> UP
        LEFT -> RIGHT
        RIGHT -> LEFT
    }

    fun turnClockwise(): Direction = when (this) {
        UP -> RIGHT
        RIGHT -> DOWN
        DOWN -> LEFT
        LEFT -> UP
    }

    fun turnCounterClockwise(): Direction = when (this) {
        UP -> LEFT
        LEFT -> DOWN
        DOWN -> RIGHT
        RIGHT -> UP
    }
}

data class Position(val x: Int, val y: Int) {
    fun move(direction: Direction): Position = Position(x + direction.dx, y + direction.dy)

    fun manhattanDistance(other: Position): Int =
        kotlin.math.abs(x - other.x) + kotlin.math.abs(y - other.y)
}
