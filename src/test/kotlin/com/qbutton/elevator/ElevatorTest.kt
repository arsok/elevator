package com.qbutton.elevator

import com.qbutton.elevator.building.api.Building
import com.qbutton.elevator.building.impl.BuildingImpl
import com.qbutton.elevator.elevator.Dispatcher
import com.qbutton.elevator.elevator.impl.ElevatorImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.TimeUnit.SECONDS
import kotlin.test.assertEquals

class ElevatorTest {
    private lateinit var passengersLatch: CountDownLatch
    private lateinit var elevator: ElevatorImpl
    private val building: Building = BuildingImpl()

    private val defaultScope = CoroutineScope(Dispatchers.Default)

    @BeforeEach
    fun init() {
        elevator = ElevatorImpl()
    }

    @AfterEach
    fun shutdown() {
        elevator.shutdown()
        Dispatcher.resetState()
    }

    @Test
    fun firstUpThenDown() {
        initPassengersLatch(6)

        launchPassengerThread(0, 10)
        launchPassengerThread(7, 15)
        launchPassengerThread(8, 12)
        launchPassengerThread(7, 5)
        launchPassengerThread(6, 2)

        SECONDS.sleep(3)
        launchPassengerThread(4, 22)

        passengersLatch.await()
        assertEquals(listOf(7, 8, 10, 12, 15, 7, 6, 5, 2, 4, 22), elevator.visitedFloors)
    }

    @Test
    fun sameDirection() {
        initPassengersLatch(2)

        launchPassengerThread(2, 12)
        launchPassengerThread(8, 506)

        passengersLatch.await()
        assertEquals(listOf(2, 8, 12, 506), elevator.visitedFloors)
    }

    @Test
    fun theParkingOrThereAndBackAgain() {
        initPassengersLatch(3)

        launchPassengerThread(-2, -7)

        MILLISECONDS.sleep(500)
        launchPassengerThread(-4, 15)

        MILLISECONDS.sleep(500)
        launchPassengerThread(3, 10)

        passengersLatch.await()
        assertEquals(listOf(-2, -7, -4, 3, 10, 15), elevator.visitedFloors)
    }

    @Test
    fun sameFloorDifferentDirections() {
        initPassengersLatch(4)

        launchPassengerThread(3, 5)
        launchPassengerThread(3, 1)

        SECONDS.sleep(3)
        launchPassengerThread(3, -100)
        launchPassengerThread(3, 222)

        passengersLatch.await()
        assertEquals(listOf(3, 5, 3, 1, 3, -100, 3, 222), elevator.visitedFloors)
    }

    @Test
    fun sameFloorContinueOpposite() {
        initPassengersLatch(2)

        launchPassengerThread(-1, -5)

        MILLISECONDS.sleep(500)
        launchPassengerThread(-5, 2)

        passengersLatch.await()
        assertEquals(listOf(-1, -5, -5, 2), elevator.visitedFloors)
    }

    @Test
    fun startFloorMultipleTimes() {
        initPassengersLatch(3)

        launchPassengerThread(0, 15)
        launchPassengerThread(18, 0)
        launchPassengerThread(16, 0)

        passengersLatch.await()
        assertEquals(listOf(15, 16, 0, 18, 0), elevator.visitedFloors)
    }

    private fun launchPassengerThread(from: Int, to: Int) {
        val callInCorrectDirection = getCallInCorrectDirection(from, to)
        MILLISECONDS.sleep(100)

        defaultScope.launch {
            callInCorrectDirection.run()
            elevator.enter()
            elevator.requestFloor(to)
            elevator.exit()
            passengersLatch.countDown()
        }
    }

    private fun getCallInCorrectDirection(from: Int, to: Int): Runnable {
        return if (from > to) {
            Runnable { building.callDown(from) }
        } else {
            Runnable { building.callUp(from) }
        }
    }

    private fun initPassengersLatch(passengersCount: Int) {
        passengersLatch = CountDownLatch(passengersCount)
    }
}