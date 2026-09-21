package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de la configuración externa y de la detección del driver JDBC.
 * No tocan la base de datos: verifican las decisiones que toma la clase
 * antes de conectarse.
 */
class ConexionBDTest {

    @AfterEach
    void limpiar() {
        System.clearProperty("BIBLIOTECA_DB_URL");
    }

    @Test
    @DisplayName("Sin configuración externa se usa el valor por defecto")
    void usaValorPorDefecto() {
        assertEquals("valor-defecto",
                ConexionBD.configuracion("CLAVE_QUE_NO_EXISTE_12345", "valor-defecto"));
    }

    @Test
    @DisplayName("Una propiedad de sistema sobrescribe el valor por defecto")
    void laPropiedadDeSistemaManda() {
        System.setProperty("BIBLIOTECA_DB_URL", "jdbc:h2:mem:configurada");
        assertEquals("jdbc:h2:mem:configurada", ConexionBD.getUrl());
    }

    @Test
    @DisplayName("Cada URL JDBC resuelve el driver de su motor")
    void detectaElDriverSegunLaUrl() {
        assertEquals("org.h2.Driver",
                ConexionBD.driverPara("jdbc:h2:file:C:/dev/biblioteca_untec"));
        assertEquals("org.mariadb.jdbc.Driver",
                ConexionBD.driverPara("jdbc:mariadb://127.0.0.1:3306/biblioteca_untec"));
        assertEquals("com.mysql.cj.jdbc.Driver",
                ConexionBD.driverPara("jdbc:mysql://127.0.0.1:3306/biblioteca_untec"));
        assertEquals("org.postgresql.Driver",
                ConexionBD.driverPara("jdbc:postgresql://127.0.0.1:5432/biblioteca_untec"));
    }

    @Test
    @DisplayName("Una URL de un motor desconocido no inventa un driver")
    void urlDesconocidaNoDevuelveDriver() {
        assertNull(ConexionBD.driverPara("jdbc:motor-inexistente://algo"));
        assertNull(ConexionBD.driverPara(null));
    }
}
