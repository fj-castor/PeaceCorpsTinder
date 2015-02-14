package com.blueoxfords.models;

import java.util.Date;

/**
 * Created by soheil on 15-02-14.
 */
public class VolunteerOpening {

    public String
            title,
            req_id,
            country,
            region,
            sector;

    public Date
            apply_date,
            know_by,
            staging_start_date;

    public boolean featured;

    public String
            project_description,
            required_skills,
            desired_skills,
            language_skills,
            language_skills_comments;

    public int volunteers_requested;

    public boolean accepts_couples;

    public String
            living_conditions_comments,
            country_medical_considerations,
            country_site_url,
            country_flag_image,
            opening_url;

    @Override
    public int hashCode() {
        return req_id.hashCode();
    }

}
