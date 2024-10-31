package org.hascoapi.utils;

import java.util.Random;
import java.security.*;

import org.hascoapi.Constants;
import org.hascoapi.RepositoryInstance;

public class Utils {

    public static int random(final int max) {
        return (int) (Math.random() * (double) max);
    }

    public static void wait(final int millis) {
        try {
            Thread.sleep(millis);
        } catch (final Exception e) {}
    }

    public static int block() {
        final int wait = 10 + random(20);
        wait(wait);
        return wait;
    }

    public static String adjustedPriority(String priority, int totContainerSlots) {
        int digits = 0;
        if (totContainerSlots < 10) {
            digits = 1;
        } else if (totContainerSlots < 100) {
            digits = 2;
        } else if (totContainerSlots < 1000) {
            digits = 3;
        } else {
            digits = 4;
        }
        String auxstr = String.valueOf(priority);
        for (int filler = auxstr.length(); filler < digits; filler++) {
            auxstr = "0" + auxstr;
        }
        return auxstr;
    }

    public static String shortPrefix(String elementType) {
        if (elementType == null || elementType.isEmpty()) {
            return null;
        }
        String shortPrefix;
        switch (elementType) {
            case "instrument":
                shortPrefix = Constants.PREFIX_INSTRUMENT;
                break;
            case "instrumentinstance":
                shortPrefix = Constants.PREFIX_INSTRUMENT_INSTANCE;
                break;
            case "detectorinstance":
                shortPrefix = Constants.PREFIX_DETECTOR_INSTANCE;
                break;
            case "platforminstance":
                shortPrefix = Constants.PREFIX_PLATFORM_INSTANCE;
                break;
            case "subcontainer":
                shortPrefix = Constants.PREFIX_SUBCONTAINER;
                break;
            case "detectorstem":
                shortPrefix = Constants.PREFIX_DETECTOR_STEM;
                break;
            case "detector":
                shortPrefix = Constants.PREFIX_DETECTOR;
                break;
            case "codebook":
                shortPrefix = Constants.PREFIX_CODEBOOK;
                break;
            case "responseoption":
                shortPrefix = Constants.PREFIX_RESPONSE_OPTION;
                break;
            case "annotationstem":
                shortPrefix = Constants.PREFIX_ANNOTATION_STEM;
                break;
            case "annotation":
                shortPrefix = Constants.PREFIX_ANNOTATION;
                break;
            case "semanticvariable":
                shortPrefix = Constants.PREFIX_SEMANTIC_VARIABLE;
                break;
            case "da":
                shortPrefix = Constants.PREFIX_DATA_ACQUISITION;
                break;
            case "dd":
                shortPrefix = Constants.PREFIX_DD;
                break;
            case "sdd":
                shortPrefix = Constants.PREFIX_SDD;
                break;
            case "semanticdatadictionary":
                shortPrefix = Constants.PREFIX_SEMANTIC_DATA_DICTIONARY;
                break;
            case "dp2":
                shortPrefix = Constants.PREFIX_DP2;
                break;
            case "str":
                shortPrefix = Constants.PREFIX_STR;
                break;
            case "ins":
                shortPrefix = Constants.PREFIX_INS;
                break;
            case "datafile":
                shortPrefix = Constants.PREFIX_DATAFILE;
                break;
            case "dsg":
                shortPrefix = Constants.PREFIX_DSG;
                break;
            case "study":
                shortPrefix = Constants.PREFIX_STUDY;
                break;
            case "studyrole":
                shortPrefix = Constants.PREFIX_STUDY_ROLE;
                break;
            case "studyobjectcollection":
                shortPrefix = Constants.PREFIX_STUDY_OBJECT_COLLECTION;
                break;
            case "studyobject":
                shortPrefix = Constants.PREFIX_STUDY_OBJECT;
                break;
            case "virtualcolumn":
                shortPrefix = Constants.PREFIX_VIRTUAL_COLUMN;
                break;
            case "place":
                shortPrefix = Constants.PREFIX_PLACE;
                break;
            case "organization":
                shortPrefix = Constants.PREFIX_ORGANIZATION;
                break;
            case "person":
                shortPrefix = Constants.PREFIX_PERSON;
                break;
            case "postaladdress":
                shortPrefix = Constants.PREFIX_POSTAL_ADDRESS;
                break;
            default:
                shortPrefix = null;
        }
        return shortPrefix;
    }

