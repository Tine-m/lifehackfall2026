package app.entities.teamb;

public class Card {

    private int cardId;
    private String imageURL;
    private String text;
    private String lifehackDescription;


    public Card(int cardId, String text, String imageURL, String lifehackDescription) {
        this.cardId = cardId;
        this.text = text;
        this.imageURL = imageURL;
        this.lifehackDescription = lifehackDescription;
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
