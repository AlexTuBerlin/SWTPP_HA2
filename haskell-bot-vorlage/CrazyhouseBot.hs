-- module (NICHT ÄNDERN!)
module CrazyhouseBot
    ( getMove
    , listMoves
    ) 
    where

import Data.Char
-- Weitere Modulen können hier importiert werden

import Util

--- external signatures (NICHT ÄNDERN!)
getMove :: String -> String
getMove = _getMoveImpl -- YOUR IMPLEMENTATION HERE


listMoves :: String -> String
listMoves = _listMovesImpl -- YOUR IMPLEMENTATION HERE


-- YOUR IMPLEMENTATION FOLLOWS HERE
--type ChessBoard = [[Square]]
--type Square = Maybe Piece
-- King Queen Rook Bishop Knight Pawn

-- 1. Eingabe lesen 2. Board erstellen(?) 3. für jede Figur Liste mit possible Moves jeweils black/white 
blackPiece :: String->Char --TODO lowerCase

whitePiece :: String->Char --TODO upperCase

straightUp
removeNum :: Char->String
removeNum p | p == '8' = "oooooooo" | p == '7' = "ooooooo" | p == '6' = "oooooo" | p == '5' = "ooooo" | p == '4' = "oooo" | p == '3' = "ooo" | p == '2' = "oo" | p == '1' = "o"

-- example input: "rnbQ2Q1/pppp3p/6k1/8/1P6/8/Pn1pPKPP/RNB2BNR/BPQRppq w"
-- p,k,r,b,q,n
-- board in zahlen umbauen mod 8 iwas (0-63)
-- input string zersetzen : zahlen zu zeichen und counten oder so
-- reihen durchgehen um felder zu checken für moves
-- update board
-- PIECES checken

-- CHESS BOARD x mod 8 = spalten matrix
-- 8a 8b 8c 8d 8e 8f 8g 8h      0   1  2  3  4  5  6  7 
-- 7a 7b 7c 7d 7e 7f 7g 7h      8   9 10 11 12 13 14 15
-- 6a 6b 6c 6d 6e 6f 6g 6h      16 17 18 19 20 21 22 23
-- 5a 5b 5c 5d 5e 5f 5g 5h      24 25 26 27 28 29 30 31
-- 4a 4b 4c 4d 4e 4f 4g 4h      32 33 34 35 36 37 38 39
-- 3a 3b 3c 3d 3e 3f 3g 3h      40 41 42 43 44 45 46 47
-- 2a 2b 2c 2d 2e 2f 2g 2h      48 49 50 51 52 53 54 55
-- 1a 1b 1c 1d 1e 1f 1g 1h      56 57 58 59 60 61 62 63

