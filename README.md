# Sistemas Distribuidos Raft

Proyecto correspondiente al final de la asignatura "Telecomunicaciones y Sistemas Distribuidos" (1968) de la Universidad Nacional de Río Cuarto.

## Consigna

Se plantea el desarrollo de un sistema de recursos distribuidos tolerante a fallas (hasta N/2 − 1 nodos), con N procesos.
El sistema debe mantener un conjunto de pares (id, v) (diccionario) replicado y responder a mensaje de clientes del tipo get(id) y set(id,v).
Los nodos del sistema deben responder a requerimientos de manera consistente, sin ninguna coor- dinaci ́on centralizada.
Un cliente se puede comunicar con cualquier nodo de la red para realizar un requerimiento (set/get).

### Requisitos

Para la evaluación de proyecto se tendrán en cuenta las siguientes propiedades de la propuesta:

* Determinar qué tipo de algoritmo distribuido se ha aplicado para solucionar el problema planteado.
* Correctitud.
* Tolerancia a fallas.
* Detalles de diseño e implementación (claridad, legibilidad de código, arquitectura, herramientas utilizadas, etc.).
* Documentación, la cual debe contener una introducción al problema, sus posibles soluciones y la descripción del algoritmo seleccionado detallando sus fortalezas y limitaciones. Además debería contener consideraciones sobre sus características (progreso, tolerancia a fallas, etc.).

Cualquier precondición o configuración asumida debería ser adecuadamente descrita.

## Implementación
Para la implementación del sistema requerido en la consigna se utilizó el protocolo de consenso Raft; el cual es una versión simplificada de Paxos, pero que mantiene su tolerancia a fallas y rendimiento. Entre las diferencias principales de ambos protocolos se encuentra la ausencia de roles y reglas de interacción de Paxos en Raft; en donde todos los nodos son servidores equivalentes (con la excepción del rol de líder de uno de ellos). 

### Sobre Raft
Como se mencionó anteriormente Raft es un algoritmo de consenso diseñado para ser fácil de entender. El consenso implica que varios servidores se pongan de acuerdo sobre un conjunto valores. Una vez que llegan a una decisión sobre un valor, esa decisión es definitiva. El protocolo es tolerante a N/2 - 1 fallas (con más de la mitad de los servidores activos sigue funcionando); mientras que si fallan más servidores, los restantes dejan de progresar (seguirán funcionando para los valores previamente consensuados, pero no se incorporarán nuevos valores).

Cada servidor tiene una máquina de estado y un registro. A los clientes les parecerá que están interactuando con una única máquina de estado fiable, incluso si una minoría de los servidores del clúster falla. Cada máquina de estado toma como entrada los comandos de su registro. 

[Mas información sobre Raft](https://raft.github.io/raft.pdf)

### Sobre mi Implementación
Para un detalle más completo sobre la implementación dirigirse a [Implementación.md](doc/Implementacion.md).


### Instalación
Para obtener un detalle de instalación y despliege dirigirse a [Instalación & Despliegue](doc/Instalacion-Despliegue.md)

## Autor
* **Elisa Boselli** 

## Licencia
This project is licensed under the GNU GENERAL PUBLIC LICENSE - see the [LICENSE](LICENSE) file for details
