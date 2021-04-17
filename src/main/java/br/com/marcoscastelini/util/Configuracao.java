package br.com.marcoscastelini.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Properties;

public class Configuracao {
    private static Properties properties;
    private final static String fileName = "./config.properties";

    public static void load() {
        try {
            File file = new File(fileName);
            boolean newFile = false;
            if (!file.exists()) {
                newFile = file.createNewFile();
            }
            FileInputStream fis = new FileInputStream(fileName);
            properties = new Properties();
            properties.load(fis);

            if (newFile) {
                properties.setProperty("nome_estabelecimento", "Nome do estabelecimento");
                properties.setProperty("qtde_etiquetas", "8");
                properties.setProperty("exames", "Endoscopia,Raio-X");
                properties.setProperty("convenios", "Unimed");
                properties.setProperty("impressora", "");
            }

            save();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Properties getProperties() {
        return properties;
    }

    public static void save() {
        try {
            properties.store(new FileOutputStream(fileName), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void reiniciarAplicacao() throws IOException {
        String SUN_JAVA_COMMAND = "sun.java.command";
        try {
            String java = System.getProperty("java.home") + "/bin/java";
            List<String> vmArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
            StringBuilder vmArgsOneLine = new StringBuilder();
            for (String arg : vmArguments) {
                if (!arg.contains("-agentlib")) {
                    vmArgsOneLine.append(arg);
                    vmArgsOneLine.append(" ");
                }
            }
            final StringBuffer cmd = new StringBuffer("\"" + java + "\" " + vmArgsOneLine);
            String[] mainCommand = System.getProperty(SUN_JAVA_COMMAND).split(" ");
            if (mainCommand[0].endsWith(".jar") || mainCommand[0].endsWith(".exe")) {
                cmd.append("-jar ").append(new File(mainCommand[0]).getPath());
            } else {
                cmd.append("-cp \"").append(System.getProperty("java.class.path")).append("\" ").append(mainCommand[0]);
            }
            for (int i = 1; i < mainCommand.length; i++) {
                cmd.append(" ");
                cmd.append(mainCommand[i]);
            }
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(500);
                        Runtime.getRuntime().exec(cmd.toString());
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

            System.exit(0);
        } catch (Exception e) {
            throw new IOException("Erro ao reinciiar a aplicação\n" + e.getMessage(), e);
        }
    }
}
