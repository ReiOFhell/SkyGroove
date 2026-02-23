# SkyGroove — Guia de Reconstrução (UI, Player, Ordenação e Performance)

Este guia descreve **como transformar o app atual em uma experiência realmente usável**.

---

## 1) Diagnóstico objetivo dos problemas atuais

### 1.1 Interface “horrível” / UX confusa
- Componentes visuais sem hierarquia clara (ações principais competem com secundárias).
- Falta de consistência entre telas (mesmo conceito com apresentações diferentes).
- “Player Supremo” não se comporta como elemento central da experiência.

### 1.2 Player “devastador” (fluxo quebrado)
- Ao tocar música na biblioteca, o usuário espera um mini-player/controle persistente.
- Esse mini-player não sobe automaticamente, quebrando expectativa básica de qualquer player.
- Controles de reprodução e estado atual não ficam sempre visíveis.

### 1.3 Ordenação de músicas confusa
- Critérios de ordenação não são explícitos na UI.
- Falta seletor de ordenação com indicação clara (A-Z, data, duração, mais tocadas etc.).
- Não existe persistência da preferência de ordenação.

### 1.4 Desempenho ruim e travamentos
- Possíveis recomposições excessivas.
- Lista potencialmente sem paginação/estratégia de renderização incremental.
- Operações pesadas (scan/indexação) concorrendo com UI em momentos críticos.

---

## 2) Meta de produto (definição de “pronto para uso”)

Para considerar o app “usável de verdade”, os seguintes critérios devem ser atendidos:

1. **Clique em música inicia reprodução em < 300 ms percebidos** na maioria dos dispositivos intermediários.
2. **Mini-player fixo** aparece automaticamente após primeira reprodução.
3. **Now Playing/Player Supremo** abre por gesto/toque no mini-player e sempre reflete estado real.
4. **Ordenação explícita e persistida** com feedback visual.
5. **Rolagem suave** na biblioteca com acervo grande (sem congelar UI).

---

## 3) Plano passo a passo (execução recomendada)

## Fase A — Estruturar UX primeiro (sem “embelezar cedo demais”)

### Passo A1 — Definir estrutura de navegação de áudio (Single Source of Truth)
- Criar um `PlaybackUiState` único (ex.: `currentTrack`, `isPlaying`, `positionMs`, `queue`, `repeatMode`, `shuffleOn`).
- Esse estado deve vir da camada de player e ser observado por:
  - Biblioteca
  - Mini-player
  - Player Supremo

**Resultado esperado:** qualquer mudança no player reflete em toda UI automaticamente.

### Passo A2 — Implementar mini-player global
- Inserir mini-player no `Scaffold` principal, acima da `BottomNavigation`.
- Exibir:
  - capa miniatura
  - título/artista
  - play/pause
  - progresso simplificado
- Ao tocar no mini-player: navegar para “Player Supremo”.

**Resultado esperado:** clicou na música → mini-player aparece sempre.

### Passo A3 — Fluxo claro de ação principal
- Na biblioteca, ação primária da linha deve ser “Tocar”.
- Favorito e “Adicionar ao grimório” viram ações secundárias (ícones consistentes).
- Remover botões redundantes por item.

**Resultado esperado:** menos ruído visual e menos erro de uso.

---

## Fase B — Consertar player e fila de reprodução

### Passo B1 — Separar comando de reprodução por contexto
- `playNow(track)` para iniciar imediatamente.
- `playFromList(list, index)` para iniciar com fila contextual da lista atual.

**Resultado esperado:** próximo/anterior funciona de forma previsível.

### Passo B2 — Sincronizar Media3 com UI
- Coletar eventos do `Player.Listener` e publicar no `PlaybackUiState`.
- Atualizar posição com ticker leve (500ms/1s) apenas quando necessário.

### Passo B3 — Notificação e background completos
- Garantir que sessão e notificação reflitam a mesma fila da UI.
- Validar transição app foreground/background sem perda de estado.

