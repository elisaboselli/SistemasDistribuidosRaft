#Escenarios de Pruebas de la Implementación

Se anexan al proyecto algunos escenarios de pruebas ejecutados previamente; con una descripción de la situación planteada, los pasos realizados, el comportamiento esperado y sus correspondientes archivos de almacenamiento y logs.

### Escenario 1
Se tienen 3 servidores los cuales identificamos por sus puertos (`6787`, `6788` y `6789`) y un cliente. La propuesta es levantar todos los servidores, dejar que se ejecute el protocolo de elección de líder y usar el cliente para setear un valor. Posteriormente, bajar el servidor líder para generar una nueva elección y volver a levantarlo para que sincronice su almacenamiento con el nuevo líder. Finalmente usar el cliente para setear un nuevo valor, y luego consultar por uno de los valores persistidos.

Para ver el detalle de este escenario dirigirse a [Escenario1.md](escenarios/Escenario1.md).



### Escenario 2

### Escenario 3

### Escenario 4

