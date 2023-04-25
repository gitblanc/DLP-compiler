/**
 * Tutorial de Diseño de Lenguajes de Programación
 * @author Raúl Izquierdo
 */

package codegeneration;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import ast.AST;
import ast.DefFuncion;
import ast.DefStruct;
import ast.DefVariable;
import ast.Parametros;
import ast.Position;
import ast.Print;
import ast.Program;
import ast.Sentencia;
import ast.VariableStruct;
import visitor.DefaultVisitor;

public class CodeSelection extends DefaultVisitor {
	
	enum Funcion{
		VALOR, DIRECCION
	}

	private Map<String, String> instruccion = new HashMap<String, String>();
	private int ifCont = 0;
	private int whileCont = 0;

	public CodeSelection(Writer writer, String sourceFile) {
		this.writer = new PrintWriter(writer);
		this.sourceFile = sourceFile;

		instruccion.put("+", "add");
		instruccion.put("-", "sub");
		instruccion.put("*", "mul");
		instruccion.put("/", "div");
		instruccion.put("&&", "and");
		instruccion.put("||", "or");

		instruccion.put("<", "lt");
		instruccion.put(">", "gt");
		instruccion.put(">=", "ge");
		instruccion.put("<=", "le");
		instruccion.put("==", "eq");
		instruccion.put("!=", "ne");
	}

	// # ----------------------------------------------------------
	/*
	 * Poner aquí los visit.
	 *
	 * Si se ha usado VGen, solo hay que copiarlos de la clase
	 * 'visitor/_PlantillaParaVisitors.txt'.
	 */

	// Ejemplo:
	//
	// public Object visit(Program node, Object param) {
	// out("#source \"" + sourceFile + "\"");
	// out("call main");
	// out("halt");
	//
	// super.visit(node, param); // Recorrer los hijos
	// return null;
	// }

	// class Program { List<Definicion> definicion; }
	public Object visit(Program node, Object param) {
		out("#source \"" + sourceFile + "\"");
		out("call main");
		out("halt");
		visitChildren(node.getDefinicion(), param);
		return null;
	}

	// class DefVariable { Tipo tipo; String nombre; }
	public Object visit(DefVariable node, Object param) {
		out("#GLOBAL " + node.getNombre() + ":" + node.getTipo().getNombreMAPL());
		return null;
	}

	// class DefStruct { String nombre; List<VariableStruct> definicion; }
	public Object visit(DefStruct node, Object param) {
		out("#TYPE " + node.getNombre() + ":" + "{");
		for (VariableStruct child : node.getVariableStruct()) {
			out(child.getNombre() + ":" + child.getTipo().getNombreMAPL());
		}
		out("}");

		return null;
	}

	// class DefFuncion { String nombre; List<Parametros> parametros; Tipo tipo;
	// List<DefVariable> defvariable; List<Sentencia> sentencia; }
	public Object visit(DefFuncion node, Object param) {
		out("#line " + node.getStart().getLine());
		int sizeLocales = 0;
		int sizeParametros = 0;
		out(node.getNombre() + ":");

		if (node.getParametros() != null) {
			for (Parametros child : node.getParametros()) {
				sizeParametros += child.getTipo().getSize();
			}
		}

		if (node.getDefvariable() != null) {
			for (DefVariable child : node.getDefvariable()) {
				sizeLocales += child.getTipo().getSize();
			}
		}

		out("enter " + sizeLocales);

		if (node.getSentencia() != null) {
			for (Sentencia child : node.getSentencia()) {
				child.accept(this, param);
			}
		}

		// Como en teoría -> ret bytesReturn, bytesLocals, bytesParams
		if (node.getTipo() != null) {
			out("ret 0, " + sizeLocales + ", " + sizeParametros);
		}

		return null;
	}

	// class Print { Expresion print; String printTipo; }
	public Object visit(Print node, Object param) {
		out("#line " + node.getEnd().getLine());
		
		if(node.getPrintTipo().equals("")) {
			node.getPrint().accept(this, Funcion.VALOR);
			out("out" + node.getPrint().getTipo().getSufijo());
		}else if(node.getPrintTipo().equals("ln") && node.getPrint() == null) {
			//\n
			out("pushb 10");
			out("outb");
		}else if(node.getPrintTipo().equals("ln") && node.getPrint() != null) {
			node.getPrint().accept(this, Funcion.VALOR);
			out("out" + node.getPrint().getTipo().getSufijo());
			//\n
			out("pushb 10");
			out("outb");
		}else if(node.getPrintTipo().equals("sp")) {
			node.getPrint().accept(this, Funcion.VALOR);
			out("out" + node.getPrint().getTipo().getSufijo());
			//" "
			out("pushb 32");
			out("outb");
		}
		
		return null;
	}

	// # ----------------------------------------------------------
	// Métodos auxiliares recomendados (opcionales) -------------

	// Imprime una línea en el fichero de salida
	private void out(String instruction) {
		writer.println(instruction);
	}

	private void line(AST node) {
		line(node.getEnd());
	}

	private void line(Position pos) {
		if (pos != null)
			out("\n#line " + pos.getLine());
		else
			System.out.println("#line no generado. Se ha pasado una Position null. Falta información en el AST");
	}

	private PrintWriter writer;
	private String sourceFile;
}
