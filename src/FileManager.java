import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class FileManager {

    private final UserInterface ui;
    private File file;
    private String outputFilename;
    private InputStream inputStream;
    private FileOutputStream outputStream;

    public FileManager(File file) {
        this.ui = new UserInterface("[FILE_MANAGER]");
        this.file = file;
    }

    public FileManager initReader() {
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            ui.abort("Error while initializing file");
        }
        return this;
    }

    public FileManager initWriter() {
        outputFilename += " " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_hh-mm-ss"));
        file = new File(file, outputFilename);
        try {
            if(!file.createNewFile()){
                ui.abort("Error while creating output file");
            }
            outputStream = new FileOutputStream(file);
        } catch (IOException e) {
            ui.abort("Error while initializing file");
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
            ui.abort("Error while reading file");
        }
        return packets;
    }

    public void writePackages(byte[][] packages) {
        try {
            for (byte[] bytes : packages) {
                outputStream.write(bytes);
            }
        } catch (IOException e) {
            ui.abort("Error while writing file");
        }
    }

    public void setOutputFilename(String outputFilename) {
        this.outputFilename = outputFilename;
    }

    public File getFile(){
        return file;
    }
}
