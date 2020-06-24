package com.xliic.sonar.model;

import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonAnySetter;

public class Issues extends HashMap<String, Issues.Issue> {

    @JsonAnySetter
    public void set(String id, Issue issue) {
        this.put(id, issue);
    }

    public static class Issue {
        public String group;
        public String subgroup;
        public Text title;
        public Text description;
        public String shortDescription;
        public Sections example;
        public Sections exploit;
        public Sections remediation;

        public String getHtml() {
            StringBuilder builder = new StringBuilder();
            if (description != null) {
                builder.append(description.text);
            }
            if (example != null) {
                builder.append(example.text());
            }
            if (exploit != null) {
                builder.append(exploit.text());
            }
            if (remediation != null) {
                builder.append(remediation.text());
            }

            return builder.toString();
        }
    }

    public static class Text {
        public String text;
        public String code;
    }

    public static class Sections {
        public Text[] sections;

        String text() {
            StringBuilder builder = new StringBuilder();
            for (Text section : sections) {
                if (section.text != null) {
                    builder.append(section.text);
                }
                if (section.code != null) {
                    builder.append(section.code);
                }
            }
            return builder.toString();
        }
    }
}