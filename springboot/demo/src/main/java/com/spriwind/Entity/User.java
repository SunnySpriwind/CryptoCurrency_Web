package com.spriwind.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Qimeng Chen
 * @since 2024/07/01
 * This class is an entity class of user data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Integer id;

    private String name;

    private String password;

    private String email;

    private String image;

    private LocalDateTime create_time;

}
