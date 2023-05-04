/**
 * 
 */
package recorreArbol;

import ast.Array;
import ast.ArrayTipo;
import ast.Asignacion;
import ast.Cast;
import ast.CharTipo;
import ast.DefFuncion;
import ast.DefVariable;
import ast.Definicion;
import ast.Expresion;
import ast.ExpresionAritmetica;
import ast.ExpresionDistinto;
import ast.ExpresionLlamada;
import ast.ExpresionLogica;
import ast.FuncionLlamada;
import ast.Ident;
import ast.If;
import ast.IntTipo;
import ast.LitChar;
import ast.LitEnt;
import ast.LitReal;
import ast.Parametros;
import ast.Print;
import ast.Program;
import ast.Read;
import ast.RealTipo;
import ast.Return;
import ast.Sentencia;
import ast.Struct;
import ast.StructTipo;
import ast.Variable;
import ast.VariableStruct;
import ast.While;
import visitor.DefaultVisitor;

/*
Plantilla para Visitors.
Para crear un nuevo Visitor cortar y pegar este código y ya se tendría un visitor que compila y
que al ejecutarlo recorrería todo el árbol (sin hacer nada aún en él).
Solo quedaría añadir a cada método visit aquello adicional que se quiera realizar sobre su nodo del AST.
*/

public class MyVisitorImplementation extends DefaultVisitor {
	StringBuffer buffer = new StringBuffer();

	// ---------------------------------------------------------
	// Tareas a realizar en cada método visit:
	//
	// Si en algún método visit NO SE QUIERE HACER NADA más que recorrer los hijos
	// entonces se puede
	// borrar (dicho método se heredaría de DefaultVisitor con el código de
	// recorrido).
	//
	// Lo siguiente es para cuando se quiera AÑADIR alguna funcionalidad adicional a
	// un visit:
	//
	// - El código que aparece en cada método visit es aquel que recorre los hijos.
	// Es el mismo código
	// que está implementado en el padre (DefaultVisitor). Por tanto la llamada a
	// 'super.visit' y el
	// resto del código del método hacen lo mismo (por ello 'super.visit' está
	// comentado).
	//
	// - Lo HABITUAL será borrar todo el código de recorrido dejando solo la llamada
	// a 'super.visit'. De esta
	// manera, cada método visit se puede centrar en la tarea que tiene que realizar
	// sobre su nodo del AST
	// (dejando que el padre se encargue del recorrido de los hijos).
	//
	// - La razón de que aparezca el código de recorrido de los hijos es por si se
	// necesita realizar alguna
	// tarea DURANTE el mismo (por ejemplo ir comprobando su tipo). En este caso, ya
	// se tiene implementado
	// dicho recorrido y solo habría que incrustar las acciones adicionales en el
	// mismo. En este caso,
	// es la llamada a 'super.visit' la que debería ser borrada.
	// ---------------------------------------------------------

	// class Program { List<Definicion> definicion; }
	public Object visit(Program node, Object param) {

		// super.visit(node, param);
		System.out.println("  ////////////////////");
		System.out.println(" //$---Programa---$//");
		System.out.println("////////////////////");
		System.out.println();
		if (node.getDefinicion() != null)
			for (Definicion child : node.getDefinicion())
				child.accept(this, param);

		return null;
	}

	// class DefVariable { Tipo tipo; String nombre; }
	public Object visit(DefVariable node, Object param) {

		// super.visit(node, param);

		System.out.print("var " + node.getNombre() + ":");
		if (node.getTipo() != null) {
			node.getTipo().accept(this, param);
		}
		System.out.println(";");

		return null;
	}

	// class DefStruct { String nombre; List<Definicion> definicion; }
//	public Object visit(DefStruct node, Object param) {
//		// super.visit(node, param);
//		System.out.print("struct " + node.getNombre());
//		System.out.println("{");
//		if (node.getDefinicion() != null)
//			for (Definicion child : node.getDefinicion())
//				child.accept(this, param);
//		System.out.println("}");
//		return null;
//	}

