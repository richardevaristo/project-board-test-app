package com.richardevaristo.projectboardapi.Service;

import com.richardevaristo.projectboardapi.Dao.UserReposity;
import com.richardevaristo.projectboardapi.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserReposity userReposity;

    public String login(String username, String password) {
        User user = userReposity.findByUsernameAndPassword(username, password);
        if(user != null) {
            user.setToken(generateToken());
            return user.getToken();
        }
        return "Unauthorized!!";
    }

    private String generateToken() {
        String alphanumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder builder = new StringBuilder();
        int tokenLenght = 20;
        while (tokenLenght-- != 0) {
            int character = (int)(Math.random()*alphanumeric.length());
            builder.append(alphanumeric.charAt(character));
        }
        return builder.toString();
    }

    public User register(User user) {
        return userReposity.save(user);
    }
}
