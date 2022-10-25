import org.jspringbot.MainContextHolder;
import org.jspringbot.spring.RobotScope;
import org.jspringbot.spring.SpringRobotLibrary;

/**
 * Created by sduan on 8/1/2016.
 */
public class NXLJSpringBotGlobal extends SpringRobotLibrary {
    public static final String ROBOT_LIBRARY_SCOPE = RobotScope.GLOBAL.getValue();

    {
        // For running Jspringbot outside "jspringbot runtime"
        MainContextHolder.create();
    }

    public NXLJSpringBotGlobal() throws Exception {
        super("spring/nxljspringbot-global.xml");
    }

    /**
     * Create new SpringRobotLibrary object using the given configuration.
     *
     * @param springConfigPath String configuration path
     */
    public NXLJSpringBotGlobal(String springConfigPath) throws Exception {
        super(springConfigPath);
    }

}
