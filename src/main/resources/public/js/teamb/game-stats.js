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
