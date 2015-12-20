package itsjustaaron.movietogether;

import android.graphics.Movie;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.ArrayList;

/**
 * Created by Aaron on 2015-12-08.
 */
@DynamoDBTable(tableName = "Lobby")
public class Entry {
    private boolean closed;
    private String name;
    private String email;
    private String phone;
    private int ref;
    private String movie;
    private int location;
    private String time;
    private String message;
    private ArrayList<String> propose = new ArrayList<String>();

    @DynamoDBIndexRangeKey(attributeName = "Name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @DynamoDBAttribute(attributeName = "E-mail")
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

    @DynamoDBHashKey(attributeName = "Reference")
    public int getRef() {
        return  ref;
    }

    public void setRef(int time) {
        this.ref = time;
    }

    @DynamoDBAttribute(attributeName = "Time")
    public String getTime() {
        return  time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @DynamoDBAttribute(attributeName = "Movie")
    public String getMovie() {
        return movie;
    }

    public void setMovie(String Movie) {
        this.movie = Movie;
    }

    @DynamoDBAttribute(attributeName = "Location")
    public int getLocation() {
        return location;
    }

    public void setLocation(int loc) {
        this.location = loc;
    }

    @DynamoDBAttribute(attributeName = "Message")
    public String getMessage() { return message;}

    public void setMessage(String msg) {message = msg;}

    @DynamoDBAttribute(attributeName = "Closed")
    public boolean getClosed() {return closed;}

    public void setClosed(boolean closed) {this.closed = closed;}

    @DynamoDBAttribute(attributeName = "Proposes")
    public ArrayList<String> getPropose() {return propose;}

    public void setPropose(ArrayList<String> pp) {propose = pp;}

    public void addPropose(String propose) {
        this.propose.add(propose);
    }
}

