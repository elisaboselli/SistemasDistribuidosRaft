## Escenario 1
Se tienen 3 servidores los cuales identificamos por sus puertos (`6787`, `6788` y `6789`) y un cliente. La propuesta es levantar todos los servidores, dejar que se ejecute el protocolo de elección de líder y usar el cliente para setear un valor. Posteriormente, bajar el servidor líder para generar una nueva elección y volver a levantarlo para que sincronice su almacenamiento con el nuevo líder. Finalmente usar el cliente para setear un nuevo valor, y luego consultar por uno de los valores persistidos.

### Configuración
- Quorum = 2
- All servers:
  - `6787` >> timeout = 10 seconds
  - `6788` >> timeout = 30 seconds
  - `6789` >> timeout = 40 seconds

### Ejecuciones Realizadas
1) `java -jar server.jar 6787`
2) `java -jar server.jar 6788`
3) `java -jar server.jar 6789`
4) `java -jar client.jar 6787`
5) Client > `set 1 1`
6) `Ctrl+c` en client
7) `Ctrl+c` en server `6787`
8) `java -jar client.jar 6788`
9) Client > `set 2 2`
10) `java -jar server.jar 6787`
11) Client > `get 2`

### Comportamiento Esperado
1) Se levantan los 3 servidores como followers.
2) `6787` da timeout, se convierte en candidate y aumenta su término.
3) `6787` envía mensajes de postulación a `6788` y `6789`.
4) Como los términos de `6788` y `6789` son menores al de `6787`, ambos responden ok a la postulación.
5) `6787` se convierte en leader tras conseguir el quorum de votos.
6) `6787` recibe un request set (1,1).
7) `6787` persiste (1,1,1) pero sin confirmar.
8) `6787` envía mensajes de append a `6788` y `6789`.
9) `6787` responde el request del cliente aceptando el set.
10) `6788` y `6789` persisten (1,1,1) sin confirmar y responden el append de `6787`.
11) `6787` recibe las respuestas a los appends y actualiza el quorum sobre dicho ítem.
12) Como se consigue el quorum del item (1,1,1), `6787` confirma la entrada y envía un mensaje de confirmación para `6788` y `6789`.
13) `6788` y `6789` confirman su entrada y responden a la confirmación de `6787`.
14) Se cae el servidor `6787`.
15) `6788` da timeout, se convierte en candidate y aumenta su término.
16) `6788` envía mensajes de postulación a `6787` y `6789`.
17) Como el término de `6789` es menor al de `6788`, responde ok a la postulación.
18) Por más que `6787` esté caído, `6788` consigue el quorum y se convierte en leader.
19) `6788` recibe un request set (2,2).
20) `6788` persiste (2,2,2) pero sin confirmar.
21) `6788` envía mensajes de append a `6787` y `6789`.
22) `6788` responde el request del cliente aceptando el set.
23) `6789` persiste (2,2,2) sin confirmar y responde el append de `6788`.
24) `6788` recibe las respuestas a los appends y actualiza el quorum sobre dicho ítem.
25) Como se consigue el quorum del item (2,2,2), `6788` confirma la entrada y envía un mensaje de confirmación para `6787` y `6789`.
26) `6789` confirma su entrada y responde a la confirmación de `6788`.
27) Se levanta el servidor `6787`.
28) `6787` recibe un heartbeat de `6788`, y como el término es mayor al suyo, lo acepta como leader.
29) Como el índice de `6787` es menor al de `6788`, le envía un mensaje de storage inconsistente junto con su índice (0).
30) `6788` busca el valor para el índice siguiente en su storage y envía el valor a `6787` (1,1).
31) `6787` persiste el valor y lo confirma (pues ya está confirmado en el storage del leader).
32) Como `6787` sigue inconsistente, vuelve a enviar un mensaje de storage inconsistente junto con su índice (1).
33) `6788` busca el valor para el índice siguiente en su storage y envía el valor a `6787` (2,2).
34) `6787` persiste el valor y lo confirma (pues ya está confirmado en el storage del leader).
35) `6788` recibe un request get (2).
36) `6788` responde el request del cliente con (2,2).

### Storages y Logs
[Caso 1](../../textFiles/ejemplos/Caso%201)