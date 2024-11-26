package com.example.uniride;

// Utility class for handling car types and images
public class CarTypeUtils {
    // Car image resource constants
    public static final int TYPE_HATCHBACK = R.drawable.hatchback;
    public static final int TYPE_SEDAN = R.drawable.sedan;
    public static final int TYPE_SUV = R.drawable.suv;
    public static final int TYPE_VAN = R.drawable.van;
    public static final int TYPE_MPV = R.drawable.mpv;

    // Get array of car types for spinner
    public static String[] getCarTypes() {
        return new String[]{"Hatchback", "Sedan", "SUV", "Van", "MPV"};
    }

    // Convert type string to corresponding image resource
    public static int getCarImageResource(String type) {
        switch(type.toLowerCase()) {
            case "hatchback":
                return TYPE_HATCHBACK;
            case "sedan":
                return TYPE_SEDAN;
            case "suv":
                return TYPE_SUV;
            case "van":
                return TYPE_VAN;
            case "mpv":
                return TYPE_MPV;
            default:
                return TYPE_SEDAN;
        }
    }

    // Get type string from image resource
    public static String getTypeFromResource(int resource) {
        if (resource == TYPE_HATCHBACK) return "Hatchback";
        if (resource == TYPE_SEDAN) return "Sedan";
        if (resource == TYPE_SUV) return "SUV";
        if (resource == TYPE_VAN) return "Van";
        if (resource == TYPE_MPV) return "MPV";
        return "Sedan";
    }
}