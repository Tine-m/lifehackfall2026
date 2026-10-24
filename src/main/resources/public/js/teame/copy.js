// Kopierer passwordet til udklipsholderen og viser "Kopieret!". vi er nødt til at have denne i js man kan kuyn i js
// Bekræftelsen forsvinder af sig selv, når der genereres et nyt password, fordi siden indlæses igen
document.addEventListener("DOMContentLoaded", () => {
    const copyButton = document.getElementById("copy-button");
    const passwordField = document.getElementById("password");
    const copiedMessage = document.getElementById("copied");

    copyButton.addEventListener("click", async () => {
        if (passwordField.value === "") {
            return;
        }
        try {
            await navigator.clipboard.writeText(passwordField.value);
            copiedMessage.hidden = false;
        } catch (error) {
            // Udklipsholderen kan være blokeret af browseren. Så markeres teksten, så brugeren selv kan kopiere
            passwordField.select();
        }
    });
});
