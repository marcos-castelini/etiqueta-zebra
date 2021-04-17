package br.com.marcoscastelini.util;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by marquinhos on 12/08/16.
 */
public class Mascara {

    public static DecimalFormat FORMATADOR_VALOR_SEM_SIFRA = new DecimalFormat(" #,##0.00");
    public static DecimalFormat FORMATADOR_VALOR_COM_SIFRA = new DecimalFormat("R$ #,##0.00");
    public static final String REGEX_SOMENTE_NUMEROS = "[[A-Z][a-z]\\W]";
    public static final String REGEX_LETRAS_NUMEROS = "[\\W]";

    public static String REGEX_DATA_COMPLETA = "\\d{4}-\\d{2}-\\d{2}";

    public static String inscricaoFederal(String insc) {
        if (insc == null) {
            insc = "";
        }
        insc = insc.replaceAll("[^0-9]", "");
        switch (insc.length()) {
            case 11:
                //Mascara de Pessoa Física
                return insc = insc.substring(0, 3) + "." + insc.substring(3, 6) + "." + insc.substring(6, 9) + "-" + insc.substring(9, 11);
            case 14:
                //Mascara de Pessoa Jurídica
                return insc = insc.substring(0, 2) + "." + insc.substring(2, 5) + "." + insc.substring(5, 8) + "/" + insc.substring(8, 12) + "-" + insc.substring(12, 14);
            default:
                return insc;
        }
    }

    public static void telefone(TextField textField) {
        textField.lengthProperty().addListener((observable, oldValue, newValue) -> {

            Platform.runLater(() -> {
                String value = textField.getText();
                value = value.replaceAll("[^0-9]", "");
                int tam = value.length();
                value = value.replaceFirst("(\\d{2})(\\d)", "($1)$2");
                value = value.replaceFirst("(\\d{4})(\\d)", "$1-$2");
                if (tam > 10) {
                    value = value.replaceAll("-", "");
                    value = value.replaceFirst("(\\d{5})(\\d)", "$1-$2");
                }
                textField.setText(value);
            });
            positionCaret(textField);

            //Limita em um valor de até 14 digitos
            textField.textProperty().addListener((observableValue, oldValue1, newValue1) -> {
                if (newValue1.length() > 14) {
                    textField.setText(oldValue1);
                }
            });

        });
    }

    public static String telefone(String telefone) {
        if(telefone == null)
            return "";

        String telefoneMascara = "";

        if (telefone.length() == 8) {
            //Verifica se só tem numeros
            if (telefone.matches("^[0-9]*$")) {
                telefoneMascara = (telefone.substring(0, 4) + "-" + telefone.substring(4));
            }
        } else if (telefone.length() == 9) {
            //Verifica se só tem numeros
            if (telefone.matches("^[0-9]*$")) {
                telefoneMascara = (telefone.substring(0, 5) + "-" + telefone.substring(5));
            }
        } else if (telefone.length() == 10) {
            //Verifica se só tem numeros
            if (telefone.matches("^[0-9]*$")) {
                telefoneMascara = ("(" + telefone.substring(0, 2) + ")"
                        + telefone.substring(2, 6) + "-" + telefone.substring(6));
            }
        } else if (telefone.length() == 11) {
            //Verifica se só tem numeros
            if (telefone.matches("^[0-9]*$")) {
                telefoneMascara = ("(" + telefone.substring(0, 2) + ")"
                        + telefone.substring(2, 7) + "-" + telefone.substring(7));
            }
        } else {
            telefoneMascara = (telefone);
        }
        return telefoneMascara;
    }

    public static String CEP(String cep) {
        if (cep == null) {
            cep = "";
        }
        cep = cep.replaceAll("[^0-9]", "");
        switch (cep.length()) {
            case 8:
                return cep.substring(0, 5) + "-" + cep.substring(5, 8);
            default:
                return "";
        }
    }

    public static String decimalFormat(int casas, double valor) {
        String formato = "#,##0.";
        for (int i = 0; i < casas; i++) {
            formato = formato.concat("0");
        }
        DecimalFormat decimal = new DecimalFormat(formato);
        return decimal.format(valor);
    }

