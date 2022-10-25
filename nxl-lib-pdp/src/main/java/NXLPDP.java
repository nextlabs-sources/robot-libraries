/**
 * Created by duan on 9/11/2015.
 */
import org.jspringbot.MainContextHolder;
import org.jspringbot.spring.RobotScope;
import org.jspringbot.spring.SpringRobotLibrary;

public class NXLPDP extends SpringRobotLibrary {
    public static final String ROBOT_LIBRARY_SCOPE = RobotScope.GLOBAL.getValue();

    {
        // For running Jspringbot outside "jspringbot runtime"
        MainContextHolder.create();
    }

    public NXLPDP() throws Exception {
        super("spring/nxl-lib-pdp.xml");
    }

    /**
     * Create new SpringRobotLibrary object using the given configuration.
     *
     * @param springConfigPath String configuration path
     */
    public NXLPDP(String springConfigPath) throws Exception {
        super(springConfigPath);
    }
}