package codesquad.application.db;

import codesquad.application.model.User;
import codesquad.was.dbcp.ConnectionPool;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.TestUtil;

class UserDaoTest {

    private static Logger log = LoggerFactory.getLogger(UserDaoTest.class);

    private static JdbcTemplate jdbcTemplate;

    private static UserDao userDao;

    private User user;

    @BeforeAll
    static void beforeAll() {
        ConnectionPool pool = TestUtil.setUpDbAndGetConnectionPool();
        jdbcTemplate = new JdbcTemplate(pool);
        userDao = new UserDao(jdbcTemplate);
    }

//    @BeforeEach
//    void setUp() {
//        userDao.deleteAll();
//        user = new User("testuser", "testnickname", "testpassword");
//    }
//
//    @Test
//    void testSave() {
//        //when
//        userDao.save(user);
//
//        //then
//        assertNotNull(user.getId());
//        assertEquals(1, userDao.findAll().size());
//    }
//
//    private void createUser() {
//        userDao.save(user);
//    }
//
//    @Test
//    void testFindByUsername() {
//        //given
//        createUser();
//
//        //when
//        User foundUser = userDao.findByUsername(user.getUsername()).get();
//
//        //then
//        assertEquals(user.getId(), foundUser.getId());
//        assertEquals(user.getUsername(), foundUser.getUsername());
//        assertEquals(user.getNickname(), foundUser.getNickname());
//        assertEquals(user.getPassword(), foundUser.getPassword());
//    }
//

}
