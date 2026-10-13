import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class TestRunner {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) throws IOException {
        Path officialDir = Path.of(args.length > 0 ? args[0] : "tests/Square");

        runUnitTests();
        runOfficialTests(officialDir);
        finish();
    }

    // ---------- testes unitários ----------

    private static void runUnitTests() {
        System.out.println("== Testes unitários ==");

        compare("keywords, identificador, símbolo e inteiro",
                xml("let x = 5;"),
                tokens("<keyword> let </keyword>",
                       "<identifier> x </identifier>",
                       "<symbol> = </symbol>",
                       "<integerConstant> 5 </integerConstant>",
                       "<symbol> ; </symbol>"));

        compare("identificador começando com underscore",
                xml("_x1"),
                tokens("<identifier> _x1 </identifier>"));

        compare("comentário de linha é ignorado",
                xml("// comentario\nclass"),
                tokens("<keyword> class </keyword>"));

        compare("comentário de linha no fim do arquivo, sem quebra",
                xml("class // fim"),
                tokens("<keyword> class </keyword>"));

        compare("comentários de bloco e de documentação são ignorados",
                xml("/* a */ class /** b\n * c */ Main"),
                tokens("<keyword> class </keyword>",
                       "<identifier> Main </identifier>"));

        compare("barra isolada é divisão, não comentário",
                xml("a / b"),
                tokens("<identifier> a </identifier>",
                       "<symbol> / </symbol>",
                       "<identifier> b </identifier>"));

        compare("// dentro de string não é comentário",
                xml("\"http://x\""),
                tokens("<stringConstant> http://x </stringConstant>"));

        compare("string sai sem as aspas",
                xml("\"ola mundo\""),
                tokens("<stringConstant> ola mundo </stringConstant>"));

        compare("escape de <, > e &",
                xml("a < b & c > d"),
                tokens("<identifier> a </identifier>",
                       "<symbol> &lt; </symbol>",
                       "<identifier> b </identifier>",
                       "<symbol> &amp; </symbol>",
                       "<identifier> c </identifier>",
                       "<symbol> &gt; </symbol>",
                       "<identifier> d </identifier>"));

        compare("colchetes de array",
                xml("a[i]"),
                tokens("<identifier> a </identifier>",
                       "<symbol> [ </symbol>",
                       "<identifier> i </identifier>",
                       "<symbol> ] </symbol>"));

        compare("maior inteiro permitido (32767)",
                xml("32767"),
                tokens("<integerConstant> 32767 </integerConstant>"));

        expectError("número acima de 32767", "32768");
        expectError("comentário de bloco sem fechamento", "class /* abc");
        expectError("string sem aspa de fechamento", "\"abc");
        expectError("caractere inesperado", "@");
    }

    // ---------- comparação com os arquivos oficiais ----------

    private static void runOfficialTests(Path dir) throws IOException {
        System.out.println();
        System.out.println("== Comparação com os XML oficiais (" + dir + ") ==");

        if (!Files.isDirectory(dir)) {
            fail("arquivos oficiais", "pasta não encontrada: " + dir);
            return;
        }

        List<Path> jackFiles;
        try (Stream<Path> files = Files.list(dir)) {
            jackFiles = files
                    .filter(p -> p.toString().endsWith(".jack"))
                    .sorted()
                    .toList();
        }

        if (jackFiles.isEmpty()) {
            fail("arquivos oficiais", "nenhum .jack encontrado em " + dir);
            return;
        }

        for (Path jack : jackFiles) {
            String base = jack.getFileName().toString().replace(".jack", "");
            Path expectedXml = dir.resolve(base + "T.xml");

            if (!Files.exists(expectedXml)) {
                fail(base + ".jack", "XML oficial não encontrado: " + expectedXml);
                continue;
            }

            try {
                Scanner scanner = new Scanner(Files.readAllBytes(jack));
                String actual = XmlWriter.write(scanner);
                String expected = Files.readString(expectedXml, StandardCharsets.UTF_8);
                compare(base + ".jack  vs  " + base + "T.xml", actual, expected);
            } catch (Throwable t) {
                fail(base + ".jack", "erro ao processar: " + t);
            }
        }
    }

    // ---------- auxiliares ----------

    private static String xml(String code) {
        Scanner scanner = new Scanner(code.getBytes(StandardCharsets.UTF_8));
        return XmlWriter.write(scanner);
    }

    private static String tokens(String... lines) {
        return "<tokens>\n" + String.join("\n", lines) + "\n</tokens>\n";
    }

    private static String normalize(String text) {
        return text.replace("\r\n", "\n").stripTrailing();
    }

    private static void compare(String name, String actual, String expected) {
        String[] a = normalize(actual).split("\n", -1);
        String[] e = normalize(expected).split("\n", -1);
        int n = Math.min(a.length, e.length);

        for (int i = 0; i < n; i++) {
            if (!a[i].equals(e[i])) {
                fail(name, "linha " + (i + 1)
                        + "\n      esperado: " + e[i]
                        + "\n      obtido:   " + a[i]);
                return;
            }
        }

        if (a.length != e.length) {
            fail(name, "quantidade de linhas diferente: esperado "
                    + e.length + ", obtido " + a.length);
            return;
        }

        pass(name);
    }

    private static void expectError(String name, String code) {
        try {
            xml(code);
            fail(name, "era esperado um erro, mas nenhum foi lançado");
        } catch (Throwable t) {
            pass(name);
        }
    }

    private static void pass(String name) {
        passed++;
        System.out.println("  [OK]    " + name);
    }

    private static void fail(String name, String detail) {
        failed++;
        System.out.println("  [FALHA] " + name + "\n      " + detail);
    }

    private static void finish() {
        System.out.println();
        System.out.println("Resultado: " + passed + " passaram, " + failed + " falharam");
        if (failed > 0) {
            System.exit(1);
        }
    }
}