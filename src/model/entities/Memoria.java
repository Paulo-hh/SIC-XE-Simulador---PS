package model.entities;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Memoria {
	private ObservableList<ListaMemoria> memoria = FXCollections.observableArrayList();
	
	public Memoria() {
		for(int i=0; i<256; i++) {
			memoria.add(new ListaMemoria(Integer.toString(i), "00"));
		}
	}
	
	public ObservableList<ListaMemoria> obterListaMemoria(){
		return memoria;
	}
	
	public String getMemoria(String endereco) {
		Integer dec = Func.hexa_para_Int(endereco);
		
		if(dec == null) {
			return null;
		}
		
		if(dec < 0 || dec > 256) {
			return null;
		}
		
		return memoria.get(dec).getValor();
	}
	
	public Boolean setMemoria(String endereco, String byte_valor) {
		Integer dec_endereco = Func.hexa_para_Int(endereco);
		Integer dec_byte = Func.hexa_para_Int(byte_valor);
		
		if(dec_endereco == null || dec_byte == null) {
			return false;
		}
		
		if(dec_endereco < 0 || dec_endereco > 256) {
			return false;
		}
		memoria.get(dec_endereco).setValor(byte_valor);
		return true;
	}	
}
