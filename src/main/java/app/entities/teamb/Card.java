package app.entities.teamb;

public class Card {

    private int cardId;
    private int pairId;
    private String imageURL;
    private String text;
    private String lifehackDescription;


    public Card(int cardId, int pairId, String imageURL, String text, String lifehackDescription) {
        this.cardId = cardId;
        this.pairId = pairId;
        this.imageURL = imageURL;
        this.text = text;
        this.lifehackDescription = lifehackDescription;
    }

    public int getPairId() {
        return pairId;
    }

    public void setPairId(int pairId) {
        this.pairId = pairId;
    }

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLifehackDescription() {
        return lifehackDescription;
    }

    public void setLifehackDescription(String lifehackDescription) {
        this.lifehackDescription = lifehackDescription;
    }
}
