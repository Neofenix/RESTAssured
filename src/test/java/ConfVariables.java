import java.util.Optional;
import java.util.Properties;

public class ConfVariables {

    public static Properties applicationProperties = ApplicationProperties.getInstance();

    public static String getHost(){
        return Optional.ofNullable(System.getenv("host")).orElse((String) applicationProperties.get("host"));
    }

    public static String getPath(){
        return Optional.ofNullable(System.getenv("pathone")).orElse((String) applicationProperties.get("pathone"));
    }
}
