package br.com.marcoscastelini.controller;

import br.com.marcoscastelini.util.Configuracao;
import br.com.marcoscastelini.util.Impressora;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import javax.print.PrintService;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerConfiguracao implements Initializable {
    @FXML
    private TextField txtNomeEstabelecimento;
    @FXML
    private TextArea txtExames, txtConvenios;
    @FXML
    private ComboBox<Integer> cmbQtdePadrao;
    @FXML
    private ComboBox<PrintService> cmbImpressora;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        for (int i = 1; i <= 16; i++)
            cmbQtdePadrao.getItems().add(i);

        txtNomeEstabelecimento.setText(Configuracao.getProperties().getProperty("nome_estabelecimento"));
        txtExames.setText(Configuracao.getProperties().getProperty("exames"));
        txtConvenios.setText(Configuracao.getProperties().getProperty("convenios"));

        cmbQtdePadrao.getSelectionModel().select(Integer.parseInt(Configuracao.getProperties().getProperty("qtde_etiquetas")) - 1);

        Impressora.selecionarImpressora(cmbImpressora);
    }

    @FXML
    public void onSalvar(ActionEvent event) {
        Configuracao.getProperties().setProperty("nome_estabelecimento", txtNomeEstabelecimento.getText());
        Configuracao.getProperties().setProperty("exames", txtExames.getText());
        Configuracao.getProperties().setProperty("convenios", txtConvenios.getText());
        Configuracao.getProperties().setProperty("qtde_etiquetas", cmbQtdePadrao.getSelectionModel().getSelectedItem().toString());
        Configuracao.getProperties().setProperty("impressora", cmbImpressora.getSelectionModel().getSelectedItem().getName());

        Configuracao.save();

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Configurações salvas, a aplicação será reiniciada.", ButtonType.OK);

        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK)
                try {
                    Configuracao.reiniciarAplicacao();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        });
    }
}
