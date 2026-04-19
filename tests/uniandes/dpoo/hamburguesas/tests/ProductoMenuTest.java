package uniandes.dpoo.hamburguesas.tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

public class ProductoMenuTest {
    
    private static final String NOMBRE_PRODUCTO = "wrap de pollo";
    private static final int PRECIO_NORMAL = 15000;
    private static final String NOMBRE_PRODUCTO_CERO = "Agua";
    private static final int PRECIO_CERO = 0;
    private static final String NOMBRE_PRODUCTO_NEGATIVO = "Hamburguesa";
    private static final int PRECIO_NEGATIVO = -5000;
    private static final String FORMATO_FACTURA_ESPERADO = "%s\n            $%d\n";
    
    private ProductoMenu producto;
    
    @BeforeEach
    void setUp() {
        producto = new ProductoMenu(NOMBRE_PRODUCTO, PRECIO_NORMAL);
    }
    
    @Test
    void testGetNombre() {
        assertEquals(NOMBRE_PRODUCTO, producto.getNombre());
        assertNotNull(producto.getNombre());
        assertFalse(producto.getNombre().isBlank());
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n", "  \t  ", " \n\t ", "\t\n "})
    void testNombresInvalidosLanzanExcepcion(String nombreInvalido) {
        assertThrows(IllegalArgumentException.class, () -> {
            new ProductoMenu(nombreInvalido, 5000);
        });
    }
    
    @Test
    void testGetPrecio() {
        assertEquals(PRECIO_NORMAL, producto.getPrecio());
        assertTrue(producto.getPrecio() > 0);
    }
    
    @Test
    void testProductoPrecioNegativo() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ProductoMenu(NOMBRE_PRODUCTO_NEGATIVO, PRECIO_NEGATIVO);
        });
    }
    
    @Test
    void testProductoGratis() {
        ProductoMenu gratis = new ProductoMenu(NOMBRE_PRODUCTO_CERO, PRECIO_CERO);
        
        assertEquals(PRECIO_CERO, gratis.getPrecio());
        assertTrue(gratis.generarTextoFactura().contains("$" + PRECIO_CERO));
        
        String expected = String.format(FORMATO_FACTURA_ESPERADO, NOMBRE_PRODUCTO_CERO, PRECIO_CERO);
        assertEquals(expected, gratis.generarTextoFactura());
    }
    
    @Test
    void testGenerarTextoFactura() {
        String factura = producto.generarTextoFactura();
        assertTrue(factura.contains(NOMBRE_PRODUCTO));
        assertTrue(factura.contains("$" + PRECIO_NORMAL));
        String expected = String.format(FORMATO_FACTURA_ESPERADO, NOMBRE_PRODUCTO, PRECIO_NORMAL);
        assertEquals(expected, factura);
    }
    
    
    @Test
    void testNoExistenSetters() {
        assertThrows(NoSuchMethodException.class, () -> {
            producto.getClass().getMethod("setNombre", String.class);
        });
        assertThrows(NoSuchMethodException.class, () -> {
            producto.getClass().getMethod("setPrecio", int.class);
        });
    }
    
    @Test
    void testIndependenciaEntreObjetosMismosDatos() {
        ProductoMenu productoA = new ProductoMenu(NOMBRE_PRODUCTO, PRECIO_NORMAL);
        ProductoMenu productoB = new ProductoMenu(NOMBRE_PRODUCTO, PRECIO_NORMAL);
        
        assertEquals(productoA.getNombre(), productoB.getNombre());
        assertEquals(productoA.getPrecio(), productoB.getPrecio());
        
        productoA = new ProductoMenu(NOMBRE_PRODUCTO, PRECIO_NORMAL + 5000);
        
        assertNotEquals(productoA.getPrecio(), productoB.getPrecio());
        assertEquals(productoA.getNombre(), productoB.getNombre());
    }
    
    
}