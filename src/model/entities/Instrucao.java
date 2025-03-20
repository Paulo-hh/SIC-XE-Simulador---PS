package model.entities;

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
		this.args.clear();
		args.forEach(x -> this.args.add(x));
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

}