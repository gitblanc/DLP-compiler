/**
 * @generated VGen (for ANTLR) 1.7.2
 */

package visitor;

import ast.*;

public interface Visitor {
	public Object visit(Program node, Object param);

	public Object visit(DefVariable node, Object param);

	public Object visit(DefStruct node, Object param);

	public Object visit(DefFuncion node, Object param);

	public Object visit(Parametros node, Object param);

	public Object visit(VariableStruct node, Object param);

	public Object visit(IntTipo node, Object param);

	public Object visit(RealTipo node, Object param);

	public Object visit(CharTipo node, Object param);

	public Object visit(ArrayTipo node, Object param);

	public Object visit(StructTipo node, Object param);

	public Object visit(Asignacion node, Object param);

	public Object visit(Print node, Object param);

	public Object visit(Read node, Object param);

	public Object visit(FuncionLlamada node, Object param);

	public Object visit(If node, Object param);

	public Object visit(While node, Object param);

	public Object visit(Return node, Object param);

	public Object visit(ExpresionAritmetica node, Object param);

	public Object visit(ExpresionLogica node, Object param);

	public Object visit(ExpresionLogicaAndOr node, Object param);

	public Object visit(ExpresionDistinto node, Object param);

	public Object visit(Variable node, Object param);

	public Object visit(Ident node, Object param);

	public Object visit(LitEnt node, Object param);

	public Object visit(LitReal node, Object param);

	public Object visit(LitChar node, Object param);

	public Object visit(Cast node, Object param);

	public Object visit(Array node, Object param);

	public Object visit(Struct node, Object param);

	public Object visit(ExpresionLlamada node, Object param);
}
