import java.io.File;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.zip.CRC32;

public class Package {

    public static final int FIRST_PACKAGE_ID = -1;
    private int id;
    private int totalPackages;
    private String md5Hash;
    private String fileName;
    private byte[] data;
    private long crc;

    private Package() {
    }

    private static Package builder() {
        return new Package();
    }

    public static Package buildFirst(File file, String md5Hash) {
        byte[] idFragment = ByteBuffer.allocate(Constants.OFFSET_SIZE).putInt(FIRST_PACKAGE_ID).array();

        int totalPackages = (int) Math.ceil(((double) file.length()) / Constants.DATA_SIZE);
        byte[] totalFragment = ByteBuffer.allocate(Constants.OFFSET_SIZE).putInt(totalPackages).array();

        byte[] hashFragment = md5Hash.getBytes();

        String fileName = file.getName();
        byte[] filenameFragment = fileName.getBytes();

        byte[] data = new byte[Constants.BUFFER_SIZE];

        System.arraycopy(idFragment, 0, data, 0, Constants.OFFSET_SIZE);
        System.arraycopy(totalFragment, 0, data, Constants.OFFSET_SIZE, Constants.OFFSET_SIZE);
        System.arraycopy(hashFragment, 0, data, Constants.OFFSET_SIZE * 2, 16);
        System.arraycopy(filenameFragment, 0, data, idFragment.length + totalFragment.length + 16, filenameFragment.length);

        return Package.builder()
            .setId(FIRST_PACKAGE_ID)
            .setTotalPackages(totalPackages)
            .setMd5Hash(md5Hash)
            .setFileName(fileName)
            .setData(data);
    }

    public static Package build(int id, byte[] content) {
        byte[] idFragment = ByteBuffer.allocate(Constants.OFFSET_SIZE).putInt(id).array();
        byte[] data = new byte[Constants.BUFFER_SIZE];

        System.arraycopy(idFragment, 0, data, 0, idFragment.length);
        System.arraycopy(content, 0, data, idFragment.length + 8, content.length);

        CRC32 crc32 = new CRC32();
        crc32.update(Arrays.copyOfRange(data, 12, data.length));
        long crc = crc32.getValue();
        System.arraycopy(longToBytes(crc), 0, data, idFragment.length, 8);

        return Package.builder()
            .setId(id)
            .setCrc(crc)
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
            byte[] totalFragment = Arrays.copyOfRange(content, Constants.OFFSET_SIZE, Constants.OFFSET_SIZE * 2);
            int totalPackages = ByteBuffer.wrap(totalFragment).getInt();

            byte[] hashFragment = Arrays.copyOfRange(content, Constants.OFFSET_SIZE * 2, (Constants.OFFSET_SIZE * 2) + 16);
            String md5Hash = new String(hashFragment).trim();

            byte[] fileNameFragment = Arrays.copyOfRange(content, (Constants.OFFSET_SIZE * 2) + 16, content.length);
            String filename = new String(fileNameFragment).trim();
            return Package.builder()
                .setId(id)
                .setTotalPackages(totalPackages)
                .setMd5Hash(md5Hash)
                .setFileName(filename);
        }

        byte[] crcFragment = Arrays.copyOfRange(content, idFragment.length, Constants.OFFSET_SIZE + 8);
        long crc = ByteBuffer.wrap(crcFragment).getLong();

        byte[] data = Arrays.copyOfRange(content, Constants.OFFSET_SIZE + 8, content.length);
        return Package.builder()
            .setId(id)
            .setCrc(crc)
            .setData(data);
    }

    public static Package decomposeACK(byte[] content) {
        byte[] idFragment = Arrays.copyOfRange(content, 0, Constants.OFFSET_SIZE);
        int id = ByteBuffer.wrap(idFragment).getInt();
        return Package.builder()
            .setId(id);
    }

    public static byte[] longToBytes(long l) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte) (l & 0xFF);
            l >>= 8;
        }
        return result;
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

    public String getMd5Hash() {
        return md5Hash;
    }

    private Package setMd5Hash(String md5Hash) {
        this.md5Hash = md5Hash;
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

    public long getCrc() {
        return crc;
    }

    private Package setCrc(long crc) {
        this.crc = crc;
        return this;
    }

    public void validate() {
        CRC32 crc32 = new CRC32();
        crc32.update(this.data);
        if (this.getCrc() != crc32.getValue()) {
            System.err.println("CRC INVÁLIDO, PACK ID: " + this.getId());
        }
    }
}
