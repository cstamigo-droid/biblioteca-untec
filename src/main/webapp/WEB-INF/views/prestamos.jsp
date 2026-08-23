<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- Vista de PRÉSTAMOS. Recibe la lista "prestamos" desde PrestamoServlet. --%>
<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="UTF-8">
<title>Préstamos — Biblioteca UNTEC</title>
<style>
  body { font-family: Segoe UI, Arial, sans-serif; background: #eef2f5; color: #22313f; margin: 26px; }
  header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
  h1 { font-size: 21px; color: #1a5276; }
  nav a { margin-left: 14px; color: #1a5276; text-decoration: none; font-size: 14px; }
  table { border-collapse: collapse; width: 100%; background: #fff; border-radius: 8px;
          overflow: hidden; box-shadow: 0 1px 5px rgba(0,0,0,.08); }
  th, td { padding: 10px 14px; border-bottom: 1px solid #e5eaee; text-align: left; font-size: 14px; }
  th { background: #1a5276; color: #fff; font-size: 12px; letter-spacing: .5px; }
  .activo { color: #b9770e; font-weight: 600; }
  .devuelto { color: #1e8449; }
  button { padding: 6px 12px; border: 0; border-radius: 4px; cursor: pointer;
           background: #1a5276; color: #fff; font-size: 13px; }
  .vacio { background: #fff; padding: 16px; border-radius: 8px; width: fit-content; }
</style>
</head>
<body>
<header>
    <h1>📖 Préstamos</h1>
    <nav>
        Hola, <b><c:out value="${sessionScope.usuario.nombre}" /></b>
        <a href="libros">Catálogo</a>
        <a href="logout">Cerrar sesión</a>
    </nav>
</header>

<c:choose>
    <c:when test="${empty prestamos}">
        <div class="vacio">Aún no hay préstamos registrados. Presta un libro desde el catálogo.</div>
    </c:when>
    <c:otherwise>
        <table>
            <tr><th>ID</th><th>Libro</th><th>Prestado por</th><th>Fecha</th><th>Estado</th><th></th></tr>
            <c:forEach items="${prestamos}" var="p">
                <tr>
                    <td><c:out value="${p.id}" /></td>
                    <td><c:out value="${p.tituloLibro}" /></td>
                    <td><c:out value="${p.nombreUsuario}" /></td>
                    <td><c:out value="${p.fechaPrestamo}" /></td>
                    <td>
                        <c:choose>
                            <c:when test="${p.devuelto}"><span class="devuelto">Devuelto</span></c:when>
                            <c:otherwise><span class="activo">Activo</span></c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <c:if test="${not p.devuelto}">
                            <form action="prestamos" method="post">
                                <input type="hidden" name="accion" value="devolver">
                                <input type="hidden" name="id" value="${p.id}">
                                <button>Registrar devolución</button>
                            </form>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:otherwise>
</c:choose>
</body>
</html>
