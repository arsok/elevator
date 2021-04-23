package com.qbutton.elevator.elevator

import java.util.Collections
import java.util.NavigableSet
import java.util.TreeSet
import java.util.concurrent.atomic.AtomicInteger

object Dispatcher {
    val requestsUp: NavigableSet<Int> = Collections.synchronizedNavigableSet(TreeSet())
    val requestsDown: NavigableSet<Int> = Collections.synchronizedNavigableSet(TreeSet())

    val floor = AtomicInteger(-1)
}