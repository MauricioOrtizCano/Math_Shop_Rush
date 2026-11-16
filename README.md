# Math Shop Rush

Juego educativo en el que el jugador asume el papel del tendero de una tienda de barrio colombiana. Debe cobrar a los clientes y entregar el vuelto correcto con billetes y monedas colombianos, mientras el reloj avanza, la fila crece y los clientes pierden la paciencia.

## Objetivo educativo

Practicar la suma y la resta en una situación cotidiana: calcular el total de una compra, determinar el vuelto y entregarlo con la menor cantidad posible de billetes y monedas. Está dirigido a niños de primaria.

## Contexto académico

Proyecto grupal del módulo **Paradigmas de Programación**, programa de Ingeniería de Software, Politécnico Grancolombiano. El objetivo del proyecto es desarrollar un juego educativo original en Java con interfaz gráfica en JavaFX, aplicando el paradigma de programación orientada a objetos.

## Integrantes

| Integrante | Rol |
|---|---|
| Cristian Mauricio Ortiz Cano | Líder del proyecto, desarrollo del modelo e integración |
| Por definir | Por definir |
| Por definir | Por definir |
| Por definir | Por definir |

## Estado del proyecto

🟡 **Fase 0: preparación del repositorio.**

- [x] Propuesta del juego (Entrega 1)
- [ ] Proyecto base configurado
- [ ] Modelo y lógica de negocio con pruebas unitarias (Entrega 2)
- [ ] Interfaz gráfica e integración (Entrega 3)
- [ ] Sustentación

## Tecnologías

| Tecnología | Uso |
|---|---|
| Java 21 (LTS) | Lenguaje de programación |
| JavaFX 21 | Interfaz gráfica de usuario |
| JUnit 5 | Pruebas unitarias |
| Maven | Construcción del proyecto y gestión de dependencias |
| Javadoc | Documentación del código |

## Requisitos e instalación

*Se completará cuando el proyecto base esté configurado.*

## Cómo ejecutar el juego y las pruebas

*Se completará cuando el proyecto base esté configurado.*

## Estructura del proyecto

El proyecto sigue el patrón **Modelo-Vista-Controlador (MVC)**:

- **Modelo**: reglas del juego y lógica de negocio (dinero, caja, clientes, jornada, niveles). No depende de JavaFX, lo que permite probarlo con JUnit de forma aislada.
- **Vista**: pantallas y elementos gráficos en JavaFX.
- **Controlador**: conecta las acciones del jugador en la vista con el modelo.

*El detalle de paquetes se completará cuando el proyecto base esté configurado.*

## Reglas del juego

Las reglas completas están numeradas (R1, R2, …) en el documento de propuesta de la Entrega 1. Cada prueba unitaria debe indicar en su documentación la regla que verifica.

En resumen:

1. El cliente llega con uno o varios productos y el jugador debe sumar el total.
2. El cliente paga y el jugador entrega el vuelto arrastrando billetes y monedas desde la caja.
3. Cualquier vuelto exacto es válido; usar la menor cantidad de piezas otorga una bonificación.
4. Los errores y los clientes impacientes reducen la reputación de la tienda.
5. La jornada va de 8:00 a.m. a 6:00 p.m.; para pasar de nivel se debe alcanzar la meta de ganancias con reputación mayor a cero.
6. El efectivo de cada jornada pasa a una caja fuerte que sirve como base para los días siguientes.

## Flujo de trabajo

### Ramas

| Rama | Propósito |
|---|---|
| `main` | Versión estable. Solo se actualiza mediante pull requests desde `develop`. |
| `develop` | Integración del trabajo en curso. |
| `feature/<nombre>` | Una rama por funcionalidad, creada desde `develop`. Ejemplo: `feature/calculo-vuelto`. |

### Commits

Mensajes en español, cortos y con un prefijo que indique el tipo de cambio:

| Prefijo | Uso | Ejemplo |
|---|---|---|
| `feat:` | Nueva funcionalidad | `feat: validación de pagos sin piezas sobrantes` |
| `fix:` | Corrección de errores | `fix: cálculo de reputación al agotarse la paciencia` |
| `test:` | Pruebas | `test: casos de vuelto con caja limitada` |
| `docs:` | Documentación | `docs: actualizar README` |
| `refactor:` | Cambios internos sin alterar el comportamiento | `refactor: separar cálculo de vuelto óptimo` |

### Cómo contribuir

1. Toma una tarea del tablero del proyecto en GitHub Projects y asígnatela.
2. Crea una rama `feature/...` desde `develop`.
3. Haz commits pequeños siguiendo la convención.
4. Abre un pull request hacia `develop` y pide revisión a otro integrante.

Quienes no programan también pueden aportar desde la web de GitHub: casos de prueba, diagramas, arte y documentación.

## Entregas

| Entrega | Semana | Contenido | Fecha |
|---|---|---|---|
| Entrega 1 | 3 | Propuesta del juego en PDF | Por definir |
| Entrega 2 | 5 | Código del modelo, pruebas unitarias en JUnit y Javadoc | Por definir |
| Entrega 3 y sustentación | 7 y 8 | Código completo, diagramas de clases y presentación | Por definir |