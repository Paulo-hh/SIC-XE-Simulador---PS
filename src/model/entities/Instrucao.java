package model.entities;

import java.util.ArrayList;
import java.util.List;

public class Instrucao {

	private String rotulo;
	private String nome;
	private List<String> args;
	private String endereco;
	private Integer numero_linha;

	public Instrucao(String rotulo, String nome, List<String> args, String endereco, Integer numero_linha) {
		this.rotulo = rotulo;
		this.nome = nome;
		this.args = args;
		this.endereco = endereco;
		this.numero_linha = numero_linha;
	}

	public void setRotulo(String rotulo) {
		this.rotulo = rotulo;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setArgs(List<String> args) {
		this.args = args;
	}

	public String getRotulo() {
		return rotulo;
	}

	public String getNome() {
		return nome;
	}

	public List<String> getArgs() {
		return args;
	}

	public String getEndereco() {
		return endereco;
	}

	public Integer getNumero_linha() {
		return numero_linha;
	}
	
	@Override
	public String toString() {
		return rotulo + "\t" + nome + "\t" + args;
	}

	public static List<Instrucao> lerTexto(String texto) {
		List<Instrucao> lista_instrucao = new ArrayList<>();
		String[] linhas = texto.split("\n");
		for (int i = 0; i < linhas.length; i++) {
			String linha = linhas[i];
			List<String> args_array = new ArrayList<>();
			List<String> listaEntrada = new ArrayList<>();
			for (String b : linha.split(",|\\s+")) {
				listaEntrada.add(b);
			}

			for (int j = 2; j < listaEntrada.size(); j++) {
				args_array.add(listaEntrada.get(j));
			}
			Instrucao instrucao_obj = new Instrucao(listaEntrada.get(0), listaEntrada.get(1), args_array, null, i);
			lista_instrucao.add(instrucao_obj);
		}
		return lista_instrucao;
	}
	
	
}