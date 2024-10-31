package org.hascoapi;

import org.hascoapi.entity.pojo.Repository;

public class RepositoryInstance {

    private static Repository instance = null;

    public static Repository getInstance() {
        if (instance == null) {
            instance = Repository.getRepository();
            if (instance == null) {
                instance = new Repository();
                instance.save();
                System.out.println("RepositoryInstance: Brand new repository metadata has been created.");
            } else {
                System.out.println("RepositoryInstance: Existing repository metadata has been retrieved. Namespace URL is [" + instance.getHasDefaultNamespaceURL() + "]");
            }
        }
        return instance;
    }


}