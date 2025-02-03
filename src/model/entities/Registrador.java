package model.entities;


import java.util.HashMap;
import java.util.Map;

public class Registrador {
	private Map<String, String> registradores = new HashMap<>();
	
	public Registrador() {
		registradores.put("A", "000000");
		registradores.put("X", "000000");
		registradores.put("L", "000000");
		registradores.put("B", "000000");
		registradores.put("S", "000000");
		registradores.put("T", "000000");
		registradores.put("F", "000000000000");
		registradores.put("PC", "000000");	
	}
	
	public Map<String, String> getRegistradores() {
		return registradores;
	}

	public String getRegistrador(String reg) {
		return registradores.get(reg);
	}
	
	public Boolean setRegistrador(String reg, String valor) {
		if(valor == null) {
			return false;
		}
		if(registradores.containsKey(reg)) {
			Integer dec = Func.hexa_para_Int(valor);
			if(dec == null) {
				return false;
			}
			if(valor.length() != 6) {
				int tamanho_valor = 6 - valor.length();
				String registrador = registradores.get(reg);
				String novo_valor = "";
				
				for(int i=0; i<tamanho_valor; i++) {
					novo_valor.concat(registrador.substring(i, i+1));
				}
				
				for(int i=0; i<valor.length(); i++) {
					novo_valor.concat(valor.substring(i, i+1));
				}
				
				registradores.remove(reg);
				registradores.put(reg, novo_valor);
				return true;
			}
			
			registradores.remove(reg);
			registradores.put(reg, valor);
			return true;
		}
		return false;
	}

	public void imprimir_registradores() {
		registradores.forEach((chave, valor) -> System.out.println(chave + " " + Func.hexa_para_Int(valor)));
	}
	
}

