package com.qbutton.elevator.elevator.impl

import com.qbutton.elevator.elevator.Dispatcher
import com.qbutton.elevator.elevator.api.Elevator
import com.qbutton.elevator.logger.ThreadLogger
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class ElevatorImpl : Elevator {

    val visitedFloors = CopyOnWriteArrayList<Int>()
    private val log = ThreadLogger(this.javaClass.name)

    private var direction = Direction.UP

    private val doorsOpen = AtomicBoolean(true)

    override fun enter() {
        while (!doorsOpen.get()) {
            log.info("waiting for doors to open to enter")
            TimeUnit.MILLISECONDS.sleep(30)
        }
        log.info("Entered elevator")
    }

    override fun exit() {
        while (!doorsOpen.get()) {
            log.info("waiting for doors to open to exit")
            TimeUnit.MILLISECONDS.sleep(30)
        }
        log.info("Exited elevator")
    }

    override fun requestFloor(floorNumber: Int) {
        log.info("requested floor $floorNumber, adding it to queue and waiting")
        Dispatcher.requests.add(floorNumber)
        while (Dispatcher.floor.get() != floorNumber) {
            TimeUnit.MILLISECONDS.sleep(100)
        }
    }

    private fun visitFloor(floorNumber: Int) {
        if (floorNumber == 0) direction = Direction.UP
        Dispatcher.floor.set(floorNumber)
        openDoors()
        log.info("visiting floor $floorNumber for a while")
        TimeUnit.MILLISECONDS.sleep(300)
        closeDoors()
        visitedFloors.add(floorNumber)
    }

    private fun openDoors() {
        log.info("opening doors")
        doorsOpen.set(true)
    }

    private fun closeDoors() {
        log.info("closing doors")
        doorsOpen.set(false)
    }

    inner class ElevatorManager : Runnable {
        override fun run() {
            while (true) {
                while (Dispatcher.requests.isEmpty()) {
                    TimeUnit.MILLISECONDS.sleep(500)
                    log.info("waiting for requests")
                }
                val nextFloor: Int?

                val curFloor = Dispatcher.floor.get()
                if (direction == Direction.UP && Dispatcher.requests.higher(curFloor) != null) {
                    nextFloor = Dispatcher.requests.higher(curFloor)
                } else {
                    nextFloor = Dispatcher.requests.lower(curFloor)
                    if (nextFloor != null) direction = Direction.DOWN
                }
                if (nextFloor == null) continue

                Dispatcher.requests.remove(nextFloor)
                visitFloor(nextFloor)
            }
        }
    }
}

enum class Direction {
    UP, DOWN
}