# Entrega — Proyecto Módulo 5: Biblioteca Digital UNTEC

**Alumno:** Cristian Amigo
**Módulo 5 — Desarrollo de aplicaciones web dinámicas en Java**
Bootcamp Desarrollo Full Stack Java Trainee · ISEG/SENCE

---

## Qué contiene esta entrega

| Archivo | Qué es |
|---|---|
| `BibliotecaDigitalUNTEC.zip` | Proyecto Eclipse completo con el código fuente |
| `biblioteca.war` | Aplicación empaquetada, lista para desplegar en Tomcat |
| `README.md` | Documentación técnica: arquitectura, MVC, DAO e instrucciones |
| `CAPTURAS/` | 12 capturas del flujo funcional corriendo en Tomcat |

Los cuatro entregables que pide la consigna están cubiertos.

---

## Cómo ejecutar la aplicación

### Opción A — Desplegar el WAR (recomendada)

1. Con Tomcat 9 en marcha, abrir `http://localhost:8080/manager/html`
2. En "Archivo WAR a desplegar", seleccionar `biblioteca.war` y presionar **Desplegar**
3. Abrir `http://localhost:8080/biblioteca/`

O bien copiar `biblioteca.war` a la carpeta `webapps/` de Tomcat.

### Opción B — Desde Eclipse

1. Descomprimir `BibliotecaDigitalUNTEC.zip`
2. `File → Import… → General → Existing Projects into Workspace`
3. Asociar **Apache Tomcat v9.0** como runtime
4. Clic derecho en el proyecto → `Run As → Run on Server`

La base de datos H2 se crea sola en el primer arranque, con datos de ejemplo.

### Credenciales de prueba

| Usuario | Correo | Contraseña |
|---|---|---|
| Bibliotecaria UNTEC | `biblioteca@untec.cl` | `untec2026` |
| Cristian Amigo | `cstamigo@gmail.com` | `admin123` |

---

## Requisitos

- **JDK 17 o superior** (verificado en JDK 21, el del ambiente del módulo)
- **Apache Tomcat 9** (probado en 9.0.120)

El WAR está compilado con `--release 17`, por lo que se ejecuta sin cambios tanto
en JDK 17 como en JDK 21.

---

## Cobertura de los requerimientos de la consigna

| Requerimiento | Dónde se cumple |
|---|---|
| Java EE, JSP, Servlets y patrón MVC | Paquetes `model` / `view` / `controller` |
| Capa DAO con acceso a datos usando JDBC | `LibroDAO`, `UsuarioDAO`, `PrestamoDAO` + `ConexionBD` (Singleton) |
| JSTL en la capa de vista | `libros.jsp` y `prestamos.jsp` (`c:out`, `c:if`, `c:choose`, `c:forEach`) |
| Formularios para interacción del usuario | Login, alta de libros, préstamo y devolución |
| Gestión de sesiones de usuario | `HttpSession` en `LoginServlet`; vistas protegidas bajo `WEB-INF` |
| Despliegue en Tomcat mediante `.WAR` | `biblioteca.war` + captura 12 del Manager |
| IDE Eclipse Enterprise Edition | Proyecto con facets `Java 17` y `Dynamic Web Module 4.0` |
| Base de datos MySQL o H2 | H2 en modo archivo, permitido por la consigna para uso educativo |

## Cobertura del "¿Qué vamos a validar?"

| Criterio | Evidencia |
|---|---|
| Aplicación del patrón MVC | Estructura de paquetes + capturas 03, 06, 07 |
| Uso adecuado de Servlets y JSP | Todas las capturas del flujo |
| Separación de lógica de negocio con DAO | Capturas 05, 06, 08, 10 |
| Flujo funcional de navegación y gestión de datos | Capturas 03 a 11, en secuencia |
| Despliegue correcto y funcional en Tomcat | **Captura 12** (Manager, `Ejecutándose: true`) |
| Código limpio, comentado y funcional | Código fuente en el ZIP |
| Documentación técnica mínima | `README.md` |
