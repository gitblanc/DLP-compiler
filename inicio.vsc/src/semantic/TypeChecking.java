/**
 * Tutorial de Diseño de Lenguajes de Programación
 * @author Raúl Izquierdo
 */

package semantic;

import ast.*;
import main.*;
import visitor.*;

public class TypeChecking extends DefaultVisitor {

	public TypeChecking(ErrorManager errorManager) {
		this.errorManager = errorManager;
	}

	// # ----------------------------------------------------------
	/*
	 * Poner aquí los visit.
	 *
	 * Si se ha usado VGen, solo hay que copiarlos de la clase
	 * 'visitor/_PlantillaParaVisitors.txt'.
	 */

//	class DefFuncion { String nombre;  List<Parametros> parametros;  Tipo tipo;  List<DefVariable> defvariable;  List<Sentencia> sentencia; }
	public Object visit(DefFuncion node, Object param) {
		for (Sentencia child : node.getSentencia()) {
			child.setFuncion(node);
		}

		// Comprobamos que el tipo de los parámetros es simple
		if (node.getParametros() != null) {
			for (Parametros child : node.getParametros()) {
				child.accept(this, param);
				predicado(esTipoSimple(child.getTipo()), "Error: Los parámetros deben ser de tipo primitivo.", node);
			}
		}

		// Comprobamos que el tipo de retorno de la función sea de tipo simple
		if (node.getTipo() != null) {
			node.getTipo().accept(this, param);
			predicado(esTipoSimple(node.getTipo()), "Error: El tipo de retorno de la función debe ser de tipo simple",
					node);
		}

		visitChildren(node.getDefvariable(), param);
		visitChildren(node.getSentencia(), param);

		return null;
	}

//	class Asignacion { Expresion izquierda;  Expresion derecha; }
	public Object visit(Asignacion node, Object param) {
		if (node.getIzquierda() != null)
			node.getIzquierda().accept(this, param);

		if (node.getDerecha() != null)
			node.getDerecha().accept(this, param);

		predicado(mismoTipo(node.getIzquierda().getTipo(), node.getDerecha().getTipo()),
				"Error: no se puede asignar (expresiones son de tipos distintos)", node);
		predicado(esTipoSimple(node.getIzquierda().getTipo()),
				"Error: no se puede asignar (la expresion de la izquierda no es de tipo primitivo)", node);
		predicado(node.getIzquierda().isModificable(),
				"Error: no se puede asignar (la expresion de la izquierda no es modificable)", node);

		return null;
	}

//	class Print { Expresion print;  String printTipo; }
	public Object visit(Print node, Object param) {
		if (node.getPrint() != null)
			node.getPrint().accept(this, param);

		predicado(esTipoSimple(node.getPrint().getTipo()), "Error: el print debe ser de tipo primitivo", node);

		return null;
	}

//	class Read { Expresion read; }
	public Object visit(Read node, Object param) {
		if (node.getRead() != null)
			node.getRead().accept(this, param);

		predicado(esTipoSimple(node.getRead().getTipo()), "Error: el read debe ser de tipo simple", node);
		predicado(node.getRead().isModificable(), "Error: el read debe ser modificable", node);

		return null;
	}

//	class FuncionLlamada { String nombre;  List<Expresion> expresion; }
	public Object visit(FuncionLlamada node, Object param) {
		visitChildren(node.getExpresion(), param);

		// Comprobamos que coincide el número de parámetros
		predicado(node.getExpresion().size() == node.getDefinicion().getParametros().size(),
				"Error: número de argumentos incorrecto. Se esperaban " + node.getDefinicion().getParametros().size()
						+ " argumentos",
				node);

		// Comprueba que el tipo de los parámetros coincide
		if (node.getExpresion().size() == node.getFunction().getParametros().size()) {
			for (int i = 0; i < node.getExpresion().size(); i++) {
				predicado(
						mismoTipo(node.getExpresion().get(i).getTipo(),
								node.getFunction().getParametros().get(i).getTipo()),
						"Error: el tipo de los parámetros no coincide", node);
			}
		}

		return null;
	}

//	class If { Expresion condicion;  List<Sentencia> if_true;  List<Sentencia> if_false; }
	public Object visit(If node, Object param) {
		if (node.getCondicion() != null)
			node.getCondicion().accept(this, param);

		predicado(node.getCondicion().getTipo() instanceof IntTipo,
				"Error: la condición del if debe ser de tipo entero", node);

		if (node.getIf_true() != null) {
			for (Sentencia child : node.getIf_true()) {
				child.setFuncion(node.getFunction());
				child.accept(this, param);
			}
		}

		if (node.getIf_false() != null) {
			for (Sentencia child : node.getIf_false()) {
				child.setFuncion(node.getFunction());
				child.accept(this, param);
			}
		}

		return null;
	}

//	class While { Expresion condicion;  List<Sentencia> sentencia; }
	public Object visit(While node, Object param) {
		if (node.getCondicion() != null)
			node.getCondicion().accept(this, param);

		predicado(node.getCondicion().getTipo() instanceof IntTipo,
				"Error: la condición del while debe ser de tipo entero", node);

		if (node.getSentencia() != null) {
			for (Sentencia child : node.getSentencia()) {
				child.setFuncion(node.getFunction());
				child.accept(this, param);
			}
		}

		return null;
	}

//	class Return { Expresion retorno; }
	public Object visit(Return node, Object param) {
		if (node.getRetorno() != null)
			node.getRetorno().accept(this, param);

		if (node.getFunction().getTipo() == null) {
			predicado(node.getRetorno() == null, "Error: el return no puede tener expresiones tipo void/null", node);
		} else {
			predicado(node.getRetorno() != null, "Error: se necesita un valor de retorno", node);

			if (node.getRetorno() != null) {
				predicado(mismoTipo(node.getRetorno().getTipo(), node.getFunction().getTipo()),
						"Error: el tipo de retorno no coincide", node);
			}
		}

		return null;
	}

//	class ExpresionAritmetica { Expresion izquierda;  String operador;  Expresion derecha; }
	public Object visit(ExpresionAritmetica node, Object param) {
		if (node.getIzquierda() != null)
			node.getIzquierda().accept(this, param);
		if (node.getDerecha() != null)
			node.getDerecha().accept(this, param);

		predicado(
				(node.getIzquierda().getTipo() instanceof IntTipo || node.getIzquierda().getTipo() instanceof RealTipo),
				"Error: los operandos deben ser de tipo int o real", node);
		predicado(mismoTipo(node.getIzquierda().getTipo(), node.getDerecha().getTipo()),
				"Error: los operandos deben ser del mismo tipo", node);

		node.setTipo(node.getIzquierda().getTipo());
		node.setModificable(false);

		return null;
	}

//	class ExpresionLogicaAndOr { Expresion izquierda;  String operador;  Expresion derecha; }
	public Object visit(ExpresionLogicaAndOr node, Object param) {
		if (node.getIzquierda() != null)
			node.getIzquierda().accept(this, param);
		if (node.getDerecha() != null)
			node.getDerecha().accept(this, param);

		predicado(
				(node.getIzquierda().getTipo() instanceof IntTipo)
						&& (mismoTipo(node.getIzquierda().getTipo(), node.getDerecha().getTipo())),
				"Error: los operandos deben ser de tipo entero", node);

		node.setTipo(node.getIzquierda().getTipo());
		node.setModificable(false);

		return null;
	}

//	class ExpresionLogicaAndOr { Expresion izquierda;  String operador;  Expresion derecha; }
	public Object visit(ExpresionLogica node, Object param) {
		if (node.getIzquierda() != null)
			node.getIzquierda().accept(this, param);
		if (node.getDerecha() != null)
			node.getDerecha().accept(this, param);

		predicado(
				(node.getIzquierda().getTipo() instanceof IntTipo || node.getIzquierda().getTipo() instanceof RealTipo),
				"Error: los operandos deben ser de tipo entero o real", node);
		predicado(mismoTipo(node.getIzquierda().getTipo(), node.getDerecha().getTipo()),
				"Error: los operandos deben de ser del mismo tipo", node);

		node.setTipo(new IntTipo());
		node.setModificable(false);
		return null;
	}

//	class ExpresionDistinto { Expresion not; }
	public Object visit(ExpresionDistinto node, Object params) {
		if (node.getNot() != null)
			node.getNot().accept(this, params);

		predicado(node.getTipo() instanceof IntTipo, "Error: la expresion debe ser de tipo entero", node);

		node.setTipo(new IntTipo());
		node.setModificable(false);

		return null;
	}

//	class Ident { String valor; }
	public Object visit(Ident node, Object param) {
		node.setModificable(true);
		node.setTipo(node.getDefinicion().getTipo());
		return null;
	}

//	class LitEnt { String valor; }
	public Object visit(LitEnt node, Object param) {
		node.setModificable(false);
		node.setTipo(new IntTipo());
		return null;
	}

