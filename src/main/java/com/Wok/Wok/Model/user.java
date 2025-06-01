package com.Wok.Wok.Model;

import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(collection = "app_user")
public class user {
    @Id
    private String id;
    private String username;
    private String password;
    private String email;
    private List<String> roles;
   
}
