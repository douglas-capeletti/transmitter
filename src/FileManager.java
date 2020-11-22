import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class FileManager {

    private static final Logger log = new Logger("[FILE_MANAGER] ");
    private File file;
    private String outputFilename;
    private InputStream inputStream;
    private FileOutputStream outputStream;

    public FileManager(File file) {
        this.file = file;
    }

    public FileManager initReader() {
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            log.error("Erro ao inicializar arquivo", e);
        }
        return this;
    }

    public FileManager initWriter() {
        outputFilename += "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_hh-mm-ss"));
        file = new File(file, outputFilename);
        try {
            if (file.createNewFile()) {
                log.info("Arquivo de saída criado com sucesso");
            }
            outputStream = new FileOutputStream(file);
        } catch (IOException e) {
            log.error("Erro ao inicializar arquivo", e);
        }
        return this;
    }

    public byte[][] readNPackets(int packetN, int packetSize) {
        byte[][] packets = new byte[packetN][packetSize];
        try {
            for (int i = 0; i < packetN; i++) {
                if (inputStream.available() > 0) {
                    packets[i] = inputStream.readNBytes(packetSize);
                } else {
                    return Arrays.copyOf(packets, i);
                }
            }
        } catch (IOException e) {
            log.error("Erro ao ler pacotes", e);
        }
        return packets;
    }

    public void writePackages(byte[][] packages) {
        try {
            for (byte[] bytes : packages) {
                outputStream.write(bytes);
            }
        } catch (IOException e) {
            log.error("Erro ao escrever no arquivo destino", e);
        }
    }

    public void setOutputFilename(String outputFilename) {
        this.outputFilename = outputFilename;
    }

    public File getFile() {
        return file;
    }
}
