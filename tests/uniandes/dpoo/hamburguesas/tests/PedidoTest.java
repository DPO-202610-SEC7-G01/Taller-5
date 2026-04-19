package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import uniandes.dpoo.hamburguesas.mundo.Combo;
import uniandes.dpoo.hamburguesas.mundo.Ingrediente;
import uniandes.dpoo.hamburguesas.mundo.Pedido;
import uniandes.dpoo.hamburguesas.mundo.ProductoAjustado;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

public class PedidoTest {
	
	
	//Cliente
    private static final String NOMBRE_CLIENTE1 = "Iván Martinez";
    private static final String DIRECCION_CLIENTE1 = "Calle 22 #2-80";
    
    private static final String NOMBRE_CLIENTE2 = "Juana Romero";
    private static final String DIRECCION_CLIENTE2 = "";
    
    //Combo
    private static final String NOMBRE_COMBO = "combo corral";
    private static final double DESCUENTO = 0.1;
    private static final String NOMBRE_PRODUCTO1 = "papas medianas";
    private static final int PRECIO_NORMAL1 = 5500;
    private static final String NOMBRE_PRODUCTO2 = "gaseosa";
    private static final int PRECIO_NORMAL2 = 5000;
    
    //Menu
    private static final String NOMBRE_PRODUCTO = "wrap de pollo";
    private static final int PRECIO_NORMAL = 15000;
    
    //Agregados
    private static final String NOMBRE_INGREDIENTE1 = "lechuga";
	private static final int PRECIO_INGREDIENTE1 = 1000;
	
    private Pedido pedido;
    private Combo comboNuevo;
	private ProductoMenu producto1;
	private ProductoMenu producto2;
	private ArrayList<ProductoMenu> productos;
	private ProductoMenu producto;
	private ProductoAjustado productoAjustado;
	private Ingrediente ingrediente1;
	
	@TempDir
	Path tempDir;
    
    @BeforeEach
    void setUp() {
        pedido = new Pedido(NOMBRE_CLIENTE1, DIRECCION_CLIENTE1);
        
        producto1 = new ProductoMenu(NOMBRE_PRODUCTO1, PRECIO_NORMAL1);
	    producto2 = new ProductoMenu(NOMBRE_PRODUCTO2, PRECIO_NORMAL2);
	    productos = new ArrayList<>();
	    productos.add(producto1);
	    productos.add(producto2);
	    comboNuevo = new Combo(NOMBRE_COMBO, DESCUENTO, productos);
	    
	    producto = new ProductoMenu(NOMBRE_PRODUCTO, PRECIO_NORMAL);
	    
	    productoAjustado = new ProductoAjustado(producto);
	    ingrediente1 = new Ingrediente(NOMBRE_INGREDIENTE1, PRECIO_INGREDIENTE1);
	    productoAjustado.agregarIngrediente(ingrediente1);

	    
    }
    
