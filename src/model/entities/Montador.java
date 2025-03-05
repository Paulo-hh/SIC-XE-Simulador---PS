package model.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Montador{

	private Map<String, Integer> conjuntoInstrucoes = new HashMap<>();
	private Map<String, String> tabelaSimbolo = new HashMap<>();
	private List<Instrucao> instrucoes = new ArrayList<>();
	private int ponteiroInstrucao = -1;
	private static final int comprimentoEndereco = 4;
	private Memoria conjuntoMemoria;
	private Registrador registradores;
	private String proximoEndereco;
	private String textoSaida = "programa: ";
	private Maquina maquina;
	
	public Map<String, String> getTabelaSimbolo() {
		return tabelaSimbolo;
	}

	public String getTextoSaida() {
		return textoSaida;
	}
	
	public void setTabelaSimbolo() {
		textoSaida = textoSaida.concat("\n\n====== Tabela de Simbolos ======\n");
		for(String s: tabelaSimbolo.keySet()) {
			textoSaida = textoSaida.concat(s + " => " + tabelaSimbolo.get(s) + "\n");
		}
	}
	
	public List<Instrucao> getInstrucoes(){
		return instrucoes;
	}

	public Montador(List<Instrucao> arrayInstrucoes, Memoria memoria, Registrador regs) {
		arrayInstrucoes.forEach(i -> instrucoes.add(i));
		this.conjuntoMemoria = memoria;
		this.registradores = regs;
		this.proximoEndereco = "0000";
		maquina = new Maquina(textoSaida, ponteiroInstrucao);
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
	}
	
	// PRIMEIRA PASSAGEM
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
			if(!instrucao.getRotulo().equals(null)) {
				tabelaSimbolo.put(instrucao.getRotulo(), proximoEndereco);
			}
			if(instrucao.getNome().equals("MCDEF")) {
				continue;
			}
			if(instrucao.getNome().equals("MCEND")) {
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

			} else if(conjuntoInstrucoes.containsKey(instrucao.getNome()) || instrucao.getNome().equals("START")
					|| instrucao.getNome().equals("END")){
				proximoEndereco = Func.somarHexa(proximoEndereco, Func.preencherZeros("3", comprimentoEndereco));
				proximoEndereco = Func.preencherZeros(proximoEndereco, comprimentoEndereco);
			}
			else {
				// colocar a chamada do processador de macros
				
			}
		}
	}
	
	// SEGUNDA PASSAGEM
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
		int token_Instrucao = determinar_Instrucao(nome_Instrucao);
		int tamanho_atual;
		textoSaida = textoSaida.concat("\nExecutando instrução: " + linha_Instrucao.getNome());
		List<Integer> tokens = Arrays.asList(2, 4, 8, 10, 22, 24, 36, 38);
		
		if (!tokens.contains(token_Instrucao) && argumentos_Instrucao.size() != 0 
				&& !argumentos_Instrucao.get(0).substring(0, 1).equals("X") && !argumentos_Instrucao.get(0).substring(0, 1).equals("#")) {
			if (argumentos_Instrucao.get(0).substring(0, 1).equals("@")) {
				instrucao_atual = Func.obterInstrucao(argumentos_Instrucao.get(0).substring(1));
			} else {
				instrucao_atual = Func.obterInstrucao(argumentos_Instrucao.get(0));
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
		
		maquina.setPonteiroInstrucao(ponteiroInstrucao);
		maquina.setTextoSaida(textoSaida);
				
		maquina.usar_Token(token_Instrucao, nome_Instrucao, endereco, tamanho_atual, argumentos_Instrucao,
				linha_Instrucao.getNumero_linha(), conjuntoMemoria, registradores);
		
		textoSaida = maquina.getTextoSaida();
		ponteiroInstrucao = maquina.getPonteiroInstrucao();

		int proximoPonteiroInstrucao = maquina.getPonteiroInstrucao();
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
	

	public int determinar_Instrucao(String instrucao_nome) { 

		if (conjuntoInstrucoes.containsKey(instrucao_nome)) {
			return conjuntoInstrucoes.get(instrucao_nome);
		}
		return -1;
	}

	

	public String resolverEndereco(String endereco_inicial, List<String> argumentos) throws Exception {
		String aux = Func.preencherZeros(endereco_inicial, 6);

		// endereçamento indexado
		if (argumentos.size() == 2 && argumentos.get(1).equals("X")) {
			String valorX = registradores.getRegistrador("X");
			String endereco = Func.somarHexa(valorX, aux);
			endereco = Func.preencherZeros(endereco, 4);
			return endereco;
		}

		// operando imediato
		if (argumentos.size() != 0 && argumentos.get(0).substring(0, 1).equals("#")) {
			return null;
		}

		// Endereçamento indireto
		if (argumentos.size() != 0 && argumentos.get(0).substring(0, 1).equals("@")) {
			Instrucao instrucao = Func.obterInstrucao(argumentos.get(0).substring(1));
			String x = Func.preencherZeros("1", comprimentoEndereco);
			String endereco = conjuntoMemoria.getMemoria(instrucao.getEndereco())
					.concat(conjuntoMemoria.getMemoria(Func.somarHexa(instrucao.getEndereco(), x)));
			return endereco;
		}
		return endereco_inicial;
	}	

}
