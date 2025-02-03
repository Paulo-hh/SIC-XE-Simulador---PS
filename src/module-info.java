module javafx {
	requires javafx.controls;
	requires javafx.fxml;
	
	opens application to javafx.graphics, javafx.fxml, javafx.base;
	exports gui to javafx.fxml;  
	opens gui to javafx.fxml;
	opens model.entities to javafx.fxml, javafx.graphics, javafx.base;

}