	// class DefFuncion { String nombre; List<Parametros> parametros; Tipo tipo;
	// List<DefVariable> defvariable; List<Sentencia> sentencia; }
	public Object visit(DefFuncion node, Object param) {

		// super.visit(node, param);

		System.out.print(node.getNombre() + "(");

		if (node.getParametros() != null) {
			int aux = 0;
			for (Parametros child : node.getParametros()) {
				if (aux++ != 0)
					System.out.print(", ");
				child.accept(this, param);
			}
		}
		System.out.print(")");

		if (node.getTipo() != null) {
			System.out.print(": ");
			node.getTipo().accept(this, param);
		}

		System.out.println(" {");
		if (node.getDefvariable() != null)
			for (DefVariable child : node.getDefvariable()) {
				child.accept(this, param);
			}

		if (node.getSentencia() != null)
			for (Sentencia child : node.getSentencia())
				child.accept(this, param);
		System.out.println("}");

		return null;
	}

	// class Parametros { String nombre; Tipo tipo; }
	public Object visit(Parametros node, Object param) {

		// super.visit(node, param);
		System.out.print(node.getNombre() + ":");
		if (node.getTipo() != null)
			node.getTipo().accept(this, param);

		return null;
	}

	// class VariableStruct { String nombre; Tipo tipo; }
	public Object visit(VariableStruct node, Object param) {

		// super.visit(node, param);
		System.out.print(node.getNombre() + ":");
		if (node.getTipo() != null)
			node.getTipo().accept(this, param);

		return null;
	}

	// class IntTipo { }
	public Object visit(IntTipo node, Object param) {
		System.out.print("int");
		return null;
	}

	// class RealTipo { }
	public Object visit(RealTipo node, Object param) {
		System.out.print("float");
		return null;
	}

	// class CharTipo { }
	public Object visit(CharTipo node, Object param) {
		System.out.print("char");
		return null;
	}

	// class ArrayTipo { String posicion; Tipo tipo; }
	public Object visit(ArrayTipo node, Object param) {

		// super.visit(node, param);
		System.out.print("[" + node.getPosicion() + "]");
		if (node.getTipo() != null)
			node.getTipo().accept(this, param);

		return null;
	}

	// class StructTipo { String nombre; }
	public Object visit(StructTipo node, Object param) {
		System.out.println(node.getNombre());
		return null;
	}

	// class Asignacion { Expresion izquierda; Expresion derecha; }
	public Object visit(Asignacion node, Object param) {

		// super.visit(node, param);

		if (node.getIzquierda() != null)
			node.getIzquierda().accept(this, param);

		System.out.print("=");

		if (node.getDerecha() != null)
			node.getDerecha().accept(this, param);

		System.out.println(";");

		return null;
	}

	// class Print { Expresion print; String printTipo; }
	public Object visit(Print node, Object param) {

		// super.visit(node, param);
		System.out.print("print ");
		if (node.getPrint() != null)
			node.getPrint().accept(this, param);
		System.out.println(";");
		return null;
	}

	// class Read { Expresion read; }
	public Object visit(Read node, Object param) {

		// super.visit(node, param);
		System.out.println("read ");
		if (node.getRead() != null)
			node.getRead().accept(this, param);
		System.out.println(";");
		return null;
	}

	// class FuncionLlamada { String nombre; List<Expresion> expresion; }
	public Object visit(FuncionLlamada node, Object param) {

		// super.visit(node, param);

		System.out.print(node.getNombre() + "(");

		if (node.getExpresion() != null)
			for (Expresion child : node.getExpresion())
				child.accept(this, param);

		System.out.println(");");

		return null;
	}

	// class If { Expresion condicion; List<Sentencia> if_true; List<Sentencia>
	// if_false; }
	public Object visit(If node, Object param) {

		// super.visit(node, param);
		System.out.print("if (");
		if (node.getCondicion() != null)
			node.getCondicion().accept(this, param);

		System.out.println(") {");

		if (node.getIf_true() != null)
			for (Sentencia child : node.getIf_true()) {
				System.out.print("  ");
				child.accept(this, param);
			}
		System.out.println("");
		System.out.print("}");
		if (node.getIf_false() != null) {
			System.out.println("");
			System.out.println(" else {");
			for (Sentencia child : node.getIf_false()) {
				System.out.print("  ");
				child.accept(this, param);
			}
			System.out.println("}");
		}
		return null;

	}

