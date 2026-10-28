
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {
        if (args.length < 1 || args.length > 2) {
            System.err.println("Uso: java Main <arquivo.jack | pasta> [pasta-de-saida]");
            System.exit(1);
        }

        Path input = Path.of(args[0]);
        Path outDir = Path.of(args.length == 2 ? args[1] : "output");

        try {
            Files.createDirectories(outDir);

            for (Path file : findJackFiles(input)) {
                compile(file, outDir);
            }

        } catch (IOException e) {
            System.err.println("Erro de leitura/escrita: " + e.getMessage());
            System.exit(1);

        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private static List<Path> findJackFiles(Path input) throws IOException {
        if (Files.isDirectory(input)) {
            try (Stream<Path> files = Files.list(input)) {
                return files
                        .filter(p -> p.toString().endsWith(".jack"))
                        .sorted()
                        .toList();
            }
        }

        if (Files.isRegularFile(input)) {
            return List.of(input);
        }

        throw new IOException("Caminho não encontrado: " + input);
    }

    private static void compile(Path file, Path outDir) throws IOException {
        byte[] bytes = Files.readAllBytes(file);

        Scanner scanner = new Scanner(bytes);
        String tokensXml = XmlWriter.write(scanner);

        Parser parser = new Parser(bytes);
        parser.compileClass();

        String syntaxXml = parser.getXmlOutput();

        String fileName = file.getFileName().toString();
        String baseName = fileName.substring(0, fileName.length() - 5);

        Path tokensTarget = outDir.resolve(baseName + "T.xml");
        Path syntaxTarget = outDir.resolve(baseName + ".xml");

        Files.writeString(
                tokensTarget,
                tokensXml,
                StandardCharsets.UTF_8
        );

        Files.writeString(
                syntaxTarget,
                syntaxXml,
                StandardCharsets.UTF_8
        );

        System.out.println("Gerado: " + tokensTarget);
        System.out.println("Gerado: " + syntaxTarget);
    }
}
