package com.citymate.userapi.dto;

import com.citymate.userapi.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicUserDTO {

    private String username;
    private String firstName;
    private String lastName;
    private String profilePictureUrl;
    private String bio;
    private String city;
    private User.ProfileType profileType;

    public PublicUserDTO(User user) {
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.profilePictureUrl = user.getProfilePictureUrl();
        this.bio = user.getBio();
        this.city = user.getCity();
        this.profileType = user.getProfileType();
    }
}
