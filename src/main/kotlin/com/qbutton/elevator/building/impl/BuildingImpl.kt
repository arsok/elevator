package com.qbutton.elevator.building.impl

import com.qbutton.elevator.building.api.Building
import com.qbutton.elevator.elevator.Dispatcher
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

class BuildingImpl : Building {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun callUp(curFloor: Int) {
        Dispatcher.requestsUp.add(curFloor)
        log.info("elevator called up, waiting at $curFloor") // wait until elevator arrives
        while (Dispatcher.floor.get() != curFloor) {
            TimeUnit.MILLISECONDS.sleep(100)
        }
        log.info("in building, elevator arrived at $curFloor, need to go up")
    }

    override fun callDown(curFloor: Int) {
        Dispatcher.requestsDown.add(curFloor)
        log.info("elevator called down, waiting at $curFloor") // wait until elevator arrives
        while (Dispatcher.floor.get() != curFloor) {
            TimeUnit.MILLISECONDS.sleep(100)
        }
        log.info("in building, elevator arrived at $curFloor, need to go down")
    }
}