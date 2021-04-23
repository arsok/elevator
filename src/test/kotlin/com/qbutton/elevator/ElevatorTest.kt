package com.qbutton.elevator

import com.qbutton.elevator.building.api.Building
import com.qbutton.elevator.building.impl.BuildingImpl
import com.qbutton.elevator.elevator.Dispatcher
import com.qbutton.elevator.elevator.impl.ElevatorImpl
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit.SECONDS
import kotlin.concurrent.thread
import kotlin.test.assertEquals

class ElevatorTest {
    private val elevator: ElevatorImpl = ElevatorImpl()
    private val building: Building = BuildingImpl()

    @Test
    fun firstUpThenDown() {
        launchPassengerThread(0, 10)
        launchPassengerThread(7, 15)
        launchPassengerThread(6, 2)
        launchPassengerThread(8, 12)
        launchPassengerThread(7, 5)

        pause()

        launchPassengerThread(4, 22)

        waitForAllRequestsToComplete()

        assertEquals(listOf(0, 7, 8, 10, 12, 15, 7, 6, 5, 2, 4, 22), elevator.visitedFloors)
    }

    @Test
    fun sameDirection() {
        launchPassengerThread(2, 12)
        launchPassengerThread(8, 506)

        waitForAllRequestsToComplete()

        assertEquals(listOf(2, 8, 12, 506), elevator.visitedFloors)
    }

    @Test
    fun theParkingOrThereAndBackAgain() {
        launchPassengerThread(-2, -7)
        launchPassengerThread(-4, 15)

        pause()

        launchPassengerThread(3, 10)

        waitForAllRequestsToComplete()

        assertEquals(listOf(-2, -7, -4, 3, 10, 15), elevator.visitedFloors)
    }

    @Test
    fun sameFloorDifferentDirections() {
        launchPassengerThread(3, 5)
        launchPassengerThread(3, 1)

        pause()

        launchPassengerThread(3, 222)
        launchPassengerThread(3, -100)

        waitForAllRequestsToComplete()

        assertEquals(listOf(3, 5, 3, 1, 3, 222, 3, -100), elevator.visitedFloors)
    }

    private fun launchPassengerThread(from: Int, to: Int) {
        val callInCorrectDirection = getCallInCorrectDirection(from, to)

        thread {
            callInCorrectDirection.run()
            elevator.enter()
            elevator.requestFloor(to)
            elevator.exit()
        }
    }

    private fun getCallInCorrectDirection(from: Int, to: Int): Runnable {
        return if (from > to) {
            Runnable { building.callDown(from) }
        } else {
            Runnable { building.callUp(from) }
        }
    }

    private fun waitForAllRequestsToComplete() {
        do {
            SECONDS.sleep(1)
        } while (Dispatcher.requestsUp.isNotEmpty() || Dispatcher.requestsDown.isNotEmpty())
    }

    private fun pause() = SECONDS.sleep(3)
}