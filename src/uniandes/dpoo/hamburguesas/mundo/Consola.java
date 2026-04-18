package uniandes.dpoo.hamburguesas.mundo;

import java.io.File;
import java.util.Scanner;
import java.io.IOException;
import java.util.ArrayList;


import uniandes.dpoo.hamburguesas.excepciones.HamburguesaException;
import uniandes.dpoo.hamburguesas.excepciones.NoHayPedidoEnCursoException;
import uniandes.dpoo.hamburguesas.excepciones.YaHayUnPedidoEnCursoException;

public class Consola{
	Restaurante miRestaurante = new Restaurante();
	Scanner lector = new Scanner(System.in);

public void cargarDatos() throws NumberFormatException, HamburguesaException, IOException {
	    
	    
	    File archivoIngredientes = new File("data/ingredientes.txt");
	    File archivoMenu = new File("data/menu.txt");
	    File archivoCombos = new File("data/combos.txt");

	    miRestaurante.cargarInformacionRestaurante(archivoIngredientes, archivoMenu, archivoCombos);
	}


// Primera Opción
public void mostrarMenu() {
	ArrayList<ProductoMenu> menu = miRestaurante.getMenuBase();
	ArrayList<Combo> combos = miRestaurante.getMenuCombos();
	
	int opcion = 0;
	
	do {
		System.out.println("\n--- ¿Qué Menú Quiere Consultar? ---");
		System.out.println("0. Menú Base");
		System.out.println("1. Menú Combos");
		System.out.println("2. Salir");
		System.out.print("Seleccione una opción: ");

		try {
			opcion = lector.nextInt();
			lector.nextLine();

			switch (opcion) {
			case 0:
				mostrarMenuBase(menu);
				break;
			case 1:
				mostrarMenuCombos(combos);
				break;
			case 2:
				System.out.println("Saliendo del sistema");
				return;
			default:
				System.out.println("Opción no válida. Intente de nuevo.");
			}
		} catch (Exception e) {
			System.out.println(" Ingrese un número válido.");
			lector.nextLine();
			opcion = 0;
		}

	} while (opcion != 2);

	lector.close();
}

public void mostrarMenuBase(ArrayList<ProductoMenu> menu) {
    System.out.println("\n========================================");
    
    for (int i = 0; i < menu.size(); i++) {
        ProductoMenu producto = menu.get(i);
        System.out.println("[" + i + "] " + producto.getNombre() + " - $" + producto.getPrecio());
        System.out.println("----------------------------------------");
    }
}

public void mostrarMenuCombos(ArrayList<Combo> combos) {
    System.out.println("\n========================================");
    for (int i = 0; i < combos.size(); i++) {
        Combo combo = combos.get(i);
        System.out.println("[" + i + "] " + combo.getNombre());
        System.out.println(combo.generarTextoFactura());
        System.out.println("----------------------------------------");
    }
}


//Segunda Opción
public void iniciarNuevoPedido() throws YaHayUnPedidoEnCursoException {

    System.out.print("Ingrese el nombre del cliente: ");
    String nombre = lector.nextLine();
    
    System.out.print("Ingrese la dirección: ");
    String direccion = lector.nextLine();

    miRestaurante.iniciarPedido(nombre, direccion);
    
    System.out.println("Pedido iniciado con éxito para: " + nombre);
    
}

//Tercera Opción
public void agregarElemento() throws NoHayPedidoEnCursoException, HamburguesaException {
    System.out.println("\n--- Agregar Elementos al Pedido ---");
    System.out.println("========================================");

    if (miRestaurante.getPedidoEnCurso() == null) {
        throw new NoHayPedidoEnCursoException();
    }

    System.out.println("¿Qué tipo de producto desea agregar?");
    System.out.println("0. Producto del Menú Base");
    System.out.println("1. Combo");
    System.out.print("Seleccione una opción: ");

    int tipo = lector.nextInt();
    lector.nextLine();

    Producto productoElegido = null;

    if (tipo == 0) {
        productoElegido = agregarDelMenu();
    } else if (tipo == 1) {
        productoElegido = agregarDelCombo();
    }

    if (productoElegido != null) {
        miRestaurante.getPedidoEnCurso().agregarProducto(productoElegido);
        System.out.println("Se agregó correctamente: " + productoElegido.getNombre());
    } else {
        System.out.println("Selección no válida o cancelada.");
    }
}

public Producto agregarDelMenu() {
    ArrayList<ProductoMenu> base = miRestaurante.getMenuBase();
    mostrarMenuBase(base);
    System.out.print("Ingrese el número del producto: ");
    int indice = lector.nextInt();
    lector.nextLine();

    if (indice >= 0 && indice < base.size()) {
        ProductoMenu baseElegido = base.get(indice);
        System.out.print("¿Desea ajustar este producto? (s/n): ");
        String ajustar = lector.nextLine();

        if (ajustar.equalsIgnoreCase("s")) {
            return manejarAjustes(baseElegido);
        }
        return baseElegido;
    }
    return null;
}

public Producto manejarAjustes(ProductoMenu baseElegido) {
    ProductoAjustado ajustado = new ProductoAjustado(baseElegido);
    int opcionAdj;

    do {
        System.out.println("\n--- Modificando: " + baseElegido.getNombre() + " ---");
        System.out.println("0. Agregar ingrediente");
        System.out.println("1. Eliminar ingrediente");
        System.out.println("2. Terminar ajustes");
        System.out.print("Seleccione una opción: ");
        
        opcionAdj = lector.nextInt();
        lector.nextLine();
        
        if (opcionAdj == 0) {
            ArrayList<Ingrediente> ingredientesDisponibles = miRestaurante.getIngredientes();
            
            System.out.println("\nLista de Ingredientes disponibles:");
            for (int i = 0; i < ingredientesDisponibles.size(); i++) {
                System.out.println(i + ". " + ingredientesDisponibles.get(i).getNombre() + " ($" + ingredientesDisponibles.get(i).getCostoAdicional() + ")");
            }
            
            System.out.print("Seleccione el número del ingrediente: ");
            int idx = lector.nextInt();
            lector.nextLine();

            if (idx >= 0 && idx < ingredientesDisponibles.size()) {
                Ingrediente elegidoAgregar = ingredientesDisponibles.get(idx);
                ajustado.agregarIngrediente(elegidoAgregar);
                System.out.println("Agregado: " + elegidoAgregar.getNombre());
            } else {
                System.out.println("Índice no válido.");
            }
        }
        
        if (opcionAdj == 1) {
            System.out.println("\nLista de Ingredientes del producto base:");
            ArrayList<Ingrediente> ingredientesActuales = ajustado.getAgregados();
            for (int i = 0; i < ingredientesActuales.size(); i++) {
                System.out.println(i + ". " + ingredientesActuales.get(i).getNombre());
            }
            
            System.out.print("Seleccione el número del ingrediente a eliminar: ");
            int idxEliminar = lector.nextInt();
            lector.nextLine();
            
            if (idxEliminar >= 0 && idxEliminar < ingredientesActuales.size()) {
                Ingrediente elegidoEliminar = ingredientesActuales.get(idxEliminar);
                ajustado.eliminarIngrediente(elegidoEliminar);
                System.out.println("Eliminado: " + elegidoEliminar.getNombre());
            } else {
                System.out.println("Índice no válido.");
            }
        }
        
        
    } while (opcionAdj != 2);

    return ajustado;
}

public Producto agregarDelCombo() {
    ArrayList<Combo> combos = miRestaurante.getMenuCombos();
    mostrarMenuCombos(combos);
    System.out.print("Ingrese el número del combo: ");
    int indice = lector.nextInt();
    lector.nextLine();

    if (indice >= 0 && indice < combos.size()) {
        return combos.get(indice);
    }
    return null;
}

//Opción 3 
public void cerrarPedido() {
 System.out.println("\n============ Cerrando Pedido y Generando Factura ========");
 
 try {
     miRestaurante.cerrarYGuardarPedido();
     System.out.println("Pedido cerrado con éxito. La factura ha sido generada. " );
     System.out.println(miRestaurante.getPedidos().getLast().getIdPedido());
 } catch (NoHayPedidoEnCursoException e) {
     System.err.println(" Error: " + e.getMessage());
 } catch (IOException e) {
     System.err.println("Error de archivo: No se pudo guardar la factura. Revisa la carpeta 'facturas'.");
 }
}

//Opción 4
public void consultarPedido() {
    System.out.println("\n========================================");
    System.out.print("Ingrese el ID del pedido: ");
    int ID = lector.nextInt();
    lector.nextLine(); 

    ArrayList<Pedido> pedidos = miRestaurante.getPedidos();
    boolean encontrado = false; 

    for (int i = 0; i < pedidos.size(); i++) {
        if (pedidos.get(i).getIdPedido() == ID) {
            String factura = pedidos.get(i).generarTextoFactura();
            System.out.print(factura);
            encontrado = true;
        }
    }

    if (!encontrado) {
        System.out.println("No se encontró ningún pedido con el ID: " + ID);
    }

}

public void guardarDatos() {
	
}

public static void main(String[] args) throws NumberFormatException, HamburguesaException, IOException {
		Consola consola = new Consola();
		Scanner lectorMenu = new Scanner(System.in);
		
		int opcion = 0;

		System.out.println("BIENVENIDO A BRUCH & BURGUERS");
		consola.cargarDatos(); // Cargar la info del restaurante 
		
		do {
			System.out.println("\n--- MENÚ PRINCIPAL ---");
			System.out.println("0. Mostrar Menú");
			System.out.println("1. Iniciar un Nuevo Pedido");
			System.out.println("2. Agregar un Elemento a un Pedido");
			System.out.println("3. Cerrar un Pedido y Guardar Factura");
			System.out.println("4. Consultar la Información de un Pedido por ID");
			System.out.println("5. Salir");
			System.out.print("Seleccione una opción: ");

			try {
				opcion = lectorMenu.nextInt();
				lectorMenu.nextLine();

				switch (opcion) {
				case 0:
					consola.mostrarMenu();
					break;
				case 1:
					consola.iniciarNuevoPedido();
					break;
				case 2:
					consola.agregarElemento();
					break;
				case 3:
					consola.cerrarPedido();
					break;
				case 4:
					consola.consultarPedido();
					break;
				case 5:
					consola.guardarDatos(); 
					System.out.println("Saliendo del sistema");
					return;
				default:
					System.out.println("Opción no válida. Intente de nuevo.");
				}
			} catch (Exception e) {
				System.out.println(" Ingrese un número válido.");
				lectorMenu.nextLine();
				opcion = 0;
			}

		} while (opcion != 5);

		lectorMenu.close();
	}
}
