package itsjustaaron.movietogether;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by Aaron on 2015-12-08.
 */
@DynamoDBTable(tableName = "Profiles")
public class Person {
    private String password;
    private String name;
    private String email;
    private String phone;
    private String sex;
    private String dob;

    @DynamoDBIndexRangeKey(attributeName = "Name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @DynamoDBHashKey(attributeName = "E-mail")
    public String getEmail() {
        return  email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @DynamoDBAttribute(attributeName = "Phone")
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @DynamoDBAttribute(attributeName = "Password")
    public String getPassword() {return password;}

    public void setPassword(String ps) {password = ps;}

    @DynamoDBAttribute(attributeName = "Sex")
    public String getSex() {return sex;}

    public void setSex(String ps) {sex = ps;}

    @DynamoDBAttribute(attributeName = "DOB")
    public String getDob() {return dob;}

    public void setDob(String ps) {dob = ps;}
}
