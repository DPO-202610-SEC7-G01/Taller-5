package uniandes.dpoo.hamburguesas.mundo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import uniandes.dpoo.hamburguesas.excepciones.HamburguesaException;
import uniandes.dpoo.hamburguesas.excepciones.IngredienteRepetidoException;
import uniandes.dpoo.hamburguesas.excepciones.NoHayPedidoEnCursoException;
import uniandes.dpoo.hamburguesas.excepciones.ProductoFaltanteException;
import uniandes.dpoo.hamburguesas.excepciones.ProductoRepetidoException;
import uniandes.dpoo.hamburguesas.excepciones.YaHayUnPedidoEnCursoException;

public class Restaurante
{
    private static final String CARPETA_FACTURAS = "./facturas/";
    private static final String PREFIJO_FACTURAS = "factura_";
    private static final String ARCHIVO_PEDIDOS = "data/pedidos.txt";

    private ArrayList<Pedido> pedidos;
    private ArrayList<Ingrediente> ingredientes;
    private ArrayList<ProductoMenu> menuBase;
    private ArrayList<Combo> menuCombos;
    private Pedido pedidoEnCurso;

    public Restaurante()
    {
        pedidos = new ArrayList<Pedido>();
        ingredientes = new ArrayList<Ingrediente>();
        menuBase = new ArrayList<ProductoMenu>();
        menuCombos = new ArrayList<Combo>();
        cargarPedidos();
    }

