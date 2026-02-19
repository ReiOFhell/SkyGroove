#!/usr/bin/env bash
set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SAFE_GRADLE_HOME="$PROJECT_ROOT/.gradle-user-home"
mkdir -p "$SAFE_GRADLE_HOME"

echo "[SkyGroove] Usando GRADLE_USER_HOME isolado: $SAFE_GRADLE_HOME"
export GRADLE_USER_HOME="$SAFE_GRADLE_HOME"

gradle --stop >/dev/null 2>&1 || true

if [ "$#" -eq 0 ]; then
  set -- help
fi

gradle "$@"
