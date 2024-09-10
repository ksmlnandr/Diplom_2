package settings;

public class OrderCreateBody {
    private String[] ingredients;

    public String[] getIngredients() {
        return ingredients;
    }

    public void setIngredients(String[] ingredients) {
        this.ingredients = ingredients;
    }

    public OrderCreateBody() {
    }

    public OrderCreateBody(String[] ingredients) {
        this.ingredients = ingredients;
    }
}
