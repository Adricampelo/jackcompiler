
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        String code =
                "// comentario de linha\n"
              + "class Main { /* bloco */\n"
              + "  /** doc\n"
              + "   * multilinha */\n"
              + "  let x = 10 / 2; // divisao\n"
              + "  let s = \"http://x // nao e comentario\";\n"
              + "}";

        Scanner scanner = new Scanner(code.getBytes(StandardCharsets.UTF_8));

        Token token;
        do {
            token = scanner.nextToken();
            System.out.println(token);
        } while (token.getType() != TokenType.EOF);
    }
}
