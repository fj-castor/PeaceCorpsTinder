package com.blueoxfords.models;

/**
 * Created by soheil on 15-02-14.
 */
public class Region {

    public int id;

    public String
            name,
            slug,
            description,
            region_page_url;

    @Override
    public int hashCode() {
        return id;
    }
}
