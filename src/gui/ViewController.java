package gui;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
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
import model.entities.ListaMemoria;
import model.entities.Memoria;
import model.entities.Operacoes;
import model.entities.Registrador;

public class ViewController implements Initializable {
	
	private List<Instrucao> lista_instrucao = new ArrayList<>();
	private Registrador registrador = new Registrador();
	private Operacoes operacao;
	private Memoria memoria = new Memoria();
		
	@FXML
	private TableView<ListaMemoria> tableView = new TableView<ListaMemoria>();
	
	@FXML
	private TableColumn<ListaMemoria, String> colunaEndereco;
	
	@FXML
	private TableColumn<ListaMemoria, String> colunaValor;
	
	@FXML
	private TextArea textoArea;
	
	@FXML
	private Button rodar;
	
	@FXML
	private Button limpar;
	
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
			boolean ok = operacao.executar_Proxima_Instrucao();
			mostrarRegistradores();
	        tableView.setItems(getLista());
	    	saida.setText(operacao.getTextoSaida());
			if (ok == false) {
				break;
			}
		}
	}
	
	@FXML
	public void onBtLimpar() throws Exception{
		memoria = new Memoria();
		registrador = new Registrador();
		textoArea.clear();
		saida.clear();
	}
	
	@FXML
	public void onBtEnviarAction() throws Exception {
		String texto = textoArea.getText().replaceAll("\n", System.getProperty("line.separator"));
		lista_instrucao = Instrucao.lerTexto(texto);
		operacao = new Operacoes(lista_instrucao, memoria, registrador);
		operacao.atribuirEndereco();
    	saida.setText(operacao.getTextoSaida());
	}
	
	@FXML
	public void onBtProximoAction() throws Exception {
		operacao.executar_Proxima_Instrucao();
		mostrarRegistradores();
        tableView.setItems(getLista());
    	saida.setText(operacao.getTextoSaida());
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
