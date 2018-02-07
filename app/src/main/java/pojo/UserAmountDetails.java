package pojo;

/**
 * Created by Kums on 1/24/2018.
 */

public class UserAmountDetails
{
    public int id;
    public String names;
    public int amount;


    public UserAmountDetails(int id, int amount) {
        this.id = id;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
