/**
 * @generated VGen (for ANTLR) 1.7.2
 */

package ast;

import visitor.*;

//	charTipo:tipo -> 

public class CharTipo extends AbstractTipo {

	@Override
	public Object accept(Visitor v, Object param) {
		return v.visit(this, param);
	}

	public String toString() {
		return "{CharTipo}";
	}

	@Override
	public int getSize() {
		return 1;
	}

	@Override
	public String getNombreMAPL() {
		return "byte";
	}

	@Override
	public char getSufijo() {
		return 'b';
	}
}
