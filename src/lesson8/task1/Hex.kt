@file:Suppress("UNUSED_PARAMETER")

package lesson8.task1

import java.lang.Math.abs

/**
 * Точка (гекс) на шестиугольной сетке.
 * Координаты заданы как в примере (первая цифра - y, вторая цифра - x)
 *
 *       60  61  62  63  64  65
 *     50  51  52  53  54  55  56
 *   40  41  42  43  44  45  46  47
 * 30  31  32  33  34  35  36  37  38
 *   21  22  23  24  25  26  27  28
 *     12  13  14  15  16  17  18
 *       03  04  05  06  07  08
 *
 * В примерах к задачам используются те же обозначения точек,
 * к примеру, 16 соответствует HexPoint(x = 6, y = 1), а 41 -- HexPoint(x = 1, y = 4).
 *
 * В задачах, работающих с шестиугольниками на сетке, считать, что они имеют
 * _плоскую_ ориентацию:
 *  __
 * /  \
 * \__/
 *
 * со сторонами, параллельными координатным осям сетки.
 *
 * Более подробно про шестиугольные системы координат можно почитать по следующей ссылке:
 *   https://www.redblobgames.com/grids/hexagons/
 */
data class HexPoint(val x: Int, val y: Int) {
    /**
     * Средняя
     *
     * Найти целочисленное расстояние между двумя гексами сетки.
     * Расстояние вычисляется как число единичных отрезков в пути между двумя гексами.
     * Например, путь межу гексами 16 и 41 (см. выше) может проходить через 25, 34, 43 и 42 и имеет длину 5.
     */
    fun distance(other: HexPoint): Int {
        val dX = x - other.x
        val dY = y - other.y
        return (abs(dX) + abs(dY) + abs(dX + dY)) / 2
    }

    override fun toString(): String = "$y.$x"
}

/**
 * Правильный шестиугольник на гексагональной сетке.
 * Как окружность на плоскости, задаётся центральным гексом и радиусом.
 * Например, шестиугольник с центром в 33 и радиусом 1 состоит из гексов 42, 43, 34, 24, 23, 32.
 */
data class Hexagon(val center: HexPoint, val radius: Int) {

    /**
     * Средняя
     *
     * Рассчитать расстояние между двумя шестиугольниками.
     * Оно равно расстоянию между ближайшими точками этих шестиугольников,
     * или 0, если шестиугольники имеют общую точку.
     *
     * Например, расстояние между шестиугольником A с центром в 31 и радиусом 1
     * и другим шестиугольником B с центром в 26 и радиуоом 2 равно 2
     * (расстояние между точками 32 и 24)
     */
    fun distance(other: Hexagon): Int {
        val x = this.center.distance(other.center) - (this.radius + other.radius)
        return when {
            x > 0 -> x
            else -> 0
        }
    }

    /**
     * Тривиальная
     *
     * Вернуть true, если заданная точка находится внутри или на границе шестиугольника
     */
    fun contains(point: HexPoint): Boolean = point.distance(this.center) <= radius
}

/**
 * Прямолинейный отрезок между двумя гексами
 */
class HexSegment(val begin: HexPoint, val end: HexPoint) {
    /**
     * Простая
     *
     * Определить "правильность" отрезка.
     * "Правильным" считается только отрезок, проходящий параллельно одной из трёх осей шестиугольника.
     * Такими являются, например, отрезок 30-34 (горизонталь), 13-63 (прямая диагональ) или 51-24 (косая диагональ).
     * А, например, 13-26 не является "правильным" отрезком.
     */
    fun isValid(): Boolean =
        when {
            this.begin.y == this.end.y && this.begin.x == this.end.x -> false
            else -> this.begin.y == this.end.y || this.begin.x == this.end.x || this.begin.y - this.end.y == this.end.x - this.begin.x
        }

    /**
     * Средняя
     *
     * Вернуть направление отрезка (см. описание класса Direction ниже).
     * Для "правильного" отрезка выбирается одно из первых шести направлений,
     * для "неправильного" -- INCORRECT.
     */
    fun direction(): Direction {
        return when {
            !isValid() -> Direction.INCORRECT
            this.begin.y == this.end.y && this.begin.x < this.end.x -> Direction.RIGHT
            this.begin.y == this.end.y && this.begin.x > this.end.x -> Direction.LEFT
            this.begin.x == this.end.x && this.begin.y < this.end.y -> Direction.UP_RIGHT
            this.end.y - this.begin.y == this.begin.x - this.end.x && this.begin.y < this.end.y -> Direction.UP_LEFT
            this.begin.y > this.end.y && this.begin.x == this.end.x -> Direction.DOWN_LEFT
            else -> Direction.DOWN_RIGHT
        }
    }

