package org.hascoapi.entity.pojo;

import java.util.ArrayList;
import java.util.List;

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

import static org.hascoapi.Constants.*;

public class SlotOperations  {
    
    public static SlotElement findSlotElement(String slotElementUri) {
        //System.out.println("inside SlotOperations.findSlotElement() with uri: " + uri);
        ContainerSlot containerSlot = ContainerSlot.find(slotElementUri);
        if (containerSlot == null) {
            Subcontainer subcontainer = Subcontainer.find(slotElementUri);
            if (subcontainer == null) {
                return null;
            } else {
                //System.out.println("  Found as SUBCONTAINER");
                return (SlotElement)subcontainer;
            }
        }
        //System.out.println("  Found as CONTAINER SLOT");
        return (SlotElement)containerSlot;    
    }

    public static boolean deleteSlotElement(String uri) {
        SlotElement current = findSlotElement(uri);
        String nullvalue = null;
        if (current == null) {
            System.out.println("[ERROR] SlotOperation.deleteSlotElement(" + uri + ") could not retrieve CURRENT.");
            return false;
        }
        return deleteSlotElement(current);
    }
    
    public static boolean deleteSlotElement(SlotElement current) {
        String nullvalue = null;
        // IS FIRST SLOT ELEMENT
        if (current.getHasPrevious() == null) {
            Container parent = Container.find(current.getBelongsTo()); 
            if (current == null) {
                System.out.println("[ERROR] SlotOperation.deleteSlotElement(" + current.getBelongsTo() + ") could not retrieve CURRENT.");
                return false;
            }

            // UNIQUE ELEMENT
            if (current.getHasNext() == null) {
                parent.setHasFirst(nullvalue);
                parent.save();
                current.delete();
                return true;
            
            // FIRST BUT NOT UNIQUE ELEMENT
            } else {
                SlotElement next = findSlotElement(current.getHasNext());
                if (next == null) {
                    System.out.println("[ERROR] SlotOperation.deleteSlotElement(" + current.getHasNext() + ") could not retrieve NEXT element.");
                    return false;
                }
                next.setHasPrevious(nullvalue);
                next.save();
                parent.setHasFirst(next.getUri());
                parent.save();
                current.delete();
                return true; 
            }

        // IS NOT THE FIRST ELEMENT
        } else {

            // CURRENT HAS NEXT
            if (current.getHasNext() != null) {
                SlotElement previous = findSlotElement(current.getHasPrevious());
                if (previous == null) {
                    System.out.println("[ERROR] SlotOperation.deleteSlotElement(" + current.getHasPrevious() + ") could not retrieve PREVIOUS element.");
                    return false;
                }
                SlotElement next = findSlotElement(current.getHasNext());
                if (next == null) {
                    System.out.println("[ERROR] SlotOperation.deleteSlotElement(" + current.getHasNext() + ") could not retrieve NEXT element.");
                    return false;
                }
                previous.setHasNext(next.getUri());
                previous.save();
                next.setHasPrevious(previous.getUri());
                next.save();
                current.delete();
                return true;

            // CURRENT DOES NOT HAVE NEXT
            } else {
                SlotElement previous = findSlotElement(current.getHasPrevious());
                if (previous == null) {
                    System.out.println("[ERROR] SlotOperation.deleteSlotElement(" + current.getHasPrevious() + ") could not retrieve PREVIOUS element.");
                    return false;
                }
                previous.setHasNext(nullvalue);
                previous.save();
                current.delete();
                return true;
            }           
        }
    }

    public static boolean moveUp(String uri) {
        SlotElement current = findSlotElement(uri);
        if (current == null) {
            System.out.println("[ERROR] SlotOperations.moveUp(): could not retrieve CURRENT.");
            return false;
        }

        if (current.getHasPrevious() == null) {
            return false;
        }
        SlotElement previous = findSlotElement(current.getHasPrevious());
        SlotElement next = findSlotElement(current.getHasNext());

        if (previous == null) {
            System.out.println("[ERROR] SlotOperations.moveUp(): could not retrieve PREVIOUS.");
            return false;
        } else if (next == null) {
            System.out.println("[ERROR] SlotOperations.moveUp(): could not retrieve NEXT.");
            return false;
        }

        // set previuos
        previous.setHasNext(next.getUri()); 
        previous.setHasPrevious(current.getUri());
        previous.save();

        // set current
        current.setHasNext(previous.getUri());
        current.setHasPrevious(previous.getHasPrevious());
        current.save();

        // set next
        if (next != null) {
            next.setHasPrevious(previous.getUri());
            next.save();
        } 

        // is the first of the list. Needs to update parent container
        if (current.getHasPrevious() == null) {
            Container parent = Container.find(current.getBelongsTo());
            parent.setHasFirst(current.getUri());
            parent.save();
        }

        return true;
    }

    public static boolean moveDown(String uri) {
        SlotElement current = findSlotElement(uri);
        if (current == null) {
            System.out.println("[ERROR] SlotOperations.moveUp(): could not retrieve CURRENT.");
            return false;
        }

        if (current.getHasNext() == null) {
            return false;
        }

        SlotElement previous = findSlotElement(current.getHasPrevious());
        SlotElement next = findSlotElement(current.getHasNext());

        if (previous == null) {
            System.out.println("[ERROR] SlotOperations.moveUp(): could not retrieve PREVIOUS.");
            return false;
        } else if (next == null) {
            System.out.println("[ERROR] SlotOperations.moveUp(): could not retrieve NEXT.");
            return false;
        }

        SlotElement nextnext = findSlotElement(next.getHasNext());

        // was the first of the list. Needs to update parent container
        if (current.getHasPrevious() == null) {
            Container parent = Container.find(current.getBelongsTo());
            parent.setHasFirst(next.getUri());
            parent.save();
        }

        // set previous
        previous.setHasNext(next.getUri());
        previous.save();

        // set current 
        current.setHasNext(next.getHasNext());
        current.setHasPrevious(next.getUri());
        current.save();

        // set next
        next.setHasNext(current.getUri()); 
        next.setHasPrevious(previous.getUri());
        next.save();

        // set nextnext
        if (nextnext != null) {
            nextnext.setHasPrevious(current.getUri());
            nextnext.save();
        }

        return true;
    }

}

