import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.swing.JOptionPane;

@SuppressWarnings("all")
public class CacheDownloader implements Runnable {

    public static final String ZIP_FILE_PATH = "./cache/cache.zip";
    public static final String VERSION_FILE_PATH = "./cache/cacheVersion.dat";

    private final Client client;

    public CacheDownloader(Client client) {
        this.client = client;
    }

    @Override
    public void run() {
        client.drawLoadingText(0, "Checking Versions");
        try {
            double newest = getNewestVersion();
            double current = getCurrentVersion();

            if (newest > current) {
                client.drawLoadingText(0, "Update found!");
                alert("Best Budz", "Update version " + newest + " has been found!\nClient will now automatically update.", true);

                
                updateClient();

                client.drawLoadingText(0, "Best Budz has been updated!");
                alert("Best Budz", "Download finished! Restarting the Client...", false);

                try (OutputStream out = new FileOutputStream(VERSION_FILE_PATH)) {
                    out.write(String.valueOf(newest).getBytes());
                }

                try {
                    Runtime.getRuntime().exec("java -jar myApp.jar");
                } catch (IOException ex) {
                    alert("Restart Failed", "Could not restart the client.\n" + ex.getMessage(), true);
                }

                System.exit(0);
            }
        } catch (IOException | NumberFormatException e) {
            handleException(e);
        }
    }

    private double getCurrentVersion() {
        try (BufferedReader br = new BufferedReader(new FileReader(VERSION_FILE_PATH))) {
            return Double.parseDouble(br.readLine());
        } catch (Exception e) {
            return 0.1;
        }
    }

    private double getNewestVersion() {
        return getCurrentVersion(); // Local-only cache
    }

    private void updateClient() {
        File zipFile = new File(ZIP_FILE_PATH);
        if (!zipFile.exists()) {
            alert("Best Budz", "Local cache.zip not found at: " + ZIP_FILE_PATH, true);
            return;
        }

        try {
            unZipFile(zipFile, new File(ClientConstants.CACHE_LOCATION));
            zipFile.delete();
        } catch (IOException e) {
            handleException(e);
        }
    }

    private void unZipFile(File zipFile, File outDir) throws IOException {
        long totalSize = calculateUncompressedSize(zipFile);
        long extracted = 0;

        try (ZipInputStream zin = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile)))) {
            ZipEntry entry;
            while ((entry = zin.getNextEntry()) != null) {
                File outFile = new File(outDir, entry.getName());
                if (entry.isDirectory()) {
                    outFile.mkdirs();
                    continue;
                }

                outFile.getParentFile().mkdirs();
                try (FileOutputStream out = new FileOutputStream(outFile)) {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = zin.read(buffer)) > 0) {
                        out.write(buffer, 0, len);
                        extracted += len;
                        setUnzipPercent((int) ((extracted * 100) / Math.max(1, totalSize)));
                    }
                }
            }
        }
    }

    private long calculateUncompressedSize(File zipFile) throws IOException {
        long size = 0;
        try (ZipInputStream zin = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry e;
            while ((e = zin.getNextEntry()) != null) {
                size += Math.max(0, e.getSize());
            }
        }
        return size;
    }

    private void alert(String title, String msg, boolean error) {
        JOptionPane.showMessageDialog(null, msg, title, error ? JOptionPane.ERROR_MESSAGE : JOptionPane.PLAIN_MESSAGE);
    }

    private void handleException(Exception e) {
        alert("Best Budz", "Something went wrong!\nError Code: [" + e.getClass().getSimpleName() + "]", true);
        System.exit(1);
    }

    public void setDownloadPercent(int amount) {
        ProgressBar.updateValue(amount);
        ProgressBar.updateString("(1/2) Downloading cache - " + amount + "%");
        client.drawLoadingText(amount, "(1/2) Downloading BestBudzCache - " + amount + "%");
    }

    public void setUnzipPercent(int amount) {
        ProgressBar.updateValue(amount);
        ProgressBar.updateString("(2/2) Extracting cache - " + amount + "%");
        client.drawLoadingText(amount, "(2/2) Extracting cache - " + amount + "%");
    }
}
