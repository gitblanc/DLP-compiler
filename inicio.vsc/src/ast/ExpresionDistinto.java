/**
 * @generated VGen (for ANTLR) 1.7.2
 */

package ast;

import visitor.Visitor;

//	expresionDistinto:expresion -> not:expresion

public class ExpresionDistinto extends AbstractExpresion {

	public ExpresionDistinto(Expresion not) {
		this.hijoNot = not;

       // Lo siguiente se puede borrar si no se quiere la posicion en el fichero.
       // Obtiene la linea/columna a partir de las de los hijos.
       setPositions(not);
	}

	public ExpresionDistinto(Object not) {
		this.hijoNot = (Expresion) getAST(not);

       // Lo siguiente se puede borrar si no se quiere la posicion en el fichero.
       // Obtiene la linea/columna a partir de las de los hijos.
       setPositions(not);
	}

	public Expresion getHijoNot() {
		return hijoNot;
	}
	public void setNot(Expresion not) {
		this.hijoNot = not;
	}

	@Override
	public Object accept(Visitor v, Object param) { 
		return v.visit(this, param);
	}

	private Expresion hijoNot;

	public String toString() {
       return "{hijoNot:" + getHijoNot() + "}";
   }
}
