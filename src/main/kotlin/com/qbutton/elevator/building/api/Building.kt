package com.qbutton.elevator.building.api

interface Building {
    fun callUp(curFloor: Int)

    fun callDown(curFloor: Int)
}