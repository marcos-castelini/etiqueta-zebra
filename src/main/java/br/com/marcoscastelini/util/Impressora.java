package br.com.marcoscastelini.util;

import javafx.scene.control.ComboBox;
import one.util.streamex.StreamEx;

import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;

public class Impressora {
    public static void selecionarImpressora(ComboBox<PrintService> comboBox) {
        DocFlavor df = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(df, null);
        comboBox.getItems().addAll(printServices);

        if (Configuracao.getProperties().getProperty("impressora") != null)
            StreamEx.of(printServices)
                    .filter(printService -> printService.getName().contains(Configuracao.getProperties().getProperty("impressora")))
                    .findAny()
                    .ifPresent(printService -> comboBox.getSelectionModel().select(printService));
    }
}
