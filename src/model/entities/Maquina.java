package model.entities;


import java.util.Arrays;
import java.util.List;

public class Maquina{

	private static String palavrasCondicoes = "";
	private static final List<String> condicoes = Arrays.asList("LT", "GT", "EQ");
	private String textoSaida;
	private static int ponteiroAnterior = -1;
	private int ponteiroInstrucao;

	public static int getPonteiroAnterior() {
		return ponteiroAnterior;
	}

	public static void setPonteiroAnterior(int ponteiroAnterior) {
		Maquina.ponteiroAnterior = ponteiroAnterior;
	}

	public void setTextoSaida(String textoSaida) {
		this.textoSaida = textoSaida;
	}

	public String getTextoSaida() {
		return textoSaida;
	}

	public int getPonteiroInstrucao() {
		return ponteiroInstrucao;
	}

	public void setPonteiroInstrucao(int ponteiroInstrucao) {
		this.ponteiroInstrucao = ponteiroInstrucao;
	}

	public Maquina(String textoSaida, int ponteiroInstrucao) {
		this.textoSaida = textoSaida;
		this.ponteiroInstrucao = ponteiroInstrucao;
	}
	
	
	public void usar_Token(int tokenInstrucao, String nome, String enderecoInicial, int tamanhoAtual,
			List<String> argumentos, int numLinha, Memoria conjuntoMemoria, Registrador registradores) throws Exception {
		String dadoHexa;

		if (enderecoInicial == null) {
			if (argumentos.get(0).substring(0, 1).equals("#")) {
				int val = Integer.parseInt(argumentos.get(0).substring(1));
				dadoHexa = Integer.toHexString(val).toUpperCase();
				dadoHexa = Func.preencherZeros(dadoHexa, 6);
			} else {
				dadoHexa = argumentos.get(0);
			}
		} else {
			dadoHexa = Func.obterDado(enderecoInicial, tamanhoAtual, conjuntoMemoria);
		}

		switch (tokenInstrucao) {
		case 1: // ADD
			int memoriaInt = Func.hexa_para_Int(dadoHexa);
			int valorADD = Func.hexa_para_Int(registradores.getRegistrador("A"));
			valorADD += memoriaInt;
			registradores.setRegistrador("A",
					Func.encontrarValor(valorADD, Func.int_para_Hexa(valorADD, 16), tamanhoAtual));
			break;

		case 2: // ADDR
			int reg1_val = Func.hexa_para_Int(registradores.getRegistrador(argumentos.get(0)));
			int reg2_val = Func.hexa_para_Int(registradores.getRegistrador(argumentos.get(1)));
			reg2_val += reg1_val;
			registradores.setRegistrador(argumentos.get(1),
					Func.encontrarValor(reg2_val, Func.int_para_Hexa(reg2_val, 16), 3));
			break;

		case 3: // AND
			int memAND = Func.hexa_para_Int(dadoHexa);
			int valorAND = Func.hexa_para_Int(registradores.getRegistrador("A"));
			valorAND &= memAND;
			registradores.setRegistrador("A",
					Func.encontrarValor(valorAND, Func.int_para_Hexa(valorAND, 16), tamanhoAtual));
			break;

		case 4: // CLEAR
			registradores.setRegistrador(argumentos.get(0), "000000");
			break;

		case 5: // COMP
			String memStringHexa = dadoHexa;
			palavrasCondicoes = condicoes.get(Func.compareHex(registradores.getRegistrador("A"), memStringHexa));
			break;

		case 6: // COMPR
			palavrasCondicoes = condicoes.get(Func.compareHex(argumentos.get(0), argumentos.get(1)));
			break;

		case 7: // DIV
			int memInt = Func.hexa_para_Int(dadoHexa);
			int valorDIV = Func.hexa_para_Int(registradores.getRegistrador("A"));
			valorDIV /= memInt;
			registradores.setRegistrador("A",
					Func.encontrarValor(valorDIV, Func.int_para_Hexa(valorDIV, 16), tamanhoAtual));
			break;

		case 8: // DIVR
			int reg1_DIVR = Func.hexa_para_Int(registradores.getRegistrador(argumentos.get(0)));
			int reg2_DIVR = Func.hexa_para_Int(registradores.getRegistrador(argumentos.get(1)));
			reg2_DIVR /= reg1_DIVR;
			registradores.setRegistrador(argumentos.get(1),
					Func.encontrarValor(reg2_DIVR, Func.int_para_Hexa(reg2_DIVR, 16), 3));
			break;

		case 9: // J
			int novoIndiceJ = Func.obterIndice(argumentos.get(0));
			if (novoIndiceJ == -1) {
				System.out.println("ERRO: Salto ilegal para rótulo na linha " + numLinha);
				System.out.println("Encerrando interpretador");
				textoSaida = textoSaida.concat("\nERRO: Salto ilegal para rótulo na linha " + numLinha);
				textoSaida = textoSaida.concat("\nEncerrando interpretador");
				ponteiroInstrucao = -1;
				return;
			}
			ponteiroInstrucao = novoIndiceJ;
			break;

		case 10: // JEQ
			if (palavrasCondicoes.equals(condicoes.get(2))) {
				int novoIndiceJEQ = Func.obterIndice(argumentos.get(0));

				if (novoIndiceJEQ == -1) {
					System.out.println("ERRO: Salto ilegal para rótulo na linha " + numLinha);
					System.out.println("Encerrando interpretador");
					textoSaida = textoSaida.concat("\nERRO: Salto ilegal para rótulo na linha " + numLinha);
					textoSaida = textoSaida.concat("\nEncerrando interpretador");
					ponteiroInstrucao = -1;
					return;
				}
				ponteiroInstrucao = novoIndiceJEQ;
			}
			break;

		case 11: // JGT
			if (palavrasCondicoes.equals(condicoes.get(1))) {
				int novoIndiceJGT = Func.obterIndice(argumentos.get(0));

				if (novoIndiceJGT == -1) {
					System.out.println("ERRO: Salto ilegal para rótulo na linha " + numLinha);
					System.out.println("Encerrando interpretador");
					
					textoSaida = textoSaida.concat("\nERRO: Salto ilegal para rótulo na linha " + numLinha);
					textoSaida = textoSaida.concat("\nEncerrando interpretador");
					ponteiroInstrucao = -1;
					return;
				}
				ponteiroInstrucao = novoIndiceJGT;
			}
			break;

		case 12: // JLT
			if (palavrasCondicoes.equals(condicoes.get(0))) {
				int novoIndiceJLT = Func.obterIndice(argumentos.get(0));

				if (novoIndiceJLT == -1) {
					System.out.println("ERRO: Salto ilegal para rótulo na linha " + numLinha);
					System.out.println("Encerrando interpretador");
					textoSaida = textoSaida.concat("\nERRO: Salto ilegal para rótulo na linha " + numLinha);
					textoSaida = textoSaida.concat("\nEncerrando interpretador");
					ponteiroInstrucao = -1;
					return;
				}
				ponteiroInstrucao = novoIndiceJLT;
			}
			break;

		case 13: // JSUB
			int novoIndiceJSUB = Func.obterIndice(argumentos.get(0));
			registradores.setRegistrador("L", registradores.getRegistrador("PC"));
			registradores.setRegistrador("PC", Func.obterInstrucao(argumentos.get(0)).getEndereco());
			ponteiroAnterior = ponteiroInstrucao;
			ponteiroInstrucao = novoIndiceJSUB;

			if (novoIndiceJSUB == -1) {
				System.out.println("ERRO: Salto ilegal para rótulo na linha " + numLinha);
				System.out.println("Encerrando interpretador");
				textoSaida = textoSaida.concat("\nERRO: Salto ilegal para rótulo na linha " + numLinha);
				textoSaida = textoSaida.concat("\nEncerrando interpretador");
				ponteiroInstrucao = -1;
				return;
			}
			break;

		case 14: // LDA
			registradores.setRegistrador("A", dadoHexa);
			break;

		case 15: // LDB
			registradores.setRegistrador("B", dadoHexa);
			break;

		case 16: // LDCH
			String valor = dadoHexa.substring(0, 2);
			registradores.setRegistrador("A", valor);
			break;

		case 17: // LDL
			registradores.setRegistrador("L", dadoHexa);
			break;

		case 18: // LDS
			registradores.setRegistrador("S", dadoHexa);
			break;

		case 19: // LDT
			registradores.setRegistrador("T", dadoHexa);
			break;

		case 20: // LDX
			registradores.setRegistrador("X", dadoHexa);
			break;

		case 21: // MUL
			int mem_int = Func.hexa_para_Int(dadoHexa);
			int valorMUL = Func.hexa_para_Int(registradores.getRegistrador("A"));
			valorMUL *= mem_int;
			registradores.setRegistrador("A",
					Func.encontrarValor(valorMUL, Func.int_para_Hexa(valorMUL, 16), tamanhoAtual));
			break;

		case 22: // MULR
			int reg1_MULR = Func.hexa_para_Int(registradores.getRegistrador(argumentos.get(0)));
			int reg2_MULR = Func.hexa_para_Int(registradores.getRegistrador(argumentos.get(1)));
			reg2_MULR *= reg1_MULR;
			registradores.setRegistrador(argumentos.get(1),
					Func.encontrarValor(reg2_MULR, Func.int_para_Hexa(reg2_MULR, 16), 3));
			break;

		case 23: // OR
			int valor_OR = Integer.parseInt(registradores.getRegistrador("A"));
			int valorMemoria = Func.hexa_para_Int(Func.obterDado(enderecoInicial, tamanhoAtual, conjuntoMemoria));
			valor_OR |= valorMemoria;
			registradores.setRegistrador("A", Func.preencherZeros(Integer.toString(valor_OR), 6));
			break;

		case 24: // RMO
			registradores.setRegistrador(argumentos.get(1), registradores.getRegistrador(argumentos.get(0)));
			break;

		case 25: // RSUB
			registradores.setRegistrador("PC", registradores.getRegistrador("L"));

			if (ponteiroAnterior == -1) {
				System.out.println("ERRO: Retorno ilegal na linha na linha " + numLinha);
				System.out.println("Encerrando operação");
				ponteiroInstrucao = -1;
				return;
			}
			ponteiroInstrucao = ponteiroAnterior;
			break;

		case 26: // SHIFTR
			int valorReg1 = Integer.parseInt(registradores.getRegistrador(argumentos.get(0)));
			int valorShiftr = Integer.parseInt(argumentos.get(1));
			valorReg1 = valorReg1 >> valorShiftr;
			registradores.setRegistrador(argumentos.get(0), Func.preencherZeros(Integer.toString(valorReg1), 6));
			break;

		case 27: // SHIFTL
			int valorReg2 = Integer.parseInt(registradores.getRegistrador(argumentos.get(0)));
			int valorShiftl = Integer.parseInt(argumentos.get(1));
			valorReg2 = valorReg2 << valorShiftl;
			registradores.setRegistrador(argumentos.get(0), Func.preencherZeros(Integer.toString(valorReg2), 6));
			break;

		case 28: // STA
			String valorA = registradores.getRegistrador("A");
			String enderecoA = enderecoInicial;
			for (String a : Func.dividirBytes(valorA)) {
				conjuntoMemoria.setMemoria(enderecoA, a);
				enderecoA = Func.int_para_Hexa(Func.hexa_para_Int(enderecoA) + 1, 16);
				enderecoA = Func.preencherZeros(enderecoA, 4);
			}
			break;

		case 29: // STB
			String valorB = registradores.getRegistrador("B");
			String enderecoB = enderecoInicial;
			for (String a : Func.dividirBytes(valorB)) {
				conjuntoMemoria.setMemoria(enderecoB, a);
				enderecoB = Func.int_para_Hexa(Func.hexa_para_Int(enderecoB) + 1, 16);
				enderecoB = Func.preencherZeros(enderecoB, 4);
			}
			break;

		case 30: // STCH
			String valorA_Char = registradores.getRegistrador("A").substring(4, 6);
			conjuntoMemoria.setMemoria(enderecoInicial, valorA_Char);
			break;

		case 31: // STL
			String valorL = registradores.getRegistrador("L");
			String enderecoL = enderecoInicial;
			for (String a : Func.dividirBytes(valorL)) {
				conjuntoMemoria.setMemoria(enderecoL, a);
				enderecoL = Func.int_para_Hexa(Func.hexa_para_Int(enderecoL) + 1, 16);
				enderecoL = Func.preencherZeros(enderecoL, 4);
			}
			break;

		case 32: // STS
			String valorS = registradores.getRegistrador("S");
			String enderecoS = enderecoInicial;
			for (String a : Func.dividirBytes(valorS)) {
				conjuntoMemoria.setMemoria(enderecoS, a);
				enderecoS = Func.int_para_Hexa(Func.hexa_para_Int(enderecoS) + 1, 16);
				enderecoS = Func.preencherZeros(enderecoS, 4);
			}
			break;

		case 33: // STT
			String valorT = registradores.getRegistrador("T");
			String enderecoT = enderecoInicial;
			for (String a : Func.dividirBytes(valorT)) {
				conjuntoMemoria.setMemoria(enderecoT, a);
				enderecoT = Func.int_para_Hexa(Func.hexa_para_Int(enderecoT) + 1, 16);
				enderecoT = Func.preencherZeros(enderecoT, 4);
			}
			break;

		case 34: // STX
			String valorX = registradores.getRegistrador("X");
			String enderecoX = enderecoInicial;
			for (String a : Func.dividirBytes(valorX)) {
				conjuntoMemoria.setMemoria(enderecoX, a);
				enderecoX = Func.int_para_Hexa(Func.hexa_para_Int(enderecoX) + 1, 16);
				enderecoX = Func.preencherZeros(enderecoX, 4);
			}
			break;

		case 35: // SUB
			int memSUB = Func.hexa_para_Int(dadoHexa);
			int valorSUB = Func.hexa_para_Int(registradores.getRegistrador("A"));
			valorSUB -= memSUB;
			registradores.setRegistrador("A",
					Func.encontrarValor(valorSUB, Func.int_para_Hexa(valorSUB, 16), tamanhoAtual));
			break;

		case 36: // SUBR
			int reg1_SUBR = Func.hexa_para_Int(registradores.getRegistrador(argumentos.get(0)));
			int reg2_SUBR = Func.hexa_para_Int(registradores.getRegistrador(argumentos.get(1)));
			reg2_SUBR -= reg1_SUBR;
			registradores.setRegistrador(argumentos.get(1),
					Func.encontrarValor(reg2_SUBR, Func.int_para_Hexa(reg2_SUBR, 16), 3));
			break;

		case 37: // TIX
			int valorXTIX = Func.hexa_para_Int(registradores.getRegistrador("X"));
			valorXTIX++;
			int memoriaIntTIX = Func.hexa_para_Int(Func.obterDado(enderecoInicial, tamanhoAtual, conjuntoMemoria));

			if (valorXTIX < memoriaIntTIX) {
				palavrasCondicoes = condicoes.get(0); // LT
			} else if (valorXTIX > memoriaIntTIX) {
				palavrasCondicoes = condicoes.get(1); // GT
			} else {
				palavrasCondicoes = condicoes.get(2); // EQ
			}
			registradores.setRegistrador("X", Func.int_para_Hexa(valorXTIX, 16));
			break;

		case 38: // TIXR
			int valorXTixR = Func.hexa_para_Int(registradores.getRegistrador("X"));
			valorXTixR++;
			String novoHexa = Func.encontrarValor(valorXTixR, Func.int_para_Hexa(valorXTixR, 16), 3);
			registradores.setRegistrador("X", novoHexa);
			palavrasCondicoes = condicoes.get(Func.compareHex(registradores.getRegistrador("X"),
					registradores.getRegistrador(argumentos.get(0))));
			break;

		default:
			System.out.println("instrução incorreta!");
			textoSaida = textoSaida.concat("\nInstrução incorreta!");
			break;
		}
	}

}