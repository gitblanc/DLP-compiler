/**
 * @generated VGen (for ANTLR) 1.7.2
 */

package ast;

import java.util.*;
import visitor.*;

//	program -> definicion:definicion*

public class Program extends AbstractAST  {

	public Program(List<Definicion> definicion) {
		this.definicion = definicion;

       // Lo siguiente se puede borrar si no se quiere la posicion en el fichero.
       // Obtiene la linea/columna a partir de las de los hijos.
       setPositions(definicion);
	}

	public Program(Object definicion) {
		this.definicion = this.<Definicion>getAstFromContexts(definicion);

       // Lo siguiente se puede borrar si no se quiere la posicion en el fichero.
       // Obtiene la linea/columna a partir de las de los hijos.
       setPositions(definicion);
	}

	public List<Definicion> getDefinicion() {
		return definicion;
	}
	public void setDefinicion(List<Definicion> definicion) {
		this.definicion = definicion;
	}

	@Override
	public Object accept(Visitor v, Object param) { 
		return v.visit(this, param);
	}

	private List<Definicion> definicion;

	public String toString() {
       return "{definicion:" + getDefinicion() + "}";
   }
}
