# Sistemas Distribuidos Raft

Proyecto correspondiente al final de la asignatura "Telecomunicaciones y Sistemas Distribuidos" (1968) de la Universidad Nacional de Río Cuarto.

## Getting Started

Se plantea el desarrollo de un sistema de recursos distribuidos tolerante a fallas (hasta N/2 − 1 nodos), con N procesos.
El sistema debe mantener un conjunto de pares (id, v) (diccionario) replicado y responder a mensaje de clientes del tipo get(id) y set(id,v).
Los nodos del sistema deben responder a requerimientos de manera consistente, sin ninguna coor- dinaci ́on centralizada.
Un cliente se puede comunicar con cualquier nodo de la red para realizar un requerimiento (set/get).

### Requisites

Para la evaluación de proyecto se tendrán en cuenta las siguientes propiedades de la propuesta:

* Determinar qué tipo de algoritmo distribuido se ha aplicado para solucionar el problema planteado.
* Correctitud.
* Tolerancia a fallas.
* Detalles de diseño e implementación (claridad, legibilidad de codigo, arquitectura, herramientas utilizadas, etc.).
* Documentación, la cual debe contener una introducción al problema, sus posibles soluciones y la descripción del algoritmo seleccionado detallando sus fortalezas y limitaciones. Ademés debería contener consideraciones sobre sus características (progreso, tolerancia a fallas, etc).

Cualquier precondición o configuración asumida debería ser adecuadamente descripta.

### Installing

Al momento de importar este proyecto en eclipse, se debera tener en cuenta agregar los siguientes jar's:
* gson-2.8.2.jar
* json-simple-2.3.1.jar

## Running the tests

Completar 

## Deployment

Completar

## Built With

* Completar

## Authors

* **Elisa Boselli** 
* **Leandro Etcharren** 

## License

This project is licensed under the GNU GENERAL PUBLIC LICENSE - see the [LICENSE](LICENSE) file for details
