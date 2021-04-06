package com.qbutton.elevator.elevator

import java.util.*
import java.util.concurrent.atomic.AtomicInteger

object Dispatcher {
    val requests = Collections.synchronizedNavigableSet(TreeSet<Int>())

    val floor = AtomicInteger()
}