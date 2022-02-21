# Instalación y Despliegue
En esta sección se detallará el paso a paso para utilizar la aplicación de forma local.

## Configuración Inicial
- Obtener el código fuente de la aplicación del [repositorio actual](https://github.com/elisaboselli/SistemasDistribuidosRaft).
- A partir de la raíz, localizar el archivo `/textFiles/allServers.txt` y declarar tantos servidores como se desee (dicho listado será utilizado por el servidor líder para enviar mensajes al resto). 
- Se pueden declarar tanto servidores locales (usando `localhost` como address) asi como remotos (usando la correspondiente dirección IP). En caso de tener servidores remotos, todos deben tener el mismo contenido en `allServers.txt`.
- A partir de la raíz, localizar el archivo `project/src/main/java/utils/Constants.java` y actualizar el valor `SERVERS_QTY` según la cantidad de servidores declarados previamente (dicho valor será utilizado para calcular el quorum).
- El cliente está pre-configurado para ejecutarse en el puerto local `6786`, en caso de desear ejecutarlo en un puerto diferente, en el archivo anterior (`Constants.java`) actualizar el valor `CLIENT_PORT` usando un valor diferente de los declarados en `allServers.txt`.

## Instalación
Completados los pasos de configuración inicial, se compilará el código y generará el ejecutable:
- En terminal, a partir de la raíz, ejecutar los comandos `cd project` y `mvn package` los cuales generarán un nuevo directorio `/target`.
- Ejecutar los comandos `cd target` y `ln -s ../../textFiles ./textFiles` para generar un link simbólico al directorio textFiles original.

## Despliegue
Finalmente para inicializar el sistema, se deberá ejecutar los siguientes comandos desde el directorio `/target` mencionado previamente.

### Cliente
Para inicializar un cliente se utilizará el comando:
>java -cp project-1.0.jar udpServers.Client `port` `address`

En donde los parámetros `port` y `address` hacen referencia al servidor al cual se conectará el cliente. El parámetro `port` es obligatorio, mientras que `address` es opcional (si es nulo se utiliza `localhost` por defecto).

Por ejemplo:
> java -cp project-1.0.jar udpServers.Client 6787

Levanta un cliente conectado al servidor local en el puerto 6787.


### Servidor
Para un servidor se utilizará el commando:
>java -cp project-1.0.jar udpServers.Server `port`

En donde el parámetro `port` (obligatorio) hace referencia al puerto local del servidor.

Por ejemplo:
>java -cp project-1.0.jar udpServers.Server 6787

Levanta un servidor en el puerto local 6787. 