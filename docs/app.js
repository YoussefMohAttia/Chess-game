const FILES = ["A", "B", "C", "D", "E", "F", "G", "H"];
const RANKS = ["FIRST", "SECOND", "THIRD", "FORTH", "FIFTH", "SIXTH", "SEVENTH", "EIGHTH"];
const Player = { WHITE: "WHITE", BLACK: "BLACK" };
const GameStatus = {
  IN_PROGRESS: "IN_PROGRESS",
  WHITE_WON: "WHITE_WON",
  BLACK_WON: "BLACK_WON",
  STALEMATE: "STALEMATE",
  WHITE_UNDER_CHECK: "WHITE_UNDER_CHECK",
  BLACK_UNDER_CHECK: "BLACK_UNDER_CHECK",
  INSUFFICIENT_MATERIAL: "INSUFFICIENT_MATERIAL",
};

const boardElement = document.getElementById("board");
const statusText = document.getElementById("statusText");
const undoButton = document.getElementById("undoButton");
const newGameButton = document.getElementById("newGameButton");
const promotionDialog = document.getElementById("promotionDialog");
const gameEndedDialog = document.getElementById("gameEndedDialog");
const gameEndedText = document.getElementById("gameEndedText");

let game = new ClassicChessGame();
let selectedSquare = null;
let validTargets = [];
let waitingForPromotion = false;

render();

undoButton.addEventListener("click", () => {
  game.undoMove();
  selectedSquare = null;
  validTargets = [];
  render();
});

newGameButton.addEventListener("click", () => {
  game = new ClassicChessGame();
  selectedSquare = null;
  validTargets = [];
  render();
});

function makeSquare(file, rank) {
  return { file, rank };
}

function squareKey(square) {
  return `${square.file}${square.rank}`;
}

function sameSquare(a, b) {
  return a && b && a.file === b.file && a.rank === b.rank;
}

function revertPlayer(player) {
  return player === Player.WHITE ? Player.BLACK : Player.WHITE;
}

function piece(type, owner) {
  return { type, owner };
}

function cloneBoard(board) {
  return board.map((row) => row.map((entry) => entry ? { ...entry } : null));
}

function fileIndex(file) {
  return FILES.indexOf(file);
}

function rankIndex(rank) {
  return RANKS.indexOf(rank);
}

function deltaX(move) {
  return fileIndex(move.to.file) - fileIndex(move.from.file);
}

function deltaY(move) {
  return rankIndex(move.to.rank) - rankIndex(move.from.rank);
}

function absDeltaX(move) {
  return Math.abs(deltaX(move));
}

function absDeltaY(move) {
  return Math.abs(deltaY(move));
}

function isHorizontalMove(move) {
  return deltaX(move) !== 0 && deltaY(move) === 0;
}

function isVerticalMove(move) {
  return deltaX(move) === 0 && deltaY(move) !== 0;
}

function isDiagonalMove(move) {
  return absDeltaX(move) === absDeltaY(move) && absDeltaX(move) !== 0;
}

class ClassicChessGame {
  constructor() {
    this.board = this.createInitialBoard();
    this.gameStatus = GameStatus.IN_PROGRESS;
    this.whoseTurn = Player.WHITE;
    this.lastMove = null;
    this.canWhiteCastleKingSide = true;
    this.canWhiteCastleQueenSide = true;
    this.canBlackCastleKingSide = true;
    this.canBlackCastleQueenSide = true;
    this.history = [];
  }

  createInitialBoard() {
    return [
      [piece("Rook", Player.WHITE), piece("Knight", Player.WHITE), piece("Bishop", Player.WHITE), piece("Queen", Player.WHITE), piece("King", Player.WHITE), piece("Bishop", Player.WHITE), piece("Knight", Player.WHITE), piece("Rook", Player.WHITE)],
      Array.from({ length: 8 }, () => piece("Pawn", Player.WHITE)),
      Array(8).fill(null),
      Array(8).fill(null),
      Array(8).fill(null),
      Array(8).fill(null),
      Array.from({ length: 8 }, () => piece("Pawn", Player.BLACK)),
      [piece("Rook", Player.BLACK), piece("Knight", Player.BLACK), piece("Bishop", Player.BLACK), piece("Queen", Player.BLACK), piece("King", Player.BLACK), piece("Bishop", Player.BLACK), piece("Knight", Player.BLACK), piece("Rook", Player.BLACK)],
    ];
  }

