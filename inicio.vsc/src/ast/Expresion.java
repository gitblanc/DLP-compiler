/**
 * @generated VGen (for ANTLR) 1.7.2
 */

package ast;

public interface Expresion extends AST {
	public void setTipo(Tipo tipo);

    public Tipo getTipo();

    public void setModificable(boolean modificable);

    public boolean isModificable();
}
