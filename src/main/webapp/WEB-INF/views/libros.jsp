<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- Vista del CATÁLOGO. Recibe la lista "libros" desde LibroServlet.
     Vive en WEB-INF/views: solo se llega a través del controlador. --%>
<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="UTF-8">
<title>Catálogo — Biblioteca UNTEC</title>
<style>
  body { font-family: Segoe UI, Arial, sans-serif; background: #eef2f5; color: #22313f; margin: 26px; }
  header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
  h1 { font-size: 21px; color: #1a5276; }
  nav a { margin-left: 14px; color: #1a5276; text-decoration: none; font-size: 14px; }
  table { border-collapse: collapse; width: 100%; background: #fff; border-radius: 8px;
          overflow: hidden; box-shadow: 0 1px 5px rgba(0,0,0,.08); }
  th, td { padding: 10px 14px; border-bottom: 1px solid #e5eaee; text-align: left; font-size: 14px; }
  th { background: #1a5276; color: #fff; font-size: 12px; letter-spacing: .5px; }
  .disp { color: #1e8449; font-weight: 600; }
  .prest { color: #c0392b; font-weight: 600; }
  form.inline { display: inline; }
  button { padding: 6px 12px; border: 0; border-radius: 4px; cursor: pointer; font-size: 13px; }
  .prestar { background: #1a5276; color: #fff; }
  .eliminar { background: #e5eaee; color: #7f2d2d; }
  .agregar { background: #fff; padding: 16px; margin-top: 18px; border-radius: 8px;
             box-shadow: 0 1px 5px rgba(0,0,0,.08); width: fit-content; }
  .agregar input { padding: 8px; margin-right: 8px; border: 1px solid #cfd8dc; border-radius: 4px; }
  .aviso { background: #fdecea; color: #c0392b; padding: 8px 12px; border-radius: 5px;
           font-size: 13px; margin-bottom: 12px; width: fit-content; }
</style>
</head>
<body>
<header>
    <h1>📚 Catálogo de libros</h1>
    <nav>
        <%-- c:out — el usuario en sesión --%>
        Hola, <b><c:out value="${sessionScope.usuario.nombre}" /></b>
        <a href="prestamos">Préstamos</a>
        <a href="logout">Cerrar sesión</a>
    </nav>
</header>

<c:if test="${param.error == 'campos'}">
    <div class="aviso">Título y autor son obligatorios para agregar un libro.</div>
</c:if>
<c:if test="${param.error == 'historial'}">
    <div class="aviso">Ese libro no se puede eliminar: tiene historial de préstamos.</div>
</c:if>
<c:if test="${param.error == 'noDisponible'}">
    <div class="aviso">Ese libro ya está prestado: no se puede prestar dos veces.</div>
</c:if>

<table>
    <tr><th>ID</th><th>Título</th><th>Autor</th><th>Estado</th><th>Acciones</th></tr>
    <%-- c:forEach — recorre el catálogo que envió el controlador --%>
    <c:forEach items="${libros}" var="libro">
        <tr>
            <td><c:out value="${libro.id}" /></td>
            <td><c:out value="${libro.titulo}" /></td>
            <td><c:out value="${libro.autor}" /></td>
            <td>
                <%-- c:if / c:choose — estado según disponibilidad --%>
                <c:choose>
                    <c:when test="${libro.disponible}"><span class="disp">Disponible</span></c:when>
                    <c:otherwise><span class="prest">Prestado</span></c:otherwise>
                </c:choose>
            </td>
            <td>
                <c:if test="${libro.disponible}">
                    <form class="inline" action="libros" method="post">
                        <input type="hidden" name="accion" value="prestar">
                        <input type="hidden" name="id" value="${libro.id}">
                        <button class="prestar">Prestar</button>
                    </form>
                    <form class="inline" action="libros" method="post">
                        <input type="hidden" name="accion" value="eliminar">
                        <input type="hidden" name="id" value="${libro.id}">
                        <button class="eliminar">Eliminar</button>
                    </form>
                </c:if>
            </td>
        </tr>
    </c:forEach>
</table>

<div class="agregar">
    <b>Agregar libro al catálogo</b><br><br>
    <form action="libros" method="post">
        <input type="hidden" name="accion" value="agregar">
        <input type="text" name="titulo" placeholder="Título" required>
        <input type="text" name="autor" placeholder="Autor" required>
        <button class="prestar">Agregar</button>
    </form>
</div>
</body>
</html>
