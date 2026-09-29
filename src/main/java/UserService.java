import java.util.Collection;
import java.util.Map;

public class UserService {
    private Map<Integer, User> users;
    public UserService(Map<Integer, User> users){
        this.users = users;
    }
    public User findUserById(int id){
        return users.get(id);
    }
    public  void addUser(User user){
        users.put(user.getId(), user);
    }
    public void removeUser(int userId){
        users.remove(userId);
    }
    public boolean containsUser(int id) {
        return users.containsKey(id);
    }
    public int getUserCount() {
        return users.size();
    }
    public Collection<User> getAllUsers() {
        return users.values();
    }
}
