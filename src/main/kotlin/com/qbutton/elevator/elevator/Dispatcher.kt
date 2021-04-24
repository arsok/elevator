package com.qbutton.elevator.elevator

import java.util.Collections
import java.util.NavigableSet
import java.util.TreeSet

object Dispatcher {
    val requestsUp: NavigableSet<Int> = Collections.synchronizedNavigableSet(TreeSet())
    val requestsDown: NavigableSet<Int> = Collections.synchronizedNavigableSet(TreeSet())

    @Volatile
    var curFloor = 0

    fun resetState() {
        curFloor = 0
        requestsUp.clear()
        requestsDown.clear()
    }
}