    override fun equals(other: Any?) =
        other is HexSegment && (begin == other.begin && end == other.end || end == other.begin && begin == other.end)

    override fun hashCode() =
        begin.hashCode() + end.hashCode()
}

/**
 * Направление отрезка на гексагональной сетке.
 * Если отрезок "правильный", то он проходит вдоль одной из трёх осей шестугольника.
 * Если нет, его направление считается INCORRECT
 */
enum class Direction {
    RIGHT,      // слева направо, например 30 -> 34
    UP_RIGHT,   // вверх-вправо, например 32 -> 62
    UP_LEFT,    // вверх-влево, например 25 -> 61
    LEFT,       // справа налево, например 34 -> 30
    DOWN_LEFT,  // вниз-влево, например 62 -> 32
    DOWN_RIGHT, // вниз-вправо, например 61 -> 25
    INCORRECT;  // отрезок имеет изгиб, например 30 -> 55 (изгиб в точке 35)

    /**
     * Простая
     *
     * Вернуть направление, противоположное данному.
     * Для INCORRECT вернуть INCORRECT
     */
    fun opposite(): Direction {
        return when (this) {
            INCORRECT -> INCORRECT
            RIGHT -> LEFT
            UP_RIGHT -> DOWN_LEFT
            UP_LEFT -> DOWN_RIGHT
            LEFT -> RIGHT
            DOWN_LEFT -> UP_RIGHT
            else -> UP_LEFT
        }
    }

    /**
     * Средняя
     *
     * Вернуть направление, повёрнутое относительно
     * заданного на 60 градусов против часовой стрелки.
     *
     * Например, для RIGHT это UP_RIGHT, для UP_LEFT это LEFT, для LEFT это DOWN_LEFT.
     * Для направления INCORRECT бросить исключение IllegalArgumentException.
     * При решении этой задачи попробуйте обойтись без перечисления всех семи вариантов.
     */
    fun next(): Direction {
        return when (this) {
            INCORRECT -> throw IllegalArgumentException()
            DOWN_RIGHT -> RIGHT
            else -> values()[ordinal + 1]
        }
    }

    /**
     * Простая
     *
     * Вернуть true, если данное направление совпадает с other или противоположно ему.
     * INCORRECT не параллельно никакому направлению, в том числе другому INCORRECT.
     */
    fun isParallel(other: Direction): Boolean {
        return when {
            this == INCORRECT -> false
            this == other || this == other.opposite() -> true
            else -> false
        }
    }
}

/**
 * Средняя
 *
 * Сдвинуть точку в направлении direction на расстояние distance.
 * Бросить IllegalArgumentException(), если задано направление INCORRECT.
 * Для расстояния 0 и направления не INCORRECT вернуть ту же точку.
 * Для отрицательного расстояния сдвинуть точку в противоположном направлении на -distance.
 *
 * Примеры:
 * 30, direction = RIGHT, distance = 3 --> 33
 * 35, direction = UP_LEFT, distance = 2 --> 53
 * 45, direction = DOWN_LEFT, distance = 4 --> 05
 */
fun HexPoint.move(direction: Direction, distance: Int): HexPoint {
    return when (direction) {
        Direction.RIGHT -> HexPoint(x + distance, y)
        Direction.LEFT -> HexPoint(x - distance, y)
        Direction.UP_RIGHT -> HexPoint(x, y + distance)
        Direction.UP_LEFT -> HexPoint(x - distance, y + distance)
        Direction.DOWN_RIGHT -> HexPoint(x + distance, y - distance)
        Direction.DOWN_LEFT -> HexPoint(x, y - distance)
        else -> throw IllegalArgumentException()
    }
}

