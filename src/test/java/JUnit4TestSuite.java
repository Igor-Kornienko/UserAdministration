import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import serviceTest._JUnit4ServiceTestSuite;
import utilTest._JUnit4UtilTestSuite;

@Suite.SuiteClasses({_JUnit4UtilTestSuite.class, _JUnit4ServiceTestSuite.class})
@RunWith(Suite.class)
public class JUnit4TestSuite {
}