  snapshot() {
    return {
      board: cloneBoard(this.board),
      gameStatus: this.gameStatus,
      whoseTurn: this.whoseTurn,
      lastMove: this.lastMove ? { ...this.lastMove, from: { ...this.lastMove.from }, to: { ...this.lastMove.to } } : null,
      canWhiteCastleKingSide: this.canWhiteCastleKingSide,
      canWhiteCastleQueenSide: this.canWhiteCastleQueenSide,
      canBlackCastleKingSide: this.canBlackCastleKingSide,
      canBlackCastleQueenSide: this.canBlackCastleQueenSide,
    };
  }

  restore(snapshot) {
    this.board = cloneBoard(snapshot.board);
    this.gameStatus = snapshot.gameStatus;
    this.whoseTurn = snapshot.whoseTurn;
    this.lastMove = snapshot.lastMove;
    this.canWhiteCastleKingSide = snapshot.canWhiteCastleKingSide;
    this.canWhiteCastleQueenSide = snapshot.canWhiteCastleQueenSide;
    this.canBlackCastleKingSide = snapshot.canBlackCastleKingSide;
    this.canBlackCastleQueenSide = snapshot.canBlackCastleQueenSide;
  }

  undoMove() {
    const previous = this.history.pop();
    if (previous) {
      this.restore(previous);
    }
  }

  getPieceAtSquare(square, board = this.board) {
    return board[rankIndex(square.rank)][fileIndex(square.file)];
  }

  setPieceAtSquare(square, value, board = this.board) {
    board[rankIndex(square.rank)][fileIndex(square.file)] = value;
  }

  hasPieceIn(square) {
    return this.getPieceAtSquare(square) !== null;
  }

  hasPieceInSquareForPlayer(square, player) {
    const found = this.getPieceAtSquare(square);
    return found !== null && found.owner === player;
  }

  isGameEnded() {
    return [
      GameStatus.WHITE_WON,
      GameStatus.BLACK_WON,
      GameStatus.STALEMATE,
      GameStatus.INSUFFICIENT_MATERIAL,
    ].includes(this.gameStatus);
  }

  isPromotionMove(move) {
    const found = this.getPieceAtSquare(move.from);
    if (!found || found.type !== "Pawn") {
      return false;
    }

    return (found.owner === Player.WHITE && move.from.rank === "SEVENTH" && move.to.rank === "EIGHTH")
      || (found.owner === Player.BLACK && move.from.rank === "SECOND" && move.to.rank === "FIRST");
  }

  makeMove(move) {
    if (!this.isValidMove(move)) {
      return false;
    }

    const previous = this.snapshot();
    let fromPiece = this.getPieceAtSquare(move.from);
    const capturedPiece = this.getPieceAtSquare(move.to);

    if (fromPiece.type === "King") {
      if (fromPiece.owner === Player.WHITE) {
        this.canWhiteCastleKingSide = false;
        this.canWhiteCastleQueenSide = false;
      } else {
        this.canBlackCastleKingSide = false;
        this.canBlackCastleQueenSide = false;
      }
    }

    if (fromPiece.type === "Rook") {
      this.clearCastlingRightForRookMove(move.from, fromPiece.owner);
    }

    if (capturedPiece && capturedPiece.type === "Rook") {
      this.clearCastlingRightForRookMove(move.to, capturedPiece.owner);
    }

    if (fromPiece.type === "Pawn" && absDeltaX(move) === 1 && !this.hasPieceIn(move.to)) {
      this.setPieceAtSquare(this.lastMove.to, null);
    }

    if (fromPiece.type === "Pawn" && (move.to.rank === "FIRST" || move.to.rank === "EIGHTH")) {
      fromPiece = piece(move.promotion, fromPiece.owner);
    }

    if (fromPiece.type === "King" && absDeltaX(move) === 2) {
      this.moveCastlingRook(move);
    }

    this.setPieceAtSquare(move.from, null);
    this.setPieceAtSquare(move.to, fromPiece);
    this.whoseTurn = revertPlayer(this.whoseTurn);
    this.lastMove = { ...move, from: { ...move.from }, to: { ...move.to } };
    this.updateGameStatus();
    this.history.push(previous);
    return true;
  }

