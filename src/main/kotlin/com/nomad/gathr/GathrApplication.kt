package com.nomad.gathr

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GathrApplication

fun main(args: Array<String>) {
    runApplication<GathrApplication>(*args)
}
