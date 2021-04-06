package com.qbutton.elevator.elevator.api

interface Elevator {
    fun enter()

    fun exit()

    fun requestFloor(floorNumber: Int)
}