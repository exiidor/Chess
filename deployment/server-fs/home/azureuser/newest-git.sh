#!/bin/bash
REPO_URL="https://github.com/software-schreiber/chess.git"
TARGET_DIR="/home/azureuser/chess"
if [ -d "$TARGET_DIR/.git" ]; then
  echo "Repo exists."
  cd "$TARGET_DIR" || exit 1
  git stash >/dev/null 2>&1
  LOCAL=$(git rev-parse @)
  REMOTE=$(git rev-parse @{0})
  if [ "$LOCAL" = "$REMOTE" ]; then
    echo "Repo is up to date"
  else
    echo "Updates available, pulling now"
    git pull
  fi

  git stash pop >/dev/null 2>&1
else
  echo "Cloning repo into $TARGET_DIR..."
  git clone "$REPO_URL" "$TARGET_DIR"
fi

LOG="/home/azureuser/newest-git.log"

if ls "$LOG" >/dev/null 2>&1; then
  echo "log file exists at $LOG"
  SIZE=$(wc -c < "$LOG")
  if [ "$SIZE" -gt 10240 ]; then
    echo "log file too big, deleting it"
    rm "$LOG"
  fi
else
echo "log file does not exist, one will be created"
fi


