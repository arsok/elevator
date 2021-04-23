package com.qbutton.elevator.elevator.impl

import com.qbutton.elevator.elevator.Dispatcher
import com.qbutton.elevator.elevator.api.Elevator
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit.MILLISECONDS
import kotlin.concurrent.fixedRateTimer

class ElevatorImpl : Elevator {
    private val log = LoggerFactory.getLogger(javaClass)

    private var direction = Direction.UP
    private var doorsOpen = true

    val visitedFloors = mutableListOf<Int>()

    init {
        loop()
    }

    override fun enter() {
        while (!doorsOpen) {
            log.info("waiting for doors to open to enter")
            MILLISECONDS.sleep(30)
        }
        log.info("Entered elevator")
    }

    override fun exit() {
        while (!doorsOpen) {
            log.info("waiting for doors to open to exit")
            MILLISECONDS.sleep(30)
        }
        log.info("Exited elevator")
    }

    override fun requestFloor(floorNumber: Int) {
        val curFloor = Dispatcher.floor.get()
        log.info("requested floor $floorNumber from $curFloor, adding it to queue and waiting")

        if (floorNumber > curFloor) Dispatcher.requestsUp.add(floorNumber)
        else Dispatcher.requestsDown.add(floorNumber)

        while (Dispatcher.floor.get() != floorNumber) {
            MILLISECONDS.sleep(100)
        }
    }

    private fun visitFloor(floorNumber: Int) {
        Dispatcher.floor.set(floorNumber)
        visitedFloors.add(floorNumber)
        log.info("visiting floor $floorNumber for a while")
        openDoors()
        MILLISECONDS.sleep(300)
        closeDoors()
    }

    private fun openDoors() {
        log.info("opening doors")
        doorsOpen = true
    }

    private fun closeDoors() {
        log.info("closing doors")
        doorsOpen = false
    }

    private fun loop() {
        fixedRateTimer(period = 500L, initialDelay = 500L) {
            val nextFloor: Int?

            val curFloor = Dispatcher.floor.get()

            nextFloor = if (direction == Direction.UP) Dispatcher.requestsUp.higher(curFloor)
            else Dispatcher.requestsDown.lower(curFloor)

            if (nextFloor != null) {
                visitFloor(nextFloor)
                if (direction == Direction.UP) {
                    Dispatcher.requestsUp.remove(nextFloor)
                } else {
                    Dispatcher.requestsDown.remove(nextFloor)
                }
            } else {
                direction = if (direction == Direction.UP) Direction.DOWN else Direction.UP
            }
        }
    }
}

enum class Direction {
    UP, DOWN
}