    private void cargarPedidos()
    {
        File archivo = new File(ARCHIVO_PEDIDOS);
        if (!archivo.exists()) {
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(";", 4);
                if (partes.length == 4) {
                    int id = Integer.parseInt(partes[0]);
                    String nombre = partes[1];
                    String direccion = partes[2];
                    String productosStr = partes[3];
                    
                    Pedido pedido = new Pedido(nombre, direccion);
                    
                    java.lang.reflect.Field fieldId = Pedido.class.getDeclaredField("idPedido");
                    fieldId.setAccessible(true);
                    fieldId.setInt(pedido, id);
                    
                    String[] productosArray = productosStr.split("\\|");
                    for (String prodStr : productosArray) {
                        if (prodStr.isEmpty()) continue;
                        String[] prodData = prodStr.split(";");
                        if (prodData.length >= 2) {
                            String tipo = prodData[0];
                            String nombreProducto = prodData[1];
                            
                            if (tipo.equals("MENU")) {
                                for (ProductoMenu pm : menuBase) {
                                    if (pm.getNombre().equals(nombreProducto)) {
                                        pedido.agregarProducto(pm);
                                        break;
                                    }
                                }
                            } else if (tipo.equals("COMBO")) {
                                for (Combo c : menuCombos) {
                                    if (c.getNombre().equals(nombreProducto)) {
                                        pedido.agregarProducto(c);
                                        break;
                                    }
                                }
                            } else if (tipo.equals("AJUSTADO")) {
                                for (ProductoMenu pm : menuBase) {
                                    if (pm.getNombre().equals(nombreProducto)) {
                                        ProductoAjustado ajustado = new ProductoAjustado(pm);
                                        pedido.agregarProducto(ajustado);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    
                    pedidos.add(pedido);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void guardarPedidos()
    {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARCHIVO_PEDIDOS))) {
            for (Pedido pedido : pedidos) {
                StringBuilder productosStr = new StringBuilder();
                
                java.lang.reflect.Field fieldProductos = Pedido.class.getDeclaredField("productos");
                fieldProductos.setAccessible(true);
                @SuppressWarnings("unchecked")
                ArrayList<Producto> productos = (ArrayList<Producto>) fieldProductos.get(pedido);
                
                for (Producto p : productos) {
                    if (p instanceof ProductoMenu) {
                        productosStr.append("MENU;").append(p.getNombre()).append("|");
                    } else if (p instanceof Combo) {
                        productosStr.append("COMBO;").append(p.getNombre()).append("|");
                    } else if (p instanceof ProductoAjustado) {
                        productosStr.append("AJUSTADO;").append(p.getNombre()).append("|");
                    }
                }
                
                writer.println(pedido.getIdPedido() + ";" + pedido.getNombreCliente() + ";" + 
                              pedido.getDireccionCliente() + ";" + productosStr.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void iniciarPedido(String nombreCliente, String direccionCliente) throws YaHayUnPedidoEnCursoException
    {
        if (pedidoEnCurso != null)
            throw new YaHayUnPedidoEnCursoException(pedidoEnCurso.getNombreCliente(), nombreCliente);

        pedidoEnCurso = new Pedido(nombreCliente, direccionCliente);
    }

    public void cerrarYGuardarPedido() throws NoHayPedidoEnCursoException, IOException
    {
        if (pedidoEnCurso == null)
            throw new NoHayPedidoEnCursoException();

        String nombreArchivo = PREFIJO_FACTURAS + pedidoEnCurso.getIdPedido() + ".txt";
        pedidoEnCurso.guardarFactura(new File(CARPETA_FACTURAS + nombreArchivo));
        pedidos.add(pedidoEnCurso);
        guardarPedidos();
        pedidoEnCurso = null;
    }

    public Pedido getPedidoEnCurso()
    {
        return pedidoEnCurso;
    }

    public ArrayList<Pedido> getPedidos()
    {
        return pedidos;
    }

    public ArrayList<ProductoMenu> getMenuBase()
    {
        return menuBase;
    }

    public ArrayList<Combo> getMenuCombos()
    {
        return menuCombos;
    }

    public ArrayList<Ingrediente> getIngredientes()
    {
        return ingredientes;
    }

    public void cargarInformacionRestaurante(File archivoIngredientes, File archivoMenu, File archivoCombos) throws HamburguesaException, NumberFormatException, IOException
    {
        if (archivoIngredientes != null) {
            cargarIngredientes(archivoIngredientes);
        }
        if (archivoMenu != null) {
            cargarMenu(archivoMenu);
        }
        if (archivoCombos != null) {
            cargarCombos(archivoCombos);
        }
    }

    private void cargarIngredientes(File archivoIngredientes) throws IngredienteRepetidoException, IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader(archivoIngredientes));
        try
        {
            String linea = reader.readLine();
            while (linea != null)
            {
                if (!linea.isEmpty())
                {
                    String[] ingredientesStr = linea.split(";");
                    String nombreIngrediente = ingredientesStr[0];
                    int costoIngrediente = Integer.parseInt(ingredientesStr[1]);
                    Ingrediente ingrediente = new Ingrediente(nombreIngrediente, costoIngrediente);

                    for (Ingrediente i : this.ingredientes)
                    {
                        if (i.getNombre().equals(nombreIngrediente))
                        {
                            throw new IngredienteRepetidoException(nombreIngrediente);
                        }
                    }
                    this.ingredientes.add(ingrediente);
                }
                linea = reader.readLine();
            }
        }
        finally
        {
            reader.close();
        }
    }

    private void cargarMenu(File archivoMenu) throws ProductoRepetidoException, IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader(archivoMenu));
        try
        {
            String linea = reader.readLine();
            while (linea != null)
            {
                if (!linea.isEmpty())
                {
                    String[] productoStr = linea.split(";");
                    String nombreProducto = productoStr[0];
                    int costoProducto = Integer.parseInt(productoStr[1]);
                    ProductoMenu producto = new ProductoMenu(nombreProducto, costoProducto);

                    for (ProductoMenu prod : this.menuBase)
                    {
                        if (prod.getNombre().equals(nombreProducto))
                        {
                            throw new ProductoRepetidoException(nombreProducto);
                        }
                    }
                    this.menuBase.add(producto);
                }
                linea = reader.readLine();
            }
        }
        finally
        {
            reader.close();
        }
    }

    private void cargarCombos(File archivoCombos) throws ProductoRepetidoException, ProductoFaltanteException, IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader(archivoCombos));
        try
        {
            String linea = reader.readLine();
            while (linea != null)
            {
                if (!linea.isEmpty())
                {
                    String[] comboStr = linea.split(";");
                    String nombreCombo = comboStr[0];
                    double descuento = Double.parseDouble(comboStr[1].replace("%", "")) / 100;

                    for (Combo c : this.menuCombos)
                    {
                        if (c.getNombre().equals(nombreCombo))
                        {
                            throw new ProductoRepetidoException(nombreCombo);
                        }
                    }

                    ArrayList<ProductoMenu> itemsCombo = new ArrayList<>();
                    for (int i = 2; i < comboStr.length; i++)
                    {
                        String nombreProducto = comboStr[i];
                        ProductoMenu productoItem = null;

                        for (ProductoMenu prod : this.menuBase)
                        {
                            if (prod.getNombre().equals(nombreProducto))
                            {
                                productoItem = prod;
                                break;
                            }
                        }

                        if (productoItem == null)
                        {
                            throw new ProductoFaltanteException(nombreProducto);
                        }

                        itemsCombo.add(productoItem);
                    }

                    Combo combo = new Combo(nombreCombo, descuento, itemsCombo);
                    this.menuCombos.add(combo);
                }
                linea = reader.readLine();
            }
        }
        finally
        {
            reader.close();
        }
    }
}