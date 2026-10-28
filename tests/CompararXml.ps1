
$ErrorActionPreference = "Stop"

$grupos = @(
    "ExpressionlessSquare",
    "Square",
    "ArrayTest"
)

$total = 0
$aprovados = 0

foreach ($grupo in $grupos) {
    $origem = "tests/$grupo"
    $destino = "output/$grupo"

    New-Item -ItemType Directory -Force -Path $destino | Out-Null

    & java -cp "./out" Main $origem $destino

    if ($LASTEXITCODE -ne 0) {
        Write-Host "FALHA na execução: $grupo"
        exit 1
    }

    $arquivos = Get-ChildItem $origem -Filter "*.jack"

    foreach ($arquivo in $arquivos) {
        $nome = [System.IO.Path]::GetFileNameWithoutExtension($arquivo.Name)

        foreach ($sufixo in @(".xml", "T.xml")) {
            $total++

            $esperado = Join-Path $origem ($nome + $sufixo)
            $gerado = Join-Path $destino ($nome + $sufixo)

            if (!(Test-Path $esperado) -or !(Test-Path $gerado)) {
                Write-Host "FALHA: arquivo ausente - $grupo/$nome$sufixo"
                continue
            }

            $textoEsperado = [System.IO.File]::ReadAllText(
                (Resolve-Path $esperado)
            )

            $textoGerado = [System.IO.File]::ReadAllText(
                (Resolve-Path $gerado)
            )

            $linhasEsperadas = $textoEsperado -split '\r?\n' |
                ForEach-Object { $_.Trim() } |
                Where-Object { $_ -ne "" }

            $linhasGeradas = $textoGerado -split '\r?\n' |
                ForEach-Object { $_.Trim() } |
                Where-Object { $_ -ne "" }

            $diferenca = Compare-Object `
                -ReferenceObject $linhasEsperadas `
                -DifferenceObject $linhasGeradas `
                -SyncWindow 0

            if ($null -eq $diferenca) {
                $aprovados++
                Write-Host "OK: $grupo/$nome$sufixo"
            } else {
                Write-Host "FALHA: $grupo/$nome$sufixo"
            }
        }
    }
}

Write-Host ""
Write-Host "Resultado: $aprovados de $total testes aprovados."

if ($aprovados -ne $total) {
    exit 1
}
