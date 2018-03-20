package kaaf.jruaa;

/**
 * Created by ashfi on 3/21/2018.
 */

public class Users {

    public String name;
    public String location;

    public Users(){

    }
    public Users(String name,String location){

        this.name = name;
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
