package model.entities;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Macros {
	private static List<Instrucao> esqueleto = new ArrayList<>();
	private Instrucao prototipo;
	private Instrucao chamada;
	
	public Macros(List<Instrucao> macro) {
		super();
		macro.forEach(x -> esqueleto.add(x));
		prototipo = esqueleto.get(0);
		System.out.println(prototipo);
		esqueleto.remove(0);
	}
	
	public Instrucao getChamada() {
		return chamada;
	}
	
	public void setChamada(Instrucao chamada) {
		this.chamada = chamada;
	}

	public List<Instrucao> getEsqueleto() {
		return esqueleto;
	}

	public void setEsqueleto(List<Instrucao> macro) {
		Macros.esqueleto = macro;
	}

	public Instrucao getPrototipo() {
		return prototipo;
	}

	public void setPrototipo(Instrucao prototipo) {
		this.prototipo = prototipo;
	}
	
	public void modoDeDefinicao() {
		int cont = 0;
		List<String> parametros = new ArrayList<>();
		parametros.add(prototipo.getRotulo());
		prototipo.getArgs().forEach(x -> parametros.add(x));
		for(Instrucao instrucaoMacro: esqueleto) {
			for(String parametro: parametros) {
				List<String> novosArgumentos = new ArrayList<>();
				instrucaoMacro.getArgs().forEach(x -> novosArgumentos.add(x));
				if(instrucaoMacro.getRotulo().equals(parametro)) {
					instrucaoMacro.setRotulo("#" + cont);
				}
				for(String argumentos: instrucaoMacro.getArgs()) {
					if(argumentos.contains(parametro)) {
						novosArgumentos.add("#" + cont);
					}
					else {
						novosArgumentos.add(argumentos);
					}
				}
				cont++;
				instrucaoMacro.setArgs(novosArgumentos);
				novosArgumentos.clear();
			}
		}
		saidaMacro();
	}
	
	public void modoDeExpansao(Instrucao chamada) {
		setChamada(chamada);
		List<String> parametros = new ArrayList<>();
		parametros.add(chamada.getRotulo());
		chamada.getArgs().forEach(x -> parametros.add(x));
		for (int cont = 0; cont < parametros.size(); cont++) {
			List<String> novosArgumentos = new ArrayList<>();
			for (Instrucao esqueletoMacro : esqueleto) {
				if (esqueletoMacro.getRotulo().equals("#" + cont)) {
					esqueletoMacro.setRotulo(parametros.get(cont));
				}
				for (String args : esqueletoMacro.getArgs()) {
					if (args.equals("#" + cont)) {
						novosArgumentos.add(parametros.get(cont));
					} else {
						novosArgumentos.add(args);
					}
				}
			}
			chamada.setArgs(novosArgumentos);
			novosArgumentos.clear();
		}
	}
	
	public static void saidaMacro(){
		String path = "C:\\Temp\\ws-eclipse\\PS__Trabalho\\src\\Saida\\MASMAPRG.ASM";
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(path))){
			for(Instrucao instrucaoMacros: esqueleto) {
				bw.write(instrucaoMacros.getRotulo() + " ");
				bw.write(instrucaoMacros.getNome() + " ");
				bw.write(instrucaoMacros.getEndereco() + "\n");
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
}
