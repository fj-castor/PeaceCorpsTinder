package com.blueoxfords;

import com.blueoxfords.models.KeywordPairing;
import com.blueoxfords.models.VolunteerOpening;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by soheil on 15-02-14.
 */
public class KeywordGenerator {

    //region STOP_WORDS
    public static final String[] STOP_WORDS_ARRAY = {
            "a",
            "about",
            "above",
            "after",
            "again",
            "against",
            "all",
            "am",
            "an",
            "and",
            "any",
            "are",
            "aren't",
            "as",
            "at",
            "be",
            "because",
            "been",
            "before",
            "being",
            "below",
            "between",
            "both",
            "but",
            "by",
            "can't",
            "cannot",
            "could",
            "couldn't",
            "did",
            "didn't",
            "do",
            "does",
            "doesn't",
            "doing",
            "don't",
            "down",
            "during",
            "each",
            "few",
            "for",
            "from",
            "further",
            "had",
            "hadn't",
            "has",
            "hasn't",
            "have",
            "haven't",
            "having",
            "he",
            "he'd",
            "he'll",
            "he's",
            "her",
            "here",
            "here's",
            "hers",
            "herself",
            "him",
            "himself",
            "his",
            "how",
            "how's",
            "i",
            "i'd",
            "i'll",
            "i'm",
            "i've",
            "if",
            "in",
            "into",
            "is",
            "isn't",
            "it",
            "it's",
            "its",
            "itself",
            "let's",
            "me",
            "more",
            "most",
            "mustn't",
            "my",
            "myself",
            "no",
            "nor",
            "not",
            "of",
            "off",
            "on",
            "once",
            "only",
            "or",
            "other",
            "ought",
            "our",
            "ours",
            "ourselves",
            "out",
            "over",
            "own",
            "same",
            "shan't",
            "she",
            "she'd",
            "she'll",
            "she's",
            "should",
            "shouldn't",
            "so",
            "some",
            "such",
            "than",
            "that",
            "that's",
            "the",
            "their",
            "theirs",
            "them",
            "themselves",
            "then",
            "there",
            "there's",
            "these",
            "they",
            "they'd",
            "they'll",
            "they're",
            "they've",
            "this",
            "those",
            "through",
            "to",
            "too",
            "under",
            "until",
            "up",
            "very",
            "was",
            "wasn't",
            "we",
            "we'd",
            "we'll",
            "we're",
            "we've",
            "were",
            "weren't",
            "what",
            "what's",
            "when",
            "when's",
            "where",
            "where's",
            "which",
            "while",
            "who",
            "who's",
            "whom",
            "why",
            "why's",
            "with",
            "won't",
            "would",
            "wouldn't",
            "you",
            "you'd",
            "you'll",
            "you're",
            "you've",
            "your",
            "yours",
            "yourself",
            "yourselves"
    };
    //endregion

    public static final List<String> STOP_WORDS = Arrays.asList(STOP_WORDS_ARRAY);

    public static List<KeywordPairing> generate(List<VolunteerOpening> openings) {
        List<KeywordPairing> keywordPairings = new ArrayList<>();

        for (VolunteerOpening opening : openings) {

            // skills
            String determinedSkillKeyword = determineKeywordFromText(opening.required_skills + " " + opening.desired_skills);
            String skillKeyword;
            if (opening.language_skills.equals("none")) {
                skillKeyword = determinedSkillKeyword;
            } else {
                skillKeyword = opening.language_skills + " " + determinedSkillKeyword;
            }
            keywordPairings.add(new KeywordPairing(skillKeyword, opening));

            // location
            keywordPairings.add(new KeywordPairing(opening.country + " " + "culture", opening));

            // type of work
            String titleKeyword = determineKeywordFromText(opening.title);
            String descriptionKeyword = determineKeywordFromText(opening.project_description);
            keywordPairings.add(new KeywordPairing(opening.sector + " " + titleKeyword + " " + descriptionKeyword, opening));
        }

        Collections.shuffle(keywordPairings);
        return keywordPairings;
    }

    public static String determineKeywordFromText(String text) {
        Map<String, Integer> wordCount = new HashMap<>();
        StringTokenizer st = new StringTokenizer(text);
        while (st.hasMoreTokens()) {
            String token = st.nextToken().toLowerCase();
            if (!STOP_WORDS.contains(token)) {
                wordCount.put(token, wordCount.get(token) == null ? 1 : wordCount.get(token) + 1);
            }
        }

        Map.Entry<String, Integer> highest = null;
        for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
            if (highest == null) {
                highest = entry;
            } else if (entry.getValue() >= highest.getValue()) {
                highest = entry;
            }
        }

        return highest.getKey();
    }

}
