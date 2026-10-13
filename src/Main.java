import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Uso: java Main <arquivo.jack | pasta>");
            System.exit(1);
        }

        Path input = Path.of(args[0]);

        try {
            for (Path file : findJackFiles(input)) {
                System.out.println("=== " + file.getFileName() + " ===");
                tokenize(file);
            }
        } catch (IOException e) {
            System.err.println("Erro de leitura: " + e.getMessage());
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

    private static void tokenize(Path file) throws IOException {
        byte[] bytes = Files.readAllBytes(file);
        Scanner scanner = new Scanner(bytes);

        Token token;
        do {
            token = scanner.nextToken();
            System.out.println(token);
        } while (token.getType() != TokenType.EOF);
    }
}
