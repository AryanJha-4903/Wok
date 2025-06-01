package com.Wok.Wok.Model;

// import com.Wok.Wok.Model.Comment;
// import com.Wok.Wok.Model.Profile;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentProfileDTO {
    private Comment comment;
    private Profile profile;
}
