/**
 * Tutorial de Diseño de Lenguajes de Programación
 * @author Raúl Izquierdo
 */

package semantic;

import java.util.HashMap;
import java.util.Map;

import ast.AST;
import ast.DefFuncion;
import ast.DefStruct;
import ast.DefVariable;
import ast.Definicion;
import ast.ExpresionLlamada;
import ast.FuncionLlamada;
import ast.Position;
import ast.Program;
import ast.StructTipo;
import ast.VariableStruct;
import main.ErrorManager;
import visitor.DefaultVisitor;

public class Identification extends DefaultVisitor {

	public Identification(ErrorManager errorManager) {
		this.errorManager = errorManager;
	}

	// FUNCIONES
	// defFuncion:definicion -> nombre:String parametros:parametros* tipo:tipo
	// defvariable:defVariable* sentencia:sentencia*
	public Object visit(DefFuncion node, Object param) {
		if (node.getTipo() != null)// cuando de un nullPointer es pq desde el grammar.g4 le paso un null
			node.getTipo().accept(this, param); // No es necesario realmente

		DefFuncion definicion = funciones.get(node.getNombre());
		predicado(definicion == null, "Función ya definida: " + node.getNombre(), node);
		funciones.put(node.getNombre(), node);

		visitChildren(node.getDefvariable(), param);
		visitChildren(node.getSentencia(), param);
		return null;
	}

	// funcionLlamada:sentencia -> nombre:String expresion:expresion*
	public Object visit(FuncionLlamada node, Object param) {
		DefFuncion definicion = funciones.get(node.getNombre());
		predicado(definicion != null, "Función no definida: " + node.getNombre(), node);
		node.setDefinicion(definicion); // Enlazar referencia con definición
		return null;
	}

	public Object visit(ExpresionLlamada node, Object param) {
		predicado(funciones.get(node.getNombre()) != null, "Función no definida: " + node.getNombre(), node);
		node.setDefinicion(funciones.get(node.getNombre())); // Enlazar referencia con definición
		visitChildren(node.getExpresion(), param);
		return null;
	}

	// STRUCTS
	public Object visit(DefStruct node, Object param) {
		DefStruct definicion = estructuras.getFromAny(node.getNombre());
		predicado(definicion == null, "Estructura ya definida: " + node.getNombre(), node);
		estructuras.put(node.getNombre(), node);

		for (VariableStruct child : node.getVariableStruct()) {
			child.setDefinicion(node);
			variablesStruct.put(child.getNombre(), child);
		}
		visitChildren(node.getVariableStruct(), param);
		return null;
	}

	public Object visit(VariableStruct node, Object param) {
		variablesStruct.remove(node.getNombre(), node);
		predicado(variablesStruct.get(node.getNombre()) == null, "Campo repetido: " + node.getNombre(), node);

		if (node.getTipo() != null)
			node.getTipo().accept(this, param);
		return null;
	}

	public Object visit(StructTipo node, Object param) {
		predicado(estructuras.getFromAny(node.getNombre()) != null, "Estructura no definida: " + node.getNombre(), node);
		node.setDefinicion(estructuras.getFromAny(node.getNombre()));
		return null;
	}

	// VARIABLES
	public Object visit(DefVariable node, Object param) {
		DefVariable definicion = variables.getFromAny(node.getNombre());
		predicado(definicion == null, "Variable ya definida: " + node.getNombre(), node);
		variables.put(node.getNombre(), node);

		if (node.getTipo() != null)
			node.getTipo().accept(this, param);
		return null;
	}

	// # --------------------------------------------------------
	// Métodos auxiliares recomendados (opcionales) -------------

	private void error(String msg, Position position) {
		errorManager.notify("Identification", msg, position);
	}

	private void predicado(boolean condition, String errorMessage, AST node) {
		if (!condition)
			error(errorMessage, node.getStart());
	}

	private ErrorManager errorManager;
	private Map<String, DefFuncion> funciones = new HashMap<String, DefFuncion>();
	private Map<String, VariableStruct> variablesStruct = new HashMap<>();
	private ContextMap<String, DefStruct> estructuras = new ContextMap<>();
	private ContextMap<String, DefVariable> variables = new ContextMap<>();
}
