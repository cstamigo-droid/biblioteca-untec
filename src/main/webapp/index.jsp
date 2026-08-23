<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- Vista de LOGIN de la Biblioteca Digital UNTEC.
     El formulario envía por POST al LoginServlet (/login). --%>
<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="UTF-8">
<title>Biblioteca Digital UNTEC — Ingreso</title>
<style>
  body { font-family: Segoe UI, Arial, sans-serif; background: #eef2f5; color: #22313f;
         display: flex; justify-content: center; align-items: center; min-height: 90vh; }
  .tarjeta { background: #fff; padding: 34px 40px; border-radius: 10px;
             box-shadow: 0 2px 10px rgba(0,0,0,.12); width: 330px; }
  h1 { font-size: 22px; color: #1a5276; margin-bottom: 4px; }
  p.sub { color: #7f8c8d; font-size: 13px; margin-bottom: 18px; }
  label { display: block; font-size: 13px; margin-top: 12px; }
  input { width: 100%; padding: 9px; margin-top: 4px; border: 1px solid #cfd8dc;
          border-radius: 5px; box-sizing: border-box; }
  button { margin-top: 18px; width: 100%; padding: 10px; background: #1a5276;
           color: #fff; border: 0; border-radius: 5px; font-size: 15px; cursor: pointer; }
  button:hover { background: #154360; }
  .error { background: #fdecea; color: #c0392b; padding: 8px 10px; border-radius: 5px;
           font-size: 13px; margin-top: 14px; }
</style>
</head>
<body>
<div class="tarjeta">
    <h1>📚 Biblioteca Digital UNTEC</h1>
    <p class="sub">Sistema de gestión de libros y préstamos</p>

    <form action="login" method="post">
        <label>Correo institucional
            <input type="email" name="email" required>
        </label>
        <label>Contraseña
            <input type="password" name="clave" required>
        </label>
        <button type="submit">Ingresar</button>
    </form>

    <%-- c:if — aviso solo cuando el login falló --%>
    <c:if test="${param.error == '1'}">
        <div class="error">
            <c:out value="Credenciales incorrectas. Verifica tu correo y contraseña." />
        </div>
    </c:if>
</div>
</body>
</html>
