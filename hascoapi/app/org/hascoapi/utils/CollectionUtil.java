package org.hascoapi.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.typesafe.config.ConfigFactory;

public class CollectionUtil {

    // private variables
    private static CollectionUtil single_instance = null;
    private static Map<String, String> configCache = null;

    private CollectionUtil() {
        configCache = new HashMap<String,String>();
        initConfigCache();
    }

    public void initConfigCache() {
        configCache = new HashMap<String, String>();
        configCache.put("hascoapi.repository.triplestore", ConfigFactory.load().getString("hascoapi.repository.triplestore"));
    }

    // static method to create instance of Singleton class
    public static CollectionUtil getInstance() {
        if (single_instance == null) {
            single_instance = new CollectionUtil();
        }
        return single_instance;
    }

    public Map<String, String> getInstanceConfigCache() {
        return configCache;
    }

    public static Map<String, String> getConfigCache() {
        return CollectionUtil.getInstance().getInstanceConfigCache();
    }

    public enum Collection {

        // triplestore
        SPARQL_QUERY ("/store/query"),
        SPARQL_UPDATE ("/store/update"),
        SPARQL_GRAPH ("/store/data");

        private final String collectionString;

        private Collection(String collectionString) {
            this.collectionString = collectionString;
        }

        public String get() {
            return collectionString;
        }
    }

    public static String getCollectionName(String collection) {
        if (Arrays.asList(
                Collection.SPARQL_QUERY.get(),
                Collection.SPARQL_UPDATE.get(),
                Collection.SPARQL_GRAPH.get()).contains(collection)) {
            return collection;
        }

        return collection;
    }

    public static String getCollectionPath(Collection collection) {
        String collectionName = null;
        switch (collection) {
            case SPARQL_QUERY:
            case SPARQL_UPDATE:
            case SPARQL_GRAPH :
                collectionName = getConfigCache().get("hascoapi.repository.triplestore") + getCollectionName(collection.get());
                break;
        }

        return collectionName;
    }
}

