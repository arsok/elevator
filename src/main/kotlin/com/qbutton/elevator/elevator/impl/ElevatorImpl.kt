package com.qbutton.elevator.elevator.impl

import com.qbutton.elevator.elevator.Dispatcher
import com.qbutton.elevator.elevator.Dispatcher.requestsDown
import com.qbutton.elevator.elevator.Dispatcher.requestsUp
import com.qbutton.elevator.elevator.api.Elevator
import com.qbutton.elevator.elevator.model.Direction
import org.slf4j.LoggerFactory
import java.util.Timer
import java.util.concurrent.TimeUnit.MILLISECONDS
import kotlin.concurrent.fixedRateTimer

class ElevatorImpl : Elevator {
    private lateinit var elevatorTimer: Timer
    private val log = LoggerFactory.getLogger(javaClass)
    private var direction = Direction.UP

    @Volatile
    private var doorsOpen = true

    val visitedFloors = mutableListOf<Int>()

    init {
        createElevatorTimer()
    }

    override fun enter() {
        while (!doorsOpen) {
            log.info("Waiting for doors to open for entering")
            MILLISECONDS.sleep(30)
        }
        log.info("Entered elevator")
    }

    override fun exit() {
        while (!doorsOpen) {
            log.info("Waiting for doors to open for exiting")
            MILLISECONDS.sleep(30)
        }
        log.info("Exited elevator")
    }

    override fun requestFloor(floorNumber: Int) {
        log.info("Requested floor $floorNumber from ${Dispatcher.curFloor}")

        if (floorNumber > Dispatcher.curFloor) requestsUp.add(floorNumber)
        else requestsDown.add(floorNumber)

        while (Dispatcher.curFloor != floorNumber) {
            MILLISECONDS.sleep(100)
        }
    }

    fun shutdown() = elevatorTimer.cancel()

    private fun createElevatorTimer() {
        elevatorTimer = fixedRateTimer(period = 500L, initialDelay = 500L, name = "Elevator-0") { loop() }
    }

    private fun loop() {
        val nextFloor = getNextFloor(Dispatcher.curFloor)

        if (nextFloor != null) {
            visitFloor(nextFloor)
            removeFloorFromRequests(nextFloor)
        } else {
            direction = getDirectionWithMostWait()
        }
    }

    private fun getNextFloor(curFloor: Int): Int? {
        return if (direction == Direction.UP) {
            requestsUp.ceiling(curFloor) ?: requestsUp.floor(curFloor)
        } else {
            requestsDown.floor(curFloor) ?: requestsDown.ceiling(curFloor)
        }
    }

    private fun visitFloor(floorNumber: Int) {
        Dispatcher.curFloor = floorNumber
        visitedFloors.add(floorNumber)
        log.info("Visiting floor $floorNumber")
        openDoors()
        MILLISECONDS.sleep(300)
        closeDoors()
    }

    private fun openDoors() {
        log.info("Opening doors")
        doorsOpen = true
    }

    private fun closeDoors() {
        log.info("Closing doors")
        doorsOpen = false
    }

    private fun removeFloorFromRequests(floor: Int) {
        if (direction == Direction.UP) requestsUp.remove(floor)
        else requestsDown.remove(floor)
    }

    private fun getDirectionWithMostWait() = if (requestsUp.size > requestsDown.size) Direction.UP else Direction.DOWN
}