    /**
     * Monta a mascara para Data (dd/MM/yyyy).
     *
     * @param textField TextField
     */
    public static void dateField(final TextField textField, int qtdeCaracteres) {
        maxField(textField, qtdeCaracteres);

        textField.lengthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            if (newValue.intValue() < qtdeCaracteres) {
                String value = textField.getText();
                value = value.replaceAll("[^0-9]", "");
                if(value.length() > 2 && value.length() < 5)
                value = value.replaceFirst("(\\d{2})(\\d)", "$1/$2");
                value = value.replaceFirst("(\\d{2})\\/(\\d{2})(\\d{4})", "$1/$2/$3");
                //value = value.replaceFirst("(\\d{2})\\/(\\d{2})(\\d)", "$1/$2/$3");
                textField.setText(value);
                positionCaret(textField);


            }
        });
    }


    /**
     * @param textField TextField.
     * @param length    Tamanho do campo.
     */
    public static void maxField(final TextField textField, final Integer length) {
        textField.textProperty().addListener((ObservableValue<? extends String> observableValue, String oldValue, String newValue) -> {
            if (newValue.length() > length) {
                textField.setText(oldValue);
            }
        });
    }

    @FXML
    public static void monetaryField(final TextField textField) {
        textField.lengthProperty().addListener((observable, oldValue, newValue) -> {

            Platform.runLater(() -> {

                String value = textField.getText();

                if (value.equals("0,00")) {
                    value = "0";
                } else if (value.contains(",")) {
                    if (value.indexOf(",") == value.length() - 1 && value.length() > 4) {
                        value = value.replaceAll("[^0-9]", "");
                        value = value.replaceAll("([0-9]{1})([0-9]{12})$", "$1.$2");
                        value = value.replaceAll("([0-9]{1})([0-9]{9})$", "$1.$2");
                        value = value.replaceAll("([0-9]{1})([0-9]{6})$", "$1.$2");
                        value = value.replaceAll("([0-9]{1})([0-9]{3})$", "$1.$2");
                        value = value.replaceAll("([0-9]{1})([0-9]{0})$", "$1,$2");
                    } else if (value.indexOf(",") == value.length() - 2 && value.length() > 4) {
                        value = value.replaceAll("[^0-9]", "");
                        value = value.replaceAll("([0-9]{1})([0-9]{13})$", "$1.$2");
                        value = value.replaceAll("([0-9]{1})([0-9]{10})$", "$1.$2");
                        value = value.replaceAll("([0-9]{1})([0-9]{7})$", "$1.$2");
                        value = value.replaceAll("([0-9]{1})([0-9]{4})$", "$1.$2");
                        value = value.replaceAll("([0-9]{1})([0-9]{1})$", "$1,$2");
                    } else if (value.indexOf(",") == value.length() - 4 && value.length() > 4) {
                        value = value.substring(0, value.length() - 1);
                    } else if (value.length() <= 4) {
                        value = "0,";
                    } else {
                        value = value.replaceAll("[^0-9]", "");
                        value = value.replaceAll("([0-9]{1})([0-9]{14})$", "$1.$2");
                        value = value.replaceAll("([0-9]{1})([0-9]{11})$", "$1.$2");
                        value = value.replaceAll("([0-9]{1})([0-9]{8})$", "$1.$2");
                        value = value.replaceAll("([0-9]{1})([0-9]{5})$", "$1.$2");
                        value = value.replaceAll("([0-9]{1})([0-9]{2})$", "$1,$2");
                    }
                } else {
                    value = value.replaceAll("[^0-9]", "");
                    value = value.replaceAll("([0-9]{1})([0-9]{13})$", "$1.$2");
                    value = value.replaceAll("([0-9]{1})([0-9]{9})$", "$1.$2");
                    value = value.replaceAll("([0-9]{1})([0-9]{6})$", "$1.$2");
                    value = value.replaceAll("([0-9]{1})([0-9]{3})$", "$1.$2");
//                    value = value.replaceAll("([0-9]{1})([0-9]{2})$", "$1,$2");
                }
                textField.setText("R$ " + value);
            });

            positionCaret(textField);


            //Limita em um valor de até 14 digitos
            textField.textProperty().addListener((observableValue, oldValue1, newValue1) -> {
                if (newValue1.length() > 17)
                    textField.setText(oldValue1);
            });
        });
    }

    @FXML
    public static void percentField(final TextField textField) {
        textField.lengthProperty().addListener((observable, oldValue, newValue) -> {

            Platform.runLater(() -> {

                String value = textField.getText();

                if (value.equals("0,00")) {
                    value = "0";
                } else if (value.contains(",")) {
                    if (value.indexOf(",") == value.length() - 1 && value.length() > 4) {
                        value = value.replaceAll("[^0-9]", "");
                        value = value.replaceAll("([0-9]{1})([0-9]{12})$", "$1.$2");
                        value = value.replaceAll("([0-9]{1})([0-9]{9})$", "$1.$2");
                        value = value.replaceAll("([0-9]{1})([0-9]{6})$", "$1.$2");
                        value = value.replaceAll("([0-9]{1})([0-9]{3})$", "$1.$2");
                        value = value.replaceAll("([0-9]{1})([0-9]{0})$", "$1,$2");
                    } else if (value.indexOf(",") == value.length() - 2 && value.length() > 4) {
                        value = value.replaceAll("[^0-9]", "");
                        value = value.replaceAll("([0-9]{1})([0-9]{13})$", "$1.$2");
                        value = value.replaceAll("([0-9]{1})([0-9]{10})$", "$1.$2");
                        value = value.replaceAll("([0-9]{1})([0-9]{7})$", "$1.$2");
                        value = value.replaceAll("([0-9]{1})([0-9]{4})$", "$1.$2");
                        value = value.replaceAll("([0-9]{1})([0-9]{1})$", "$1,$2");
                    } else if (value.indexOf(",") == value.length() - 4 && value.length() > 4) {
                        value = value.substring(0, value.length() - 1);
                    } else if (value.length() <= 4) {
                        value = "0,";
                    } else {
                        value = value.replaceAll("[^0-9]", "");
                        value = value.replaceAll("([0-9]{1})([0-9]{14})$", "$1.$2");
                        value = value.replaceAll("([0-9]{1})([0-9]{11})$", "$1.$2");
                        value = value.replaceAll("([0-9]{1})([0-9]{8})$", "$1.$2");
                        value = value.replaceAll("([0-9]{1})([0-9]{5})$", "$1.$2");
                        value = value.replaceAll("([0-9]{1})([0-9]{2})$", "$1,$2");
                    }
                } else {
                    value = value.replaceAll("[^0-9]", "");
                    value = value.replaceAll("([0-9]{1})([0-9]{13})$", "$1.$2");
                    value = value.replaceAll("([0-9]{1})([0-9]{9})$", "$1.$2");
                    value = value.replaceAll("([0-9]{1})([0-9]{6})$", "$1.$2");
                    value = value.replaceAll("([0-9]{1})([0-9]{3})$", "$1.$2");
//                    value = value.replaceAll("([0-9]{1})([0-9]{2})$", "$1,$2");
                }
                textField.setText("% " + value);
            });

            positionCaret(textField);


            //Limita em um valor de até 14 digitos
            textField.textProperty().addListener((observableValue, oldValue1, newValue1) -> {
                if (newValue1.length() > 17)
                    textField.setText(oldValue1);
            });
        });
    }

    private static void positionCaret(final TextField textField) {
        Platform.runLater(() -> {
            // Posiciona o cursor sempre a direita.
            textField.positionCaret(textField.getText().length());
        });
    }

    /**
     * @param mascara   formato de exebição
     * @param textfield campo em que a mascara vai ser aplicada
     * @param regex     caracteres negados
     */
    public static void mascaraGenerica(String mascara, TextField textfield, String regex) {
        textfield.lengthProperty().addListener((ObservableValue<? extends Number> observableValue, Number number, Number number2) -> {

            String alphaAndDigits = textfield.getText().replaceAll(regex, "");
            StringBuilder resultado = new StringBuilder();
            int i = 0;
            int quant = 0;

            if (number2.intValue() > number.intValue()) {
                if (textfield.getText().length() <= mascara.length()) {
                    while (i < mascara.length()) {
                        if (quant < alphaAndDigits.length()) {
                            if ("#".equals(mascara.substring(i, i + 1))) {
                                resultado.append(alphaAndDigits.substring(quant, quant + 1));
                                quant++;
                            } else {
                                resultado.append(mascara.substring(i, i + 1));
                            }
                        }
                        i++;
                    }
                    textfield.setText(resultado.toString());
                }
                if (textfield.getText().length() > mascara.length()) {
                    textfield.setText(textfield.getText(0, mascara.length()));
                }
            }
        });
    }

    @FXML
    public static void mascaraQuantidade(TextField textField) {
        textField.setAlignment(Pos.CENTER_LEFT);

        textField.lengthProperty().addListener((observable, oldValue, newValue) -> {

            Platform.runLater(() -> {
                String value = textField.getText();
                value = value.replaceAll("[^0-9\\.]", "");
                //value = value.replaceAll("([0-9]{1})([0-9]{3})$", "$1.$2");
                value = value.replaceAll("([0-9]{4})$", "$1");
                textField.setText(value);
            });

            positionCaret(textField);

            //Limita em um valor de at� 4 digitos
            textField.textProperty().addListener((observableValue, oldValue1, newValue1) -> {
                if (newValue1.length() > 5)
                    textField.setText(oldValue1);
            });
        });
    }

    @FXML
    public static void mascaraQuantidadeInteira(TextField textField) {
        textField.setAlignment(Pos.CENTER_LEFT);

        textField.lengthProperty().addListener((observable, oldValue, newValue) -> {

            Platform.runLater(() -> {
                String value = textField.getText();
                value = value.replaceAll("[^0-9]", "");
//                    value = value.replaceAll("([0-9])$", "$1");
                textField.setText(value);
            });

            positionCaret(textField);

            //Limita em um valor de at� 4 digitos
            textField.textProperty().addListener((observableValue, oldValue1, newValue1) -> {
                if (newValue1.length() > 5)
                    textField.setText(oldValue1);
            });
        });
    }

    @FXML
    public static void mascaraQuantidadeInteira(TextField textField, int limit) {
        textField.setAlignment(Pos.CENTER_LEFT);

        textField.lengthProperty().addListener((observable, oldValue, newValue) -> {

            Platform.runLater(() -> {
                String value = textField.getText();
                value = value.replaceAll("[^0-9]", "");
                if (!value.equals(""))
                    if (Integer.parseInt(value) > limit) {
                        value = value.substring(0, value.length() - 1);
                    }
                textField.setText(value);
            });

            positionCaret(textField);

            //Limita em um valor de at� 4 digitos
            textField.textProperty().addListener((observableValue, oldValue1, newValue1) -> {
                if (newValue1.length() > 5)
                    textField.setText(oldValue1);
            });
        });
    }

    @FXML
    public static void mascaraQuantidade(TextField textField, BigDecimal limit) {
        textField.setAlignment(Pos.CENTER_LEFT);
        textField.lengthProperty().addListener((observable, oldValue, newValue) -> {

            Platform.runLater(() -> {
                String value = textField.getText();
                value = value.replaceAll("[^0-9\\.]", "");
                //value = value.replaceAll("([0-9]{1})([0-9]{3})$", "$1.$2");
                value = value.replaceAll("([0-9]{4})$", "$1");
                if (!value.equals("")) {
                    if (limit.compareTo(new BigDecimal(value)) == -1) {
                        value = value.substring(0, value.length() - 1);
                    }
                }
                textField.setText(value);
            });
            positionCaret(textField);

            //Limita em um valor de at� 4 digitos
            textField.textProperty().addListener((observableValue, oldValue1, newValue1) -> {
                if (newValue1.length() > 5)
                    textField.setText(oldValue1);
            });
        });
    }

    public static void porta(final TextField textField) {
        maxField(textField, 5);
        textField.textProperty().addListener((observable, oldValue, newValue) -> {

            newValue = newValue.replaceAll("[^0-9]", "");

            textField.setText(newValue);
        });
    }

    public static void ipOrUrl(final TextField textField) {

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            newValue = newValue.replaceAll("[\\W]", "");

            textField.setText(newValue);
        });
    }

    public static void host(final TextField textField) {

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            newValue = newValue.replaceAll("[^a-zA-Z0-9./]", "");

            textField.setText(newValue);
        });
    }
}