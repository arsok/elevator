package com.qbutton.elevator.logger

import java.util.logging.Logger

class ThreadLogger(fullName: String) {

    private val name = fullName.substringAfterLast('.')

    private var logger: Logger = Logger.getLogger(name)

    fun info(msg: String) {
        logger.info("[${name}bs] [${Thread.currentThread().name}]: $msg")
    }
}