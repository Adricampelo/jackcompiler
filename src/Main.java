import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Uso: java Main <arquivo.jack>");
            System.exit(1);
        }

        Path file = Path.of(args[0]);

        try {
            tokenize(file);
        } catch (IOException e) {
            System.err.println("Erro ao ler " + file + ": " + e.getMessage());
            System.exit(1);
        }
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
