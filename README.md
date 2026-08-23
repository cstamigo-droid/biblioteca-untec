# Biblioteca Digital UNTEC

Proyecto de evaluación del **Módulo 5 — Desarrollo de aplicaciones web dinámicas en Java**
(Bootcamp Full Stack Java Trainee).

Autor: **Cristian Amigo**

Aplicación web Java EE para gestionar el catálogo, los usuarios y los préstamos de la
biblioteca digital de la Universidad UNTEC: permite iniciar sesión, consultar y administrar
libros, registrar préstamos y devoluciones desde una interfaz web simple.

---

## Tecnologías utilizadas

| Capa | Tecnología |
|---|---|
| Lenguaje | Java (JDK 17+) |
| Web | Servlets + JSP con JSTL 1.2 |
| Patrón | MVC (paquetes `model`, `view`, `controller`) + DAO + Singleton |
| Base de datos | H2 (modo archivo, uso educativo) vía JDBC |
| Servidor | Apache Tomcat 9 |
| IDE | Eclipse IDE for Enterprise Java |

## Estructura del proyecto

```
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
    └── lib/            ← jstl-1.2.jar + h2-2.2.224.jar
```

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

1. **Requisitos:** JDK 17+, Apache Tomcat 9.
2. **Opción A — WAR (recomendada):** subir `dist/biblioteca.war` mediante el
   Tomcat Manager (`http://localhost:8080/manager`) o copiarlo a `webapps/`.
3. **Opción B — Eclipse:** importar el proyecto (File > Import > Existing Projects),
   asociar Tomcat 9 como runtime y ejecutar con *Run on Server*.
4. Abrir `http://localhost:8080/biblioteca/`.
5. La base de datos H2 se crea sola en el primer arranque (archivo
   `C:/dev/biblioteca_untec.mv.db`) con datos de ejemplo.

### Credenciales de prueba

| Usuario | Correo | Contraseña |
|---|---|---|
| Bibliotecaria UNTEC | `biblioteca@untec.cl` | `untec2026` |
| Cristian Amigo | `cstamigo@gmail.com` | `admin123` |

## Flujo funcional

1. Login (validación en servidor; con error visible si las credenciales fallan).
2. Catálogo: listado con estado Disponible/Prestado, agregar libro, eliminar
   (solo si no tiene historial de préstamos — integridad referencial), prestar.
3. Préstamos: historial con fecha y estado, registrar devolución (el libro
   vuelve a quedar disponible).
4. Toda página de gestión exige sesión iniciada; Cerrar sesión la invalida.

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
