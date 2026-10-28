
// loading game board
// Kortene er nu lavet af Thymeleaf i game.html, så her kobler vi kun klik på dem
function loadGameBoard() {

    const board = document.getElementById('board');

    board.querySelectorAll('.card').forEach(el => {

        // emoji-kort får en ekstra klasse, så de vises større
        const span = el.querySelector('.card-back span');
        if (span && /\p{Extended_Pictographic}/u.test(span.textContent)) {
            el.querySelector('.card-back').classList.add('emoji');
        }
        el.addEventListener('click', () => handleCardClick(el));
    });
}//test


let firstCard = null;
let secondCard = null;
let lockBoard = false;

// game logic methods: US2
function handleCardClick(el) {
    //US2: vende to kort ad gangen.

    if (lockBoard) return;
    // begge ifs ignorere kliks på allerede flippede og matchede kort
    if (el.classList.contains('flipped')) return;
    if (el.classList.contains('matched')) return;

    startTimer();

    el.classList.add('flipped'); // vi erstatter vores toggle med add

    if (!firstCard){
        firstCard = el;
        return;
    }
    secondCard = el;
    checkForMatch();

}

function checkForMatch() {
    lockBoard = true;

    const isMatch = firstCard.dataset.pairId === secondCard.dataset.pairId;
    increaseMoves();
    console.log("matched cards: ", isMatch);

    if (isMatch) {
        // US3: Matchende kort forbliver vendt
        firstCard.classList.add('matched');
        secondCard.classList.add('matched');


        resetBoard();

        if (increaseMatchedPairs()) {
            stopTimer();
        }
    } else {
        setTimeout(() => {
            // US4: Ikke-matchende kort vendes tilbage
            firstCard.classList.remove('flipped');
            secondCard.classList.remove('flipped');
            resetBoard();
        }, 1000);
    }
}

function resetBoard() {
    firstCard = null;
    secondCard = null;
    lockBoard = false;
}

loadGameBoard();