Write-Host "[SkyGroove] Reparando cache corrompido do Gradle..." -ForegroundColor Cyan

$gradleHome = Join-Path $env:USERPROFILE ".gradle"
$journalPath = Join-Path $gradleHome "caches\journal-1"

if (-not (Test-Path $gradleHome)) {
  Write-Host "Diretório $gradleHome não encontrado. Nada para limpar." -ForegroundColor Yellow
  exit 0
}

try {
  & gradle --stop | Out-Null
} catch {
  Write-Host "Aviso: não foi possível executar 'gradle --stop'. Prosseguindo..." -ForegroundColor Yellow
}

if (Test-Path $journalPath) {
  Write-Host "Removendo $journalPath" -ForegroundColor Yellow
  Remove-Item $journalPath -Recurse -Force -ErrorAction SilentlyContinue
}

Write-Host "Cache de journal removido. Próximo passo:" -ForegroundColor Green
Write-Host "  ./gradlew --refresh-dependencies help" -ForegroundColor Green
