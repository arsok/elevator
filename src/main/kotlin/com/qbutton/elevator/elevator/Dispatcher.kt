package com.qbutton.elevator.elevator

import java.util.*
import java.util.concurrent.atomic.AtomicInteger

object Dispatcher {
    const val capacity = 2

    //mb array of elevatorDispatchers? each has requests and curFloor

    val requests = Collections.synchronizedNavigableSet(TreeSet<Int>())

    val floor = AtomicInteger()

    val passengersCount = AtomicInteger()
}