    public static String uriGen(String elementType) {
        if (elementType == null) {
            System.out.println("[ERROR] Utils.uriGen(): elementType not provided.");
            return null;
        }
        String repoUri = RepositoryInstance.getInstance().getHasDefaultNamespaceURL();
        if (repoUri == null || repoUri.isEmpty()) {
            System.out.println("[ERROR] Utils.uriGen(): no baseURL found for current repository.");
            return null;
        }

        String shortPrefix = Utils.shortPrefix(elementType);
        if (shortPrefix == null) {
            System.out.println("[ERROR] Utils.uriGen(): could not found valid short prefix for elementType [" + elementType + "]");
            return null;
        }

        if (!repoUri.endsWith("/")) {
            repoUri += "/";
        }

        String generatedUri = Utils.uriGen(repoUri, shortPrefix);
        //System.out.println("Utils.uriGen() generated [" + generatedUri + "]");

        return generatedUri;
    }

    public static String uriGen(String repoUri, String shortPrefix) {
        //String uid = getCurrentUserId();
        String iid = System.currentTimeMillis() + String.valueOf(new Random().nextInt(90000) + 10000); // + uid;
        return repoUri + shortPrefix + iid;
    }

    public static String uriHashGen(String elementType, String identifier) {
        if (elementType == null) {
            System.out.println("[ERROR] Utils.uriHashGen(): elementType not provided.");
            return null;
        }
        String repoUri = RepositoryInstance.getInstance().getHasDefaultNamespaceURL();
        if (repoUri == null || repoUri.isEmpty()) {
            System.out.println("[ERROR] Utils.uriHashGen(): no baseURL found for current repository.");
            return null;
        }

        String shortPrefix = Utils.shortPrefix(elementType);
        if (shortPrefix == null) {
            System.out.println("[ERROR] Utils.uriHashGen(): could not found valid short prefix for elementType [" + elementType + "]");
            return null;
        }

        if (!repoUri.endsWith("/")) {
            repoUri += "/";
        }

        String hexString = null;        
        try {
            // Convert input string to bytes
            byte[] inputBytes = identifier.getBytes("UTF-8");

            // Calculate MD5 hash
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] md5Bytes = md.digest(inputBytes);

            // Convert byte array to hexadecimal string
            StringBuilder sb = new StringBuilder();
            for (byte b : md5Bytes) {
                sb.append(String.format("%02x", b & 0xff));
            }
            hexString = sb.toString();

            // Ensure the hexString is exactly 16 digits
            if (hexString.length() < 16) {
                hexString = String.format("%16s", hexString).replace(' ', '0');
            } else if (hexString.length() > 16) {
                hexString = hexString.substring(0, 16);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return repoUri + shortPrefix + hexString;

    }

    public static String uriPlainGen(String elementType, String identifier) {
        if (elementType == null) {
            System.out.println("[ERROR] Utils.uriHashGen(): elementType not provided.");
            return null;
        }
        String repoUri = RepositoryInstance.getInstance().getHasDefaultNamespaceURL();
        if (repoUri == null || repoUri.isEmpty()) {
            System.out.println("[ERROR] Utils.uriHashGen(): no baseURL found for current repository.");
            return null;
        }

        String shortPrefix = Utils.shortPrefix(elementType);
        if (shortPrefix == null) {
            System.out.println("[ERROR] Utils.uriHashGen(): could not found valid short prefix for elementType [" + elementType + "]");
            return null;
        }

        if (!repoUri.endsWith("/")) {
            repoUri += "/";
        }

        return repoUri + shortPrefix + '_' + identifier;

    }

}
