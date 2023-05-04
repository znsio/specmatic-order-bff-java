package controllers

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
open class Application

fun main() {
    SpringApplication.run(Application::class.java)
}