	// class While { Expresion condicion; List<Sentencia> sentencia; }
	public Object visit(While node, Object param) {

		// super.visit(node, param);
		System.out.print("while (");
		if (node.getCondicion() != null)
			node.getCondicion().accept(this, param);

		System.out.println(") {");

		if (node.getSentencia() != null)
			for (Sentencia child : node.getSentencia()) {
				System.out.print("  ");
				child.accept(this, param);
			}

		System.out.println("}");

		return null;
	}

	// class Return { Expresion retorno; }
	public Object visit(Return node, Object param) {

		// super.visit(node, param);
		System.out.print("return ");
		if (node.getRetorno() != null)
			node.getRetorno().accept(this, param);
		System.out.println(";");
		return null;
	}

	// class ExpresionAritmetica { Expresion izquierda; String operador; Expresion
	// derecha; }
	public Object visit(ExpresionAritmetica node, Object param) {

		// super.visit(node, param);

		if (node.getIzquierda() != null)
			node.getIzquierda().accept(this, param);
		System.out.print(" " + node.getOperador() + " ");
		if (node.getDerecha() != null)
			node.getDerecha().accept(this, param);

		return null;
	}

	// class ExpresionLogica { Expresion izquierda; String operador; Expresion
	// derecha; }
	public Object visit(ExpresionLogica node, Object param) {

		// super.visit(node, param);

		if (node.getIzquierda() != null)
			node.getIzquierda().accept(this, param);

		System.out.print(" " + node.getOperador() + " ");

		if (node.getDerecha() != null)
			node.getDerecha().accept(this, param);

		return null;
	}

	// class ExpresionDistinto { Expresion not; }
	public Object visit(ExpresionDistinto node, Object param) {

		// super.visit(node, param);
		System.out.println(" ! ");
		if (node.getHijoNot() != null)
			node.getHijoNot().accept(this, param);

		return null;
	}

	// class Variable { String nombre; }
	public Object visit(Variable node, Object param) {
		System.out.print(node.getNombre());
		return null;
	}

	// class Ident { String valor; }
	public Object visit(Ident node, Object param) {
		System.out.print(node.getValor());
		return null;
	}

	// class LitEnt { String valor; }
	public Object visit(LitEnt node, Object param) {
		System.out.print(node.getValor());
		return null;
	}

	// class LitReal { String valor; }
	public Object visit(LitReal node, Object param) {
		System.out.print(node.getValor());
		return null;
	}

	// class LitChar { String valor; }
	public Object visit(LitChar node, Object param) {
		System.out.print(node.getValor());
		return null;
	}

	// class Cast { Tipo tipo; Expresion valor; }
	public Object visit(Cast node, Object param) {

		// super.visit(node, param);
		System.out.print("<");
		if (node.getTipo() != null)
			node.getTipo().accept(this, param);
		System.out.print(">(");
		if (node.getValor() != null)
			node.getValor().accept(this, param);
		System.out.println(")");
		return null;
	}

	// class Array { Expresion nombre; Expresion valor; }
	public Object visit(Array node, Object param) {

		// super.visit(node, param);

		if (node.getNombre() != null)
			node.getNombre().accept(this, param);
		System.out.print("[");
		if (node.getValor() != null)
			node.getValor().accept(this, param);
		System.out.print("]");
		return null;
	}

	// class Struct { Expresion nombre; String campos; }
	public Object visit(Struct node, Object param) {

		// super.visit(node, param);
		if (node.getNombre() != null) {
			node.getNombre().accept(this, param);
			System.out.println(".");
		}
		return null;
	}

	// class ExpresionLlamada { String nombre; List<Expresion> expresion; }
	public Object visit(ExpresionLlamada node, Object param) {

		// super.visit(node, param);
		System.out.print(node.getNombre() + "(");
		if (node.getExpresion() != null)
			for (Expresion child : node.getExpresion())
				child.accept(this, param);
		System.out.print(")");
		return null;
	}
}
