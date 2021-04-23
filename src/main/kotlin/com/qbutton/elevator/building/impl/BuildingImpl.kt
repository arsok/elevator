package com.qbutton.elevator.building.impl

import com.qbutton.elevator.building.api.Building
import com.qbutton.elevator.elevator.Dispatcher
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit.MILLISECONDS

class BuildingImpl : Building {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun callUp(curFloor: Int) {
        if (curFloor != Dispatcher.curFloor) {
            Dispatcher.requestsUp.add(curFloor)
            log.info("Elevator called up from $curFloor, will wait")

            waitForElevatorArrival(curFloor)
        }

        log.info("Elevator arrived at $curFloor, will go up")
    }

    override fun callDown(curFloor: Int) {
        if (curFloor != Dispatcher.curFloor) {
            Dispatcher.requestsDown.add(curFloor)
            log.info("Elevator called down from $curFloor, will wait")

            waitForElevatorArrival(curFloor)
        }

        log.info("Elevator arrived at $curFloor, will go down")
    }

    private fun waitForElevatorArrival(curFloor: Int) {
        while (Dispatcher.curFloor != curFloor) {
            MILLISECONDS.sleep(100)
        }
    }
}