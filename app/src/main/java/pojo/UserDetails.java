package pojo;

/**
 * Created by Kums on 1/19/2018.
 */

public class UserDetails
{
    public String name,mobileNumber,imageUrl,gender;

    public UserDetails()
    {
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

    public UserDetails(String name, String mobileNumber, String imageUrl, String gender) {
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.imageUrl = imageUrl;
        this.gender = gender;
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
