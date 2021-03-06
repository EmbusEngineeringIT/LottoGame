package pojo;

/**
 * Created by Kums on 1/19/2018.
 */

public class UserDetails
{
    public String name,mobileNumber,imageUrl,gender,date,pay_status;

    public String getDate()
    {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public UserDetails(String imageUrl)
    {
        this.imageUrl = imageUrl;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public UserDetails(String name, String mobileNumber, String imageUrl, String gender,String pay_status) {
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.imageUrl = imageUrl;
        this.gender = gender;
        this.pay_status = pay_status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
}
