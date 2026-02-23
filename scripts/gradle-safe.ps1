param(
  [Parameter(ValueFromRemainingArguments = $true)]
  [string[]]$GradleArgs
)

$ErrorActionPreference = "Stop"
$projectRoot = Split-Path -Parent $PSScriptRoot
$safeGradleHome = Join-Path $projectRoot ".gradle-user-home"

if (-not (Test-Path $safeGradleHome)) {
  New-Item -ItemType Directory -Path $safeGradleHome | Out-Null
}

Write-Host "[SkyGroove] Usando GRADLE_USER_HOME isolado: $safeGradleHome" -ForegroundColor Cyan
$env:GRADLE_USER_HOME = $safeGradleHome

try {
  & gradle --stop | Out-Null
} catch {
  Write-Host "Aviso: não foi possível parar daemons antigos." -ForegroundColor Yellow
}

if ($GradleArgs.Count -eq 0) {
  $GradleArgs = @("help")
}

& gradle @GradleArgs
