/**
 * @generated VGen (for ANTLR) 1.7.2
 */

package ast;

public abstract class AbstractSentencia extends AbstractAST implements Sentencia {

	private DefFuncion defFunction;

	@Override
	public void setFuncion(DefFuncion function) {
		this.defFunction = function;

	}

	@Override
	public DefFuncion getFunction() {
		return defFunction;
	}

}
