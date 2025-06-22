
package demogame.dao;

import demogame.model.UserData;
import java.awt.Image;
import java.io.File;
import java.sql.ResultSet;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author acer
 */
public class UserDaoTest {
    
    public UserDaoTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of authenticate method, of class UserDao.
     */
    @Test
    public void testAuthenticate() {
        System.out.println("authenticate");
        String username = "";
        String password = "";
        UserDao instance = new UserDao();
        UserData expResult = null;
        UserData result = instance.authenticate(username, password);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAllUsers method, of class UserDao.
     */
    @Test
    public void testGetAllUsers() {
        System.out.println("getAllUsers");
        UserDao instance = new UserDao();
        List<UserData> expResult = null;
        List<UserData> result = instance.getAllUsers();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of ensureAdminUser method, of class UserDao.
     */
    @Test
    public void testEnsureAdminUser() {
        System.out.println("ensureAdminUser");
        UserDao instance = new UserDao();
        instance.ensureAdminUser();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTotalUserCount method, of class UserDao.
     */
    @Test
    public void testGetTotalUserCount() {
        System.out.println("getTotalUserCount");
        UserDao instance = new UserDao();
        int expResult = 0;
        int result = instance.getTotalUserCount();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getNewestUser method, of class UserDao.
     */
    @Test
    public void testGetNewestUser() {
        System.out.println("getNewestUser");
        UserDao instance = new UserDao();
        String expResult = "";
        String result = instance.getNewestUser();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateUser method, of class UserDao.
     */
    @Test
    public void testUpdateUser() {
        System.out.println("updateUser");
        UserData user = null;
        UserDao instance = new UserDao();
        boolean expResult = false;
        boolean result = instance.updateUser(user);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of register method, of class UserDao.
     */
    @Test
    public void testRegister_UserData() {
        System.out.println("register");
        UserData user = null;
        UserDao instance = new UserDao();
        boolean expResult = false;
        boolean result = instance.register(user);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getUserByUsername method, of class UserDao.
     */
    @Test
    public void testGetUserByUsername() {
        System.out.println("getUserByUsername");
        String username = "";
        UserDao instance = new UserDao();
        UserData expResult = null;
        UserData result = instance.getUserByUsername(username);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getUserById method, of class UserDao.
     */
    @Test
    public void testGetUserById() throws Exception {
        System.out.println("getUserById");
        int userId = 0;
        UserDao instance = new UserDao();
        UserData expResult = null;
        UserData result = instance.getUserById(userId);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateUsername method, of class UserDao.
     */
    @Test
    public void testUpdateUsername() {
        System.out.println("updateUsername");
        int userId = 0;
        String newUsername = "";
        UserDao instance = new UserDao();
        boolean expResult = false;
        boolean result = instance.updateUsername(userId, newUsername);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateProfilePicture method, of class UserDao.
     */
    @Test
    public void testUpdateProfilePicture() {
        System.out.println("updateProfilePicture");
        int userId = 0;
        File imageFile = null;
        UserDao instance = new UserDao();
        boolean expResult = false;
        boolean result = instance.updateProfilePicture(userId, imageFile);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getProfilePicture method, of class UserDao.
     */
    @Test
    public void testGetProfilePicture() {
        System.out.println("getProfilePicture");
        int userId = 0;
        UserDao instance = new UserDao();
        Image expResult = null;
        Image result = instance.getProfilePicture(userId);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of deleteUser method, of class UserDao.
     */
    @Test
    public void testDeleteUser() {
        System.out.println("deleteUser");
        int userId = 0;
        UserDao instance = new UserDao();
        boolean expResult = false;
        boolean result = instance.deleteUser(userId);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getUserChangeHistory method, of class UserDao.
     */
    @Test
    public void testGetUserChangeHistory() {
        System.out.println("getUserChangeHistory");
        int userId = 0;
        UserDao instance = new UserDao();
        ResultSet expResult = null;
        ResultSet result = instance.getUserChangeHistory(userId);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of deleteUserByUsername method, of class UserDao.
     */
    @Test
    public void testDeleteUserByUsername() {
        System.out.println("deleteUserByUsername");
        String username = "";
        UserDao instance = new UserDao();
        boolean expResult = false;
        boolean result = instance.deleteUserByUsername(username);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getUsernameById method, of class UserDao.
     */
    @Test
    public void testGetUsernameById() throws Exception {
        System.out.println("getUsernameById");
        int userId = 0;
        UserDao instance = new UserDao();
        String expResult = "";
        String result = instance.getUsernameById(userId);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updatePassword method, of class UserDao.
     */
    @Test
    public void testUpdatePassword() {
        System.out.println("updatePassword");
        String username = "";
        String newPassword = "";
        UserDao instance = new UserDao();
        boolean expResult = false;
        boolean result = instance.updatePassword(username, newPassword);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getEmailByUsername method, of class UserDao.
     */
    @Test
    public void testGetEmailByUsername() {
        System.out.println("getEmailByUsername");
        String username = "";
        UserDao instance = new UserDao();
        String expResult = "";
        String result = instance.getEmailByUsername(username);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updatePasswordByEmail method, of class UserDao.
     */
    @Test
    public void testUpdatePasswordByEmail() {
        System.out.println("updatePasswordByEmail");
        String email = "";
        String newPassword = "";
        UserDao instance = new UserDao();
        boolean expResult = false;
        boolean result = instance.updatePasswordByEmail(email, newPassword);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of register method, of class UserDao.
     */
    @Test
    public void testRegister_3args() {
        System.out.println("register");
        String email = "";
        String password = "";
        String role = "";
        UserDao instance = new UserDao();
        boolean expResult = false;
        boolean result = instance.register(email, password, role);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getErrorMessage method, of class UserDao.
     */
    @Test
    public void testGetErrorMessage() {
        System.out.println("getErrorMessage");
        UserDao instance = new UserDao();
        String expResult = "";
        String result = instance.getErrorMessage();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
