/**
 * Tutorial de Diseño de Lenguajes de Programación
 * @author Raúl Izquierdo
 */

package codegeneration;

import ast.*;
import visitor.*;

/**
 * Clase encargada de asignar direcciones a las variables.
 */
public class MemoryAllocation extends DefaultVisitor {

	// class Programa { List<DefVariable> definiciones; List<Sentencia> sentencias;
	// }
	public Object visit(Program node, Object param) {

		int currentAddress = 0;

		for (Definicion child : node.getDefinicion()) {
			if (child instanceof DefVariable) {
				((DefVariable) child).setAddress(currentAddress);
				currentAddress += ((DefVariable) child).getTipo().getSize();
			}
		}
		return null;
	}

	// class DefStruct { String nombre; List<VariableStruct> definicion; }
	public Object visit(DefStruct node, Object param) {
		int address = 0;

		if (node.getVariableStruct() != null) {
			for (VariableStruct child : node.getVariableStruct()) {
				child.setAddress(address);
				address += child.getTipo().getSize();
				child.accept(this, param);
			}
		}

		return null;
	}

	// class DefFuncion { String nombre; List<Parametros> parametros; Tipo tipo;
	// List<DefVariable> defvariable; List<Sentencia> sentencia; }
	public Object visit(DefFuncion node, Object param) {

		// Si la función tiene parámetros
		if (node.getParametros() != null) {
			int address = 4;

			for (int i = (node.getParametros().size() - 1); i >= 0; i--) {
				node.getParametros().get(i).setAddress(address);
				address += node.getParametros().get(i).getTipo().getSize();
				node.getParametros().get(i).accept(this, param);
			}
		}

		// Si el tipo de la función no es nulo
		if (node.getTipo() != null)
			node.getTipo().accept(this, param);

		// Si tiene variables locales definidas
		if (node.getDefvariable() != null) {
			int address = 0;
			for (DefVariable child : node.getDefvariable()) {
				address -= child.getTipo().getSize();
				child.setAddress(address);
				child.accept(this, param);
			}
		}

		// Si tiene sentencias dentro
		if (node.getSentencia() != null) {
			for (Sentencia child : node.getSentencia())
				child.accept(this, param);
		}

		return null;
	}

}
