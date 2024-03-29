/**
 * Tutorial de Diseño de Lenguajes de Programación
 * @author Raúl Izquierdo
 */

package codegeneration;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ast.AST;
import ast.Array;
import ast.ArrayTipo;
import ast.Asignacion;
import ast.Cast;
import ast.DefFuncion;
import ast.DefStruct;
import ast.DefVariable;
import ast.ExpresionAritmetica;
import ast.ExpresionDistinto;
import ast.ExpresionLlamada;
import ast.ExpresionLogica;
import ast.ExpresionLogicaAndOr;
import ast.FuncionLlamada;
import ast.Ident;
import ast.If;
import ast.LitChar;
import ast.LitEnt;
import ast.LitReal;
import ast.Parametros;
import ast.Position;
import ast.Print;
import ast.Program;
import ast.Read;
import ast.Return;
import ast.Sentencia;
import ast.Struct;
import ast.StructTipo;
import ast.Variable;
import ast.VariableStruct;
import ast.While;
import visitor.DefaultVisitor;

public class CodeSelection extends DefaultVisitor {

	enum Funcion {
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
		if (node.getTipo() == null) {
			out("ret 0, " + sizeLocales + ", " + sizeParametros);
		}

		return null;
	}

	// class Asignacion { Expresion izquierda; Expresion derecha; }
	public Object visit(Asignacion node, Object param) {
		out("#line " + node.getEnd().getLine());
		node.getIzquierda().accept(this, Funcion.DIRECCION);
		node.getDerecha().accept(this, Funcion.VALOR);
		out("store" + node.getIzquierda().getTipo().getSufijo());

		return null;
	}

	// class Print { Expresion print; String printTipo; }
	public Object visit(Print node, Object param) {
		out("#line " + node.getEnd().getLine());

		if (node.getPrintTipo().equals("")) {
			node.getPrint().accept(this, Funcion.VALOR);
			out("out" + node.getPrint().getTipo().getSufijo());
		} else if (node.getPrintTipo().equals("ln") && node.getPrint() == null) {
			// \n
			out("pushb 10");
			out("outb");
		} else if (node.getPrintTipo().equals("ln") && node.getPrint() != null) {
			node.getPrint().accept(this, Funcion.VALOR);
			out("out" + node.getPrint().getTipo().getSufijo());
			// \n
			out("pushb 10");
			out("outb");
		} else if (node.getPrintTipo().equals("sp")) {
			node.getPrint().accept(this, Funcion.VALOR);
			out("out" + node.getPrint().getTipo().getSufijo());
			// " "
			out("pushb 32");
			out("outb");
		}

		return null;
	}

	// class Read { Expresion read; }
	public Object visit(Read node, Object param) {
		out("#line " + node.getEnd().getLine());
		node.getRead().accept(this, Funcion.DIRECCION);
		out("in" + node.getRead().getTipo().getSufijo());
		out("store" + node.getRead().getTipo().getSufijo());

		return null;
	}

	// class FuncionLlamada { String nombre; List<Expresion> expresion; }
	public Object visit(FuncionLlamada node, Object param) {
		out("#line " + node.getStart().getLine());
		if (node.getExpresion() != null) {
			visitChildren(node.getExpresion(), Funcion.VALOR);
		}

		out("call " + node.getNombre());

		if (node.getDefinicion().getTipo() != null) {
			out("pop");
		}

		return null;
	}

	// class If { Expresion condicion; List<Sentencia> if_true; List<Sentencia>
	// if_false; }
	public Object visit(If node, Object param) {
		out("#line " + node.getStart().getLine());
		int ifCont = this.ifCont;
		this.ifCont++;

		node.getCondicion().accept(this, Funcion.VALOR);

		if (node.getIf_false() != null) {
			out("jz else" + ifCont);
		} else {
			out("jz finif" + ifCont);
		}

		for (int i = 0; i < node.getIf_true().size(); i++) {
			node.getIf_true().get(i).accept(this, param);
			if (i == node.getIf_true().size() - 1 && !(node.getIf_true().get(i) instanceof Return)) {
				out("jmp finif" + ifCont);
			}
		}

		if (node.getIf_false() != null) {
			out("else" + ifCont + ":");
			for (Sentencia child : node.getIf_false()) {
				child.accept(this, param);
			}
		}

		out("finif" + ifCont + ":");

		return null;
	}

	// class While { Expresion condicion; List<Sentencia> sentencia; }
	public Object visit(While node, Object param) {
		out("#line " + node.getStart().getLine());
		int whileCont = this.whileCont;
		this.whileCont++;

		out("while" + whileCont + ":");
		node.getCondicion().accept(this, Funcion.VALOR);

		out("jz finWhile" + whileCont);
		visitChildren(node.getSentencia(), param);

		out("jmp while" + whileCont);
		out("finWhile" + whileCont + ":");

		return null;
	}

