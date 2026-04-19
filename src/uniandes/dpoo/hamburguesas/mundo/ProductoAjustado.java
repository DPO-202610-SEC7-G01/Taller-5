package uniandes.dpoo.hamburguesas.mundo;

import java.util.ArrayList;

/**
 * Un producto ajustado es un producto para el cual el cliente solicitó alguna modificación.
 */
public class ProductoAjustado implements Producto
{
    /**
     * El producto base que el cliente sobre el cual el cliente quiere hacer ajustes
     */
    private ProductoMenu productoBase;

    /**
     * La lista de ingrediente que el usuario quiere agregar. El mismo ingrediente puede aparecer varias veces.
     */
    private ArrayList<Ingrediente> agregados;

    /**
     * La lista de ingrediente que el usuario quiere eliminar.
     */
    private ArrayList<Ingrediente> eliminados;

    /**
     * Construye un nuevo producto ajustado a partir del producto base y sin modificaciones
     * @param productoBase El producto base que se va a ajustar
     */
    public ProductoAjustado( ProductoMenu productoBase )
    {
        this.productoBase = productoBase;
        agregados = new ArrayList<Ingrediente>( );
        eliminados = new ArrayList<Ingrediente>( );
    }
    
    //Métodos Adicionales
    public void agregarIngrediente(Ingrediente ingrediente) {
        boolean yaExiste = false;
        for (Ingrediente ing : this.agregados) {
            if (ing.getNombre().equals(ingrediente.getNombre())) {
                yaExiste = true;
                break;
            }
        }
        if (!yaExiste) {
            this.agregados.add(ingrediente);
        }
        
        for (int i = 0; i < this.eliminados.size(); i++) {
            if (this.eliminados.get(i).getNombre().equals(ingrediente.getNombre())) {
                this.eliminados.remove(i);
                break;
            }
        }
    }

    public void eliminarIngrediente(Ingrediente ingrediente) {
        boolean existeEnAgregados = false;
        for (Ingrediente ing : this.agregados) {
            if (ing.getNombre().equals(ingrediente.getNombre())) {
                existeEnAgregados = true;
                break;
            }
        }
        
        if (!existeEnAgregados) {
            boolean yaEstaEliminado = false;
            for (Ingrediente ing : this.eliminados) {
                if (ing.getNombre().equals(ingrediente.getNombre())) {
                    yaEstaEliminado = true;
                    break;
                }
            }
            if (!yaEstaEliminado) {
                throw new IllegalArgumentException("El ingrediente " + ingrediente.getNombre() + " no está en el producto para eliminar");
            }
            return;
        }
        
        boolean yaExisteEnEliminados = false;
        for (Ingrediente ing : this.eliminados) {
            if (ing.getNombre().equals(ingrediente.getNombre())) {
                yaExisteEnEliminados = true;
                break;
            }
        }
        
        if (!yaExisteEnEliminados) {
            this.eliminados.add(ingrediente);
        }
        
        for (int i = 0; i < this.agregados.size(); i++) {
            if (this.agregados.get(i).getNombre().equals(ingrediente.getNombre())) {
                this.agregados.remove(i);
                break;
            }
        }
    }
    
    public ArrayList<Ingrediente> getAgregados(){
    	return agregados;
    }
    
    public ArrayList<Ingrediente> getEliminados() {
    	return eliminados;
    }
    
    @Override
    public String getNombre( )
    {
        return productoBase.getNombre( );
    }

    /**
     * Retorna el precio del producto ajustado, que debe ser igual al del producto base, sumándole el precio de los ingredientes adicionales.
     */
    @Override
    public int getPrecio()    {
        int precioTotal = productoBase.getPrecio();
        
        // Sumar el costo de los ingredientes agregados
        for (Ingrediente ing : agregados) {
            precioTotal += ing.getCostoAdicional();
        }
         return precioTotal;
    }
    
    /**
     * Genera el texto que debe aparecer en la factura.
     * 
     * El texto incluye el producto base, los ingredientes adicionales con su costo, los ingredientes eliminados, y el precio total
     */
    @Override
    public String generarTextoFactura( )
    {
        StringBuffer sb = new StringBuffer( );
        sb.append( productoBase.getNombre() );
        for( Ingrediente ing : agregados )
        {
            sb.append( "    +" + ing.getNombre( ) );
            sb.append( "                " + ing.getCostoAdicional( ) );
        }
        for( Ingrediente ing : eliminados )
        {
            sb.append( "    -" + ing.getNombre( ) );
        }

        sb.append( "            " + productoBase.getPrecio( ) + "\n" );

        return sb.toString( );
    }


}
