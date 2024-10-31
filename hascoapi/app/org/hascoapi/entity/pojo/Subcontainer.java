package org.hascoapi.entity.pojo;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSetRewindable;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.hascoapi.annotations.PropertyField;
import org.hascoapi.utils.CollectionUtil;
import org.hascoapi.utils.NameSpaces;
import org.hascoapi.utils.SPARQLUtils;
import org.hascoapi.utils.URIUtils;
import org.hascoapi.utils.Utils;
import org.hascoapi.vocabularies.HASCO;
import org.hascoapi.vocabularies.RDF;
import org.hascoapi.vocabularies.RDFS;
import org.hascoapi.vocabularies.VSTOI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hascoapi.Constants.*;

@JsonFilter("subcontainerFilter")
public class Subcontainer extends Container implements SlotElement {

	private static final Logger log = LoggerFactory.getLogger(Subcontainer.class);

	public Subcontainer () {
    }

	public Subcontainer (String className) {
		super(className);
    }

	@Override
	public boolean equals(Object o) {
		if((o instanceof Subcontainer) && (((Subcontainer)o).getUri().equals(this.getUri()))) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return getUri().hashCode();
	}

	public static Subcontainer find(String uri) {
	    Subcontainer subcontainer = null;
	    Statement statement;
	    RDFNode object;
	    
	    String queryString = "DESCRIBE <" + uri + ">";
	    Model model = SPARQLUtils.describe(CollectionUtil.getCollectionPath(
                CollectionUtil.Collection.SPARQL_QUERY), queryString);
		
		StmtIterator stmtIterator = model.listStatements();

		if (!stmtIterator.hasNext()) {
			return null;
		} else {
			subcontainer = new Subcontainer(VSTOI.SUBCONTAINER);
		}
		
		while (stmtIterator.hasNext()) {
		    statement = stmtIterator.next();
		    object = statement.getObject();
			String str = URIUtils.objectRDFToString(object);
			if (uri != null && !uri.isEmpty()) {
				if (statement.getPredicate().getURI().equals(RDFS.LABEL)) {
					subcontainer.setLabel(str);
				} else if (statement.getPredicate().getURI().equals(RDFS.SUBCLASS_OF)) {
					subcontainer.setSuperUri(str); 
				} else if (statement.getPredicate().getURI().equals(HASCO.HASCO_TYPE)) {
					subcontainer.setHascoTypeUri(str);
				} else if (statement.getPredicate().getURI().equals(VSTOI.HAS_STATUS)) {
					subcontainer.setHasStatus(str);
				} else if (statement.getPredicate().getURI().equals(RDFS.COMMENT)) {
					subcontainer.setComment(str);
				} else if (statement.getPredicate().getURI().equals(VSTOI.BELONGS_TO)) {
					subcontainer.setBelongsTo(str);
				} else if (statement.getPredicate().getURI().equals(VSTOI.HAS_FIRST)) {
					subcontainer.setHasFirst(str);
				} else if (statement.getPredicate().getURI().equals(VSTOI.HAS_NEXT)) {
					subcontainer.setHasNext(str);
				} else if (statement.getPredicate().getURI().equals(VSTOI.HAS_PREVIOUS)) {
					subcontainer.setHasPrevious(str);
				} else if (statement.getPredicate().getURI().equals(VSTOI.HAS_PRIORITY)) {
					subcontainer.setHasPriority(str);
				} else if (statement.getPredicate().getURI().equals(VSTOI.HAS_INFORMANT)) {
					subcontainer.setHasInformant(str);
				} else if (statement.getPredicate().getURI().equals(HASCO.HAS_IMAGE)) {
					subcontainer.setImage(str);
				} else if (statement.getPredicate().getURI().equals(VSTOI.HAS_SHORT_NAME)) {
					subcontainer.setHasShortName(str);
				} else if (statement.getPredicate().getURI().equals(VSTOI.HAS_LANGUAGE)) {
					subcontainer.setHasLanguage(str);
				} else if (statement.getPredicate().getURI().equals(VSTOI.HAS_VERSION)) {
					subcontainer.setHasVersion(str);
				} else if (statement.getPredicate().getURI().equals(VSTOI.HAS_SIR_MANAGER_EMAIL)) {
					subcontainer.setHasSIRManagerEmail(str);
				}
			}
		}
		subcontainer.setUri(uri);
		if (subcontainer.getHascoTypeUri().equals(VSTOI.SUBCONTAINER)) {
            return subcontainer;
        } 
        return null;
	}

