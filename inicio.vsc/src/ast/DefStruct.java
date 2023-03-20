/**
 * @generated VGen (for ANTLR) 1.7.2
 */

package ast;

import java.util.*;
import org.antlr.v4.runtime.*;

import visitor.Visitor;

//	defStruct:definicion -> nombre:String  definicion:definicion*

public class DefStruct extends AbstractDefinicion {

	public DefStruct(String nombre, List<VariableStruct> varstruct) {
		this.nombre = nombre;
		this.varstruct = varstruct;

		// Lo siguiente se puede borrar si no se quiere la posicion en el fichero.
		// Obtiene la linea/columna a partir de las de los hijos.
		setPositions(varstruct);
	}

	public DefStruct(Object nombre, Object varstruct) {
		this.nombre = (nombre instanceof Token) ? ((Token) nombre).getText() : (String) nombre;
		this.varstruct = this.<VariableStruct>getAstFromContexts(varstruct);

		// Lo siguiente se puede borrar si no se quiere la posicion en el fichero.
		// Obtiene la linea/columna a partir de las de los hijos.
		setPositions(nombre, varstruct);
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public List<VariableStruct> getVariableStruct() {
		return varstruct;
	}

	public void setVariableStruct(List<VariableStruct> varstruct) {
		this.varstruct = varstruct;
	}

	@Override
	public Object accept(Visitor v, Object param) {
		return v.visit(this, param);
	}

	private String nombre;
	private List<VariableStruct> varstruct;

	public String toString() {
		return "{nombre:" + getNombre() + ", varstruct:" + getVariableStruct() + "}";
	}

}
