# Servicio web SOAP · Biblioteca con Axis2

Servicio web **SOAP** hecho en **Java** con **Apache Axis2 1.6.2** y desplegado en **Tomcat**. Sirve para gestionar una biblioteca: usuarios, catálogo de libros y préstamos. Las credenciales no se guardan aquí, sino que se delegan en un servicio de autenticación externo. También hay un **cliente** que prueba el servicio abriendo varias sesiones a la vez.

Es una práctica de la asignatura *Sistemas Orientados a Servicios* (3.º del Grado en Ingeniería Informática, Universidad Politécnica de Madrid). La hicimos en un grupo de 3 personas.

Es la segunda parte de la asignatura. La primera fue la [API REST de biblioteca](https://github.com/RespinosaDev/api-biblioteca-sos) con Spring Boot.

---

## Arquitectura

```
 Cliente (stub ADB) ──SOAP──► ETSIINFLibrary (Axis2 en Tomcat) ──► UPMAuthenticationAuthorization
   1 sesión HTTP           │  scope = transportsession              (alta, baja, login,
   por cliente             │  · usuario de la sesión: por instancia  cambio de contraseña)
                           │  · libros y préstamos: compartidos
```

- **Contrato primero**: las clases de mensajes y el esqueleto se generaron con `wsdl2java` a partir del [WSDL](servicio/resources/ETSIINFLibrary.wsdl); la lógica está en [`ETSIINFLibrarySkeleton.java`](servicio/src/es/upm/etsiinf/sos/ETSIINFLibrarySkeleton.java).
- **Sesiones**: el servicio se despliega con `scope="transportsession"`, así que Axis2 crea una instancia por cada sesión HTTP de cliente. El usuario autenticado vive en esa instancia. El catálogo y los préstamos son estado compartido (estático) que persiste entre sesiones, y su acceso está sincronizado porque los clientes son concurrentes.
- **Autenticación delegada**: las altas, bajas, logins y cambios de contraseña de los usuarios se resuelven en el servicio `UPMAuthenticationAuthorization`. El usuario **`admin`** (contraseña inicial `admin`) se gestiona en el propio servicio.

> El servicio de autenticación era un servicio remoto de la asignatura que ya no está disponible. El repositorio incluye una simulación local con la misma interfaz, con contraseñas autogeneradas para los usuarios nuevos.

## Operaciones

| Operación | Quién | Descripción |
|-----------|-------|-------------|
| `login` / `logout` | Todos | Abre o cierra la sesión del cliente. Un usuario puede tener varias sesiones a la vez y `logout` solo cierra la suya. En una sesión ya abierta, `login` solo acepta al mismo usuario. |
| `addUser` | Admin | Alta de usuario. Devuelve la contraseña generada. |
| `deleteUser` | Admin o el propio usuario | Baja. Sus sesiones abiertas dejan de ser válidas y sus préstamos vuelven a la biblioteca. El admin no se puede borrar. |
| `changePassword` | Usuario con sesión | Cambio de contraseña (la del admin, en el propio servicio). |
| `addBook` / `removeBook` | Admin | Añade o retira un ejemplar. Varios ejemplares pueden compartir ISSN. |
| `getBook`, `listBooks`, `getBooksFromAuthor` | Usuario con sesión | Consultas del catálogo disponible. Primero aparecen los libros añadidos más recientemente. |
| `borrowBook` / `returnBook` | Usuario con sesión | Préstamo y devolución, con un ejemplar como máximo de cada ISSN. |
| `listBorrowedBooks` | Usuario con sesión | Libros que tiene prestados el usuario. |

## Estructura

```
servicio/
├── build.xml                 # ant → build/lib/ETSIINFLibrary.aar
├── resources/
│   ├── services.xml          # Descriptor de despliegue (operaciones y scope de sesión)
│   └── ETSIINFLibrary.wsdl
└── src/es/upm/
    ├── etsiinf/sos/          # Esqueleto, mensajes y modelo generados + implementación
    └── fi/sos/t3/backend/    # Servicio de autenticación (simulado)
cliente/
├── build.xml                 # ant run → ejecuta las pruebas
└── src/es/upm/etsiinf/sos/
    ├── client/ETSIINFLibraryStub.java   # Stub generado con wsdl2java
    └── client/ClienteBiblioteca.java    # Cliente de prueba (varias sesiones simultáneas)
```

## Puesta en marcha

Requiere **JDK 8 o superior**, **Apache Ant**, **Axis2 1.6.2** (`AXIS2_HOME`) y **Tomcat** con la *webapp* de Axis2.

```bash
cd servicio && ant                                   # genera build/lib/ETSIINFLibrary.aar
cp build/lib/ETSIINFLibrary.aar $CATALINA_HOME/webapps/axis2/WEB-INF/services/
cd ../cliente && ant run                             # ejecuta el cliente de prueba
```

El cliente abre cinco sesiones a la vez (admin, dos de un mismo usuario, otro usuario y una sin login) y hace **59 comprobaciones**: permisos, sesiones concurrentes, préstamos, cambio de contraseña y bajas con sesiones abiertas. Cada ejecución usa nombres únicos, así que se puede lanzar varias veces contra el mismo servidor.

## Qué cambié respecto a la entrega

La versión que entregamos estaba incompleta, así que antes de subirla la terminé y corregí varias cosas:

- `services.xml` no declaraba las operaciones, así que el servicio desplegado no respondía a ninguna llamada.
- Sin `scope` de sesión, Axis2 creaba una instancia nueva por petición y se perdían la sesión y los datos.
- `login` no guardaba el usuario en la sesión, y el `admin` no podía iniciarla.
- El servicio de autenticación simulado no generaba contraseñas.
- Faltaban permisos en `addUser` y `deleteUser`, y `borrowBook` fallaba la primera vez que un usuario pedía un libro.
- El cliente no tenía operaciones. Se generó el *stub* y se escribió el cliente de prueba.

Lo he probado todo con el cliente sobre **Tomcat 9 + Axis2 1.6.2**.