	/** 
	 *  Creates a subcontainer and includes it as a slot in the slotElement list of the container that it belongs to.
	 *  When deleting a container that is also going to be removed from the slotElement list, use the 
	 *  SlotOperation.deleteSlotElement()
	 */
	public boolean saveAsSlot() {
		//System.out.println("Subcontainer's named graph: " + this.getNamedGraph());
		System.out.println("Subcontainer's getBelongsTo: " + this.getBelongsTo());
		// Subcontair always added to the top of the list. This means that the parent container needs to be updated 
		Container parent = Container.find(getBelongsTo());
		if (parent == null) {
			System.out.println("[ERROR] Subcontainer.saveAndAttach(): could not find parent container");
			return false;
		}
		parent.setNamedGraph(this.getNamedGraph());
		if (parent.getHasFirst() == null) {
			// Subcointainer is the only element in the list
			parent.setHasFirst(this.getUri());
			parent.save();
			this.setHasNext(null); 
			this.setHasPrevious(null);
		} else {
			List<SlotElement> slotElements = parent.getSlotElements();
			if (slotElements == null || slotElements.size() <= 0) {
				System.out.println("[ERROR] Subcontainer.saveAndAttach(): cannot retrieve slot elements.");
				return false;
			}
			SlotElement last = slotElements.get(slotElements.size() - 1);
			if (last == null) {
				System.out.println("[ERROR] Subcontainer.saveAndAttach(): cannot retrieve last element of slots.");
				return false;
			}
			last.setNamedGraph(this.getNamedGraph());
			last.setHasNext(this.getUri());
			this.setHasNext(null);
			this.setHasPrevious(last.getUri());
			last.save();
		}
		// SAVE SUBCONTAINET
		this.save();
		return true;
	}

	public static boolean updateAsSlot(String subcontainerJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        Subcontainer subcontainer;
		try {
			subcontainer = (Subcontainer)objectMapper.readValue(subcontainerJson, Subcontainer.class);
            } catch (JsonProcessingException e) {
                String message = e.getMessage();
				return false;
            }
		return Subcontainer.updateAsSlot(subcontainer);
	}

	public static boolean updateAsSlot(Subcontainer newSubcontainer) {
		Subcontainer curSubcontainer = Subcontainer.find(newSubcontainer.getUri());
		if (curSubcontainer == null) {
			return false;
		} 

		// properties that this update can change in the subcontainer
		curSubcontainer.setLabel(newSubcontainer.getLabel());
		curSubcontainer.setSuperUri(newSubcontainer.getSuperUri()); 
		curSubcontainer.setHascoTypeUri(newSubcontainer.getHascoTypeUri());
		curSubcontainer.setHasStatus(newSubcontainer.getHasStatus());
		curSubcontainer.setComment(newSubcontainer.getComment());
		curSubcontainer.setHasPriority(newSubcontainer.getHasPriority());
		curSubcontainer.setHasInformant(newSubcontainer.getHasInformant());
		curSubcontainer.setImage(newSubcontainer.getImage());
		curSubcontainer.setHasShortName(newSubcontainer.getHasShortName());
		curSubcontainer.setHasLanguage(newSubcontainer.getHasLanguage());
		curSubcontainer.setHasVersion(newSubcontainer.getHasVersion());
		curSubcontainer.setHasSIRManagerEmail(newSubcontainer.getHasSIRManagerEmail());

		// properties that this update CANNOT change in the subcontainer
		// to create a subcontainer from scratch, use Subcontainer.saveAsSlot()
		// to delete a subcontainer, use SlotOperations.deleteSlotElement()
		//  - curSubcontainer.setUri();
		//  - curSubcontainer.setBelongsTo();
		//  - curSubcontainer.setHasFirst();
		//  - curSubcontainer.setHasNext();
		//  - curSubcontainer.setHasPrevious();

		curSubcontainer.save();
		return true;

	}

    @Override public void save() {
		saveToTripleStore();
	}

    @Override public void delete() {
		deleteFromTripleStore();
	}

}