/**
 * Сложная
 *
 * Найти кратчайший путь между двумя заданными гексами, представленный в виде списка всех гексов,
 * которые входят в этот путь.
 * Начальный и конечный гекс также входят в данный список.
 * Если кратчайших путей существует несколько, вернуть любой из них.
 *
 * Пример (для координатной сетки из примера в начале файла):
 *   pathBetweenHexes(HexPoint(y = 2, x = 2), HexPoint(y = 5, x = 3)) ->
 *     listOf(
 *       HexPoint(y = 2, x = 2),
 *       HexPoint(y = 2, x = 3),
 *       HexPoint(y = 3, x = 3),
 *       HexPoint(y = 4, x = 3),
 *       HexPoint(y = 5, x = 3)
 *     )
 */
fun pathBetweenHexes(from: HexPoint, to: HexPoint): List<HexPoint> {
    if (from.distance(to) < 2)
        return listOf(from, to)

    val result = mutableListOf<HexPoint>()
    val length = from.distance(to)

    if (HexSegment(from, to).direction() == Direction.LEFT || HexSegment(from, to).direction() == Direction.RIGHT) {
        result.add(from)
        for (i in 1 until length) {
            if (from.x < to.x)
                result.add(HexPoint(from.x + i, from.y))
            else result.add(HexPoint(from.x - i, from.y))
        }
        result.add(to)

    } else {
        var last1 = HexPoint(from.x + from.y - to.y, to.y)
        var last2 = HexPoint(from.x, to.y)
        var begin = from
        var end = to

        val segment = if (last1.distance(begin) + last1.distance(end) < last2.distance(begin) + last2.distance(end))
            HexSegment(from, last1).direction()
        else HexSegment(from, last2).direction()

        if (segment == Direction.UP_LEFT || segment == Direction.UP_RIGHT) {
            result.add(to)
            begin = to
            end = from
            last1 = HexPoint(begin.x + begin.y - end.y, end.y)
            last2 = HexPoint(begin.x, end.y)
        } else result.add(from)

        var x = begin.x
        var y = begin.y

        if (last1.x >= end.x && (segment == Direction.UP_LEFT || segment == Direction.DOWN_RIGHT) ||
            last2.x <= end.x && (segment == Direction.UP_RIGHT || segment == Direction.DOWN_LEFT)
        ) {
            while (x != end.x) {
                x++
                y--
                result.add(HexPoint(x, y))
            }
            while (y != end.y) {
                y--
                result.add(HexPoint(x, y))
            }
        } else when (segment) {
            Direction.UP_LEFT, Direction.DOWN_RIGHT -> {
                while (y != end.y) {
                    y--
                    x++
                    result.add(HexPoint(x, y))
                }
                while (x != end.x) {
                    x++
                    result.add(HexPoint(x, y))
                }

            }
            else -> {
                while (y != last2.y) {
                    y--
                    result.add(HexPoint(x, y))
                }
                while (x != end.x) {
                    x--
                    result.add(HexPoint(x, y))
                }
            }
        }

        if (segment == Direction.UP_LEFT || segment == Direction.UP_RIGHT)
            result.reverse()
    }

    return result
}

/**
 * Очень сложная
 *
 * Дано три точки (гекса). Построить правильный шестиугольник, проходящий через них
 * (все три точки должны лежать НА ГРАНИЦЕ, а не ВНУТРИ, шестиугольника).
 * Все стороны шестиугольника должны являться "правильными" отрезками.
 * Вернуть null, если такой шестиугольник построить невозможно.
 * Если шестиугольников существует более одного, выбрать имеющий минимальный радиус.
 *
 * Пример: через точки 13, 32 и 44 проходит правильный шестиугольник с центром в 24 и радиусом 2.
 * Для точек 13, 32 и 45 такого шестиугольника не существует.
 * Для точек 32, 33 и 35 следует вернуть шестиугольник радиусом 3 (с центром в 62 или 05).
 *
 * Если все три точки совпадают, вернуть шестиугольник нулевого радиуса с центром в данной точке.
 */
fun hexagonByThreePoints(a: HexPoint, b: HexPoint, c: HexPoint): Hexagon? = TODO()

/**
 * Очень сложная
 *
 * Дано множество точек (гексов). Найти правильный шестиугольник минимального радиуса,
 * содержащий все эти точки (безразлично, внутри или на границе).
 * Если множество пустое, бросить IllegalArgumentException.
 * Если множество содержит один гекс, вернуть шестиугольник нулевого радиуса с центром в данной точке.
 *
 * Пример: 13, 32, 45, 18 -- шестиугольник радиусом 3 (с центром, например, в 15)
 */
fun minContainingHexagon(vararg points: HexPoint): Hexagon = TODO()