  clearCastlingRightForRookMove(square, owner) {
    if (owner === Player.WHITE) {
      if (square.file === "A" && square.rank === "FIRST") this.canWhiteCastleQueenSide = false;
      if (square.file === "H" && square.rank === "FIRST") this.canWhiteCastleKingSide = false;
    } else {
      if (square.file === "A" && square.rank === "EIGHTH") this.canBlackCastleQueenSide = false;
      if (square.file === "H" && square.rank === "EIGHTH") this.canBlackCastleKingSide = false;
    }
  }

  moveCastlingRook(move) {
    const rank = move.to.rank;
    if (move.to.file === "G") {
      const rookFrom = makeSquare("H", rank);
      const rookTo = makeSquare("F", rank);
      this.setPieceAtSquare(rookTo, this.getPieceAtSquare(rookFrom));
      this.setPieceAtSquare(rookFrom, null);
    } else if (move.to.file === "C") {
      const rookFrom = makeSquare("A", rank);
      const rookTo = makeSquare("D", rank);
      this.setPieceAtSquare(rookTo, this.getPieceAtSquare(rookFrom));
      this.setPieceAtSquare(rookFrom, null);
    }
  }

  isValidMove(move) {
    if (this.isGameEnded()) {
      return false;
    }

    const fromPiece = this.getPieceAtSquare(move.from);
    if (!fromPiece || fromPiece.owner !== this.whoseTurn || !this.isValidPieceMove(fromPiece, move)) {
      return false;
    }

    const toPiece = this.getPieceAtSquare(move.to);
    if (toPiece && toPiece.owner === this.whoseTurn) {
      return false;
    }

    return !this.willOwnKingBeAttacked(this.whoseTurn, move);
  }

  isValidPieceMove(found, move) {
    switch (found.type) {
      case "Pawn":
        return this.isValidPawnMove(move);
      case "Knight":
        return (absDeltaX(move) === 1 && absDeltaY(move) === 2) || (absDeltaX(move) === 2 && absDeltaY(move) === 1);
      case "Bishop":
        return isDiagonalMove(move) && !this.isTherePieceInBetween(move);
      case "Rook":
        return (isHorizontalMove(move) || isVerticalMove(move)) && !this.isTherePieceInBetween(move);
      case "Queen":
        return (isDiagonalMove(move) || isHorizontalMove(move) || isVerticalMove(move)) && !this.isTherePieceInBetween(move);
      case "King":
        return this.isValidKingMove(found, move);
      default:
        return false;
    }
  }

  isValidPawnMove(move) {
    const whoseTurn = this.whoseTurn;
    const from = move.from;
    const to = move.to;

    if (whoseTurn === Player.WHITE) {
      if (from.rank === "SECOND" && deltaY(move) === 2) {
        return deltaX(move) === 0
          && !this.hasPieceIn(makeSquare(from.file, "THIRD"))
          && !this.hasPieceIn(makeSquare(from.file, "FORTH"));
      }

      if (deltaY(move) !== 1) return false;
      if (to.rank === "EIGHTH" && !move.promotion) return false;
      if (absDeltaX(move) === 1) return this.hasPieceInSquareForPlayer(to, revertPlayer(whoseTurn)) || this.canCaptureEnPassant(move);
      if (absDeltaX(move) === 0) return !this.hasPieceIn(to);
    } else {
      if (from.rank === "SEVENTH" && deltaY(move) === -2) {
        return deltaX(move) === 0
          && !this.hasPieceIn(makeSquare(from.file, "SIXTH"))
          && !this.hasPieceIn(makeSquare(from.file, "FIFTH"));
      }

      if (deltaY(move) !== -1) return false;
      if (to.rank === "FIRST" && !move.promotion) return false;
      if (absDeltaX(move) === 1) return this.hasPieceInSquareForPlayer(to, revertPlayer(whoseTurn)) || this.canCaptureEnPassant(move);
      if (absDeltaX(move) === 0) return !this.hasPieceIn(to);
    }

    return false;
  }

  canCaptureEnPassant(move) {
    if (!this.lastMove) return false;
    const lastPiece = this.getPieceAtSquare(this.lastMove.to);
    return lastPiece
      && lastPiece.type === "Pawn"
      && absDeltaY(this.lastMove) === 2
      && this.lastMove.to.rank === move.from.rank
      && this.lastMove.to.file === move.to.file;
  }

