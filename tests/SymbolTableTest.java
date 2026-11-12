
public class SymbolTableTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        test("Definir variável static", () -> {
            SymbolTable table = new SymbolTable();

            table.define("contador", "int", Kind.STATIC);

            check(table.kindOf("contador") == Kind.STATIC);
            check(table.typeOf("contador").equals("int"));
            check(table.indexOf("contador") == 0);
            check(table.varCount(Kind.STATIC) == 1);
        });

        test("Índices independentes por categoria", () -> {
            SymbolTable table = new SymbolTable();

            table.define("a", "int", Kind.FIELD);
            table.define("b", "int", Kind.FIELD);
            table.define("c", "int", Kind.STATIC);

            check(table.indexOf("a") == 0);
            check(table.indexOf("b") == 1);
            check(table.indexOf("c") == 0);
        });

        test("Definir argumentos e variáveis locais", () -> {
            SymbolTable table = new SymbolTable();

            table.define("x", "int", Kind.ARGUMENT);
            table.define("y", "boolean", Kind.VAR);

            check(table.indexOf("x") == 0);
            check(table.indexOf("y") == 0);
            check(table.varCount(Kind.ARGUMENT) == 1);
            check(table.varCount(Kind.VAR) == 1);
        });

        test("Reiniciar escopo da sub-rotina", () -> {
            SymbolTable table = new SymbolTable();

            table.define("campo", "int", Kind.FIELD);
            table.define("x", "int", Kind.ARGUMENT);
            table.define("y", "int", Kind.VAR);

            table.startSubroutine();

            check(table.kindOf("campo") == Kind.FIELD);
            check(table.kindOf("x") == Kind.NONE);
            check(table.kindOf("y") == Kind.NONE);
            check(table.varCount(Kind.ARGUMENT) == 0);
            check(table.varCount(Kind.VAR) == 0);
        });

        test("Variável local tem prioridade sobre variável da classe", () -> {
            SymbolTable table = new SymbolTable();

            table.define("valor", "int", Kind.FIELD);
            table.define("valor", "boolean", Kind.VAR);

            check(table.kindOf("valor") == Kind.VAR);
            check(table.typeOf("valor").equals("boolean"));
            check(table.indexOf("valor") == 0);
        });

        test("Identificador não declarado", () -> {
            SymbolTable table = new SymbolTable();

            check(table.kindOf("inexistente") == Kind.NONE);
            check(table.typeOf("inexistente") == null);
            check(table.indexOf("inexistente") == -1);
        });

        test("Impedir declaração duplicada no mesmo escopo", () -> {
            SymbolTable table = new SymbolTable();

            table.define("idade", "int", Kind.VAR);

            expectException(() ->
                    table.define("idade", "boolean", Kind.VAR)
            );
        });

        System.out.println("\n===== RESULTADO DOS TESTES =====");
        System.out.println("Aprovados: " + passed);
        System.out.println("Reprovados: " + failed);

        if (failed > 0) {
            throw new AssertionError("Existem testes que falharam.");
        }
    }

    private static void test(String name, Runnable action) {
        try {
            action.run();
            passed++;
            System.out.println("[APROVADO] " + name);
        } catch (Exception e) {
            failed++;
            System.out.println("[REPROVADO] " + name);
            System.out.println("Motivo: " + e.getMessage());
        }
    }

    private static void check(boolean condition) {
        if (!condition) {
            throw new AssertionError("Condição não atendida.");
        }
    }

    private static void expectException(Runnable action) {
        try {
            action.run();
            throw new AssertionError("Era esperada uma exceção.");
        } catch (IllegalArgumentException expected) {
            // Resultado esperado.
        }
    }
}
