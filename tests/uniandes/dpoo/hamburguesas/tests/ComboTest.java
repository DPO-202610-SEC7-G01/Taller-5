package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import uniandes.dpoo.hamburguesas.mundo.Combo;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

public class ComboTest {
	
	private static final String NOMBRE_COMBO = "combo corral";
    private static final double DESCUENTO = 0.1;
    private static final String NOMBRE_PRODUCTO1 = "papas medianas";
    private static final int PRECIO_NORMAL1 = 5500;
    private static final String NOMBRE_PRODUCTO2 = "gaseosa";
    private static final int PRECIO_NORMAL2 = 5000;
    
    
	private Combo comboNuevo;
	private ProductoMenu producto1;
	private ProductoMenu producto2;
	private ArrayList<ProductoMenu> productos;
	
	@BeforeEach
	void setUp() {
	    producto1 = new ProductoMenu(NOMBRE_PRODUCTO1, PRECIO_NORMAL1);
	    producto2 = new ProductoMenu(NOMBRE_PRODUCTO2, PRECIO_NORMAL2);
	    productos = new ArrayList<>();
	    productos.add(producto1);
	    productos.add(producto2);
	    comboNuevo = new Combo(NOMBRE_COMBO, DESCUENTO, productos);
	   
	}
	
	 @Test
	 void testGetNombre() {
	        assertEquals(NOMBRE_COMBO, comboNuevo.getNombre());
	        assertNotNull(comboNuevo.getNombre());
	        assertFalse(comboNuevo.getNombre().isBlank());
	    }
	    
	 @ParameterizedTest
	 @NullAndEmptySource
	 @ValueSource(strings = {"   ", "\t", "\n", "  \t  ", " \n\t ", "\t\n "})
	 void testNombresInvalidosLanzanExcepcion(String nombreInvalido) {
	        assertThrows(IllegalArgumentException.class, () -> {
	            new Combo(nombreInvalido, DESCUENTO,productos);
	        });
	    }
	 
	 // Debe haber un test que de error si se intenta con un descuento fuera de 0 o 1. 
	 
	 
	  @Test
	  void testGetPrecio() {
	    	assertTrue(comboNuevo.getPrecio() > 0);
	        assertEquals((PRECIO_NORMAL1+PRECIO_NORMAL2)*DESCUENTO, comboNuevo.getPrecio());
	    
	    }
    
	  //Debe haber un test que de error si se intenta crear un combo quee no tiene productos
	    
	  @Test
	  void testGenerarTextoFactura() {
	        String factura = comboNuevo.generarTextoFactura();
	        
	        assertTrue(factura.contains(NOMBRE_COMBO));
	        assertTrue(factura.contains("+" + NOMBRE_PRODUCTO1));
	        assertTrue(factura.contains("+" + NOMBRE_PRODUCTO2));
	        assertTrue(factura.contains(String.valueOf(PRECIO_NORMAL1)));
	        assertTrue(factura.contains(String.valueOf(PRECIO_NORMAL2)));
	    }
	  
	  // No se pueden modificar los elementos de un combo  osea no debe haber un setter ni nada de eso
	  // para la lista de productos del combo
	  
	  
}