    @Test
    void testGetNombre() {
        assertEquals(NOMBRE_CLIENTE1, pedido.getNombreCliente());
        assertNotNull(pedido.getNombreCliente());
        assertFalse(pedido.getNombreCliente().isBlank());
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
    void testGetDireccion() {
        assertEquals(DIRECCION_CLIENTE1, pedido.getDireccionCliente());
    }
    
    
    @Test
    void testGenerarTextoFactura() {
        pedido.agregarProducto(producto1);
        pedido.agregarProducto(producto2);
        
        String factura = pedido.generarTextoFactura();
        
        assertTrue(factura.contains("Cliente: " + NOMBRE_CLIENTE1));
        assertTrue(factura.contains("Dirección: " + DIRECCION_CLIENTE1));
        assertTrue(factura.contains(NOMBRE_PRODUCTO1));
        assertTrue(factura.contains(String.valueOf(PRECIO_NORMAL1)));
        assertTrue(factura.contains(NOMBRE_PRODUCTO2));
        assertTrue(factura.contains(String.valueOf(PRECIO_NORMAL2)));
        assertTrue(factura.contains("Precio Neto"));
        assertTrue(factura.contains("IVA"));
        assertTrue(factura.contains("Precio Total"));
    }
    
    @Test
    void testIdPedidoIncrementaCorrectamente() throws Exception {
        Field field = Pedido.class.getDeclaredField("numeroPedidos");
        field.setAccessible(true);
        field.setInt(null, 0);
        
        Pedido pedido1 = new Pedido(NOMBRE_CLIENTE1, DIRECCION_CLIENTE1);
        Pedido pedido2 = new Pedido(NOMBRE_CLIENTE2, DIRECCION_CLIENTE2);
        Pedido pedido3 = new Pedido(NOMBRE_CLIENTE1, DIRECCION_CLIENTE2);
        
        assertEquals(0, pedido1.getIdPedido());
        assertEquals(1, pedido2.getIdPedido());
        assertEquals(2, pedido3.getIdPedido());
    }
    
    @Test
    void testAgregarProductoCombo() {
        int precioAntes = pedido.getPrecioTotalPedido();
        assertEquals(0, precioAntes);
        
        pedido.agregarProducto(comboNuevo);
        
        int precioDespues = pedido.getPrecioTotalPedido();
        int precioEsperado = (int)((PRECIO_NORMAL1 + PRECIO_NORMAL2) * (1 - DESCUENTO));
        int ivaEsperado = (int)(precioEsperado * 0.19);
        int precioTotalEsperado = precioEsperado + ivaEsperado;
        
        assertEquals(precioTotalEsperado, precioDespues);
    }
    
    @Test
    void testAgregarProductoMenu() {
        int precioAntes = pedido.getPrecioTotalPedido();
        assertEquals(0, precioAntes);
        
        pedido.agregarProducto(producto);
        
        int precioDespues = pedido.getPrecioTotalPedido();
        int ivaEsperado = (int)(PRECIO_NORMAL * 0.19);
        int precioTotalEsperado = PRECIO_NORMAL + ivaEsperado;
        
        assertEquals(precioTotalEsperado, precioDespues);
    }
 
    @Test
    void testAgregarProductoAjustado() {
        int precioAntes = pedido.getPrecioTotalPedido();
        assertEquals(0, precioAntes);
        
        pedido.agregarProducto(productoAjustado);
        
        int precioDespues = pedido.getPrecioTotalPedido();
        int precioNetoEsperado = PRECIO_NORMAL + PRECIO_INGREDIENTE1;
        int ivaEsperado = (int)(precioNetoEsperado * 0.19);
        int precioTotalEsperado = precioNetoEsperado + ivaEsperado;
        
        assertEquals(precioTotalEsperado, precioDespues);
    }


    
    @Test
    void testPrecioTotalAntesYDespuesDeAgregarMultiplesProductos() {
        int precioAntes = pedido.getPrecioTotalPedido();
        assertEquals(0, precioAntes);
        
        pedido.agregarProducto(producto1);
        pedido.agregarProducto(comboNuevo);
        pedido.agregarProducto(productoAjustado);
        
        int precioDespues = pedido.getPrecioTotalPedido();
        
        int precioProducto1 = PRECIO_NORMAL1 + (int)(PRECIO_NORMAL1 * 0.19);
        int precioCombo = (int)((PRECIO_NORMAL1 + PRECIO_NORMAL2) * (1 - DESCUENTO));
        int ivaCombo = (int)(precioCombo * 0.19);
        int precioTotalCombo = precioCombo + ivaCombo;
        int precioAjustadoNeto = PRECIO_NORMAL + PRECIO_INGREDIENTE1;
        int ivaAjustado = (int)(precioAjustadoNeto * 0.19);
        int precioTotalAjustado = precioAjustadoNeto + ivaAjustado;
        int precioTotalEsperado = precioProducto1 + precioTotalCombo + precioTotalAjustado;
        
        assertEquals(precioTotalEsperado, precioDespues);
    } 

    @Test
    void testGuardarFacturaExitosamente() throws IOException {
        pedido.agregarProducto(producto1);
        pedido.agregarProducto(comboNuevo);
        
        File archivoFactura = tempDir.resolve("factura_test.txt").toFile();
        
        pedido.guardarFactura(archivoFactura);
        
        assertTrue(archivoFactura.exists());
        assertTrue(archivoFactura.length() > 0);
        
        String contenido = Files.readString(archivoFactura.toPath());
        assertTrue(contenido.contains(NOMBRE_CLIENTE1));
        assertTrue(contenido.contains(DIRECCION_CLIENTE1));
        assertTrue(contenido.contains(NOMBRE_PRODUCTO1));
        assertTrue(contenido.contains(NOMBRE_COMBO));
    }
    
    @Test
    void testFacturasIndependientes() throws Exception {
        Field field = Pedido.class.getDeclaredField("numeroPedidos");
        field.setAccessible(true);
        field.setInt(null, 0);
        
        Pedido pedido1 = new Pedido(NOMBRE_CLIENTE1, DIRECCION_CLIENTE1);
        Pedido pedido2 = new Pedido(NOMBRE_CLIENTE2, DIRECCION_CLIENTE2);
        
        pedido1.agregarProducto(producto1);
        pedido2.agregarProducto(producto2);
        
        String factura1 = pedido1.generarTextoFactura();
        String factura2 = pedido2.generarTextoFactura();
        
        assertTrue(factura1.contains(NOMBRE_CLIENTE1));
        assertTrue(factura1.contains(NOMBRE_PRODUCTO1));
        assertTrue(factura1.contains(String.valueOf(PRECIO_NORMAL1)));
        
        assertTrue(factura2.contains(NOMBRE_CLIENTE2));
        assertTrue(factura2.contains(NOMBRE_PRODUCTO2));
        assertTrue(factura2.contains(String.valueOf(PRECIO_NORMAL2)));
        
        pedido1.agregarProducto(comboNuevo);
        
        String factura1Modificada = pedido1.generarTextoFactura();
        String factura2SinModificar = pedido2.generarTextoFactura();
        
        assertTrue(factura1Modificada.contains(NOMBRE_COMBO));
        assertTrue(factura1Modificada.contains(String.valueOf(comboNuevo.getPrecio())));
        assertFalse(factura2SinModificar.contains(NOMBRE_COMBO));
        
        assertEquals(factura2, factura2SinModificar);
    }

}