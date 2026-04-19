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
	 
	 @Test
		void testDescuentoInvalidoNegativo() {
		    assertThrows(IllegalArgumentException.class, () -> {
		        new Combo(NOMBRE_COMBO, -0.5, productos);
		    });
		}
		
		@Test
		void testDescuentoInvalidoMayorAUno() {
		    assertThrows(IllegalArgumentException.class, () -> {
		        new Combo(NOMBRE_COMBO, 1.5, productos);
		    });
		}
	 
	  @Test
	  void testGetPrecio() {
	    	assertTrue(comboNuevo.getPrecio() > 0);
	        assertEquals((PRECIO_NORMAL1+PRECIO_NORMAL2)*(1-DESCUENTO), comboNuevo.getPrecio());
	    
	    }
    
	  @Test
		void testComboConUnSoloProducto() {
		    ArrayList<ProductoMenu> unProducto = new ArrayList<>();
		    unProducto.add(producto1);
		    Combo comboSolo = new Combo("combo simple", 0.15, unProducto);
		    int precioEsperado = (int)(PRECIO_NORMAL1 * (1 - 0.15));
		    assertEquals(precioEsperado, comboSolo.getPrecio());
		}
	    
	  @Test
	  void testGenerarTextoFactura() {
	      String factura = comboNuevo.generarTextoFactura();
	      
	      assertTrue(factura.contains("Combo " + NOMBRE_COMBO));
	      assertTrue(factura.contains("Descuento: " + DESCUENTO));
	      assertTrue(factura.contains("$" + comboNuevo.getPrecio()));
	  }
	  
	  @Test
		void testNoExistenSettersParaProductos() {
		    assertThrows(NoSuchMethodException.class, () -> {
		        comboNuevo.getClass().getMethod("setItemsCombo", ArrayList.class);
		    });
		}
	  
	  @Test
		void testProductosOriginalesNoSeModificanAlCrearCombo() {
		    int precioOriginalProducto1 = producto1.getPrecio();
		    int precioOriginalProducto2 = producto2.getPrecio();
		    String nombreOriginalProducto1 = producto1.getNombre();
		    String nombreOriginalProducto2 = producto2.getNombre();
		    
		    new Combo(NOMBRE_COMBO, DESCUENTO, productos);
		    
		    assertEquals(precioOriginalProducto1, producto1.getPrecio());
		    assertEquals(precioOriginalProducto2, producto2.getPrecio());
		    assertEquals(nombreOriginalProducto1, producto1.getNombre());
		    assertEquals(nombreOriginalProducto2, producto2.getNombre());
		}
	  
	  
}
