## Escenario 3
Se tienen 3 servidores los cuales identificamos por sus puertos (`6787`, `6788` y `6789`) y un cliente. La propuesta es levantar todos los servidores y dejar que se ejecute el protocolo de elección de líder. Posteriormente bajar uno de los servidores seguidores y usar el cliente para setear un valor, lo que generará un consenso que no se cumplirá por no alcanzar el quorum de `appends`. Finalmente usar el cliente para consultar por el valor persistido previamente, obteniendo una respuesta negativa.

### Configuración
- Quorum = 3
- All servers:
  - `6787` >> timeout = 10 seconds
  - `6788` >> timeout = 30 seconds
  - `6789` >> timeout = 40 seconds

### Ejecuciones Realizadas
1) `java -jar server.jar 6787`
2) `java -jar server.jar 6788`
3) `java -jar server.jar 6789`
4) `Ctrl+c` en server `6789`
5) `java -jar client.jar 6787
6) Client > `set 1 1`
7) Client > `get 1`

### Comportamiento Esperado
1) Se levantan los 3 servidores como followers.
2) `6787` da timeout, se convierte en candidate y aumenta su término.
3) `6787` envía mensajes de postulación a `6788` y `6789`.
4) Como los términos de `6788` y `6789` son menores al de `6787`, ambos responden ok a la postulación.
5) `6787` se convierte en leader tras conseguir el quorum de votos.
6) Se cae el servidor `6789`.
7) `6787` recibe un request set (1,1).
8) `6787` persiste (1,1,1) pero sin confirmar.
9) `6787` envía mensajes de append a `6788` y `6789`.
10) `6787` responde el request del cliente aceptando el set.
11) `6788` persiste (1,1,1) sin confirmar y responde el append de `6787`.
12) `6787` recibe la respuesta de `6788` al append y actualiza el quorum sobre dicho ítem.
13) Como aún no consigue el quorum del item (1,1,1), `6787` no confirma la entrada.
14) `6788` recibe un request get (1).
15) `6788` responde el request que no encontró resultados.

### Storages y Logs
[Caso 3](../../textFiles/ejemplos/Caso%203)