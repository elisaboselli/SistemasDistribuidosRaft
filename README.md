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
* Detalles de diseño e implementación (claridad, legibilidad de codigo, arquitectura, herramientas utilizadas, etc.).
* Documentación, la cual debe contener una introducción al problema, sus posibles soluciones y la descripción del algoritmo seleccionado detallando sus fortalezas y limitaciones. Ademés debería contener consideraciones sobre sus características (progreso, tolerancia a fallas, etc).

Cualquier precondición o configuración asumida debería ser adecuadamente descripta.

## Implementación
Para la implementación del sistema requerido en la consigna se utilizó el protocolo de consenso Raft; el cual es un versión simplificada de Paxos pero que mantiene su tolerancia a fallas y rendimiento. Entre las diferencias principales de ambos protocolos se encuentra la ausencia de roles y reglas de interacción de Paxos en Raft; en donde todos los nodos son servidores equivalentes (con la excepción del rol de lider de uno de ellos). 

### Sobre Raft
Como se mecionó anteriormente Raft es un algoritmo de consenso diseñado para ser fácil de entender. El consenso implica que varios servidores se pongan de acuerdo sobre un conjunto valores. Una vez que llegan a una decisión sobre un valor, esa decisión es definitiva. El protocolo es tolerante a N/2 - 1 fallas (con mas de la mitad de los servidores activos sigue funcionando); mientras que si  fallan más servidores, los restantes dejan de progresar (seguiran funcionando para los valores previamente consensuados pero no se incorporarán nuevos valores).

Cada servidor tiene una máquina de estado y un registro. A los clientes les parecerá que están interactuando con una única máquina de estado fiable, incluso si una minoría de los servidores del clúster falla. Cada máquina de estado toma como entrada los comandos de su registro. 

[Mas información sobre Raft](https://raft.github.io/raft.pdf)




### Instalación
* Obtener el código fuente del proyecto desde el repositorio.

####Instalación desde IntelliJ IDEA
* Abrir IntelliJ IDEA, seleccionar la opción "open" y localizar el directorio project dentro del directorio raíz del proyecto descargado (SistemasDistribuidos Raft).
* En caso que no tome al directorio project como modulo, añadirlo manualmente desde project structure con lenguaje level 8.
* En caso de que no se haga automáticamente, sincronizar las dependencias desde la pestaña maven.
* Buildear proyecto para validar que las dependecias se hayan sincronizado correctamente.

####Instalación desde Terminal
* Ubicado en el directorio project dentro del repositorio decargado, ejecutar mvn compile.

## Despliegue
### Antes de Comenzar
Modificar el archivo "allServers.txt" en la raiz del repositorio con la lista de servidores a utilizar y sus correspondientes direcciones y puertos.
La lista de nodos del sistema no es dinamica, por lo que se utiliza este archivo para determinar todos los mismos.

### Desde IDE
* Desde la opción Run/Debug Configurations se deberían haber importar automáticamente las configuraciones de un cliente y tres servidores.
* Basta con ejecutar los mismos para levantar el sistema.
* Dentro de cada configuración se pueden modificar los parámetros que seran el puerto local en caso de los servidores y el puerto y dirección al cual conectarse en el caso del cliente.

### Desde Terminal usando los Artifacts del IDE
* Desde el IDE, ir a Build > Build Artifacts y seleccionar All Artifacts > Build.
* Esto generará dos directorios en /out/artifacts, uno para el ejecutable del Client y otro para el ejecutable del Server.
* Para levantar el sistema, ejecutar java -jar client.jar o java -jar server.jar segun corresponda, teniendo en cuenta que el server lleva como arg su puerto de conexión y el client lleva el puerto y dirección a las que se desea conectar (en caso de no pasar dirección se usa localhost por defecto).
* Ejemplos:
  * java -jar server.jar 6787 (levanta un servidor en el puerto 6787)
  * java -jar client.jar 6787 (se conecta al servidor anterior de forma local)
  * java -jar client.jar 6787 192.168.0.11 (se conecta al servidor anterior de forma remota)

## Autor
* **Elisa Boselli** 

## Licencia
This project is licensed under the GNU GENERAL PUBLIC LICENSE - see the [LICENSE](LICENSE) file for details
