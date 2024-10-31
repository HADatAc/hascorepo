package org.hascoapi.entity.pojo;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.hascoapi.vocabularies.VSTOI;

import static org.hascoapi.Constants.*;

@JsonFilter("detectorInstanceFilter")
public class DetectorInstance extends VSTOIInstance {

	public DetectorInstance() {
		this.setTypeUri(VSTOI.DETECTOR_INSTANCE);
		this.setHascoTypeUri(VSTOI.DETECTOR_INSTANCE); 
	}

	public static DetectorInstance find(String uri) {
		DetectorInstance instance = new DetectorInstance();
		return (DetectorInstance)VSTOIInstance.find(instance,uri);
	} 

}
