package com.spriwind.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Sunny Spriwind
 * @since
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Preference {

    private String username;

    private int userid;

    private String currencyname;

    private String marketname;

    private String selectcharts;
}
