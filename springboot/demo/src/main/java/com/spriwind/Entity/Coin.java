package com.spriwind.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Qimeng Chen
 * @since 2024/07/01
 * This class is an entity class of Coin data.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coin {

    private int id;

    private String name;

    private String price;

    private String market;

    private LocalDateTime pricetime;

    private String vol;

    private String marketcap;
}
