package com.spriwind.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Qimeng Chen
 * @since 2024/07/01
 * This class is an entity class of user assets.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OwnedCoin {

    private int id;

    private String coinid;

    private String coin;

    private String owner;
}
