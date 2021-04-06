package com.qbutton.elevator.building.impl

import com.qbutton.elevator.ThreadLogger
import com.qbutton.elevator.building.api.Building
import com.qbutton.elevator.elevator.Dispatcher
import java.util.concurrent.TimeUnit

class BuildingImpl : Building {
    private val log = ThreadLogger(this.javaClass.name)

    override fun callUp(curFloor: Int) {
        log.info("elevator called up, waiting at $curFloor") // wait until elevator arrives
        while (Dispatcher.floor.get() != curFloor || Dispatcher.passengersCount.get() >= Dispatcher.capacity) {
            TimeUnit.MILLISECONDS.sleep(300)
            log.info("Waiting for good elevator at $curFloor")
            Dispatcher.requests.add(curFloor)
        }
        Dispatcher.requests.add(curFloor)
        log.info("in building, elevator arrived at $curFloor, need to go up")
    }

    override fun callDown(curFloor: Int) {
        log.info("elevator called down, waiting at $curFloor") // wait until elevator arrives
        while (Dispatcher.floor.get() != curFloor || Dispatcher.passengersCount.get() >= Dispatcher.capacity) {
            TimeUnit.MILLISECONDS.sleep(300)
            log.info("Waiting for good elevator at $curFloor")
            Dispatcher.requests.add(curFloor)
        }
        Dispatcher.requests.add(curFloor)
        log.info("in building, elevator arrived at $curFloor, need to go down")
    }
}