  isValidKingMove(found, move) {
    if (absDeltaX(move) <= 1 && absDeltaY(move) <= 1) {
      return true;
    }

    if (absDeltaY(move) !== 0 || this.isTherePieceInBetween(move)) {
      return false;
    }

    if (deltaX(move) === 2) {
      if (found.owner === Player.WHITE && this.canWhiteCastleKingSide && move.from.file === "E" && move.from.rank === "FIRST") {
        return this.hasCastleRook("H", "FIRST", found.owner);
      }
      if (found.owner === Player.BLACK && this.canBlackCastleKingSide && move.from.file === "E" && move.from.rank === "EIGHTH") {
        return this.hasCastleRook("H", "EIGHTH", found.owner);
      }
    } else if (deltaX(move) === -2) {
      if (found.owner === Player.WHITE && this.canWhiteCastleQueenSide && move.from.file === "E" && move.from.rank === "FIRST") {
        return this.hasCastleRook("A", "FIRST", found.owner);
      }
      if (found.owner === Player.BLACK && this.canBlackCastleQueenSide && move.from.file === "E" && move.from.rank === "EIGHTH") {
        return this.hasCastleRook("A", "EIGHTH", found.owner);
      }
    }

    return false;
  }

  hasCastleRook(file, rank, owner) {
    const rook = this.getPieceAtSquare(makeSquare(file, rank));
    return rook && rook.type === "Rook" && rook.owner === owner;
  }

  isTherePieceInBetween(move, board = this.board) {
    const startFile = fileIndex(move.from.file);
    const startRank = rankIndex(move.from.rank);
    const stepX = Math.sign(deltaX(move));
    const stepY = Math.sign(deltaY(move));
    let currentFile = startFile + stepX;
    let currentRank = startRank + stepY;

    while (currentFile !== fileIndex(move.to.file) || currentRank !== rankIndex(move.to.rank)) {
      if (board[currentRank][currentFile] !== null) {
        return true;
      }
      currentFile += stepX;
      currentRank += stepY;
    }

    return false;
  }

  willOwnKingBeAttacked(whoseTurn, move) {
    const clone = cloneBoard(this.board);
    const movingPiece = this.getPieceAtSquare(move.from, clone);
    const intermediateCastleSquare = this.tryGetIntermediateCastleSquare(movingPiece, move);
    this.setPieceAtSquare(move.from, null, clone);
    this.setPieceAtSquare(move.to, movingPiece, clone);

    if (movingPiece.type === "Pawn" && absDeltaX(move) === 1 && this.getPieceAtSquare(move.to, this.board) === null && this.lastMove) {
      this.setPieceAtSquare(this.lastMove.to, null, clone);
    }

    const kingSquare = this.getKingSquare(whoseTurn, clone);
    const originalKingSquare = movingPiece.type === "King" && absDeltaX(move) === 2 ? move.from : null;

    for (const file of FILES) {
      for (const rank of RANKS) {
        const square = makeSquare(file, rank);
        const found = this.getPieceAtSquare(square, clone);
        if (!found || found.owner === whoseTurn) continue;

        if (this.isAttackingSquare(found, square, kingSquare, clone)) return true;
        if (intermediateCastleSquare && this.isAttackingSquare(found, square, intermediateCastleSquare, clone)) return true;
        if (originalKingSquare && this.isAttackingSquare(found, square, originalKingSquare, this.board)) return true;
      }
    }

    return false;
  }

  tryGetIntermediateCastleSquare(found, move) {
    if (found && found.type === "King" && absDeltaX(move) === 2 && deltaY(move) === 0) {
      return makeSquare(deltaX(move) === 2 ? "F" : "D", move.from.rank);
    }
    return null;
  }

  isInCheck(player, board = this.board) {
    const kingSquare = this.getKingSquare(player, board);
    for (const file of FILES) {
      for (const rank of RANKS) {
        const square = makeSquare(file, rank);
        const found = this.getPieceAtSquare(square, board);
        if (found && found.owner !== player && this.isAttackingSquare(found, square, kingSquare, board)) {
          return true;
        }
      }
    }
    return false;
  }

