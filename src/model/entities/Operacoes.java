package model.entities;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Operacoes {

	private List<Instrucao> instrucoes = new ArrayList<>();
	private int ponteiroInstrucao = -1;
	private int ponteiroAnterior = -1;
	private Memoria conjuntoMemoria;
	private Registrador registradores;
	private String proximoEndereco;
	private String palavrasCondicoes = "";
	private static final int comprimentoEndereco = 4;
	private String textoSaida = "programa: ";

	public String getTextoSaida() {
		return textoSaida;
	}
	private static final List<String> condicoes = Arrays.asList("LT", "GT", "EQ");

	public Operacoes(List<Instrucao> arrayInstrucoes, Memoria memoria, Registrador regs) {
		arrayInstrucoes.forEach(i -> instrucoes.add(i));
		this.conjuntoMemoria = memoria;
		this.registradores = regs;
		this.proximoEndereco = "0000";
	}

	public void atribuirEndereco() {
		if (instrucoes == null || instrucoes.isEmpty()) {
			textoSaida.concat("\nPor favor, carregue um arquivo");
			return;
		}
		ponteiroInstrucao = 0;

		for (Instrucao instrucao : instrucoes) {
			if (instrucao.getNome().equals("START")) { // no start, deve ter um endereço inicial na memoria
				if (instrucao.getArgs().get(0).length() != comprimentoEndereco) {
					throw new IllegalArgumentException("Endereço inválido na linha: " + instrucao.getNumero_linha());
				}
				proximoEndereco = instrucao.getArgs().get(0);
				continue;
			}
			if (instrucao.getNome().equals("END")) {
				continue;
			}

			instrucao.setEndereco(proximoEndereco);

			if (instrucao.getNome().equals("WORD")) {
				String valor_string;
				String aux3 = Func.int_para_Hexa(Integer.parseInt(instrucao.getArgs().get(0)), 16);
				valor_string = Func.preencherZeros(aux3, 6);
				for (int i = 0; i < 6; i += 2) {
					conjuntoMemoria.setMemoria(proximoEndereco, valor_string.substring(i, i + 2));
					proximoEndereco = Func.somarHexa(proximoEndereco, Func.preencherZeros("1", comprimentoEndereco))
							.toUpperCase();
					proximoEndereco = Func.preencherZeros(proximoEndereco, comprimentoEndereco);
				}

			} else if (determinar_Instrucao(instrucao.getNome()) == -1) {
				textoSaida = textoSaida.concat("\nERRO: nome de instrução inválida na linha " + instrucao.getNumero_linha().toString());
				textoSaida = textoSaida.concat("\nSaindo do interpretador");
				ponteiroInstrucao = -1;
				break;
			} else {
				proximoEndereco = Func.somarHexa(proximoEndereco, Func.preencherZeros("3", comprimentoEndereco));
				proximoEndereco = Func.preencherZeros(proximoEndereco, comprimentoEndereco);
			}
		}
	}

	public Boolean executar_Proxima_Instrucao() throws Exception {
		if (ponteiroInstrucao == -1 || ponteiroInstrucao == instrucoes.size() || 
				instrucoes.get(ponteiroInstrucao).getNome().equals("END")) {
			textoSaida = textoSaida.concat("\nFim do código");
			ponteiroInstrucao = -1;
			return false;
		}
		while (instrucoes.get(ponteiroInstrucao).getNome().equals("WORD") 
				|| instrucoes.get(ponteiroInstrucao).getNome().equals("START")) {
			ponteiroInstrucao++;
			if (ponteiroInstrucao == instrucoes.size()) {
				textoSaida = textoSaida.concat("\nFim do código");
				ponteiroInstrucao = -1;
				return false;
			}
		}

		Instrucao linha_Instrucao = instrucoes.get(ponteiroInstrucao);
		ponteiroInstrucao++;
		String nome_Instrucao = linha_Instrucao.getNome();
		List<String> argumentos_Instrucao = new ArrayList<>();
		linha_Instrucao.getArgs().forEach(s -> argumentos_Instrucao.add(s));
		Instrucao instrucao_atual;
		int tamanho_atual;
		textoSaida = textoSaida.concat("\nExecutando instrução: " + linha_Instrucao.getNome());
		int token_Instrucao = determinar_Instrucao(nome_Instrucao);
		List<Integer> tokens = Arrays.asList(2, 4, 8, 10, 22, 24, 36, 38);
		
		if (!tokens.contains(token_Instrucao) && argumentos_Instrucao.size() != 0 
				&& argumentos_Instrucao.get(0).substring(0, 1) != "X" && argumentos_Instrucao.get(0).substring(0, 1) != "#") {
			if (argumentos_Instrucao.get(0).substring(0, 1) == "@") {
				instrucao_atual = obterInstrucao(argumentos_Instrucao.get(0).substring(1));
			} else {
				instrucao_atual = obterInstrucao(argumentos_Instrucao.get(0));
			}
			tamanho_atual = (instrucao_atual.getNome().equals("WORD")) ? 3 : 0;
		}
		else {
			tamanho_atual = 0;
			instrucao_atual = null;
		} 

		String endereco = (instrucao_atual != null) 
				? resolverEndereco(instrucao_atual.getEndereco(), argumentos_Instrucao) 
				: resolverEndereco(null, argumentos_Instrucao);

		usar_Token(token_Instrucao, nome_Instrucao, endereco, tamanho_atual, argumentos_Instrucao,
				linha_Instrucao.getNumero_linha());

		// Find next instruction and set PC to its address
		int proximoPonteiroInstrucao = ponteiroInstrucao;
		while (proximoPonteiroInstrucao < instrucoes.size()
				&& (instrucoes.get(proximoPonteiroInstrucao).getNome().equals("START")
				|| instrucoes.get(proximoPonteiroInstrucao).getNome().equals("WORD"))) {
			proximoPonteiroInstrucao++;
		}

		if (proximoPonteiroInstrucao < instrucoes.size()) {
			registradores.setRegistrador("PC",
			Func.preencherZeros(instrucoes.get(proximoPonteiroInstrucao).getEndereco(), 6));
		}
		return true;
	}

	public int determinar_Instrucao(String instrucao_nome) { // função para retornar os tokens

		Map<String, Integer> conjuntoInstrucoes = new HashMap<>();
		conjuntoInstrucoes.put("ADD", 1);
		conjuntoInstrucoes.put("ADDR", 2);
		conjuntoInstrucoes.put("AND", 3);
		conjuntoInstrucoes.put("CLEAR", 4);
		conjuntoInstrucoes.put("COMP", 5);
		conjuntoInstrucoes.put("COMPR", 6);
		conjuntoInstrucoes.put("DIV", 7);
		conjuntoInstrucoes.put("DIVR", 8);
		conjuntoInstrucoes.put("J", 9);
		conjuntoInstrucoes.put("JEQ", 10);
		conjuntoInstrucoes.put("JGT", 11);
		conjuntoInstrucoes.put("JLT", 12);
		conjuntoInstrucoes.put("JSUB", 13);
		conjuntoInstrucoes.put("LDA", 14);
		conjuntoInstrucoes.put("LDB", 15);
		conjuntoInstrucoes.put("LDCH", 16);
		conjuntoInstrucoes.put("LDL", 17);
		conjuntoInstrucoes.put("LDS", 18);
		conjuntoInstrucoes.put("LDT", 19);
		conjuntoInstrucoes.put("LDX", 20);
		conjuntoInstrucoes.put("MUL", 21);
		conjuntoInstrucoes.put("MULR", 22);
		conjuntoInstrucoes.put("OR", 23);
		conjuntoInstrucoes.put("RMO", 24);
		conjuntoInstrucoes.put("RSUB", 25);
		conjuntoInstrucoes.put("SHIFTR", 26);
		conjuntoInstrucoes.put("SHIFTL", 27);
		conjuntoInstrucoes.put("STA", 28);
		conjuntoInstrucoes.put("STB", 29);
		conjuntoInstrucoes.put("STCH", 30);
		conjuntoInstrucoes.put("STL", 31);
		conjuntoInstrucoes.put("STS", 32);
		conjuntoInstrucoes.put("STT", 33);
		conjuntoInstrucoes.put("STX", 34);
		conjuntoInstrucoes.put("SUB", 35);
		conjuntoInstrucoes.put("SUBR", 36);
		conjuntoInstrucoes.put("TIX", 37);
		conjuntoInstrucoes.put("TIXR", 38);
		conjuntoInstrucoes.put("END", 39);

		if (conjuntoInstrucoes.containsKey(instrucao_nome)) {
			return conjuntoInstrucoes.get(instrucao_nome);
		}
		return -1;
	}

	public void usar_Token(int tokenInstrucao, String nome, String enderecoInicial, int tamanhoAtual,
			List<String> argumentos, int numLinha) throws Exception {
		String dadoHexa;

		if (enderecoInicial == null) {
			if (argumentos.get(0).substring(0, 1) == "#") {
				int val = Integer.parseInt(argumentos.get(0).substring(1));
				dadoHexa = Integer.toHexString(val).toUpperCase();
			} else {
				dadoHexa = argumentos.get(0).substring(1);
			}
		} else {
			dadoHexa = obterDado(enderecoInicial, tamanhoAtual);
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
			int novoIndiceJ = obterIndice(argumentos.get(0));
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
				int novoIndiceJEQ = obterIndice(argumentos.get(0));

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
				int novoIndiceJGT = obterIndice(argumentos.get(0));

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
				int novoIndiceJLT = obterIndice(argumentos.get(0));

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
			int novoIndiceJSUB = obterIndice(argumentos.get(0));
			registradores.setRegistrador("L", registradores.getRegistrador("PC"));
			registradores.setRegistrador("PC", obterInstrucao(argumentos.get(0)).getEndereco());
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
			int valorMemoria = Func.hexa_para_Int(obterDado(enderecoInicial, tamanhoAtual));
			valor_OR |= valorMemoria;
			registradores.setRegistrador("A", Func.preencherZeros(Integer.toString(valor_OR), 6));
			break;

		case 24: // RMO
			registradores.setRegistrador(argumentos.get(1), registradores.getRegistrador(argumentos.get(0)));
			break;

		case 25: // RSUB
			this.registradores.setRegistrador("PC", registradores.getRegistrador("L"));

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
			for (String a : dividirBytes(valorA)) {
				conjuntoMemoria.setMemoria(enderecoA, a);
				enderecoA = Func.int_para_Hexa(Func.hexa_para_Int(enderecoA) + 1, 16);
				enderecoA = Func.preencherZeros(enderecoA, 4);
			}
			break;

		case 29: // STB
			String valorB = registradores.getRegistrador("B");
			String enderecoB = enderecoInicial;
			for (String a : dividirBytes(valorB)) {
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
			for (String a : dividirBytes(valorL)) {
				conjuntoMemoria.setMemoria(enderecoL, a);
				enderecoL = Func.int_para_Hexa(Func.hexa_para_Int(enderecoL) + 1, 16);
				enderecoL = Func.preencherZeros(enderecoL, 4);
			}
			break;

		case 32: // STS
			String valorS = registradores.getRegistrador("S");
			String enderecoS = enderecoInicial;
			for (String a : dividirBytes(valorS)) {
				conjuntoMemoria.setMemoria(enderecoS, a);
				enderecoS = Func.int_para_Hexa(Func.hexa_para_Int(enderecoS) + 1, 16);
				enderecoS = Func.preencherZeros(enderecoS, 4);
			}
			break;

		case 33: // STT
			String valorT = registradores.getRegistrador("T");
			String enderecoT = enderecoInicial;
			for (String a : dividirBytes(valorT)) {
				conjuntoMemoria.setMemoria(enderecoT, a);
				enderecoT = Func.int_para_Hexa(Func.hexa_para_Int(enderecoT) + 1, 16);
				enderecoT = Func.preencherZeros(enderecoT, 4);
			}
			break;

		case 34: // STX
			String valorX = registradores.getRegistrador("X");
			String enderecoX = enderecoInicial;
			for (String a : dividirBytes(valorX)) {
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
			int memoriaIntTIX = Func.hexa_para_Int(obterDado(enderecoInicial, tamanhoAtual));

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

	public String resolverEndereco(String endereco_inicial, List<String> argumentos) throws Exception {
		String aux = Func.preencherZeros(endereco_inicial, 6);

		// endereçamento indexado
		if (argumentos.size() == 2 && argumentos.get(1) == "X") {
			String valorX = registradores.getRegistrador("X");
			String endereco = Func.somarHexa(valorX, aux);
			endereco = Func.preencherZeros(endereco, 4);
			return endereco;
		}

		// operando imediato
		if (argumentos.size() != 0 && argumentos.get(0).substring(0) == "#") {
			return null;
		}

		// Endereçamento indireto
		if (argumentos.size() != 0 && argumentos.get(0).substring(0, 1) == "@") {
			Instrucao instrucao = obterInstrucao(argumentos.get(0).substring(1));
			String x = Func.preencherZeros("1", comprimentoEndereco);
			String endereco = conjuntoMemoria.getMemoria(instrucao.getEndereco())
					+ conjuntoMemoria.getMemoria(Func.somarHexa(instrucao.getEndereco(), x));
			return endereco;
		}
		return endereco_inicial;
	}

	public Instrucao obterInstrucao(String rotulo) throws Exception {
		for (Instrucao instr : instrucoes) {
			if (instr.getRotulo().equals(rotulo)) {
				return instr;
			}
		}
		throw new Exception("ERRO: o rótulo - '" + rotulo + "' pode estar errado");
	}

	public String obterDado(String enderecoInicial, int tamanho) {
		String endereco = enderecoInicial;
		String memoriaStringHexa = "";
		for (int i = 0; i < tamanho; i++) {
			memoriaStringHexa = memoriaStringHexa.concat(conjuntoMemoria.getMemoria(endereco));
			endereco = Func.somarHexa(endereco, Func.preencherZeros("1", comprimentoEndereco)).toUpperCase();
			endereco = Func.preencherZeros(endereco, 4);
		}
		return memoriaStringHexa;
	}

	public int obterIndice(String rotulo) {
		for (int i = 0; i < instrucoes.size(); i++) {
			if (instrucoes.get(i).getRotulo().equals(rotulo)) {
				return i;
			}
		}
		return -1;
	}

	public static List<String> dividirBytes(String hexaString) {
		List<String> byteLista = new ArrayList<>();
		for (int i = 0; i < hexaString.length(); i += 2) {
			byteLista.add(hexaString.substring(i, Math.min(i + 2, hexaString.length())));
		}
		return byteLista;
	}

}