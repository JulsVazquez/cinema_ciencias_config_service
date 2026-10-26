package mx.unam.fciencias.cinema_ciencias.config_service.cinema_ciencias_config_service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.config.server.EnableConfigServer

@SpringBootApplication
@EnableConfigServer
class CinemaCienciasConfigServiceApplication

fun main(args: Array<String>) {
	runApplication<CinemaCienciasConfigServiceApplication>(*args)
}
