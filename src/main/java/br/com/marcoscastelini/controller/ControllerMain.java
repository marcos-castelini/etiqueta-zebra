package br.com.marcoscastelini.controller;

import br.com.marcoscastelini.model.Sexo;
import br.com.marcoscastelini.util.Configuracao;
import br.com.marcoscastelini.util.Impressora;
import br.com.marcoscastelini.util.Mascara;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import one.util.streamex.StreamEx;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import javax.print.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class ControllerMain implements Initializable {
    private static ControllerMain instance;

    @FXML
    private Label txtNomeEstabelecimento;
    @FXML
    private TextField txtNome;
    @FXML
    private TextField txtNomeMae;
    @FXML
    private TextField txtNascimento;
    @FXML
    private ComboBox<Sexo> cmbSexo;
    @FXML
    private TextField txtConvenio;
    @FXML
    private TextField txtMedico;
    @FXML
    private GridPane gridExames;
    @FXML
    private ComboBox<Integer> cmbQtde;
    @FXML
    private ComboBox<PrintService> cmbImpressora;

    private List<String> exames;
    private Set<String> convenios;

    private AutoCompletionBinding<String> autoCompleteConvenio;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Mascara.mascaraGenerica("##/##/####", txtNascimento, Mascara.REGEX_SOMENTE_NUMEROS);
        Impressora.selecionarImpressora(cmbImpressora);

        for (int i = 1; i <= 16; i++)
            cmbQtde.getItems().add(i);

        txtNomeEstabelecimento.setText(Configuracao.getProperties().getProperty("nome_estabelecimento"));
        cmbQtde.getSelectionModel().select(Integer.parseInt(Configuracao.getProperties().getProperty("qtde_etiquetas")) - 1);

        cmbSexo.getItems().addAll(Sexo.values());

        this.exames = Arrays.asList(Configuracao.getProperties().getProperty("exames").split(","));
        this.convenios = new HashSet<>(Arrays.asList(Configuracao.getProperties().getProperty("convenios").split(",")));

        autoCompleteConvenio = TextFields.bindAutoCompletion(txtConvenio, convenios);
        initializeGridExames();

        txtConvenio.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                adicionarConvenio(txtConvenio.getText().trim());
            }
        });
        txtConvenio.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue)
                adicionarConvenio(txtConvenio.getText().trim());
        });
    }

    private void adicionarConvenio(String convenio) {
        if (!convenios.contains(convenio)) {
            convenios.add(convenio);
            Configuracao.getProperties().setProperty("convenios", String.join(",", convenios));
            Configuracao.save();
        }

        if (autoCompleteConvenio != null) {
            autoCompleteConvenio.dispose();
        }
        autoCompleteConvenio = TextFields.bindAutoCompletion(txtConvenio, convenios);

    }

    public void initializeGridExames() {
        CheckBox checkBox;
        int coluna = 0;
        int linha = 0;
        for (int i = 0; i < exames.size(); i++) {
            String exame = exames.get(i);
            checkBox = new CheckBox(exame.trim());

            GridPane.setConstraints(checkBox, coluna, linha);
            gridExames.getChildren().add(checkBox);
            if (i > 0 && i % 2 != 0) {
                linha++;
            }
            if (coluna == 0)
                coluna = 1;
            else
                coluna = 0;
        }
    }

    @FXML
    public void onPrint(ActionEvent event) {
        String exames = gridExames.getChildren().stream().filter(node -> ((CheckBox) node).isSelected()).map(node -> ((CheckBox) node).getText()).collect(Collectors.joining(" + "));
        PrintService impressora = cmbImpressora.getSelectionModel().getSelectedItem();
        String impressao = "I8,A,001\n" +
                "\n" +
                "\n" +
                "Q240,024\n" +
                "q831\n" +
                "rN\n" +
                "S4\n" +
                "D7\n" +
                "ZT\n" +
                "JF\n" +
                "OD\n" +
                "R16,0\n" +
                "f100\n" +
                "N\n" +
                "A18,28,0,4,1,1,N,\"Nome:\"\n" +
                "A103,28,0,4,1,1,N,\"{nome}\"\n" +
                "A18,57,0,4,1,1,N,\"Nome Mãe:\"\n" +
                "A169,57,0,4,1,1,N,\"{nomeMae}\"\n" +
                "A18,89,0,4,1,1,N,\"Nasc:\"\n" +
                "A107,89,0,4,1,1,N,\"{nascimento}\"\n" +
                "A288,89,0,4,1,1,N,\"Sexo:\"\n" +
                "A377,89,0,4,1,1,N,\"{sexo}\"\n" +
                "A18,121,0,4,1,1,N,\"Convênio:\"\n" +
                "A169,121,0,4,1,1,N,\"{convenio}\"\n" +
                "A16,150,0,4,1,1,N,\"Médico:\"\n" +
                "A133,150,0,4,1,1,N,\"{medico}\"\n" +
                "A18,185,0,4,1,1,N,\"Exame:\"\n" +
                "A120,185,0,4,1,1,N,\"{exame}\"\n" +
                "P1\n";


        impressao = impressao.replace("{nome}", txtNome.getText());
        impressao = impressao.replace("{nomeMae}", txtNomeMae.getText());
        impressao = impressao.replace("{nascimento}", txtNascimento.getText());
        impressao = impressao.replace("{sexo}", cmbSexo.getSelectionModel().getSelectedItem().getAbreviacao());
        impressao = impressao.replace("{convenio}", txtConvenio.getText());
        impressao = impressao.replace("{medico}", txtMedico.getText());
        impressao = impressao.replace("{exame}", exames);

        if (impressora != null) {
            int qtde = cmbQtde.getSelectionModel().getSelectedItem();
            for (int i = 0; i < qtde; i++) {
                try {
                    DocPrintJob dpj = impressora.createPrintJob();
                    InputStream stream = new ByteArrayInputStream(impressao.getBytes(StandardCharsets.ISO_8859_1));
                    DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
                    Doc doc = new SimpleDoc(stream, flavor, null);
                    dpj.print(doc, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    public void onConfiguracoes(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Configuracao.fxml"));
            Stage palco = new Stage();
            palco.setTitle("Configurações");
            StackPane pane;

            pane = (StackPane) loader.load();

            Scene scene = new Scene(pane);
            palco.setScene(scene);
            palco.initModality(Modality.WINDOW_MODAL);
            palco.initOwner(((Node) event.getSource()).getScene().getWindow());
            palco.setResizable(false);
            palco.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ControllerMain getInstance() {
        if (instance == null) {
            instance = new ControllerMain();
        }
        return instance;
    }

}