	// class LitReal { String valor; }
	public Object visit(LitReal node, Object param) {
		node.setModificable(false);
		node.setTipo(new RealTipo());
		return null;
	}

	// class LitChar { String valor; }
	public Object visit(LitChar node, Object param) {
		node.setModificable(false);
		node.setTipo(new CharTipo());
		return null;
	}

//	class Cast { Tipo tipo;  Expresion valor; }
	public Object visit(Cast node, Object param) {
		super.visit(node, param);

		node.setModificable(false);

		predicado(!mismoTipo(node.getTipo(), node.getValor().getTipo()),
				"Error: no se puede hacer un cast al mismo tipo", node);
		predicado(esTipoSimple(node.getTipo()), "Error: no se puede hacer cast a tipos complejos", node.getStart());
		predicado(esTipoSimple(node.getValor().getTipo()), "Error: no se puede hacer cast a tipos complejos",
				node.getStart());

		return null;
	}

//	class Array { Expresion nombre;  Expresion valor; }
	public Object visit(Array node, Object param) {
		if (node.getNombre() != null)
			node.getNombre().accept(this, param);

		if (node.getValor() != null)
			node.getValor().accept(this, param);

		predicado(node.getNombre().getTipo() instanceof ArrayTipo, "Error: ha de ser de tipo Array", node);

		if (node.getNombre().getTipo() instanceof ArrayTipo) {
			predicado(node.getValor().getTipo() instanceof IntTipo, "Error: el índice debe ser entero", node);
			node.setTipo(((ArrayTipo) node.getNombre().getTipo()).getTipo());
		}

		node.setModificable(true);
		return null;
	}

//	class Struct { Expresion nombre;  String campos; }
	public Object visit(Struct node, Object param) {
		if (node.getNombre() != null)
			node.getNombre().accept(this, param);

		predicado(node.getNombre().getTipo() instanceof StructTipo, "Error: el tipo del struct es necesario", node);

		if (node.getNombre().getTipo() instanceof StructTipo) {
			StructTipo st = (StructTipo) node.getNombre().getTipo();
			DefStruct s = (DefStruct) st.getDefinicion();
			for (VariableStruct c : s.getVariableStruct()) {
				if (c.getNombre().equals(node.getCampos())) {
					node.setTipo(c.getTipo());
				}
			}

			predicado(node.getTipo() != null, "Error: struct no definido", node.getStart());
		}

		return null;
	}

//	class ExpresionLlamada { String nombre;  List<Expresion> expresion; }
	public Object visit(ExpresionLlamada node, Object param) {
		visitChildren(node.getExpresion(), param);
		predicado(node.getDefinicion().getTipo() != null, "Error: la función no tiene tipo de retorno", node);

		predicado(node.getExpresion().size() == node.getDefinicion().getParametros().size(),
				"Error: se esperaban " + node.getDefinicion().getParametros().size() + " argumentos", node);

		if (node.getExpresion().size() == node.getDefinicion().getParametros().size())
			for (int i = 0; i < node.getExpresion().size(); i++) {
				predicado(
						mismoTipo(node.getExpresion().get(i).getTipo(),
								node.getDefinicion().getParametros().get(i).getTipo()),
						"Error: el tipo de los parámetros no coincide", node);
			}

		node.setTipo(node.getDefinicion().getTipo());
		node.setModificable(false);
		return null;
	}

