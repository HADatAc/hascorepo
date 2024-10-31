package module;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.hascoapi.RepositoryInstance;
import org.hascoapi.utils.NameSpaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hascoapi.utils.ConfigProp;


@Singleton
public class OnStart {

    private static final Logger log = LoggerFactory.getLogger(OnStart.class);

    @Inject
    public OnStart() {
        initDirectoryStructure();
        RepositoryInstance.getInstance();
        NameSpaces.getInstance().updateLocalNamespace();
    }

    private void initDirectoryStructure() {
        List<String> listFolderPaths = new LinkedList<String>();
        listFolderPaths.add(ConfigProp.getPathIngestion());
		/*listFolderPaths.add("tmp");
		listFolderPaths.add("logs");
		listFolderPaths.add("processed_csv");
		listFolderPaths.add("unprocessed_csv");
		listFolderPaths.add("downloaded_csv");
		listFolderPaths.add("working_csv");
		listFolderPaths.add("tmp/ttl");
		listFolderPaths.add("tmp/cache");
		listFolderPaths.add("tmp/uploads");*/

		for(String path : listFolderPaths){
			File folder = new File(path);
			// if the directory does not exist, create it
			if (!folder.exists()) {
				System.out.println("creating directory: " + path);
				try{
					folder.mkdir();
				} 
				catch(SecurityException se){
					System.out.println("Failed to create directory.");
				}
				System.out.println("DIR created");
			}
		}
    }
}

