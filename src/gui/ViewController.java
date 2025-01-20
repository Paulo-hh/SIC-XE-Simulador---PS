package gui;


import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.entities.ListaMemoria;

public class ViewController implements Initializable {
	
	private ObservableList<ListaMemoria> list = FXCollections.observableArrayList();
	
	@FXML
	private TableView<ListaMemoria> tableView;
	
	@FXML
	private TableColumn<ListaMemoria, String> colunaEndereco;
	
	@FXML
	private TableColumn<ListaMemoria, String> colunaValor;
	
	
	
    @Override
    public void initialize (URL url, ResourceBundle rb){
        colunaEndereco.setCellValueFactory(new PropertyValueFactory<ListaMemoria,String>("endereco"));
        colunaValor.setCellValueFactory(new PropertyValueFactory<ListaMemoria,String>("valor"));
        tableView.setItems(getRegistradores());
       // executionMode.setItems(executionModeList);
    }
    
    public ObservableList<ListaMemoria> getRegistradores(){
        return list;
    }
	
}
