package rcms.utilities.daqaggregator.data;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import rcms.utilities.daqaggregator.datasource.FlashlistType;
import rcms.utilities.daqaggregator.mappers.FlashlistUpdatable;

/**
 * set of trigger rates obtained from the TCDS flashlists
 */
public class TCDSTriggerRates implements FlashlistUpdatable {
	
  //arrays of rate values tt[i], where i=0,...,k (tt0 always at array position 0 etc.)
	private List<Double> sup_trg_rate_beamactive_tt_values;
	private List<Double> sup_trg_rate_tt_values;
	private List<Double> trg_rate_beamactive_tt_values;
	private List<Double> trg_rate_tt_values;

	//totals
	private double sup_trg_rate_beamactive_total;
	private double sup_trg_rate_total;
	private double trg_rate_beamactive_total;
	private double trg_rate_total;

	//lumi section to which rates monitoring data correspond
	private int sectionNumber_rates;

	public TCDSTriggerRates() {
		super();
		
		sup_trg_rate_beamactive_tt_values = new ArrayList<Double>();
		sup_trg_rate_tt_values = new ArrayList<Double>();
		trg_rate_beamactive_tt_values = new ArrayList<Double>();
		trg_rate_tt_values = new ArrayList<Double>();
		
	}

	public void updateFromFlashlist(FlashlistType flashlistType, JsonNode flashlistRow) {
	
		//reset arrays to flush old entries
		sup_trg_rate_beamactive_tt_values = new ArrayList<Double>();
		sup_trg_rate_tt_values = new ArrayList<Double>();
		trg_rate_beamactive_tt_values = new ArrayList<Double>();
		trg_rate_tt_values = new ArrayList<Double>();

		//set ls number
		sectionNumber_rates = flashlistRow.get("section_number").asInt();

		//set totals
		sup_trg_rate_beamactive_total = flashlistRow.get("sup_trg_rate_beamactive_total").asDouble();
		sup_trg_rate_total = flashlistRow.get("sup_trg_rate_total").asDouble();
		trg_rate_beamactive_total = flashlistRow.get("trg_rate_beamactive_total").asDouble();
		trg_rate_total = flashlistRow.get("trg_rate_total").asDouble();

		//pattern for column name
		String pattern;

		//tt[0] to tt[max] are not parsed in guaranteed order, so this map is needed to later store in correct order
		Map<Integer,Double> positionToValue;

		//fill sup_trg_rate_beamactive_tt_values
		pattern = "sup_trg_rate_beamactive_tt\\d+";

		positionToValue = new HashMap<Integer, Double>();

		for (Iterator<String> fieldNameIter = flashlistRow.fieldNames(); fieldNameIter.hasNext() ;){
			String fieldName = fieldNameIter.next();
			if (!Pattern.matches(pattern, fieldName)){
				continue;
			}

			int i = Integer.parseInt(fieldName.substring(26));
			positionToValue.put(i, flashlistRow.get(fieldName).asDouble());
		}

		//adding values from map to array, in tt[0]->tt[max] ordering
		for (int i=0;i<positionToValue.size();i++){
			sup_trg_rate_beamactive_tt_values.add(positionToValue.get(i));
		}

		//fill sup_trg_rate_tt_values
		pattern = "sup_trg_rate_tt\\d+";

		positionToValue = new HashMap<Integer, Double>();

		for (Iterator<String> fieldNameIter = flashlistRow.fieldNames(); fieldNameIter.hasNext() ;){
			String fieldName = fieldNameIter.next();
			if (!Pattern.matches(pattern, fieldName)){
				continue;
			}

			int i = Integer.parseInt(fieldName.substring(15));
			positionToValue.put(i, flashlistRow.get(fieldName).asDouble());
		}

		//adding values from map to array, in tt[0]->tt[max] ordering
		for (int i=0;i<positionToValue.size();i++){
			sup_trg_rate_tt_values.add(positionToValue.get(i));
		}

		//fill trg_rate_beamactive_tt_values
		pattern = "trg_rate_beamactive_tt\\d+";

		positionToValue = new HashMap<Integer, Double>();

		for (Iterator<String> fieldNameIter = flashlistRow.fieldNames(); fieldNameIter.hasNext() ;){
			String fieldName = fieldNameIter.next();
			if (!Pattern.matches(pattern, fieldName)){
				continue;
			}

			int i = Integer.parseInt(fieldName.substring(22));
			positionToValue.put(i, flashlistRow.get(fieldName).asDouble());
		}

		//adding values from map to array, in tt[0]->tt[max] ordering
		for (int i=0;i<positionToValue.size();i++){
			trg_rate_beamactive_tt_values.add(positionToValue.get(i));
		}

		//fill trg_rate_tt_values
		pattern = "trg_rate_tt\\d+";

		positionToValue = new HashMap<Integer, Double>();

		for (Iterator<String> fieldNameIter = flashlistRow.fieldNames(); fieldNameIter.hasNext() ;){
			String fieldName = fieldNameIter.next();
			if (!Pattern.matches(pattern, fieldName)){
				continue;
			}

			int i = Integer.parseInt(fieldName.substring(11));
			positionToValue.put(i, flashlistRow.get(fieldName).asDouble());
		}

		//adding values from map to array, in tt[0]->tt[max] ordering
		for (int i=0;i<positionToValue.size();i++){
			trg_rate_tt_values.add(positionToValue.get(i));
		}

	}

