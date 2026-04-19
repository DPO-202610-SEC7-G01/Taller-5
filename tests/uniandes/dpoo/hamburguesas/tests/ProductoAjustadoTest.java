package uniandes.dpoo.hamburguesas.tests;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import uniandes.dpoo.hamburguesas.mundo.*;


public class ProductoAjustadoTest {
	 
	private static final String NOMBRE_PRODUCTO = "wrap de pollo";
	private static final int PRECIO_NORMAL = 15000;

	private static final String NOMBRE_INGREDIENTE1 = "lechuga";
	private static final int PRECIO_INGREDIENTE1 = 1000;
	private static final String NOMBRE_INGREDIENTE2 = "queso mozzarella";
	private static final int PRECIO_INGREDIENTE2 = 2500;
	private static final String NOMBRE_INGREDIENTE3 = "tomate";
	private static final int PRECIO_INGREDIENTE3 = 1000;
	private static final String NOMBRE_INGREDIENTE4 = "tocineta expres";
	private static final int PRECIO_INGREDIENTE4 = 2500;
	
	private ProductoAjustado productoAjustado;
	private ProductoMenu productoBase;
	private Ingrediente ingrediente1;
	private Ingrediente ingrediente2;
	private Ingrediente ingrediente3;
	private Ingrediente ingrediente4;
 
	@BeforeEach
	void setUp() {
	    productoBase = new ProductoMenu(NOMBRE_PRODUCTO, PRECIO_NORMAL);
	    productoAjustado = new ProductoAjustado(productoBase);
	    
	    ingrediente1 = new Ingrediente(NOMBRE_INGREDIENTE1, PRECIO_INGREDIENTE1);
	    ingrediente2 = new Ingrediente(NOMBRE_INGREDIENTE2, PRECIO_INGREDIENTE2);
	    ingrediente3 = new Ingrediente(NOMBRE_INGREDIENTE3, PRECIO_INGREDIENTE3);
	    ingrediente4 = new Ingrediente(NOMBRE_INGREDIENTE4, PRECIO_INGREDIENTE4);
	    
	    productoAjustado.agregarIngrediente(ingrediente1);
	    productoAjustado.agregarIngrediente(ingrediente2);
	    productoAjustado.agregarIngrediente(ingrediente3);
	    productoAjustado.agregarIngrediente(ingrediente4);
	}
	
    @Test
    void testGetNombre() {
        assertEquals(NOMBRE_PRODUCTO, productoAjustado.getNombre());
        assertNotNull(productoAjustado.getNombre());
        assertFalse(productoAjustado.getNombre().isBlank());
    }
    
    @Test
    void testGetPrecio() {
    	assertTrue(productoAjustado.getPrecio() > 0);
        assertEquals(PRECIO_NORMAL + PRECIO_INGREDIENTE1 + PRECIO_INGREDIENTE2 + PRECIO_INGREDIENTE3 + PRECIO_INGREDIENTE4, productoAjustado.getPrecio());
    }
    
    @Test
    void agregarCorrectamente() {//Queremos agregar productos y obtener el precio final
        ProductoAjustado nuevoAjustado = new ProductoAjustado(productoBase);
        
        int precioInicial = nuevoAjustado.getPrecio();
        assertEquals(PRECIO_NORMAL, precioInicial);
        
        nuevoAjustado.agregarIngrediente(ingrediente1);
        assertEquals(PRECIO_NORMAL + PRECIO_INGREDIENTE1, nuevoAjustado.getPrecio());
        
        nuevoAjustado.agregarIngrediente(ingrediente2);
        assertEquals(PRECIO_NORMAL + PRECIO_INGREDIENTE1 + PRECIO_INGREDIENTE2, nuevoAjustado.getPrecio());
        
        assertTrue(nuevoAjustado.getAgregados().contains(ingrediente1));
        assertTrue(nuevoAjustado.getAgregados().contains(ingrediente2));
    }
    
