
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        String code =
                "class Main { let contador = 132 + 5; "
                        + "let mensagem = \"Ola mundo\"; }";

        Scanner scanner = new Scanner(code.getBytes(StandardCharsets.UTF_8));

        Token token;
        do {
            token = scanner.nextToken();
            System.out.println(token);
        } while (token.getType() != TokenType.EOF);
    }
}
