/**
 * @generated VGen (for ANTLR) 1.7.2
 */

package ast;

import visitor.*;

//	intTipo:tipo -> 

public class IntTipo extends AbstractTipo {

	@Override
	public Object accept(Visitor v, Object param) { 
		return v.visit(this, param);
	}


	public String toString() {
       return "{IntTipo}";
   }


	@Override
	public int getSize() {
		return 2;
	}


	@Override
	public String getNombreMAPL() {
		return "int";
	}


	@Override
	public char getSufijo() {
		return 'i';
	}
}
