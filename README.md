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
