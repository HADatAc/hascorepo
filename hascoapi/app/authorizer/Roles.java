package authorizers;

public class Roles {

    public static final String AUTHENTICATED = "authenticated";
    public static final String ADMINISTRATOR = "administrator";
 
    public static final String AUTHENTICATED_ADMINISTRATOR = AUTHENTICATED + "," + ADMINISTRATOR;
    
}
