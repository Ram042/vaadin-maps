package com.example.demo

import java.util.function.BiFunction

fun <K, V> MutableMap<K, V>.putAndGet(key: K, value: V): V {
    return this.compute(key) { _, _ -> value }!!
}