const display = document.getElementById("display");
let timer = null;
let startTime = 0;
let elapsedTime = 0;
let isRunning = false;

function start(){

    //Tjek om timer kører, hvis den ikke kører, så start

}

function stop(){

    if(isRunning){
        clearInterval(timer);
        elapsedTime = Date.now() - startTime;
        isRunning = false;
    }
}

function update(){
    const currentTime = Date.now();
    elapsedTime = currentTime - startTime;

    let minutes = Math.floor(elapsedTime / (1000 * 60) % 60);
    let seconds = Math.floor(elapsedTime / 1000 % 60);

    minutes = String(minutes).padStart(2, "0");
    seconds = String(seconds).padStart(2, "0");
    display.textContent = `${minutes}:${seconds}`;

}

if(!isRunning){
    startTime = Date.now() - elapsedTime;
    timer = setInterval(update, 10);
    isRunning = true;
}