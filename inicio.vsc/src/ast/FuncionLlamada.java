/**
 * @generated VGen (for ANTLR) 1.7.2
 */

package ast;

import java.util.*;
import org.antlr.v4.runtime.*;

import visitor.*;

//	funcionLlamada:sentencia -> nombre:String  expresion:expresion*

public class FuncionLlamada extends AbstractSentencia {

	public FuncionLlamada(String nombre, List<Expresion> expresion) {
		this.nombre = nombre;
		this.expresion = expresion;

		// Lo siguiente se puede borrar si no se quiere la posicion en el fichero.
		// Obtiene la linea/columna a partir de las de los hijos.
		setPositions(expresion);
	}

	public FuncionLlamada(Object nombre, Object expresion) {
		this.nombre = (nombre instanceof Token) ? ((Token) nombre).getText() : (String) nombre;
		this.expresion = this.<Expresion>getAstFromContexts(expresion);

		// Lo siguiente se puede borrar si no se quiere la posicion en el fichero.
		// Obtiene la linea/columna a partir de las de los hijos.
		setPositions(nombre, expresion);
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public List<Expresion> getExpresion() {
		return expresion;
	}

	public void setExpresion(List<Expresion> expresion) {
		this.expresion = expresion;
	}

	@Override
	public Object accept(Visitor v, Object param) {
		return v.visit(this, param);
	}

	// Para el enlazado
	public DefFuncion getDefinicion() {
		return this.definicion;
	}

	public void setDefinicion(DefFuncion definition) {
		this.definicion = definition;
	}

	private DefFuncion definicion;
	// Fin enlazado

	private String nombre;
	private List<Expresion> expresion;

	public String toString() {
		return "{nombre:" + getNombre() + ", expresion:" + getExpresion() + "}";
	}
}
