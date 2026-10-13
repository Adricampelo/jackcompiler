public class XmlWriter {

    // Converte o tipo interno na categoria oficial do nand2tetris
    public static String category(TokenType type) {
        switch (type) {
            case NUMBER:
                return "integerConstant";
            case STRING:
                return "stringConstant";
            case IDENT:
                return "identifier";

            case LPAREN: case RPAREN:
            case LBRACE: case RBRACE:
            case LBRACKET: case RBRACKET:
            case COMMA: case SEMICOLON: case DOT:
            case PLUS: case MINUS: case ASTERISK: case SLASH:
            case AND: case OR: case NOT:
            case LT: case GT: case EQ:
                return "symbol";

            case EOF:
                throw new IllegalArgumentException("EOF não é escrito no XML");

            default:
                return "keyword";
        }
    }

    // O & precisa ser o primeiro, senão ele escapa os próprios escapes
    public static String escape(String text) {
        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
    
      public static String write(Scanner scanner) {
        StringBuilder sb = new StringBuilder();
        sb.append("<tokens>\n");

        Token token = scanner.nextToken();
        while (token.getType() != TokenType.EOF) {
          String tag = category(token.getType());

          sb.append("<").append(tag).append("> ")
            .append(escape(token.getLexeme()))
            .append(" </").append(tag).append(">\n");

          token = scanner.nextToken();
      }

      sb.append("</tokens>\n");
      return sb.toString();
  }

}
