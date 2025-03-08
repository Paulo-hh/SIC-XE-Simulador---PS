package model.entities;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Macros {
	private static List<Instrucao> esqueleto = new ArrayList<>();
	private List<Instrucao> originalMacro = new ArrayList<>();
	private Instrucao prototipo;
	private Instrucao chamada;
	
	
	public Macros(List<Instrucao> macro) {
		super();
		macro.forEach(x -> esqueleto.add(x));
		macro.forEach(x -> originalMacro.add(x));
		prototipo = esqueleto.get(1);
		esqueleto.remove(0);
		esqueleto.remove(0);
		esqueleto.remove(esqueleto.size()-1);
	}
	
	public List<Instrucao> getOriginalMacro() {
		return originalMacro;
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
		List<String> parametros = new ArrayList<>();
		parametros.add(prototipo.getRotulo());
		prototipo.getArgs().forEach(x -> parametros.add(x));
		for(Instrucao instrucaoMacro: esqueleto) {
			int cont = 0;
			for(String parametro: parametros) {
				List<String> novosArgumentos = new ArrayList<>();
				String endereco = "#" + cont;
				if(instrucaoMacro.getRotulo().equals(parametro)) {
					instrucaoMacro.setRotulo(endereco);
				}
				for(String argumento: instrucaoMacro.getArgs()) {
					if(argumento.equals(parametro)) {
						novosArgumentos.add(endereco);
					}
					else{
						novosArgumentos.add(argumento);
					}
					instrucaoMacro.setArgs(novosArgumentos);
				}
				novosArgumentos.clear();
				cont++;
			}
		}
		//saidaMacro();
	}
	
	public void modoDeExpansao(Instrucao chamada) {
		setChamada(chamada);
		List<String> parametros = new ArrayList<>();
		parametros.add(chamada.getRotulo());
		chamada.getArgs().forEach(x -> parametros.add(x));
		for (int cont=0; cont < parametros.size(); cont++) {
			String endereco = "#" + cont;
			List<String> novosArgumentos = new ArrayList<>();
			for (Instrucao esqueletoMacro : esqueleto) {
				if (esqueletoMacro.getRotulo().equals(endereco)) {
					esqueletoMacro.setRotulo(parametros.get(cont));
				}
				for (String args : esqueletoMacro.getArgs()) {
					if (args.equals(endereco)) {
						novosArgumentos.add(parametros.get(cont));
					} else {
						novosArgumentos.add(args);
					}
					esqueletoMacro.setArgs(novosArgumentos);
				}
				novosArgumentos.clear();
			}
		}
		parametros.clear();
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
