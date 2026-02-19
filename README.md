# SkyGroove

Player offline Android em Kotlin com estética **Imperial High-Tech Dark Fantasy**.

## Arquitetura
- `ui/`: telas Compose (Trono, Biblioteca, Player Supremo, Grimórios, Perfil, Estatísticas).
- `domain/`: modelos e regras de progressão (XP/moedas/nível).
- `data/`: MediaStore scanner + Room (favoritos, histórico, playlists, estatísticas).
- `service/`: `MediaSessionService` para playback em background e notificação de mídia.

Escolha: **Clean-ish + MVVM** em módulo único para MVP robusto e fácil de evoluir para multi-módulo.

## Rodar
1. Android Studio Iguana+.
2. Sincronize Gradle.
3. Execute em device Android 8+.
4. Conceda permissão de mídia no onboarding.

## Testar
- `./gradlew test`

## Evolução
- Adicionar Paging para bibliotecas grandes.
- Completar edição visual dos Grimórios.
- Ranking mensal e badges imperiais.


## Troubleshooting (Gradle cache corrompido no Windows)
Se aparecer erro como `CorruptedCacheException ... caches/journal-1/file-access.bin`:

1. Feche Android Studio e terminais do Gradle.
2. Rode o script de reparo:
   - PowerShell: `./scripts/repair-gradle-cache.ps1`
   - Bash: `./scripts/repair-gradle-cache.sh`
3. Rebaixe metadados/dependências:
   - `./gradlew --refresh-dependencies help`
4. Tente novamente:
   - `./gradlew test`

> Observação: o projeto desabilita file system watching (`org.gradle.vfs.watch=false`) para reduzir reincidência em ambientes Windows com journal corrompido.


## Build resiliente (evita cache global corrompido)
Se o Windows continuar falhando com `Corrupted IndexBlock/DataBlock ... file-access.bin` e erro secundário de assinatura (`Configuration.fileCollection(...)`), execute o build com **cache isolado do projeto**:

- PowerShell: `./scripts/gradle-safe.ps1 test`
- Bash: `./scripts/gradle-safe.sh test`

Esses scripts forçam `GRADLE_USER_HOME=.gradle-user-home` dentro do repositório, evitando o cache global de `C:\Users\<usuario>\.gradle`.
