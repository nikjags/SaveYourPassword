
/**
 * The {@code Record} class represents a simple login-password pair with a {@code tag} field, containing
 * an information about what's this pair for.
 * <p> Class contains some methods for changing and getting inner fields.
 *
 * @author nikjag
 * @version 1.0
 */

public class Record {
    /**
     * Field is used to represent an affiliation with something requiring login-password authentication:
     * sites, games, your office computer, etc.
     */
    private String tag;

    /**
     * Field is used to represent a login.
     */
    private String login;

    /**
     * Field is used to represent a password.
     */
    private String password;

    /**
     * Initializes a new {@code Record} object with {@code tag},{@code login},{@code password} fields.
     */
    Record(String tag, String login, String password){
        this.tag = tag;
        this.login = login;
        this.password = password;
    }

    /**
     * Changes a tag field.
     * @param tag
     *        A new tag value
     */
    public void setTag(String tag){
        this.tag = tag;
    }

    /**
     * Changes a {@code login} field.
     * @param login
     *        A new login value
     */
    public void setLogin(String login){
        this.login = login;
    }

    /**
     * Changes a {@code password} field.
     * @param password
     *        A new password value
     */
    public void setPassword(String password){
        this.password = password;
    }

    /**
     * Returns a tag.
     * @return a value in the {@code tag} field.
     */
    public String getTag(){
        return tag;
    }

    /**
     * Returns a login.
     * @return a value in the {@code login} field.
     */
    public String getLogin(){
        return login;
    }

    /**
     * Returns a password.
     * @return a value in the {@code password} field.
     */
    public String getPassword(){
        return password;
    }

    /**
     * Return a string representation of a Record object
     * @return a {@code String} object, representing a string representation of a Record.
     */
    @Override
    public String toString(){
        return "Tags: " + tag + "; login: " + login + "; password: " + password;
    }

}
