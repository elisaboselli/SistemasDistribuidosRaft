# Escenarios de Pruebas de la Implementación
Se anexan al proyecto algunos escenarios de pruebas ejecutados previamente; con una descripción de la situación planteada, los pasos realizados, el comportamiento esperado y sus correspondientes archivos de almacenamiento y logs.

### Escenario 1
Se tienen 3 servidores los cuales identificamos por sus puertos (`6787`, `6788` y `6789`) y un cliente. La propuesta es levantar todos los servidores, dejar que se ejecute el protocolo de elección de líder y usar el cliente para setear un valor. Posteriormente, bajar el servidor líder para generar una nueva elección y volver a levantarlo para que sincronice su almacenamiento con el nuevo líder. Finalmente usar el cliente para setear un nuevo valor, y luego consultar por uno de los valores persistidos.

Para ver el detalle de este escenario dirigirse a [Escenario1](escenarios/Escenario1.md).

### Escenario 2
Se tienen 4 servidores los cuales identificamos por sus puertos (`6787`, `6788`, `6789` y `6790`). La propuesta es levantar todos los servidores y dejar que se ejecute el protocolo de elección de líder. Posteriormente bajar dos servidores, el líder y un seguidor, lo que generará una nueva elección que no será exitosa por no lograr el quorum de votos. Finalmente volver a levantar uno de los dos servidores, para que la siguiente elección sea exitosa.

Para ver el detalle de este escenario dirigirse a [Escenario2](escenarios/Escenario2.md).

### Escenario 3
Se tienen 3 servidores los cuales identificamos por sus puertos (`6787`, `6788` y `6789`) y un cliente. La propuesta es levantar todos los servidores y dejar que se ejecute el protocolo de elección de líder. Posteriormente bajar uno de los servidores seguidores y usar el cliente para setear un valor, lo que generará un consenso que no se cumplirá por no alcanzar el quorum de `appends`. Finalmente usar el cliente para consultar por el valor persistido previamente, obteniendo una respuesta fallida.

Para ver el detalle de este escenario dirigirse a [Escenario3](escenarios/Escenario3.md).


### Escenario 4
Se tienen 3 servidores los cuales identificamos por sus puertos (`6787`, `6788` y `6789`) y un cliente. La propuesta es levantar todos los servidores y dejar que se ejecute el protocolo de elección de líder. Posteriormente usar el cliente para setear un valor, lo que generará un consenso (`appends` y `commits`). Finalmente usar el cliente para consultar por el valor persistido previamente, obteniendo una respuesta exitosa.

Para ver el detalle de este escenario dirigirse a [Escenario4](escenarios/Escenario4.md).
