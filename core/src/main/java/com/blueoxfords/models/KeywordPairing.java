package com.blueoxfords.models;

import java.util.Set;

/**
 * Created by soheil on 15-02-14.
 */
public class KeywordPairing {

    public String keyword;

    public VolunteerOpening volunteerOpening;

    public KeywordPairing(String keyword, VolunteerOpening volunteerOpening) {
        this.keyword = keyword;
        this.volunteerOpening = volunteerOpening;
    }

    @Override
    public String toString() {
        return keyword + ":" + volunteerOpening.toString();
    }

}
