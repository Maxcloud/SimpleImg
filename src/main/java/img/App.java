package img;

import img.crypto.WzCryptography;
import img.json.KeyFileRepository;
import img.service.ExtractJsonService;
import img.service.ExtractImgService;
import img.service.ExtractWithoutCanvas;
import picocli.CommandLine;
import wz.ListWzFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@CommandLine.Command(name = "app", mixinStandardHelpOptions = true, version = "1.0", description = "WZ/Img Command Line Tool")
public class App implements Runnable {

    @CommandLine.Option(names = "--version", required = true,
            description = "The version of the game to use.")
    private short version;

    @CommandLine.Option(names = "--mode", required = true,
            description = "The mode of operation: 'img', 'json' or 'rebuild'.")
    private String mode;

    @CommandLine.Option(names = "--input", required = true,
            description = "The input path to the Wz/Img files.")
    private String inPath;

    @CommandLine.Option(names = "--output", required = true,
            description = "The output path to the Wz/Img files.")
    private String outPath;

    @CommandLine.Option(names = "--merge", required = true,
            description = "Whether to merge the Wz/Img files or not. For example Effect00.wz, Effect01.wz.")
    private boolean bMergeDirectories;

    private WzCryptography cryptography;
    private KeyFileRepository<WzVersion> repository;

    @Override
    public void run() {
        var iPath = Path.of(inPath);
        var oPath = Path.of(outPath);
        this.repository = new KeyFileRepository<>(WzVersion.class);
        this.repository.setSecret(String.format("8:%d:1", version));
        byte[] secret = getSecret();

        if (mode.equals("img")) {
            System.out.println("Extracting img files. Please wait...");

            var service = new ExtractImgService(oPath, version, secret);
            service.readWzDirectory(iPath);
            if (bMergeDirectories) {
                System.out.println("Extraction Complete. Merging folders now.");
                try {
                    Thread.sleep(1000);
                    service.fnMoveFiles(oPath);
                } catch (InterruptedException | IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                System.out.println("Extraction Complete.");
            }

        } else if (mode.equals("json")) {
            System.out.println("Extracting strings to JSON. Please wait, this might take awhile...");
            Path listWz = iPath.resolve("List.wz");
            List<String> entries = Files.isRegularFile(listWz)
                    ? new ListWzFile(listWz, secret).entries()
                    : List.of();
            ExtractJsonService export = new ExtractJsonService(
                    version,
                    secret,
                    entries
            );
            export.dump(outPath);
            System.out.println("Extraction of strings to JSON completed. Please double check the logs for any errors.");
        } else if (mode.equals("rebuild")) {
            System.out.println("Starting to re-write img files without canvas properties. Please wait...");
            var service = new ExtractWithoutCanvas(oPath, iPath, version, secret);

            service.writeImgFile(iPath);
            System.out.println("Operation completed. Please double check the logs for any errors.");
        }
    }

    public byte[] getSecret() {
        if (cryptography == null) {
            cryptography = new WzCryptography(repository);
        }
        return cryptography.getSecret();
    }

    public static void main(String[] args) {
        CommandLine command = new CommandLine(new App());

        int exitCode = command.execute(args);
        System.exit(exitCode);
    }
}
