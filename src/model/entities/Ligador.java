package model.entities;

import java.util.ArrayList;
import java.util.List;

public class Ligador {
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
	
	public static List<Instrucao> unirModulos(List<Instrucao> modulo1, List<Instrucao> modulo2) {
		modulo2.remove(0);
		modulo1.removeIf(x -> x.getNome().equals("END"));
		modulo2.forEach(x -> modulo1.add(x));
		return new ArrayList<Instrucao>(modulo1);
	}
}
