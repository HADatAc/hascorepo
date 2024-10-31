package org.hascoapi.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;
import org.hascoapi.RepositoryInstance;

import org.json.simple.JSONArray;

import com.typesafe.config.ConfigFactory;

public class ConfigProp {

    private static Properties getProperties(String confFileName) {
        Properties prop = new Properties();
        try {
            InputStream is = ConfigProp.class.getClassLoader().getResourceAsStream(confFileName);
            prop.load(is);
            is.close();
        } catch (Exception e) {
            return null;
        }

        return prop;
    }

    public static String getPropertyValue(String confFileName, String field) {
        Properties prop = getProperties(confFileName);
        if (null == prop) {
            return "";
        }
        return prop.getProperty(field);
    }

    public static void setPropertyValue(String confFileName, String field, String value) {
        Properties prop = getProperties(confFileName);
        if (null == prop) {
            return;
        }
        prop.setProperty(field, value);
        URL url = ConfigProp.class.getClassLoader().getResource(confFileName);
        try {
            prop.store(new FileOutputStream(new File(url.toURI())), null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static String getBaseURL() {
        return ConfigFactory.load().getString("hascoapi.console.host_deploy");
    }

    public static String getJWTSecret() {
        return ConfigFactory.load("application.conf").getString("pac4j.jwt.secret");
    }
    public static String getKbPrefix() {
        //return RepositoryInstance.getInstance().getHasDefaultNamespaceAbbreviation() + "-kb:";
        return RepositoryInstance.getInstance().getHasDefaultNamespaceAbbreviation() + ":";
    }

	public static String getTemplateFileName() {
        System.out.println("ConfigProp: getTemplateFileName()=" + ConfigFactory.load().getString("hascoapi.templates.template_filename"));
		return ConfigFactory.load().getString("hascoapi.templates.template_filename");
	}
    
	public static String getPathIngestion() {
	    return ConfigFactory.load().getString("hascoapi.paths.ingestion");
	}



    /**
    public static String getBasePrefix() {
        return ConfigFactory.load().getString("hascoapi.community.ont_prefix");
    }

    public static String getPageTitle() {
        return ConfigFactory.load().getString("hascoapi.community.pagetitle");
    }
    public static String getShortName() {
        return ConfigFactory.load().getString("hascoapi.community.shortname");
    }
    public static String getFullName() {
        return ConfigFactory.load().getString("hascoapi.community.fullname");
    }
    public static String getDescription() {
        return ConfigFactory.load().getString("hascoapi.community.description");
    }
    public static String getNSAbbreviation() {
        return ConfigFactory.load().getString("hascoapi.namespace.abbreviation");
    }
    public static String getNSValue() {
        return ConfigFactory.load().getString("hascoapi.namespace.value");
    }
     */


}
