import java.util.Arrays;

public class Parser {

    private final Scanner scan;
    private Token currentToken;
    private Token peekToken;

    private final StringBuilder xmlOutput = new StringBuilder();

    public Parser(byte[] input) {
        this.scan = new Scanner(input);

        // Carrega o primeiro e o segundo tokens.
        advance();
        advance();
    }

    // Avança os tokens.
    private void advance() {
        currentToken = peekToken;
        peekToken = scan.nextToken();
    }

    // Consulta o próximo token sem consumi-lo.
    private Token peek() {
        return peekToken;
    }

    // Verifica o tipo do token atual.
    private boolean match(TokenType type) {
        return currentToken != null
                && currentToken.getType() == type;
    }

    // Verifica, registra e consome o token atual.
    private Token consume(TokenType expected) {
        if (!match(expected)) {
            throw error(
                    currentToken,
                    "Esperado " + expected.name()
            );
        }

        Token token = currentToken;
        
        if (token.getType() != TokenType.EOF) {
            writeTerminal(token);
        }

        advance();

        return token;
    }

    // Escreve um token terminal no XML.
    private void writeTerminal(Token token) {
        String tag = XmlWriter.category(token.getType());

        xmlOutput.append("<")
                .append(tag)
                .append("> ")
                .append(XmlWriter.escape(token.getLexeme()))
                .append(" </")
                .append(tag)
                .append(">\n");
    }

    // Cria uma mensagem de erro sintático.
    private RuntimeException error(
            Token token,
            String message
    ) {
        String encontrado;

        if (token == null) {
            encontrado = "nenhum token";
        } else {
            encontrado = token.getType()
                    + " ('" + token.getLexeme() + "')";
        }

        return new IllegalArgumentException(
                "Erro sintático: "
                        + message
                        + ". Encontrado: "
                        + encontrado
        );
    }

    // Retorna o XML produzido até o momento.
    public String getXmlOutput() {
        return xmlOutput.toString();
    }
}