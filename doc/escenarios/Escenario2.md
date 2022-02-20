## Escenario 2
Se tienen 4 servidores los cuales identificamos por sus puertos (`6787`, `6788`, `6789` y `6790`). La propuesta es levantar todos los servidores y dejar que se ejecute el protocolo de elección de líder. Posteriormente bajar dos servidores, el líder y un seguidor, lo que generará una nueva elección que no será exitosa por no lograr el quorum de votos. Finalmente volver a levantar uno de los dos servidores, para que la siguiente elección sea exitosa.

### Configuración
- Quorum = 3
- All servers:
    - `6787` >> timeout = 15 seconds
    - `6788` >> timeout = 20 seconds
    - `6789` >> timeout = 90 seconds
    - `6790` >> timeout = 90 seconds

### Ejecuciones Realizadas
1) `java -jar server.jar 6787`
2) `java -jar server.jar 6788`
3) `java -jar server.jar 6789`
4) `java -jar server.jar 6790`
5) `Ctrl+c` server `6787`
6) `Ctrl+c` server `6790`
7) `java -jar server.jar 6790`

### Comportamiento Esperado
1) Se levantan los 4 servidores como followers.
2) `6787` da timeout, se convierte en candidate y aumenta su término.
3) `6787` envía mensajes de postulación a `6788`, `6789` y `6790`.
4) Como los términos de `6788`, `6789` y `6790` son menores al de `6787`, todos responden ok a la postulación.
5) `6787` se convierte en leader tras conseguir el quorum de votos.
6) Se cae el servidor `6787`.
7) Se cae el servidor `6790`.
8) `6788` da timeout, se convierte en candidate y aumenta su término.
9) `6788` envía mensajes de postulación a `6787`, `6789` y `6790`.
10) Como el término de `6789` es menor al de `6788`, responde ok a la postulación.
11) Como `6787` y `6790` no responden al mensaje de candidate, `6788` no consigue el quorum.
12) `6788` no consigue la cantidad de votos necesarios y vuelve a ser follower.
13) `6788` da timeout, se convierte en candidate y aumenta su término.
14) Se levanta el servidor `6787`.
15) `6788` envía mensajes de postulación a `6787`, `6789` y `6790`.
16) Como los términos de `6787` y `6789` son menores al de `6788`, ambos responden ok a la postulación.
17) Por más que `6790` esté caído, `6788` consigue el quorum y se convierte en leader.

### Storages y Logs
[Caso 2](../../textFiles/ejemplos/Caso%202) (se omiten los files de storage, ya que para este caso de uso todos se encuentran vacíos).