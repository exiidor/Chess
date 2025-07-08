#!/bin/bash
cd ../chess/chess-gui-nuxt/
NUXT_PUBLIC_CHESS_SERVER_ADDRESS=https://example.com:3010 \
npm run dev
