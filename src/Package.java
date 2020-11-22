import java.io.File;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class Package {

    public static final int FIRST_PACKAGE_ID = -1;
    private int id;
    private int totalPackages;
    private String fileName;
    private byte[] data;

    private Package() {
    }

    private static Package builder() {
        return new Package();
    }

    public static Package buildFirst(File file) {
        byte[] idFragment = ByteBuffer.allocate(Constants.OFFSET_SIZE).putInt(FIRST_PACKAGE_ID).array();

        int totalPackages = (int) Math.ceil(((double) file.length()) / Constants.DATA_SIZE);
        byte[] totalFragment = ByteBuffer.allocate(Constants.OFFSET_SIZE).putInt(totalPackages).array();

        String fileName = file.getName();
        byte[] filenameFragment = fileName.getBytes();

        byte[] data = new byte[Constants.BUFFER_SIZE];
        System.arraycopy(idFragment, 0, data, 0, idFragment.length);
        System.arraycopy(totalFragment, 0, data, idFragment.length, totalFragment.length);
        System.arraycopy(filenameFragment, 0, data, idFragment.length + totalFragment.length, filenameFragment.length);

        return Package.builder()
            .setId(FIRST_PACKAGE_ID)
            .setTotalPackages(totalPackages)
            .setFileName(fileName)
            .setData(data);
    }

    public static Package build(int id, byte[] content) {
        byte[] idFragment = ByteBuffer.allocate(Constants.OFFSET_SIZE).putInt(id).array();
        byte[] data = new byte[Constants.BUFFER_SIZE];
        System.arraycopy(idFragment, 0, data, 0, idFragment.length);
        System.arraycopy(content, 0, data, idFragment.length, content.length);
        return Package.builder()
            .setId(id)
            .setData(data);
    }

    public static Package buildACK(int id) {
        byte[] idFragment = ByteBuffer.allocate(Constants.OFFSET_SIZE).putInt(id).array();
        return Package.builder()
            .setId(id)
            .setData(idFragment);
    }

    public static Package decompose(byte[] content) {
        byte[] idFragment = Arrays.copyOfRange(content, 0, Constants.OFFSET_SIZE);
        int id = ByteBuffer.wrap(idFragment).getInt();
        if (id == FIRST_PACKAGE_ID) {
            byte[] totalFragment = Arrays.copyOfRange(content, idFragment.length, Constants.OFFSET_SIZE * 2);
            int totalPackages = ByteBuffer.wrap(totalFragment).getInt();
            byte[] fileNameFragment = Arrays.copyOfRange(content, Constants.OFFSET_SIZE * 2, content.length);
            String filename = new String(fileNameFragment).trim();
            return Package.builder()
                .setId(id)
                .setTotalPackages(totalPackages)
                .setFileName(filename);
        }
        byte[] data = Arrays.copyOfRange(content, Constants.OFFSET_SIZE, content.length);
        return Package.builder()
            .setId(id)
            .setData(data);
    }

    public static Package decomposeACK(byte[] content) {
        byte[] idFragment = Arrays.copyOfRange(content, 0, Constants.OFFSET_SIZE);
        int id = ByteBuffer.wrap(idFragment).getInt();
        return Package.builder()
            .setId(id);
    }

    public boolean isFirst() {
        return id == FIRST_PACKAGE_ID;
    }

    public int getId() {
        return id;
    }

    private Package setId(int id) {
        this.id = id;
        return this;
    }

    public int getTotalPackages() {
        return totalPackages;
    }

    private Package setTotalPackages(int totalPackages) {
        this.totalPackages = totalPackages;
        return this;
    }

    public String getFileName() {
        return fileName;
    }

    private Package setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public byte[] getData() {
        return data;
    }

    private Package setData(byte[] data) {
        this.data = data;
        return this;
    }
}
