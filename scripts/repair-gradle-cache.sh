#!/usr/bin/env bash
set -euo pipefail

echo "[SkyGroove] Reparando cache corrompido do Gradle..."
GRADLE_HOME_DIR="${GRADLE_USER_HOME:-$HOME/.gradle}"
JOURNAL_DIR="$GRADLE_HOME_DIR/caches/journal-1"

gradle --stop >/dev/null 2>&1 || true

if [ -d "$JOURNAL_DIR" ]; then
  echo "Removendo $JOURNAL_DIR"
  rm -rf "$JOURNAL_DIR"
else
  echo "Nada para limpar em $JOURNAL_DIR"
fi

echo "Próximo passo: ./gradlew --refresh-dependencies help"
