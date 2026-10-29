let red = 128;
let green = 128;
let blue = 128;
const redSlider = document.querySelector("#redSlider")
const greenSlider = document.querySelector("#greenSlider")
const blueSlider = document.querySelector("#blueSlider")

function getRed(red = 128) {
    return `rgb(${red},0,0)`
}

function getGreen(green = 128) {
    return `rgb(0,${green},0)`
}

function getBlue(blue = 128) {
    return `rgb(0,0,${blue})`
}

function displayRed() {
    document.querySelector('#guessBoxR').style.backgroundColor = getRed(red);
}

function displayGreen() {
    document.querySelector('#guessBoxG').style.backgroundColor = getGreen(green);
}

function displayBlue() {
    document.querySelector('#guessBoxB').style.backgroundColor = getBlue(blue);
}
/* Kalder display-funktionerne, så guessfarverne starter med at være 128*/
displayRed();
displayGreen();
displayBlue();

/* Hver slider har en eventlistener som opdaterer farverne i guess boksene*/
redSlider.addEventListener('input', () => {
    red = redSlider.value;
    displayRed();
})
greenSlider.addEventListener('input', () => {
    green = greenSlider.value;
    displayGreen();
})
blueSlider.addEventListener('input', () => {
    blue = blueSlider.value;
    displayBlue();
})