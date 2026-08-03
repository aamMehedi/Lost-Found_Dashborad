package com.lostandfound;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Item {
    private int id;
    private String name;
    private String category;
    private String description;
    private String status;
    private String dateReported;
    private String imagePath;

    public Item(int id, String name, String category, String description, String status, String dateReported, String imagePath) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
        this.status = status;
        this.dateReported = dateReported;
        this.imagePath = imagePath;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public String getDateReported() { return dateReported; }
    public String getImagePath() { return imagePath; }

    // --- NEW DUSTY LOGIC ---
    
    // Calculates how many days this item has been logged
    public long getDaysInStorage() {
        try {
            LocalDate reportedDate = LocalDate.parse(this.dateReported);
            return ChronoUnit.DAYS.between(reportedDate, LocalDate.now());
        } catch (Exception e) {
            return 0; // Fallback if date parsing fails
        }
    }

    // Checks if an item is sitting unclaimed for 30 or more days
    public boolean isDusty() {
        return getDaysInStorage() >= 30 && !"Claimed".equalsIgnoreCase(status);
    }

    // Formatted display for our UI table
    public String getAgingBadge() {
        if ("Claimed".equalsIgnoreCase(status)) {
            return "Resolved ✅";
        }
        long days = getDaysInStorage();
        if (days >= 30) {
            return "🧹 Dusty (" + days + " days)";
        } else {
            return "Fresh (" + days + " days)";
        }
    }
}