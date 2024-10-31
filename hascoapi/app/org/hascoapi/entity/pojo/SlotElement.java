package org.hascoapi.entity.pojo;

public interface SlotElement {

    /*
     *  ContainerSlot and Subcontainers are slot elements of containers.
     */

    public String getUri();

    public String getHascoTypeUri();

    public String getHasNext();

    public void setHasNext(String hasPriority);

    public String getHasPrevious();

    public void setHasPrevious(String hasPrevious);

    public String getBelongsTo();

    public String getNamedGraph();

    public void setNamedGraph(String namedGraph);

    public String getHasPriority();

    public void setHasPriority(String hasPriority);

    public void save();

    public void delete();

}
