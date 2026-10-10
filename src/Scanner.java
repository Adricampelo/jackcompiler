
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

class Scanner {

    private final byte[] input;
    private int current = 0;

    private static final Map<String, TokenType> keywords =
            new HashMap<>();

    // Palavras-chave do Jack
    static {
        keywords.put("class", TokenType.CLASS);
        keywords.put("constructor", TokenType.CONSTRUCTOR);
        keywords.put("function", TokenType.FUNCTION);
        keywords.put("method", TokenType.METHOD);
        keywords.put("field", TokenType.FIELD);
        keywords.put("static", TokenType.STATIC);
        keywords.put("var", TokenType.VAR);
        keywords.put("int", TokenType.INT);
        keywords.put("char", TokenType.CHAR);
        keywords.put("boolean", TokenType.BOOLEAN);
        keywords.put("void", TokenType.VOID);
        keywords.put("true", TokenType.TRUE);
        keywords.put("false", TokenType.FALSE);
        keywords.put("null", TokenType.NULL);
        keywords.put("this", TokenType.THIS);
        keywords.put("let", TokenType.LET);
        keywords.put("do", TokenType.DO);
        keywords.put("if", TokenType.IF);
        keywords.put("else", TokenType.ELSE);
        keywords.put("while", TokenType.WHILE);
        keywords.put("return", TokenType.RETURN);
    }

    public Scanner(byte[] input) {
        this.input = input;
    }

    private boolean isAtEnd() {
        return current >= input.length;
    }

    private char advance() {
        return (char) (input[current++] & 0xFF);
    }

    private char peek() {
        if (isAtEnd()) {
            return '\0';
        }

        return (char) (input[current] & 0xFF);
    }


    private boolean isAlpha(char ch) {
        return (ch >= 'a' && ch <= 'z')
                || (ch >= 'A' && ch <= 'Z');
    }


    private boolean isAlphaNumeric(char ch) {
        return isAlpha(ch)
                || (ch >= '0' && ch <= '9')
                || ch == '_';
    }


    private Token identifier() {
        int start = current;

        while (!isAtEnd() && isAlphaNumeric(peek())) {
            advance();
        }

        String text = new String(
                input,
                start,
                current - start,
                StandardCharsets.UTF_8
        );

        TokenType type = keywords.get(text);

        if (type == null) {
            type = TokenType.IDENT;
        }

        return new Token(type, text);
    }


    public Token nextToken() {
        while (!isAtEnd() && Character.isWhitespace(peek())) {
            advance();
        }

        if (isAtEnd()) {
            return new Token(TokenType.EOF, "");
        }

        if (isAlpha(peek())) {
            return identifier();
        }

        throw new Error(
                "Caractere inesperado: " + peek()
        );
    }
}
