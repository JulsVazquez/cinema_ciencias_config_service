# Cinema Ciencias Config Service

Servidor central de **configuración distribuida** (Spring Cloud Config Server) para
los microservicios de Cinema Ciencias. Permite centralizar los archivos de
configuración de cada servicio (`.yml`) y servirlos vía HTTP.

## Stack

| Capa | Tecnología |
|---|---|
| Lenguaje | Kotlin 2.2.21 |
| Framework | Spring Boot 4.0.3 + Spring Cloud Config Server |
| Perfil | `native` (configuración desde classpath, sin repositorio Git) |
| Build | Gradle 9.3.1 (wrapper) |
| Base de datos | Ninguna |
| Puerto | **8888** |

> Los clientes (`auth`, `gateway`, `cinema`, `audit`) hacen
> `spring.config.import: optional:configserver:http://localhost:8888`. De acá obtienen el secreto
> del JWT, el secreto Gateway↔monolito y la URL de Eureka (`shared-config/application.yml`).
> Por eso este servicio tiene que arrancar **primero**.

## Prerequisitos

- **JDK 21** instalado y disponible en `JAVA_HOME`

## Puesta en marcha

### 1. Ejecutar

```bash
./gradlew bootRun
```

El servidor queda escuchando en **http://localhost:8888**.

## Verificación

```bash
curl http://localhost:8888/actuator/health
```

Respuesta esperada: `{"status":"UP"}`.

### Probar la entrega de configuración (opcional)

El servidor busca los archivos de config en `classpath:/shared-config/`:

```text
src/main/resources/shared-config/
├── application.yml      # comunes a TODOS los servicios (secretos JWT, Eureka)
└── gateway-service.yml  # solo para spring.application.name = gateway-service
```

Pedilo al servidor:

```bash
curl http://localhost:8888/gateway-service/default | jq '.propertySources[].name'
```

Debe devolver dos *property sources* (`gateway-service.yml` y `application.yml`). Si la carpeta
`shared-config/` estuviera vacía el servicio arrancaría igual, pero no serviría nada.

## Integración con el ecosistema

En la arquitectura de la tesis, el orden de arranque recomendado es:

1. **Config Service** (este, 8888)
2. **Registry Service** (Eureka, 8761)
3. **Gateway Service** (9000)
4. Resto de microservicios

Los consumidores son `auth`, `gateway`, `cinema` y `audit`. El import es `optional:`, así que
arrancan aunque el config server no esté, pero las propiedades que viven acá
(`cinema.security.*`) no se resolverán y `auth`, `gateway` y `cinema` fallarán con un error claro.

## Tests

```bash
./gradlew test
```

(Solo hay un smoke test de contexto.)

## Notas

- El puerto se **corrigió de 8889 a 8888** para ser consistente con la importación
  del gateway (`http://localhost:8888`) y con la documentación de la tesis.