	// class Return { Expresion retorno; }
	public Object visit(Return node, Object param) {
		int sizeLocales = 0;
		int sizeParams = 0;

		for (Parametros child : node.getFunction().getParametros()) {
			sizeParams += child.getTipo().getSize();
		}
		for (DefVariable child : node.getFunction().getDefvariable()) {
			sizeLocales += child.getTipo().getSize();
		}

		if (node.getRetorno() == null) {
			out("ret 0," + sizeLocales + "," + sizeParams);
		} else {
			node.getRetorno().accept(this, Funcion.VALOR);
			out("ret " + node.getRetorno().getTipo().getSize() + "," + sizeLocales + "," + sizeParams);
		}

		return null;
	}

	// class ExpresionAritmetica { Expresion izquierda; String operador; Expresion
	// derecha; }
	public Object visit(ExpresionAritmetica node, Object param) {
		node.getIzquierda().accept(this, Funcion.VALOR);
		node.getDerecha().accept(this, Funcion.VALOR);
		out(instruccion.get(node.getOperador()) + node.getIzquierda().getTipo().getSufijo());

		return null;
	}

	// class ExpresionLogica { Expresion izquierda; String operador; Expresion
	// derecha; }
	public Object visit(ExpresionLogica node, Object param) {
		node.getIzquierda().accept(this, Funcion.VALOR);
		node.getDerecha().accept(this, Funcion.VALOR);
		out(instruccion.get(node.getOperador()) + node.getIzquierda().getTipo().getSufijo());

		return null;
	}

	// class ExpresionLogica { Expresion izquierda; String operador; Expresion
	// derecha; }
	public Object visit(ExpresionLogicaAndOr node, Object param) {
		node.getIzquierda().accept(this, Funcion.VALOR);
		node.getDerecha().accept(this, Funcion.VALOR);
		out(instruccion.get(node.getOperador()));

		return null;
	}

	// class ExpresionDistinto { Expresion not; }
	public Object visit(ExpresionDistinto node, Object param) {
		if (node.getHijoNot() != null) {
			node.getHijoNot().accept(this, Funcion.VALOR);
		}
		out("not");

		return null;
	}

	// class Ident { String valor; }
	public Object visit(Ident node, Object param) {
		if (Funcion.DIRECCION.equals(param)) {
			if (node.getDefinicion().getAmbito().equals("global")) {
				out("pusha " + node.getDefinicion().getAddress());
			} else if (node.getDefinicion().getAmbito().equals("param")) {
				out("pusha BP");
				out("push " + node.getDefinicion().getParametro().getAddress());
				out("add");
			} else if (node.getDefinicion().getAmbito().equals("local")) {
				out("pusha BP");
				out("push " + node.getDefinicion().getAddress());
				out("add");
			}
		}
		if (Funcion.VALOR.equals(param)) {
			visit(node, Funcion.DIRECCION);
			out("load" + node.getDefinicion().getTipo().getSufijo());
		}
		return null;
	}

	// class LitEnt { String valor; }
	public Object visit(LitEnt node, Object param) {
		out("pushi " + node.getValor());
		return null;
	}

	// class LitReal { String valor; }
	public Object visit(LitReal node, Object param) {
		out("pushf " + node.getValor());
		return null;
	}

	// class LitChar { String valor; }
	public Object visit(LitChar node, Object param) {
		if ("'\\n'".equals(node.getValor()))
			out("pushb 10");
		else
			out("pushb " + (int) node.getValor().charAt(1));
		return null;
	}

	// class Cast { Tipo tipo; Expresion valor; }
	public Object visit(Cast node, Object param) {
		node.getTipo().accept(this, param);
		node.getValor().accept(this, Funcion.VALOR);
		out(node.getValor().getTipo().getSufijo() + "2" + node.getTipo().getSufijo());

		return null;
	}

	// class Array { Expresion nombre; Expresion valor; }
	public Object visit(Array node, Object param) {
		node.getNombre().accept(this, Funcion.DIRECCION);
		out("push " + ((ArrayTipo) (node.getNombre().getTipo())).getTipo().getSize());
		node.getValor().accept(this, Funcion.VALOR);
		out("mul");
		out("add");

		if (Funcion.VALOR.equals(param))
			out("load" + ((ArrayTipo) node.getNombre().getTipo()).getTipo().getSufijo());

		return null;
	}

	// class Struct { Expresion nombre; String campos; }
	public Object visit(Struct node, Object param) {
		if (Funcion.VALOR.equals(param)) {
			node.accept(this, Funcion.DIRECCION);
			out("load" + node.getTipo().getSufijo());
		} else {
			node.getNombre().accept(this, Funcion.DIRECCION);
			List<VariableStruct> lista = ((StructTipo) (node.getNombre()).getTipo()).getDefinicion()
					.getVariableStruct();
			for (VariableStruct child : lista) {
				if (child.getNombre().equals(node.getCampos())) {
					out("push " + child.getAddress());
				}
			}
			out("add");
		}

		return null;
	}

	// class ExpresionLlamada { String nombre; List<Expresion> expresion; }
	public Object visit(ExpresionLlamada node, Object param) {
		if (node.getExpresion() != null)
			visitChildren(node.getExpresion(), Funcion.VALOR);
		out("call " + node.getNombre());

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
