package uniandes.dpoo.hamburguesas.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import uniandes.dpoo.hamburguesas.mundo.*;
import uniandes.dpoo.hamburguesas.excepciones.*;

public class RestauranteTest {

	private Restaurante restaurante;
	private static final String NOMBRE_CLIENTE1 = "Iván Martinez";
    private static final String DIRECCION_CLIENTE1 = "Calle 22 #2-80";
	
    private static final String NOMBRE_CLIENTE2 = "Juana Romero";
    private static final String DIRECCION_CLIENTE2 = "";
    
    private static final String NOMBRE_PRODUCTO = "wrap de pollo";
    private static final int PRECIO_NORMAL = 15000;
    
    public TemporaryFolder tempFolder = new TemporaryFolder();
    
    private File archivoIngredientes;
    private File archivoMenu;
    private File archivoCombos;

    
    @Before
    public void setUp() throws IOException {
       File archivoPedidos = new File("data/pedidos.txt");
    		    if (archivoPedidos.exists()) {
    		        archivoPedidos.delete();
    		    }
        restaurante = new Restaurante();
        
        archivoIngredientes = new File("data/ingredientes.txt");
        archivoMenu = new File("data/menu.txt");
        archivoCombos = new File("data/combos.txt");
    }
    
    @Test
    public void testConstructorInicializaListasVacias() {
    	restaurante = new Restaurante();
        assertTrue(restaurante.getPedidos().isEmpty());
        assertTrue(restaurante.getIngredientes().isEmpty());
        assertTrue(restaurante.getMenuBase().isEmpty());
        assertTrue(restaurante.getMenuCombos().isEmpty());
        assertNull(restaurante.getPedidoEnCurso());
    }
	
    @Test
    public void testCargarIngredientesPorSeparado() throws Exception {
        restaurante.cargarInformacionRestaurante(archivoIngredientes, null, null);
        
        assertEquals(15, restaurante.getIngredientes().size());
        assertEquals(0, restaurante.getMenuBase().size());
        assertEquals(0, restaurante.getMenuCombos().size());
    }

    @Test
    public void testCargarMenuPorSeparado() throws Exception {
        restaurante.cargarInformacionRestaurante(null, archivoMenu, null);
        
        assertEquals(0, restaurante.getIngredientes().size());
        assertEquals(22, restaurante.getMenuBase().size());
        assertEquals(0, restaurante.getMenuCombos().size());
    }

    @Test
    public void testCargarCombosPorSeparado() throws Exception {
        restaurante.cargarInformacionRestaurante(archivoIngredientes, archivoMenu, null);
        
        restaurante.cargarInformacionRestaurante(null, null, archivoCombos);
        
        assertEquals(15, restaurante.getIngredientes().size());
        assertEquals(22, restaurante.getMenuBase().size());
        assertEquals(4, restaurante.getMenuCombos().size());
    }

    @Test
    public void testCargarInformacionCompleta() throws Exception {
        restaurante.cargarInformacionRestaurante(archivoIngredientes, archivoMenu, archivoCombos);
        
        assertEquals(15, restaurante.getIngredientes().size());
        assertEquals(22, restaurante.getMenuBase().size());
        assertEquals(4, restaurante.getMenuCombos().size());
        
        assertTrue(restaurante.getIngredientes().get(0).getNombre() != null);
        assertTrue(restaurante.getMenuBase().get(0).getNombre() != null);
        assertTrue(restaurante.getMenuCombos().get(0).getNombre() != null);
    }
    
    @Test
    public void testCargarProductoRepetidoLanzaExcepcion() throws Exception {
        File tempIngredientes = new File("data/ingredientes_temp.txt");
        File tempMenu = new File("data/menu_temp.txt");
        
        try (FileWriter writer = new FileWriter(tempIngredientes)) {
            writer.write("lechuga;1000\n");
            writer.write("tomate;1000\n");
        }
        
        try (FileWriter writer = new FileWriter(tempMenu)) {
            writer.write("Hamburguesa Sencilla;10000\n");
            writer.write("Hamburguesa Sencilla;10000\n");
        }
        
        assertThrows(ProductoRepetidoException.class, () -> {
            restaurante.cargarInformacionRestaurante(tempIngredientes, tempMenu, null);
        });
        
        tempIngredientes.delete();
        tempMenu.delete();
    }
   

    @Test
    public void testCargarComboConProductoFaltanteLanzaExcepcion() throws Exception {
        File tempIngredientes = new File("data/ingredientes_temp.txt");
        File tempMenu = new File("data/menu_temp.txt");
        File tempCombos = new File("data/combos_temp.txt");
        
        try (FileWriter writer = new FileWriter(tempIngredientes)) {
            writer.write("lechuga;1000\n");
        }
        
        try (FileWriter writer = new FileWriter(tempMenu)) {
            writer.write("Papas Fritas;5000\n");
        }
        
        try (FileWriter writer = new FileWriter(tempCombos)) {
            writer.write("Combo Corral;10%;Hamburguesa Sencilla;Papas Fritas\n");
        }
        
        assertThrows(ProductoFaltanteException.class, () -> {
            restaurante.cargarInformacionRestaurante(tempIngredientes, tempMenu, tempCombos);
        });
        
        tempIngredientes.delete();
        tempMenu.delete();
        tempCombos.delete();
    }

    
    