	// # ----------------------------------------------------------
	// Métodos auxiliares recomendados (opcionales) -------------

	/**
	 * predicado. Método auxiliar para implementar los predicados. Borrar si no se
	 * quiere usar.
	 *
	 * Ejemplos de uso (suponiendo que existe un método "esPrimitivo(expr)"):
	 *
	 * 1. predicado(esPrimitivo(expr.tipo), "La expresión debe ser de un tipo
	 * primitivo", expr.getStart()); 2. predicado(esPrimitivo(expr.tipo), "La
	 * expresión debe ser de un tipo primitivo", expr); // Se asume getStart() 3.
	 * predicado(esPrimitivo(expr.tipo), "La expresión debe ser de un tipo
	 * primitivo");
	 *
	 * NOTA: El método getStart() (ejemplo 1) indica la linea/columna del fichero
	 * fuente donde estaba el nodo (y así poder dar información más detallada de la
	 * posición del error). Si se usa VGen, dicho método habrá sido generado en
	 * todos los nodos del AST. No es obligatorio llamar a getStart() (ejemplo 2),
	 * ya que si se pasa el nodo, se usará por defecto dicha posición. Si no se
	 * quiere imprimir la posición del fichero, se puede omitir el tercer argumento
	 * (ejemplo 3).
	 *
	 * @param condition     Debe cumplirse para que no se produzca un error
	 * @param errorMessage  Se imprime si no se cumple la condición
	 * @param posicionError Fila y columna del fichero donde se ha producido el
	 *                      error.
	 */

	private void predicado(boolean condition, String errorMessage, AST node) {
		predicado(condition, errorMessage, node.getStart());
	}

	private void predicado(boolean condition, String errorMessage, Position position) {
		if (!condition)
			errorManager.notify("Type Checking", errorMessage, position);
	}

	private void predicado(boolean condition, String errorMessage) {
		predicado(condition, errorMessage, (Position) null);
	}

	// Método que nos permite saber si un tipo es primitivo (char real o entero)
	private boolean esTipoSimple(Tipo tipo) {
		return tipo instanceof IntTipo || tipo instanceof RealTipo || tipo instanceof CharTipo;
	}

	// Método que nos permite saber si dos tipos son asignables y del mismo tipo
	private boolean mismoTipo(Tipo tipo1, Tipo tipo2) {
		return tipo1 != null && tipo2 != null ? tipo1.getClass().isAssignableFrom(tipo2.getClass()) : false;
	}

	private ErrorManager errorManager;
}
