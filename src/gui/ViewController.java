package gui;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import model.entities.Func;
import model.entities.Instrucao;
import model.entities.Ligador;
import model.entities.ListaMemoria;
import model.entities.Memoria;
import model.entities.Montador;
import model.entities.Registrador;

public class ViewController implements Initializable {
	
	private List<Instrucao> lista_instrucao_mod1 = new ArrayList<>();
	private List<Instrucao> lista_instrucao_mod2 = new ArrayList<>();
	private List<Instrucao> lista_instrucoes_ligadas;
	private Registrador registrador = new Registrador();
	private Montador montador;
	private Memoria memoria = new Memoria();
	
	public ViewController() {
	}
	
	@FXML
	private TableView<ListaMemoria> tableView = new TableView<ListaMemoria>();
	
	@FXML
	private TableColumn<ListaMemoria, String> colunaEndereco;
	
	@FXML
	private TableColumn<ListaMemoria, String> colunaValor;
	
	@FXML
	private TextArea textoArea1;
	
	@FXML
	private TextArea textoArea2;
	
	@FXML
	private Button rodar;
	
	@FXML
	private Button proximo;
	
	@FXML
	private Button enviar;
	
	@FXML
	private Label A;
	
	@FXML
	private Label X;
	
	@FXML
	private Label L;
	
	@FXML
	private Label B;
	
	@FXML
	private Label S;
	
	@FXML
	private Label T;
	
	@FXML
	private Label PC;
	
	@FXML
	private TextArea saida;
	
	
	@FXML
	public void onBtRodarAction() throws Exception {
		while (true) {
			boolean ok = montador.executar_Proxima_Instrucao();
			mostrarRegistradores();
	        tableView.setItems(getLista());
	    	saida.setText(montador.getTextoSaida());
			if (ok == false) {
				break;
			}
		}
	}
	
	@FXML
	public void onBtEnviarAction() throws Exception {
		montador = new Montador(memoria, registrador);
		String texto1 = textoArea1.getText().replaceAll("\n", System.getProperty("line.separator"));
		lista_instrucao_mod1 = Ligador.lerTexto(texto1);		
		montador.setInstrucoes(lista_instrucao_mod1);
		lista_instrucao_mod1 = montador.processadorDeMacros();
		String texto2 = textoArea2.getText().replaceAll("\n", System.getProperty("line.separator"));
		lista_instrucao_mod2 = Ligador.lerTexto(texto2);
		montador.setInstrucoes(lista_instrucao_mod2);
		lista_instrucao_mod2 = montador.processadorDeMacros();		
		lista_instrucoes_ligadas = Ligador.unirModulos(lista_instrucao_mod1, lista_instrucao_mod2);
		Func.setInstrucoes(lista_instrucoes_ligadas);
		montador.setInstrucoes(lista_instrucoes_ligadas);
		montador.atribuirEndereco();
    	saida.setText(montador.getTextoSaida());
	}
	
	@FXML
	public void onBtProximoAction() throws Exception {
		montador.executar_Proxima_Instrucao();
		mostrarRegistradores();
        tableView.setItems(getLista());
    	saida.setText(montador.getTextoSaida());
	}
	
	@FXML
	public void onMenuItemTabelaSimboloAction() {
		montador.setTabelaSimbolo();
		saida.setText(montador.getTextoSaida());
	}
	
    @Override
    public void initialize (URL url, ResourceBundle rb){
    	colunaEndereco.setCellValueFactory(new PropertyValueFactory<ListaMemoria,String>("endereco"));
        colunaValor.setCellValueFactory(new PropertyValueFactory<ListaMemoria,String>("valor"));
        tableView.setItems(getLista());
    }
    
    public ObservableList<ListaMemoria> getLista(){
    	return memoria.obterListaMemoria();
    }
    
    public void mostrarRegistradores() {
        A.setText(Func.hexa_para_Int(registrador.getRegistrador("A")).toString());
    	X.setText(Func.hexa_para_Int(registrador.getRegistrador("X")).toString());
    	L.setText(Func.hexa_para_Int(registrador.getRegistrador("L")).toString());
    	B.setText(Func.hexa_para_Int(registrador.getRegistrador("B")).toString());
    	S.setText(Func.hexa_para_Int(registrador.getRegistrador("S")).toString());
    	T.setText(Func.hexa_para_Int(registrador.getRegistrador("T")).toString());
    	PC.setText(Func.hexa_para_Int(registrador.getRegistrador("PC")).toString());
    }
    
}
