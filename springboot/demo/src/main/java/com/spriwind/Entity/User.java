package com.spriwind.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Sunny Spriwind
 * @since
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
