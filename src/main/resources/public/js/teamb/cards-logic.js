
// loading game board
async function loadGameBoard() {

    const board = document.getElementById('board');

    try {
        const res = await fetch('/cards');
        if (!res.ok) throw new Error('Status ' + res.status);

        const cards = await res.json();
        board.innerHTML = '';
        cards.forEach(card => board.appendChild(createCard(card)));
    } catch (err) {
        console.error(err);
        board.textContent = 'Kort kunne ikke læses!';
    }
}

function createCard(card) {

    const el = document.createElement('div');
    el.className = 'card';
    el.dataset.cardId = card.cardId;
    el.dataset.pairId = card.pairId;

    const inner = document.createElement('div');
    inner.className = 'card-inner';

    // Kortets forside
    const front = document.createElement('div');
    front.className = 'card-front';
    front.textContent = 'LH';

    // Kortets bagside (indholdet ses her)
    const back = document.createElement('div');
    back.className = 'card-back';

    if (card.imageURL) {
        const img = document.createElement('img');
        img.src = card.imageURL;
        back.appendChild(img);
    }
    if (card.text) {
        const span = document.createElement('span');
        span.textContent = card.text;
        // emoji-kort får en ekstra klasse, så de vises større
        if (/\p{Extended_Pictographic}/u.test(card.text)) {
            back.classList.add('emoji');
        }
        back.appendChild(span);
    }

    inner.appendChild(front);
    inner.appendChild(back);
    el.appendChild(inner);

    el.addEventListener('click', () => handleCardClick(el));
    return el;
}


let firstCard = null;
let secondCard = null;
let lockBoard = false;

// game logic methods: us2
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