	public void clean() {
		
		sup_trg_rate_beamactive_tt_values = new ArrayList<Double>();
		sup_trg_rate_tt_values = new ArrayList<Double>();
		trg_rate_beamactive_tt_values = new ArrayList<Double>();
		trg_rate_tt_values = new ArrayList<Double>();
		sectionNumber_rates = 0;
		sup_trg_rate_beamactive_total = 0;
		sup_trg_rate_total = 0;
		trg_rate_beamactive_total = 0;
		trg_rate_total = 0;

	}
	
	public List<Double> getSup_trg_rate_beamactive_tt_values() {
		return sup_trg_rate_beamactive_tt_values;
	}

	public void setSup_trg_rate_beamactive_tt_values(List<Double> sup_trg_rate_beamactive_tt_values) {
		this.sup_trg_rate_beamactive_tt_values = sup_trg_rate_beamactive_tt_values;
	}

	public List<Double> getSup_trg_rate_tt_values() {
		return sup_trg_rate_tt_values;
	}

	public void setSup_trg_rate_tt_values(List<Double> sup_trg_rate_tt_values) {
		this.sup_trg_rate_tt_values = sup_trg_rate_tt_values;
	}

	public List<Double> getTrg_rate_beamactive_tt_values() {
		return trg_rate_beamactive_tt_values;
	}

	public void setTrg_rate_beamactive_tt_values(List<Double> trg_rate_beamactive_tt_values) {
		this.trg_rate_beamactive_tt_values = trg_rate_beamactive_tt_values;
	}

	public List<Double> getTrg_rate_tt_values() {
		return trg_rate_tt_values;
	}

	public void setTrg_rate_tt_values(List<Double> trg_rate_tt_values) {
		this.trg_rate_tt_values = trg_rate_tt_values;
	}

	public double getSup_trg_rate_beamactive_total() {
		return sup_trg_rate_beamactive_total;
	}

	public void setSup_trg_rate_beamactive_total(double sup_trg_rate_beamactive_total) {
		this.sup_trg_rate_beamactive_total = sup_trg_rate_beamactive_total;
	}

	public double getSup_trg_rate_total() {
		return sup_trg_rate_total;
	}

	public void setSup_trg_rate_total(double sup_trg_rate_total) {
		this.sup_trg_rate_total = sup_trg_rate_total;
	}

	public double getTrg_rate_beamactive_total() {
		return trg_rate_beamactive_total;
	}

	public void setTrg_rate_beamactive_total(double trg_rate_beamactive_total) {
		this.trg_rate_beamactive_total = trg_rate_beamactive_total;
	}

	public double getTrg_rate_total() {
		return trg_rate_total;
	}

	public void setTrg_rate_total(double trg_rate_total) {
		this.trg_rate_total = trg_rate_total;
	}

	public int getSectionNumber_rates() {
		return sectionNumber_rates;
	}

	public void setSectionNumber_rates(int sectionNumber_rates) {
		this.sectionNumber_rates = sectionNumber_rates;
	}
	
}
