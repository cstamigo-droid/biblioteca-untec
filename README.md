# Biblioteca Digital UNTEC

**Aplicación web Java EE para gestionar el catálogo, los préstamos y las devoluciones de una
biblioteca universitaria.** Inicio de sesión con control de acceso, catálogo con altas y bajas,
préstamo y devolución con la disponibilidad actualizada en cada operación.

🔗 **Capturas del flujo completo:** [`CAPTURAS/`](CAPTURAS/) · **Cómo ejecutarlo:** [más abajo](#cómo-ejecutar)

### Qué demuestra este proyecto

| | |
|---|---|
| **Arquitectura** | MVC estricto: las vistas viven en `WEB-INF` y sólo se alcanzan desde su controlador |
| **Acceso a datos** | Patrón DAO — todo el SQL en una capa, con `PreparedStatement`; cambiar de motor no toca los Servlets |
| **Pruebas** | 21 pruebas JUnit 5 sobre reglas de negocio, no sobre getters |
| **Criterio de ingeniería** | Se detectó y corrigió un fallo de autorización real: la vista ocultaba el botón *Prestar*, pero un `POST` directo creaba un segundo préstamo del mismo ejemplar. La comprobación se movió al servidor |
| **Portabilidad** | Compila para Java 17 y corre en JDK 21; la base se crea sola, sin instalar nada |

> Desarrollado como proyecto evaluado del Módulo 5 (Desarrollo de aplicaciones web dinámicas en
> Java) del bootcamp Full Stack Java Trainee. Autor: Cristian Amigo.

---

## Tecnologías utilizadas

| Capa | Tecnología |
|---|---|
| Lenguaje | Java (compilado para Java 17, ejecutado en JDK 21) |
| Web | Servlets + JSP con JSTL 1.2 |
| Patrón | MVC (paquetes `model`, `view`, `controller`) + DAO + Singleton |
| Base de datos | H2 (modo archivo, uso educativo) vía JDBC |
| Servidor | Apache Tomcat 9 |
| Construcción | Apache Maven (proyecto WAR) |
| Pruebas | JUnit 5 + JaCoCo (cobertura) |
| IDE | Eclipse IDE for Enterprise Java / VS Code |

## Estructura del proyecto

```
pom.xml                 ← Maven: dependencias, versión de Java y empaquetado WAR
src/main/java/
├── model/              ← Modelo: entidades + acceso a datos
│   ├── Usuario.java, Libro.java, Prestamo.java      (entidades POJO)
│   ├── ConexionBD.java                              (JDBC + patrón Singleton)
│   └── UsuarioDAO.java, LibroDAO.java, PrestamoDAO.java  (patrón DAO, CRUD)
├── view/
│   └── Paginas.java    ← rutas de las vistas, centralizadas
└── controller/         ← Controladores (Servlets)
    ├── LoginServlet.java     (/login  — valida y crea la sesión)
    ├── LibroServlet.java     (/libros — catálogo: listar/agregar/eliminar/prestar)
    ├── PrestamoServlet.java  (/prestamos — historial y devoluciones)
    └── LogoutServlet.java    (/logout — cierra la sesión)

src/main/webapp/
├── index.jsp           ← Vista de login (formulario POST a /login)
└── WEB-INF/
    ├── web.xml         ← descriptor de despliegue
    ├── views/          ← vistas protegidas (solo accesibles vía controlador)
    │   ├── libros.jsp      (JSTL: c:out, c:if, c:choose, c:forEach)
    │   └── prestamos.jsp
    └── (sin lib/: las dependencias las declara el pom.xml)

src/test/java/model/    ← Pruebas unitarias (JUnit 5)
├── ConexionBDTest.java     (configuración externa y driver JDBC)
├── UsuarioDAOTest.java     (login correcto, incorrecto, inyección SQL)
├── LibroDAOTest.java       (CRUD + regla de eliminación con historial)
└── PrestamoDAOTest.java    (préstamo, devolución y sus reglas)
```

Las librerías **no se copian a mano**: `jstl` y `h2` se declaran en el `pom.xml`
y Maven las resuelve e incorpora al WAR.

## Diagrama MVC

```
 Navegador ──petición──▶ CONTROLADOR (Servlet)
                              │  pide datos
                              ▼
                          MODELO (Service/DAO ── JDBC Singleton ──▶ BD H2)
                              │  entrega objetos
                              ▼
                         VISTA (JSP + JSTL en WEB-INF) ──HTML──▶ Navegador
```

El controlador nunca arma HTML y la vista nunca toca la base de datos.
La capa DAO encapsula TODO el SQL: cada entidad tiene su DAO con operaciones
CRUD usando `PreparedStatement`, y la conexión es única (patrón Singleton).

## Cómo ejecutar

1. **Requisitos:** JDK 21, Maven y Apache Tomcat 9 (el ambiente del Módulo 5).

**Construir desde cero:**

```
mvn clean test package
```

Genera `target/biblioteca.war` tras ejecutar las 21 pruebas.
2. **Opción A — WAR (recomendada):** subir `dist/biblioteca.war` mediante el
   Tomcat Manager (`http://localhost:8080/manager`) o copiarlo a `webapps/`.
3. **Opción B — Eclipse:** importar el proyecto (File > Import > Existing Projects),
   asociar Tomcat 9 como runtime y ejecutar con *Run on Server*.
4. Abrir `http://localhost:8080/biblioteca/`.
5. La base de datos H2 se crea sola en el primer arranque (archivo
   `~/biblioteca_untec.mv.db (carpeta personal del usuario)`) con datos de ejemplo.

### Credenciales de prueba

| Usuario | Correo | Contraseña |
|---|---|---|
| Bibliotecaria UNTEC | `biblioteca@untec.cl` | `untec2026` |
| Administrador UNTEC | `admin@untec.cl` | `admin123` |

## Flujo funcional

1. Login (validación en servidor; con error visible si las credenciales fallan).
2. Catálogo: listado con estado Disponible/Prestado, agregar libro, eliminar
   (solo si no tiene historial de préstamos — integridad referencial), prestar.
3. Préstamos: historial con fecha y estado, registrar devolución (el libro
   vuelve a quedar disponible).
4. Toda página de gestión exige sesión iniciada; Cerrar sesión la invalida.

## Configuración de la base de datos

Por defecto usa **H2 en archivo** (permitido por la consigna) y no requiere instalar
nada. La conexión es configurable **desde fuera del código**, sin recompilar:

| Variable | Valor por defecto |
|---|---|
| `BIBLIOTECA_DB_URL` | `jdbc:h2:~/biblioteca_untec` |
| `BIBLIOTECA_DB_USER` | `sa` |
| `BIBLIOTECA_DB_PASSWORD` | *(vacía)* |

Se leen primero como propiedad de sistema (`-DBIBLIOTECA_DB_URL=...`) y después
como variable de entorno. `ConexionBD` deduce el driver a partir de la URL
(H2, MariaDB, MySQL o PostgreSQL) y lo carga explícitamente, de modo que un
error de configuración diga qué driver falta en vez de un `No suitable driver`.

## Pruebas

```
mvn test            # 21 pruebas
mvn clean verify    # pruebas + cobertura en target/site/jacoco/index.html
```

Las pruebas usan H2 **en memoria**, así que no tocan la base de datos real.
No prueban getters para inflar el porcentaje: prueban las reglas que dejarían
el catálogo en un estado imposible si fallaran.

| Regla verificada | Prueba |
|---|---|
| Las credenciales incorrectas no dejan entrar | `UsuarioDAOTest` |
| Una comilla en el email no altera la consulta | `UsuarioDAOTest` |
| Un libro con historial de préstamos no se elimina | `LibroDAOTest` |
| Un libro ya prestado no se puede prestar de nuevo | `PrestamoDAOTest` |
| El mismo préstamo no se puede devolver dos veces | `PrestamoDAOTest` |
| Un libro devuelto vuelve a estar disponible | `PrestamoDAOTest` |

## Arquitectura DAO

El patrón DAO (*Data Access Object*) separa el acceso a datos del resto de la
aplicación: **todo el SQL vive en la capa DAO y en ningún otro lugar**. Ni el
Servlet ni la JSP saben que detrás hay una base de datos.

```
Servlet (controlador)      ← recibe la petición, no escribe SQL
    │
    ▼
DAO (LibroDAO · UsuarioDAO · PrestamoDAO)
    │   PreparedStatement + ResultSet
    ▼
ConexionBD (Singleton)     ← una única conexión compartida
    │   JDBC
    ▼
Base de datos H2
```

**Una clase DAO por entidad.** `LibroDAO` tiene el CRUD del catálogo,
`UsuarioDAO` valida credenciales y `PrestamoDAO` registra préstamos y
devoluciones. Cada uno expone métodos con nombres del dominio —`listar()`,
`prestar()`, `devolver()`— y devuelve objetos Java (`Libro`, `Usuario`,
`Prestamo`), nunca `ResultSet`.

**Por qué importa.** Cambiar H2 por MySQL no obliga a tocar los Servlets ni las
JSP: basta cambiar la URL de conexión, porque el contrato de los DAO se mantiene.
Esa es la ventaja concreta de separar la capa de acceso a datos.

**Consultas parametrizadas.** Todo el SQL usa `PreparedStatement` con `?` en vez
de concatenar texto, de modo que los datos del usuario nunca se mezclan con la
sentencia. Hay una prueba que lo verifica enviando una comilla en el email.

**Las reglas de negocio viven sobre el SQL, no dentro de él:** un libro con
historial de préstamos no se elimina, un libro ya prestado no se presta de nuevo
y un préstamo no se devuelve dos veces.

## Lecciones aprendidas

- El 404 inicial de un proyecto vacío no es un error: es la señal de que el
  servidor está vivo y falta contenido.
- La validación del lado del servidor es la que vale: `getParameter` puede
  llegar nulo o vacío aunque el formulario tenga `required`.
- Dentro de Tomcat, el driver JDBC de `WEB-INF/lib` debe cargarse con
  `Class.forName` — el DriverManager no lo descubre solo.
- Probar el flujo completo encuentra errores que el código "correcto" esconde:
  la eliminación de libros con historial rompía la clave foránea y terminó
  convertida en una regla de negocio con mensaje al usuario.
- Las vistas en `WEB-INF` obligan a pasar por el controlador: sin eso, el
  patrón MVC se puede "saltar" desde la barra de direcciones.

---

## Evidencia funcional (capturas)

En `CAPTURAS/` hay **12 imagenes** del flujo completo, tomadas sobre la aplicacion
realmente desplegada en Apache Tomcat 9.0.120 (`http://localhost:8080/biblioteca/`):

| # | Captura | Que demuestra |
|---|---|---|
| 01-02 | login / login incorrecto | JSP, formularios y validacion del lado del servidor |
| 03-05 | catalogo / alta de libro | Servlet + JSP + JSTL + DAO · CRUD Create |
| 06 | libro en estado Prestado | Logica de negocio |
| 07-09 | historial, devolucion y regreso a Disponible | CRUD Update · navegacion entre vistas |
| 10 | aviso al eliminar un libro con historial | Integridad referencial |
| 11 | gate de sesion tras cerrar sesion | HttpSession |
| 12 | Tomcat Manager con `/biblioteca` ejecutandose | Despliegue del WAR |

El detalle esta en `CAPTURAS/_GUION_CAPTURAS.md`.

## Compatibilidad de Java

El WAR se compila con **`--release 17`** (bytecode 61), de modo que se ejecuta tanto en
JDK 17 como en JDK 21 (el del ambiente del modulo). El proyecto Eclipse declara el facet
`Java 17` y `Dynamic Web Module 4.0`, con Apache Tomcat v9.0 como Targeted Runtime.

> Nota tecnica: si se recompila el proyecto, hacerlo con el JDK del ambiente del curso.
> Compilar con un JDK mas nuevo genera bytecode que la JVM 21 de Tomcat no puede cargar
> (`UnsupportedClassVersionError`) y la aplicacion responde HTTP 500 en todas sus rutas.

## Entregables

| Archivo | Contenido |
|---|---|
| `BibliotecaDigitalUNTEC.zip` | Proyecto Eclipse completo con el codigo fuente |
| `biblioteca.war` | Aplicacion lista para desplegar en Tomcat |
| `README.md` | Este documento |
| `CAPTURAS/` | 12 capturas del flujo funcional |
- Ocultar el botón *Prestar* en la vista no impide que llegue un POST directo
  con el id de un libro ya prestado. La comprobación de disponibilidad tuvo que
  bajar al servidor: la vista decide qué se muestra, no qué se permite.
- Un `BUILD SUCCESS` no dice con qué JDK se compiló. Fijar
  `maven.compiler.release` en el `pom.xml` hace que el bytecode no dependa de
  qué `javac` encuentre primero el PATH.
- Una prueba en verde puede no estar midiendo nada: se desactivó a propósito la
  regla del préstamo duplicado y se comprobó que la prueba efectivamente falla.