  isAttackingSquare(found, pieceSquare, squareUnderAttack, board) {
    const move = { from: pieceSquare, to: squareUnderAttack };
    switch (found.type) {
      case "Pawn":
        return deltaY(move) === (found.owner === Player.WHITE ? 1 : -1) && absDeltaX(move) === 1;
      case "Knight":
        return (absDeltaX(move) === 1 && absDeltaY(move) === 2) || (absDeltaX(move) === 2 && absDeltaY(move) === 1);
      case "Bishop":
        return isDiagonalMove(move) && !this.isTherePieceInBetween(move, board);
      case "Rook":
        return (isHorizontalMove(move) || isVerticalMove(move)) && !this.isTherePieceInBetween(move, board);
      case "Queen":
        return (isDiagonalMove(move) || isHorizontalMove(move) || isVerticalMove(move)) && !this.isTherePieceInBetween(move, board);
      case "King":
        return absDeltaX(move) <= 1 && absDeltaY(move) <= 1;
      default:
        return false;
    }
  }

  getKingSquare(player, board = this.board) {
    for (const file of FILES) {
      for (const rank of RANKS) {
        const square = makeSquare(file, rank);
        const found = this.getPieceAtSquare(square, board);
        if (found && found.type === "King" && found.owner === player) {
          return square;
        }
      }
    }
    throw new Error("There is no king on the board.");
  }

  getAllValidMovesFromSquare(square) {
    const moves = [];
    for (const file of FILES) {
      for (const rank of RANKS) {
        const to = makeSquare(file, rank);
        if (this.isValidMove({ from: square, to, promotion: "Queen" })) {
          moves.push(to);
        }
      }
    }
    return moves;
  }

  hasAnyValidMoves() {
    for (const file of FILES) {
      for (const rank of RANKS) {
        if (this.getAllValidMovesFromSquare(makeSquare(file, rank)).length > 0) {
          return true;
        }
      }
    }
    return false;
  }

  updateGameStatus() {
    const player = this.whoseTurn;
    const inCheck = this.isInCheck(player);
    const hasMoves = this.hasAnyValidMoves();

    if (inCheck) {
      if (!hasMoves && player === Player.WHITE) this.gameStatus = GameStatus.BLACK_WON;
      else if (!hasMoves && player === Player.BLACK) this.gameStatus = GameStatus.WHITE_WON;
      else if (player === Player.WHITE) this.gameStatus = GameStatus.WHITE_UNDER_CHECK;
      else this.gameStatus = GameStatus.BLACK_UNDER_CHECK;
    } else if (!hasMoves) {
      this.gameStatus = GameStatus.STALEMATE;
    } else {
      this.gameStatus = GameStatus.IN_PROGRESS;
    }

    if (this.isInsufficientMaterial()) {
      this.gameStatus = GameStatus.INSUFFICIENT_MATERIAL;
    }
  }

  isInsufficientMaterial() {
    let whiteBishopCount = 0;
    let blackBishopCount = 0;
    let whiteKnightCount = 0;
    let blackKnightCount = 0;

    for (const file of FILES) {
      for (const rank of RANKS) {
        const found = this.getPieceAtSquare(makeSquare(file, rank));
        if (!found || found.type === "King") continue;

        if (found.type === "Bishop") {
          if (found.owner === Player.WHITE) whiteBishopCount += 1;
          else blackBishopCount += 1;
        } else if (found.type === "Knight") {
          if (found.owner === Player.WHITE) whiteKnightCount += 1;
          else blackKnightCount += 1;
        } else {
          return false;
        }
      }
    }

    return whiteBishopCount + whiteKnightCount <= 1 && blackBishopCount + blackKnightCount <= 1;
  }
}

