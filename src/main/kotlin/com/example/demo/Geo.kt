package com.example.demo

import kotlin.math.pow
import kotlin.math.sqrt


private val Point.x: Double
    get() {
        return this.lat
    }
private val Point.y: Double
    get() {
        return this.lng
    }

public fun checkPointInsidePolygon(polygonPoints: List<Point>, point: Point): Boolean {
    // https://en.wikipedia.org/wiki/Point_in_polygon
    val vertices: List<Point> = listOf(*polygonPoints.toTypedArray())

    var segments: MutableList<Segment> = mutableListOf()

    vertices.dropLast(1).forEachIndexed { i, p ->
        segments.add(Segment(p, vertices[i + 1]))
    }
    segments.add(Segment(vertices.first(), vertices.last()))

    val middleOfEdge: Point = fun(p1: Point, p2: Point): Point {
        return (p1 + p2) / 2.0
    }(segments.first().p1, segments.first().p2)

    val ray = Segment(point, middleOfEdge * 5);

    var intersections = 0;
    segments.forEach {
        intersections += if (intersectsNoTouch(ray, it)) 1 else 0
    }
    return intersections % 2 == 1
}

private operator fun Point.times(i: Int): Point = Point(x * i, y * i)

public fun checkPointInsidePolygon(polygon: Polygon, point: Point): Boolean {
    return checkPointInsidePolygon(polygon.points, point)
}


public fun checkPolygonValid(polygonPoints: List<Point>, newPoint: Point): Boolean {
    val vertices: List<Point> = listOf(*polygonPoints.toTypedArray(), newPoint)

    // 0 or 1 points
    if (vertices.size < 3) return true

    if (vertices.size == 3) {
        val d1 = distance(vertices[0], vertices[1]);
        val d2 = distance(vertices[1], vertices[2]);
        val d3 = distance(vertices[1], vertices[2]);
        return d1 + d2 != d3
    };

    // points in order form set of lines
    var segments: MutableList<Segment> = mutableListOf()

    vertices.dropLast(1).forEachIndexed { i, p ->
        segments.add(Segment(p, vertices[i + 1]))
    }
    segments.add(Segment(vertices.first(), vertices.last()))

    // check last 2 lines do not intersect with others
    segments.dropLast(2).forEach {
        if (intersectsAllowTouch(it, segments.last(0))) {
            return false
        }
        if (intersectsAllowTouch(it, segments.last(1))) {
            return false
        }
    }
    return true
}


/**
 * Check if edged of new polygon intersect itself.
 *
 * @param polygon non-intersecting polygon
 */
public fun checkPolygonValid(polygon: Polygon, newPoint: Point): Boolean = checkPolygonValid(polygon.points, newPoint)

fun <T> List<T>.last(n: Int): T {
    return get(size - 1 - n);
}

fun intersects(l1: Segment, l2: Segment, allowTouch: Boolean): Boolean {
    val s0 = arrayOf(arrayOf(l1.p1.x, l1.p1.y), arrayOf(l1.p2.x, l1.p2.y))
    val s1 = arrayOf(arrayOf(l2.p1.x, l2.p1.y), arrayOf(l2.p2.x, l2.p2.y))

    val dx0 = s0[1][0] - s0[0][0]
    val dx1 = s1[1][0] - s1[0][0]
    val dy0 = s0[1][1] - s0[0][1]
    val dy1 = s1[1][1] - s1[0][1]
    val p0 = dy1 * (s1[1][0] - s0[0][0]) - dx1 * (s1[1][1] - s0[0][1])
    val p1 = dy1 * (s1[1][0] - s0[1][0]) - dx1 * (s1[1][1] - s0[1][1])
    val p2 = dy0 * (s0[1][0] - s1[0][0]) - dx0 * (s0[1][1] - s1[0][1])
    val p3 = dy0 * (s0[1][0] - s1[1][0]) - dx0 * (s0[1][1] - s1[1][1])
    return if (allowTouch) {
        (p0 * p1 < 0) && (p2 * p3 < 0);
    } else {
        (p0 * p1 <= 0) && (p2 * p3 <= 0);
    }
}

fun intersectsAllowTouch(l1: Segment, l2: Segment): Boolean = intersects(l1, l2, true)

fun intersectsNoTouch(l1: Segment, l2: Segment): Boolean = intersects(l1, l2, false)


fun distance(p1: Point, p2: Point): Double {
    return sqrt((p1.x - p2.x).pow(2) + (p1.y - p2.y).pow(2))
}

operator fun Point.minus(p2: Point) = Point(x - p2.x, y - p2.y)
operator fun Point.plus(p2: Point) = Point(x + p2.x, y + p2.y)
operator fun Point.plus(v: Double) = Point(x + v, y + v)

operator fun Point.div(v: Double) = Point(x / v, y / v);

data class Segment(val p1: Point, val p2: Point)