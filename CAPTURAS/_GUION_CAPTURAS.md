# Capturas del flujo funcional — Proyecto M5 · Biblioteca Digital UNTEC

Generadas el 19-ago-2026 recorriendo la aplicacion **realmente desplegada** en
Apache Tomcat 9.0.120 (`http://localhost:8080/biblioteca/`), con Chrome a 1280x780.

| # | Archivo | Que muestra | Competencia evaluada |
|---|---|---|---|
| 01 | `01_login.png` | Pantalla de ingreso | JSP + formulario |
| 02 | `02_login_incorrecto.png` | Credenciales invalidas → mensaje de error | Validacion del lado del SERVIDOR |
| 03 | `03_catalogo.png` | Catalogo con estados Disponible/Prestado | Servlet + JSP + JSTL + DAO |
| 04 | `04_agregar_formulario.png` | Formulario con "Rayuela" escrito, antes de enviar | Formularios + POST |
| 05 | `05_libro_agregado.png` | El libro ya incorporado al catalogo | CRUD · Create |
| 06 | `06_catalogo_libro_prestado.png` | "Rayuela" en estado **Prestado** (rojo) | Logica de negocio + CRUD Update |
| 07 | `07_prestamos_historial.png` | Historial con fecha y estado Activo/Devuelto | Segunda vista + navegacion |
| 08 | `08_devolucion_registrada.png` | El prestamo pasa a Devuelto | CRUD · Update |
| 09 | `09_catalogo_tras_devolucion.png` | El libro vuelve a Disponible | Integridad del flujo |
| 10 | `10_regla_negocio_eliminar.png` | "Ese libro no se puede eliminar: tiene historial de prestamos" | Integridad referencial |
| 11 | `11_sesion_cerrada_gate.png` | Tras cerrar sesion, /libros exige volver a entrar | HttpSession |
| 12 | `12_tomcat_manager_desplegado.png` | Manager con `/biblioteca` — Biblioteca Digital UNTEC — Ejecutandose: true | **DESPLIEGUE del WAR en Tomcat** |

## Cobertura del "¿Que vamos a validar?" del enunciado
- Patron MVC correctamente aplicado → 03, 06, 07 (el controlador decide, la vista presenta)
- Uso adecuado de Servlets y JSP → todas
- Separacion de logica con DAO → 05, 06, 08, 10 (persistencia y reglas)
- Flujo funcional de navegacion y gestion de datos → 03 a 11 en secuencia
- **Despliegue correcto y funcional en Tomcat → 12**

## Como se regeneran
    python C:\Users\cstam\java-web-clases\capturar_biblioteca.py

Requiere Tomcat arriba con /biblioteca desplegado. El script recorre la app con
Playwright sobre Chrome y vuelve a guardar las 12 imagenes aca.

## 13 — Regla de servidor: un libro prestado no se presta dos veces (29-ago-2026)

`13_regla_libro_ya_prestado.png`

Agregada tras corregir un defecto: la vista ocultaba el boton "Prestar" para un
libro ya prestado, pero un **POST directo** con `accion=prestar&id=N` creaba un
segundo prestamo del mismo ejemplar. La comprobacion se movio al servidor
(`PrestamoDAO.prestar`), y la captura muestra el POST directo rechazado con su
aviso. Es la evidencia de que la autorizacion no depende de esconder botones.
