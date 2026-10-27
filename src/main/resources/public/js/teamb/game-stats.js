/* US10: Se timer */
let seconds = 0;
let timerInterval = null;

function startTimer() {
    if (timerInterval !== null) {
        return;
    }

    timerInterval = setInterval(() => {
        seconds++;

        const minutes = Math.floor(seconds / 60);
        const remainingSeconds = seconds % 60;

        const formattedTime =
            String(minutes).padStart(2, "0") + ":" +
            String(remainingSeconds).padStart(2, "0");

        document.getElementById("timer").textContent =
            `Timer: ${formattedTime}`;
    }, 1000);
}

/* US5: Antal forsøg */
let moves = 0;

function increaseMoves() {
    moves++;

    document.getElementById("moves").textContent =
        `Moves: ${moves}`;
}


/* Stop timeren når spillet er slut */
function stopTimer() {
    clearInterval(timerInterval);
    timerInterval = null;
}

/* US: Matched pairs */
let matchedPairs = 0;

function increaseMatchedPairs() {
    matchedPairs++;

    // 16 kort = 8 par
    const totalPairs = document.querySelectorAll(".card").length / 2;

    document.getElementById("matched").textContent =
        `Matched pairs: ${matchedPairs}/${totalPairs}`;

    return matchedPairs === totalPairs; // true når alle par er fundet
}

// "Afslut spil" knap fører til resultatsiden
  /* document.getElementById("finish-game").addEventListener("click", () => {
    window.location.href = "/results";
});*/

