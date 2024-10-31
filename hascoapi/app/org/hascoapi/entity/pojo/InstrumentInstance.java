package org.hascoapi.entity.pojo;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.hascoapi.vocabularies.VSTOI;

import static org.hascoapi.Constants.*;

@JsonFilter("instrumentInstanceFilter")
public class InstrumentInstance extends VSTOIInstance {

	public InstrumentInstance() {
		this.setTypeUri(VSTOI.INSTRUMENT_INSTANCE);
		this.setHascoTypeUri(VSTOI.INSTRUMENT_INSTANCE); 
	}

	public static InstrumentInstance find(String uri) {
		InstrumentInstance instance = new InstrumentInstance();
		return (InstrumentInstance)VSTOIInstance.find(instance,uri);
	} 

}