function render() {
  boardElement.innerHTML = "";
  updateStatusText();

  const checkedSquare = getCheckedKingSquare();
  const validKeys = new Set(validTargets.map(squareKey));

  for (let rankIndexValue = 7; rankIndexValue >= 0; rankIndexValue -= 1) {
    for (let fileIndexValue = 0; fileIndexValue < 8; fileIndexValue += 1) {
      const square = makeSquare(FILES[fileIndexValue], RANKS[rankIndexValue]);
      const button = document.createElement("button");
      button.type = "button";
      button.className = `square ${(fileIndexValue + rankIndexValue) % 2 === 0 ? "light" : "dark"}`;
      button.dataset.file = square.file;
      button.dataset.rank = square.rank;
      button.setAttribute("aria-label", `${square.file}${rankIndexValue + 1}`);

      if (sameSquare(selectedSquare, square)) button.classList.add("selected");
      if (validKeys.has(squareKey(square))) button.classList.add("valid");
      if (checkedSquare && sameSquare(checkedSquare, square)) button.classList.add("check");

      const coord = document.createElement("span");
      coord.className = "coord";
      coord.textContent = `${square.file}${rankIndexValue + 1}`;
      button.appendChild(coord);

      const found = game.getPieceAtSquare(square);
      if (found) {
        const img = document.createElement("img");
        img.className = "piece";
        img.alt = `${found.owner.toLowerCase()} ${found.type.toLowerCase()}`;
        img.src = `assets/${found.owner === Player.WHITE ? "White" : "Black"}${found.type}.png`;
        button.appendChild(img);
      }

      button.addEventListener("click", () => handleSquareClick(square));
      boardElement.appendChild(button);
    }
  }
}

function updateStatusText() {
  if (game.gameStatus === GameStatus.WHITE_WON) statusText.textContent = "White won. Game ended.";
  else if (game.gameStatus === GameStatus.BLACK_WON) statusText.textContent = "Black won. Game ended.";
  else if (game.gameStatus === GameStatus.STALEMATE) statusText.textContent = "Stalemate. Game ended.";
  else if (game.gameStatus === GameStatus.INSUFFICIENT_MATERIAL) statusText.textContent = "Draw by insufficient material. Game ended.";
  else if (game.gameStatus === GameStatus.WHITE_UNDER_CHECK) statusText.textContent = "White is in check.";
  else if (game.gameStatus === GameStatus.BLACK_UNDER_CHECK) statusText.textContent = "Black is in check.";
  else statusText.textContent = `${game.whoseTurn === Player.WHITE ? "White" : "Black"} to move`;
}

function getCheckedKingSquare() {
  if (game.gameStatus === GameStatus.WHITE_UNDER_CHECK) return game.getKingSquare(Player.WHITE);
  if (game.gameStatus === GameStatus.BLACK_UNDER_CHECK) return game.getKingSquare(Player.BLACK);
  return null;
}

async function handleSquareClick(square) {
  if (waitingForPromotion || game.isGameEnded()) return;

  const clickedPiece = game.getPieceAtSquare(square);
  if (!selectedSquare) {
    if (clickedPiece && clickedPiece.owner === game.whoseTurn) {
      selectedSquare = square;
      validTargets = game.getAllValidMovesFromSquare(square);
      render();
    }
    return;
  }

  if (clickedPiece && clickedPiece.owner === game.whoseTurn && !sameSquare(selectedSquare, square)) {
    selectedSquare = square;
    validTargets = game.getAllValidMovesFromSquare(square);
    render();
    return;
  }

  const move = { from: selectedSquare, to: square, promotion: null };
  if (game.isPromotionMove(move)) {
    waitingForPromotion = true;
    const promotion = await choosePromotion();
    waitingForPromotion = false;
    if (!promotion) {
      selectedSquare = null;
      validTargets = [];
      render();
      return;
    }
    move.promotion = promotion;
  }

  const moved = game.makeMove(move);
  selectedSquare = null;
  validTargets = [];
  render();

  if (moved) {
    showEndGameDialogIfNeeded();
  }
}

function choosePromotion() {
  return new Promise((resolve) => {
    const closeHandler = () => {
      promotionDialog.removeEventListener("close", closeHandler);
      resolve(promotionDialog.returnValue || null);
      promotionDialog.returnValue = "";
    };
    promotionDialog.addEventListener("close", closeHandler);
    promotionDialog.showModal();
  });
}

function showEndGameDialogIfNeeded() {
  let message = "";
  if (game.gameStatus === GameStatus.WHITE_WON) message = "White won. Game ended.";
  else if (game.gameStatus === GameStatus.BLACK_WON) message = "Black won. Game ended.";
  else if (game.gameStatus === GameStatus.STALEMATE) message = "Stalemate. Game ended.";
  else if (game.gameStatus === GameStatus.INSUFFICIENT_MATERIAL) message = "Draw by insufficient material. Game ended.";

  if (message) {
    gameEndedText.textContent = message;
    gameEndedDialog.showModal();
  }
}
