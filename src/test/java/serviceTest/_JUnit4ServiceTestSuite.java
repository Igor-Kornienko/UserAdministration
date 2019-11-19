package serviceTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@Suite.SuiteClasses({AuthServiceJUnit4Test.class, CustomUserDetailServiceJUnit4Test.class,
        GoogleDriveServiceJUnit4Test.class, UserElasticsearchServiceJUnit4Test.class})
@RunWith(Suite.class)
public class _JUnit4ServiceTestSuite {
}
