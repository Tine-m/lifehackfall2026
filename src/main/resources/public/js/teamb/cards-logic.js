
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

    const isMatch =
        firstCard.dataset.pairId === secondCard.dataset.pairId;

    increaseMoves();

    console.log("Matched cards:", isMatch);

    if (isMatch) {
        firstCard.classList.add('matched');
        secondCard.classList.add('matched');

        const gameWon = increaseMatchedPairs();

        // Check whether every card has been matched
        const allCards = document.querySelectorAll('#board .card');
        const matchedCards = document.querySelectorAll('#board .card.matched');

        console.log("Matched cards:", matchedCards.length);
        console.log("Total cards:", allCards.length);

        resetBoard();

        if (gameWon && allCards.length > 0 &&
            matchedCards.length === allCards.length) {

            stopTimer();

            document.getElementById("finish-time").value = seconds;
            document.getElementById("finish-moves").value = moves;
            document.getElementById("finish-completed").value = "true";

            console.log("YOU WON! All pairs found.");

            document.getElementById("finish-form").submit();
        }
    } else {
        setTimeout(() => {
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