import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FileSystem extends Thread {

    private String folderLocation;

    public FileSystem(String folderLocation) {
        this.folderLocation = folderLocation;
    }

    public void run() {
        File f = new File(folderLocation);
        f.mkdirs();

        while (true) {
            updatePing();
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (Exception e) {
                System.err.println("FileSystem Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void updatePing() {
        findFiles(folderLocation, Main.files);
        int totalSize = 0;
        for (File file : Main.files) {
            totalSize += file.length();
        }

        Main.myPing.sizeOfFiles = totalSize;
        Main.myPing.numFiles = Main.files.size();
    }

    private void findFiles(String directoryName, List<File> f) {
        File directory = new File(directoryName);

        File[] fList = directory.listFiles();
        if (fList != null) {
            for (File file : fList) {
                if (file.isFile() && !Main.files.contains(file)) {
                    f.add(file);
                } else if (file.isDirectory()) {
                    findFiles(file.getAbsolutePath(), f);
                }
            }
        }
    }
}