    @Test
    public void testIniciarPedidoCorrectamente() throws YaHayUnPedidoEnCursoException {
        restaurante.iniciarPedido(NOMBRE_CLIENTE1, DIRECCION_CLIENTE1);
        assertNotNull(restaurante.getPedidoEnCurso());
        assertEquals(NOMBRE_CLIENTE1, restaurante.getPedidoEnCurso().getNombreCliente());
        assertEquals(DIRECCION_CLIENTE1, restaurante.getPedidoEnCurso().getDireccionCliente());
    }
    
    @Test
    public void testIniciarPedidoConPedidoActivoLanzaExcepcion() throws YaHayUnPedidoEnCursoException {
        restaurante.iniciarPedido(NOMBRE_CLIENTE1, DIRECCION_CLIENTE1);
        assertThrows(YaHayUnPedidoEnCursoException.class, () -> {
            restaurante.iniciarPedido(NOMBRE_CLIENTE2, DIRECCION_CLIENTE2);
        });
    }
  
    @Test
    public void testCerrarPedidoSinPedidoActivoLanzaExcepcion() {
        assertThrows(NoHayPedidoEnCursoException.class, () -> {
            restaurante.cerrarYGuardarPedido();
        });
    }
    
    @Test
    public void testCerrarPedidoGeneraFacturaYAgregaALista() throws Exception {
        Pedido.reiniciarContadorParaTests();
        
        restaurante.iniciarPedido(NOMBRE_CLIENTE1, DIRECCION_CLIENTE1);
        restaurante.getPedidoEnCurso().agregarProducto(new ProductoMenu(NOMBRE_PRODUCTO, PRECIO_NORMAL));
        restaurante.cerrarYGuardarPedido();
        
        File factura = new File("./facturas/factura_0.txt");
        assertTrue(factura.exists());
        assertNull(restaurante.getPedidoEnCurso());
        assertEquals(1, restaurante.getPedidos().size());
        factura.delete();
    }
    
    @Test
    public void testMultiplesPedidosSeAgreganALista() throws Exception {
        Pedido.reiniciarContadorParaTests();
        
        restaurante.iniciarPedido(NOMBRE_CLIENTE1, DIRECCION_CLIENTE1);
        restaurante.getPedidoEnCurso().agregarProducto(new ProductoMenu(NOMBRE_PRODUCTO, PRECIO_NORMAL));
        restaurante.cerrarYGuardarPedido();
        
        restaurante.iniciarPedido(NOMBRE_CLIENTE2, DIRECCION_CLIENTE2);
        restaurante.getPedidoEnCurso().agregarProducto(new ProductoMenu(NOMBRE_PRODUCTO, PRECIO_NORMAL - 100));
        restaurante.cerrarYGuardarPedido();
        
        assertEquals(2, restaurante.getPedidos().size());
        assertEquals(0, restaurante.getPedidos().get(0).getIdPedido());
        assertEquals(1, restaurante.getPedidos().get(1).getIdPedido());
        
        new File("./facturas/factura_0.txt").delete();
        new File("./facturas/factura_1.txt").delete();
    }
    
    @Test
    public void testPedidosNoSeSobrescriben() throws Exception {
        Pedido.reiniciarContadorParaTests();
        
        restaurante.iniciarPedido(NOMBRE_CLIENTE1, DIRECCION_CLIENTE1);
        int idPedido1 = restaurante.getPedidoEnCurso().getIdPedido();
        restaurante.getPedidoEnCurso().agregarProducto(new ProductoMenu(NOMBRE_PRODUCTO, PRECIO_NORMAL));
        restaurante.cerrarYGuardarPedido();
        
        File factura1 = new File("./facturas/factura_" + idPedido1 + ".txt");
        assertTrue(factura1.exists());
        
        restaurante.iniciarPedido(NOMBRE_CLIENTE2, DIRECCION_CLIENTE2);
        int idPedido2 = restaurante.getPedidoEnCurso().getIdPedido();
        restaurante.getPedidoEnCurso().agregarProducto(new ProductoMenu(NOMBRE_PRODUCTO, PRECIO_NORMAL - 100));
        restaurante.cerrarYGuardarPedido();
        
        File factura2 = new File("./facturas/factura_" + idPedido2 + ".txt");
        assertTrue(factura2.exists());
        assertTrue(factura1.exists());
        
        String contenido1 = java.nio.file.Files.readString(factura1.toPath());
        String contenido2 = java.nio.file.Files.readString(factura2.toPath());
        
        assertTrue(contenido1.contains(NOMBRE_CLIENTE1));
        assertTrue(contenido2.contains(NOMBRE_CLIENTE2));
        assertTrue(contenido1.contains(String.valueOf(PRECIO_NORMAL)));
        assertTrue(contenido2.contains(String.valueOf(PRECIO_NORMAL - 100)));
        
        factura1.delete();
        factura2.delete();
    }
    
    
}