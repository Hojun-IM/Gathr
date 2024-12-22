package com.nomad.gathr

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class GathrApplication

fun main(args: Array<String>) {
    runApplication<GathrApplication>(*args)
}
