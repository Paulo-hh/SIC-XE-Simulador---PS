package model.entities;

import java.io.Serializable;
import java.util.ArrayList;

import javafx.collections.ObservableList;

public class Memoria implements Serializable {

	 ArrayList<Object> memoria = new ArrayList<Object>(500);

	    private final int max_size = 500; //Cada posição corresponde à uma palavra de 16bits - No total a memória terá 1KB

	    ListaMemoria listaMemoria;

	    /*Construtor da memoria*/
	    public Memoria (ObservableList<ListaMemoria> list){ //Inicializa toda a memória
	       for (Integer i = 0;i<max_size;i++){
	           memoria.add(null); //Preenche a pilha com NULL
	           list.add(i, new ListaMemoria(i.toString(), "0"));
	        }

	        memoria.set(2,10); 
	        list.set(2, new ListaMemoria("2","10"));
	    }
	    
	    public void set_element (int index, Integer element){
	        memoria.set(index,element);
	    }

	    public void set_string (int index, String element){
	        memoria.set(index,element);
	    }

	    public Object get(int index) {
	        return memoria.get(index);
	    }
	
}
