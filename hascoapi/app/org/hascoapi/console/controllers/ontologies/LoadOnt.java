package org.hascoapi.console.controllers.ontologies;

import org.hascoapi.utils.Feedback;
import org.hascoapi.utils.Triplestore;
import play.mvc.*;

import org.hascoapi.entity.pojo.NameSpace;
import org.hascoapi.utils.NameSpaces;

import com.typesafe.config.ConfigFactory;

public class LoadOnt extends Controller {

    public static final String LAST_LOADED_NAMESPACE = "/last-loaded-namespaces-properties";

    public static String playLoadOntologiesAsync(String oper, String kb) {
        String resp = "";
        if (oper.equals("load")) {
            NameSpaces.getInstance();
            Triplestore ts = new Triplestore("user", "password", kb, false);
            resp = ts.loadOntologies(Feedback.WEB);
            //for (NameSpace ns : NameSpaces.getInstance().getNamespaces().values()) {
            //    ns.updateNumberOfLoadedTriples();
            //    ns.updateFromTripleStore();
            //}
        } else {
            for (NameSpace ns : NameSpaces.getInstance().getNamespaces().values()) {
                ns.deleteTriples();
                ns.setNumberOfLoadedTriples();
                //ns.updateFromTripleStore();
            }
            resp = "Content of ontologies deleted from the knowledge graph";
        }
        return resp;
    }

    public static String playLoadOntologies(String oper) {
        String resp = "";
        if (oper.equals("load")) {
            NameSpaces.getInstance();
            Triplestore ts = new Triplestore("user", "password", ConfigFactory.load().getString("hascoapi.repository.triplestore"), false);
            resp = ts.loadOntologies(Feedback.WEB);
            for (NameSpace ns : NameSpaces.getInstance().getNamespaces().values()) {
                ns.setNumberOfLoadedTriples();
                //ns.updateFromTripleStore();
            }
        } else {
            for (NameSpace ns : NameSpaces.getInstance().getNamespaces().values()) {
                ns.deleteTriples();
                ns.setNumberOfLoadedTriples();
                //ns.updateFromTripleStore();
            }
            resp = "Content of ontologies deleted from the knowledge graph";
        }
        return resp;
    }

    public Result reloadNamedGraphFromRemote(String abbreviation) {
        NameSpace ns = NameSpaces.getInstance().getNamespaces().get(abbreviation);
        ns.deleteTriples();

        String url = ns.getSource();
        if (!url.isEmpty()) {
            ns.loadTriples(url, true);
        }
        ns.setNumberOfLoadedTriples();
        //ns.updateFromTripleStore();

        return redirect(routes.Maintenance.index());
    }

    public Result deleteNamedGraph(String abbreviation) {
        NameSpace ns = NameSpaces.getInstance().getNamespaces().get(abbreviation);
        ns.deleteTriples();
        ns.setNumberOfLoadedTriples();
        //ns.updateFromTripleStore();

        return redirect(routes.Maintenance.index());
    }

    public Result deleteAllNamedGraphs() {
        for (NameSpace ns : NameSpaces.getInstance().getNamespaces().values()) {
            ns.deleteTriples();
            ns.setNumberOfLoadedTriples();
            //ns.updateFromTripleStore();
        }

        return redirect(routes.Maintenance.index());
    }

}

