let red = 128;
let green = 128;
let blue = 128;
const redSlider = document.querySelector("#redSlider")
const greenSlider = document.querySelector("#greenSlider")
const blueSlider = document.querySelector("#blueSlider")

function getColour(red = 128, green = 128, blue = 128) {
    return `rgb(${red},${green},${blue})`
}

function displayColour() {
    document.querySelector('#guessBox').style.backgroundColor = getColour(red, green, blue);
}

/* Kalder displayColour(), så guessfarven starter med at være rgb(128,128,128)*/
displayColour();

/* Hver slider har en eventlistener som opdaterer farven i guess boksen*/
redSlider.addEventListener('input', () => {
    red = redSlider.value;
    displayColour();
})
greenSlider.addEventListener('input', () => {
    green = greenSlider.value;
    displayColour();
})
blueSlider.addEventListener('input', () => {
    blue = blueSlider.value;
    displayColour();
})

