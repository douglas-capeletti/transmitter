import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class FileManager {

    private final UserInterface ui;
    private File file;
    private String outputFilename;
    private String md5Hash;
    private InputStream inputStream;
    private FileOutputStream outputStream;

    public FileManager(File file) {
        this.ui = new UserInterface("[FILE_MANAGER]");
        this.file = file;
    }

    public FileManager initReader() {
        try {
            inputStream = new FileInputStream(file);
            this.setMd5Hash(generateMD5Hash());
        } catch (FileNotFoundException e) {
            ui.abort("Error while initializing file");
        }
        return this;
    }

    public FileManager initWriter() {
        outputFilename += " " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_hh-mm-ss"));
        file = new File(file, outputFilename);
        try {
            if (!file.createNewFile()) {
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

    public void writeAndValidate(byte[][] packages) {
        try {
            for (int i = 0; i < packages.length; i++) {
                byte[] bytes = packages[i];
                String content = new String(bytes).trim();
                if (!(content.isEmpty())) {
                    outputStream.write(content.getBytes());
                }
            }
            validateMd5Hash();
        } catch (IOException e) {
            ui.abort("Error while writing file");
        }
    }

    private void validateMd5Hash() {
        String newHash = generateMD5Hash();
        if (newHash.equals(md5Hash)) {
            ui.log("MD5Hash successfully validated");
        } else {
            ui.abort("Invalid MD5Hash");
        }
    }

    public String getMd5Hash() {
        return md5Hash;
    }

    public void setMd5Hash(String md5Hash) {
        this.md5Hash = md5Hash;
    }

    public void setOutputFilename(String outputFilename) {
        this.outputFilename = outputFilename;
    }

    public String generateMD5Hash() {
        try {
            MessageDigest md5Digest = MessageDigest.getInstance("MD5");
            String md5Hash = getFileChecksum(md5Digest, file);
            ui.log("Hash MD5 do arquivo: " + md5Hash);
            return md5Hash;
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            ui.log("Error generating MD5Hash");
        }
        return null;
    }

    private String getFileChecksum(MessageDigest digest, File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);

        byte[] byteArray = new byte[1024];
        int bytesCount;

        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        }

        fis.close();
        StringBuilder sb = new StringBuilder();

        byte[] bytes = digest.digest();
        for (byte aByte : bytes) {
            sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }
}