    @Test
    void eliminarCorrectamente() { //Queremos eliminar productos y obtener el precio final 
        int precioAntesEliminar = productoAjustado.getPrecio();
        int precioEsperadoAntes = PRECIO_NORMAL + PRECIO_INGREDIENTE1 + PRECIO_INGREDIENTE2 + PRECIO_INGREDIENTE3 + PRECIO_INGREDIENTE4;
        assertEquals(precioEsperadoAntes, precioAntesEliminar);
        
        productoAjustado.eliminarIngrediente(ingrediente1);
        
        int precioDespuesEliminar = productoAjustado.getPrecio();
        int precioEsperadoDespues = PRECIO_NORMAL + PRECIO_INGREDIENTE2 + PRECIO_INGREDIENTE3 + PRECIO_INGREDIENTE4;
        assertEquals(precioEsperadoDespues, precioDespuesEliminar);
        
        assertFalse(productoAjustado.getAgregados().contains(ingrediente1));
        assertTrue(productoAjustado.getEliminados().contains(ingrediente1));
    }
    
    @Test
    void testGenerarTextoFactura() { // Verificar que lafactura tenga los datos que queremos
        String factura = productoAjustado.generarTextoFactura();
        
        assertTrue(factura.contains(NOMBRE_PRODUCTO));
        assertTrue(factura.contains("+" + NOMBRE_INGREDIENTE1));
        assertTrue(factura.contains("+" + NOMBRE_INGREDIENTE2));
        assertTrue(factura.contains(String.valueOf(PRECIO_INGREDIENTE1)));
        assertTrue(factura.contains(String.valueOf(PRECIO_INGREDIENTE2)));
        assertTrue(factura.contains(String.valueOf(PRECIO_NORMAL)));
    }
    
    
    @Test
    void testEliminarMismoIngredienteDosVeces() { // Eliminar ingredientes de forma correcta
        productoAjustado.eliminarIngrediente(ingrediente1);
        int precioDespuesPrimeraVez = productoAjustado.getPrecio();
        
        productoAjustado.eliminarIngrediente(ingrediente1);
        int precioDespuesSegundaVez = productoAjustado.getPrecio();
        
        assertEquals(PRECIO_NORMAL + PRECIO_INGREDIENTE2 + PRECIO_INGREDIENTE3 + PRECIO_INGREDIENTE4, precioDespuesPrimeraVez);
        assertEquals(PRECIO_NORMAL + PRECIO_INGREDIENTE2 + PRECIO_INGREDIENTE3 + PRECIO_INGREDIENTE4, precioDespuesSegundaVez);
        assertEquals(1, productoAjustado.getEliminados().size());
    }
    
    @Test
    void testEliminarIngredienteNoAgregadoLanzaExcepcion() { // Probar los errores para cuando se quiere eliminar cosas que no estaban agregadas
        ProductoAjustado nuevoAjustado = new ProductoAjustado(productoBase);
        Ingrediente ingredienteNuevo = new Ingrediente("piña", 2500);
        
        assertThrows(IllegalArgumentException.class, () -> {
            nuevoAjustado.eliminarIngrediente(ingredienteNuevo);
        });
    }
    
    @Test
    void testEliminarIngredienteYaEliminado() { // Verificar si está correctamente eliminado
        productoAjustado.eliminarIngrediente(ingrediente1);
        assertTrue(productoAjustado.getEliminados().contains(ingrediente1));
        
        productoAjustado.eliminarIngrediente(ingrediente1);
        assertEquals(1, productoAjustado.getEliminados().size());
    }
    
 
    
    @Test
    void testNoSeModificaProductoBaseOriginal() { // Ver que no se modifique el producto base con el valor de los agregados
        int precioBaseOriginal = productoBase.getPrecio();
        String nombreBaseOriginal = productoBase.getNombre();
        
        productoAjustado.agregarIngrediente(ingrediente1);
        productoAjustado.eliminarIngrediente(ingrediente2);
        
        assertEquals(precioBaseOriginal, productoBase.getPrecio());
        assertEquals(nombreBaseOriginal, productoBase.getNombre());
    }
    
    
}