---

## Fase C — Ordenação e biblioteca compreensíveis

### Passo C1 — Criar modelo explícito de ordenação
- `enum class TrackSort { TITLE_ASC, TITLE_DESC, ARTIST_ASC, DATE_ADDED_DESC, DURATION_DESC, MOST_PLAYED }`

### Passo C2 — UI de ordenação persistente
- Adicionar botão “Ordenar” na biblioteca com `ModalBottomSheet`.
- Persistir escolha em `DataStore`.
- Mostrar chip/label ativo: “Ordenado por: Título (A-Z)”.

### Passo C3 — Busca e filtros previsíveis
- Debounce na busca (150–250ms).
- Filtros com feedback: total de resultados + estado vazio temático.

---

## Fase D — Performance e estabilidade

### Passo D1 — Indexação e leitura de biblioteca fora da UI thread
- Scan via `Dispatchers.IO`.
- Evitar re-scan completo em toda entrada de tela (usar cache + invalidação).

### Passo D2 — Lista escalável
- `LazyColumn` com `key = track.id`.
- Carregar arte de capa sob demanda (placeholder + cache).
- Avaliar `Paging 3` se biblioteca for muito grande.

### Passo D3 — Reduzir recomposição
- Derivar estados com `derivedStateOf`.
- Evitar passar lambdas/objetos instáveis em massa.
- Isolar item de lista em composable estável.

### Passo D4 — Instrumentar
- Macrobenchmark (scroll + startup + click to play).
- Baseline Profiles.
- Medir tempo de “tap-to-audio-start”.

---

## 4) Backlog técnico priorizado

## Prioridade P0 (fazer agora)
1. Mini-player global funcional.
2. Fila contextual (`playFromList`) com next/prev correto.
3. Ordenação explícita + persistência em DataStore.
4. Mover operações de scan/index para IO + cache mínimo.

## Prioridade P1
1. Melhorias visuais do design system imperial (tipografia, cartões, spacing, contraste).
2. Estados vazios e erros com linguagem temática clara.
3. Otimização de recomposição e item de lista estável.

## Prioridade P2
1. Paging para acervos grandes.
2. Macrobenchmark + baseline profile.
3. Evolução visual por nível (XP) com temas graduais.

---

## 5) Guia de implementação por sprint (sugestão)

## Sprint 1 (2–4 dias)
- Implementar `PlaybackUiState` central.
- Subir mini-player no `Scaffold`.
- Ajustar clique da biblioteca para abrir fila contextual.

**Critério de aceite:** usuário toca música e controla sem se perder.

## Sprint 2 (2–4 dias)
- Ordenação persistente em DataStore.
- UI de sort/filter clara.
- Melhorias de lista e busca com debounce.

**Critério de aceite:** usuário entende como músicas estão ordenadas.

## Sprint 3 (3–5 dias)
- Otimização de performance (IO, cache, recomposição).
- Métricas de benchmark.
- Refino visual completo do tema imperial.

**Critério de aceite:** app fluido em aparelho intermediário.

---

## 6) Checklist de validação final (QA)

- [ ] Tocar música na biblioteca inicia reprodução e mostra mini-player.
- [ ] Mini-player sempre reflete faixa atual.
- [ ] Player Supremo abre a partir do mini-player e controla faixa real.
- [ ] Next/Prev/Seek/Shuffle/Repeat funcionam com fila contextual.
- [ ] Ordenação escolhida permanece após fechar/reabrir app.
- [ ] Biblioteca com 1000+ faixas rola sem travamento severo.
- [ ] Background + notificação continuam funcionando com app minimizado.

---

## 7) Observação final

O caminho para sair de “usável entre aspas” não é apenas trocar visual; é alinhar **arquitetura de estado do player + UX de navegação + performance de lista**. Quando esses 3 pilares forem resolvidos juntos, a interface final naturalmente ficará muito melhor e previsível.
