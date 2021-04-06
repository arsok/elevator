package com.qbutton.elevator

import com.qbutton.elevator.building.api.Building
import com.qbutton.elevator.building.impl.BuildingImpl
import com.qbutton.elevator.elevator.impl.ElevatorImpl
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.test.assertEquals

class SimpleTest {
    private val elevator: ElevatorImpl = ElevatorImpl()
    private val building: Building = BuildingImpl()

    private val threadPool = Executors.newFixedThreadPool(1)

    @BeforeEach
    fun launchElevatorManager() {
        threadPool.submit(elevator.ElevatorManager())
    }

    @Test
    fun testManyPassengers() {
        launchPassengerThread(0, 10)
        launchPassengerThread(7, 15)
        launchPassengerThread(6, 2)
        launchPassengerThread(8, 12)

        TimeUnit.SECONDS.sleep(5)

        launchPassengerThread(7, 5)
        launchPassengerThread(4, 22)

        TimeUnit.SECONDS.sleep(30)

        assertEquals(listOf(6, 7, 8, 10, 12, 15, 2, 0, 4, 7, 22, 5), elevator.visitedFloors, "foil")
    }

    @Test
    fun testEnterExitOrder() {
        launchPassengerThread(0, 10)
        launchPassengerThread(10, 12)

        TimeUnit.SECONDS.sleep(10)
    }

    private fun launchPassengerThread(from: Int, to: Int) {
        if (from > to) {
            thread {
                building.callDown(from)
                elevator.enter()
                elevator.requestFloor(to)
                elevator.exit()
            }
        } else {
            thread {
                building.callUp(from)
                elevator.enter()
                elevator.requestFloor(to)
                elevator.exit()
            }
        }
    }
}