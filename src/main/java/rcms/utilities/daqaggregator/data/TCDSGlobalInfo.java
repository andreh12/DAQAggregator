package rcms.utilities.daqaggregator.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;

import rcms.utilities.daqaggregator.datasource.FlashlistType;
import rcms.utilities.daqaggregator.mappers.FlashlistUpdatable;

public class TCDSGlobalInfo implements FlashlistUpdatable{

	/**
	 * Container of global information from the TCDS flashlists
	 *
	 * @author Michail Vougioukas (michail.vougioukas@cern.ch)
	 */

	//pm channel global tts states
	private Map<String, GlobalTTSState> globalTtsStates;// = new HashMap<String, GlobalTTSState>();

	/*cpm counts*/
	
	//lumi section to which counts monitoring data correspond
	private int sectionNumber_counts;
	
	//totals
	private int sup_trg_cnt_beamactive_total;
	private int sup_trg_cnt_total;
	private int trg_cnt_beamactive_total;
	private int trg_cnt_total;

	//arrays of count values tt[i], where i=0,...,k (tt0 always at array position 0 etc.)
	private List<Integer> sup_trg_cnt_beamactive_tt_values;
	private List<Integer> sup_trg_cnt_tt_values;
	private List<Integer> trg_cnt_beamactive_tt_values;
	private List<Integer> trg_cnt_tt_values;

	/*cpm deadtimes*/
	
	//lumi section to which deadtimes monitoring data correspond
	private int sectionNumber_deadtimes;
	
	private int fillNumber;
	
	//map with all deadtime values, indexed by their flashlist column name
	private Map<String, Double> deadTimes;

	/*cpm rates*/
	
	//lumi section to which rates monitoring data correspond
	private int sectionNumber_rates;
	
	//totals
	private double sup_trg_rate_beamactive_total;
	private double sup_trg_rate_total;
	private double trg_rate_beamactive_total;
	private double trg_rate_total;

	//arrays of rate values tt[i], where i=0,...,k (tt0 always at array position 0 etc.)
	private List<Double> sup_trg_rate_beamactive_tt_values;
	private List<Double> sup_trg_rate_tt_values;
	private List<Double> trg_rate_beamactive_tt_values;
	private List<Double> trg_rate_tt_values;


	/*pm action counts*/
	//action[i]_count, , where i=0,...,m (action0 always at array position 0 etc.)
	private List<Integer> actionCounts;

	public TCDSGlobalInfo() {
		super();
		
		globalTtsStates = new HashMap<String, GlobalTTSState>(); //should, in principle, reset between aggregations in the same DAQAggregator exec.
														//(refactor this field's setting to take place in this updateFromFlashlist(arg) before)
		
		sup_trg_cnt_beamactive_tt_values = new ArrayList<Integer>();
		sup_trg_cnt_tt_values = new ArrayList<Integer>();
		trg_cnt_beamactive_tt_values = new ArrayList<Integer>();
		trg_cnt_tt_values = new ArrayList<Integer>();
		sup_trg_rate_beamactive_tt_values = new ArrayList<Double>();
		sup_trg_rate_tt_values = new ArrayList<Double>();
		trg_rate_beamactive_tt_values = new ArrayList<Double>();
		trg_rate_tt_values = new ArrayList<Double>();
		actionCounts = new ArrayList<Integer>();
		deadTimes = new HashMap<String, Double>(); //should, in principle, reset between aggregations in the same DAQAggregator exec.
	}

	public Map<String, GlobalTTSState> getGlobalTtsStates() {
		return globalTtsStates;
	}

	public void setGlobalTtsStates(Map<String, GlobalTTSState> globalTtsStates) {
		this.globalTtsStates = globalTtsStates;
	}

	public int getSup_trg_cnt_beamactive_total() {
		return sup_trg_cnt_beamactive_total;
	}

	public void setSup_trg_cnt_beamactive_total(int sup_trg_cnt_beamactive_total) {
		this.sup_trg_cnt_beamactive_total = sup_trg_cnt_beamactive_total;
	}

	public int getSup_trg_cnt_total() {
		return sup_trg_cnt_total;
	}

	public void setSup_trg_cnt_total(int sup_trg_cnt_total) {
		this.sup_trg_cnt_total = sup_trg_cnt_total;
	}

	public int getTrg_cnt_beamactive_total() {
		return trg_cnt_beamactive_total;
	}

	public void setTrg_cnt_beamactive_total(int trg_cnt_beamactive_total) {
		this.trg_cnt_beamactive_total = trg_cnt_beamactive_total;
	}

	public int getTrg_cnt_total() {
		return trg_cnt_total;
	}

	public void setTrg_cnt_total(int trg_cnt_total) {
		this.trg_cnt_total = trg_cnt_total;
	}

	public List<Integer> getSup_trg_cnt_beamactive_tt_values() {
		return sup_trg_cnt_beamactive_tt_values;
	}

	public void setSup_trg_cnt_beamactive_tt_values(List<Integer> sup_trg_cnt_beamactive_tt_values) {
		this.sup_trg_cnt_beamactive_tt_values = sup_trg_cnt_beamactive_tt_values;
	}

	public List<Integer> getSup_trg_cnt_tt_values() {
		return sup_trg_cnt_tt_values;
	}

	public void setSup_trg_cnt_tt_values(List<Integer> sup_trg_cnt_tt_values) {
		this.sup_trg_cnt_tt_values = sup_trg_cnt_tt_values;
	}

	public List<Integer> getTrg_cnt_beamactive_tt_values() {
		return trg_cnt_beamactive_tt_values;
	}

	public void setTrg_cnt_beamactive_tt_values(List<Integer> trg_cnt_beamactive_tt_values) {
		this.trg_cnt_beamactive_tt_values = trg_cnt_beamactive_tt_values;
	}

	public List<Integer> getTrg_cnt_tt_values() {
		return trg_cnt_tt_values;
	}

	public void setTrg_cnt_tt_values(List<Integer> trg_cnt_tt_values) {
		this.trg_cnt_tt_values = trg_cnt_tt_values;
	}

	public int getFillNumber() {
		return fillNumber;
	}

	public void setFillNumber(int fillNumber) {
		this.fillNumber = fillNumber;
	}

	public Map<String, Double> getDeadTimes() {
		return deadTimes;
	}

	public void setDeadTimes(Map<String, Double> deadTimes) {
		this.deadTimes = deadTimes;
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

	public List<Integer> getActionCounts() {
		return actionCounts;
	}

	public void setActionCounts(List<Integer> actionCounts) {
		this.actionCounts = actionCounts;
	}

	public int getSectionNumber_counts() {
		return sectionNumber_counts;
	}

	public void setSectionNumber_counts(int sectionNumber_counts) {
		this.sectionNumber_counts = sectionNumber_counts;
	}

	public int getSectionNumber_deadtimes() {
		return sectionNumber_deadtimes;
	}

	public void setSectionNumber_deadtimes(int sectionNumber_deadtimes) {
		this.sectionNumber_deadtimes = sectionNumber_deadtimes;
	}

	public int getSectionNumber_rates() {
		return sectionNumber_rates;
	}

	public void setSectionNumber_rates(int sectionNumber_rates) {
		this.sectionNumber_rates = sectionNumber_rates;
	}

	@Override
	public void updateFromFlashlist(FlashlistType flashlistType, JsonNode flashlistRow) {
		
		/*Instance(s) of this type are also flashlist-updated by the tcds_pm_tts_channel flashlist.
		 * This is nevertheless not handled in this method but in the flashlist dispatcher using setters,
		 * where tcds_pm_tts_channel flashlist updates other data types as well. This way, the
		 * flashlist is only parsed once (which would be good to preserve).
		 * Should be refactored.*/

		if (flashlistType == FlashlistType.TCDS_CPM_COUNTS){
			//reset arrays to flush old entries
			sup_trg_cnt_beamactive_tt_values = new ArrayList<Integer>();
			sup_trg_cnt_tt_values = new ArrayList<Integer>();
			trg_cnt_beamactive_tt_values = new ArrayList<Integer>();
			trg_cnt_tt_values = new ArrayList<Integer>();
			
			//set ls number
			sectionNumber_counts = flashlistRow.get("section_number").asInt();
			
			//set totals
			sup_trg_cnt_beamactive_total = flashlistRow.get("sup_trg_cnt_beamactive_total").asInt();
			sup_trg_cnt_total = flashlistRow.get("sup_trg_cnt_total").asInt();
			trg_cnt_beamactive_total = flashlistRow.get("trg_cnt_beamactive_total").asInt();
			trg_cnt_total = flashlistRow.get("trg_cnt_total").asInt();
			
			
			//pattern for column name
			String pattern;
			
			//tt[0] to tt[max] are not parsed in guaranteed order, so this map is needed to later store in correct order
			Map<Integer,Integer> positionToValue;
			
			
			//fill sup_trg_cnt_beamactive_tt_values
			pattern = "sup_trg_cnt_beamactive_tt\\d+";
			
			positionToValue = new HashMap<Integer, Integer>();
			
			for (Iterator<String> fieldNameIter = flashlistRow.fieldNames(); fieldNameIter.hasNext() ;){
				String fieldName = fieldNameIter.next();
				if (!Pattern.matches(pattern, fieldName)){
					continue;
				}
				
				int i = Integer.parseInt(fieldName.substring(25));
				positionToValue.put(i, flashlistRow.get(fieldName).asInt());
			}
			
			//adding values from map to array, in tt[0]->tt[max] ordering
			for (int i=0;i<positionToValue.size();i++){
				sup_trg_cnt_beamactive_tt_values.add(positionToValue.get(i));
			}
			
			
			//fill sup_trg_cnt_tt_values
			pattern = "sup_trg_cnt_tt\\d+";
			
			positionToValue = new HashMap<Integer, Integer>();
			
			for (Iterator<String> fieldNameIter = flashlistRow.fieldNames(); fieldNameIter.hasNext() ;){
				String fieldName = fieldNameIter.next();
				if (!Pattern.matches(pattern, fieldName)){
					continue;
				}
				
				int i = Integer.parseInt(fieldName.substring(14));
				positionToValue.put(i, flashlistRow.get(fieldName).asInt());
			}
			
			//adding values from map to array, in tt[0]->tt[max] ordering
			for (int i=0;i<positionToValue.size();i++){
				sup_trg_cnt_tt_values.add(positionToValue.get(i));
			}
			
			//fill trg_cnt_beamactive_tt_values
			pattern = "trg_cnt_beamactive_tt\\d+";
			
			positionToValue = new HashMap<Integer, Integer>();
			
			for (Iterator<String> fieldNameIter = flashlistRow.fieldNames(); fieldNameIter.hasNext() ;){
				String fieldName = fieldNameIter.next();
				if (!Pattern.matches(pattern, fieldName)){
					continue;
				}
				
				int i = Integer.parseInt(fieldName.substring(21));
				positionToValue.put(i, flashlistRow.get(fieldName).asInt());
			}
			
			//adding values from map to array, in tt[0]->tt[max] ordering
			for (int i=0;i<positionToValue.size();i++){
				trg_cnt_beamactive_tt_values.add(positionToValue.get(i));
			}
			
			
			//fill trg_cnt_tt_values
			pattern = "trg_cnt_tt\\d+";
			
			positionToValue = new HashMap<Integer, Integer>();
			
			for (Iterator<String> fieldNameIter = flashlistRow.fieldNames(); fieldNameIter.hasNext() ;){
				String fieldName = fieldNameIter.next();
				if (!Pattern.matches(pattern, fieldName)){
					continue;
				}
				
				int i = Integer.parseInt(fieldName.substring(10));
				positionToValue.put(i, flashlistRow.get(fieldName).asInt());
			}
			
			//adding values from map to array, in tt[0]->tt[max] ordering
			for (int i=0;i<positionToValue.size();i++){
				trg_cnt_tt_values.add(positionToValue.get(i));
			}
		}

		if (flashlistType == FlashlistType.TCDS_CPM_RATES){
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

		if (flashlistType == FlashlistType.TCDS_CPM_DEADTIMES){
			
			//set fillnumber
			fillNumber = flashlistRow.get("fill_number").asInt();
			
			//set ls number
			sectionNumber_deadtimes = flashlistRow.get("section_number").asInt();
			
			//set map of deadtimes
			String pattern = "deadtime.*";
			
			for (Iterator<String> fieldNameIter = flashlistRow.fieldNames(); fieldNameIter.hasNext() ;){
				String fieldName = fieldNameIter.next();
				if (!Pattern.matches(pattern, fieldName)){
					continue;
				}
				
				//stripping "deadtime_" prefix, common to all values, to save some space
				deadTimes.put(fieldName.substring(9, fieldName.length()), flashlistRow.get(fieldName).asDouble());
			}
		}

		if (flashlistType == FlashlistType.TCDS_PM_ACTION_COUNTS){
			//reset array to flush old entries
			actionCounts = new ArrayList<Integer>();
			
			
			//set action counts array with values
			String pattern = "action\\d+_count";
			
			Map<Integer,Integer> positionToValue = new HashMap<Integer, Integer>();
			
			for (Iterator<String> fieldNameIter = flashlistRow.fieldNames(); fieldNameIter.hasNext() ;){
				String fieldName = fieldNameIter.next();
				if (!Pattern.matches(pattern, fieldName)){
					continue;
				}
				
				/*explicit decoding of position needed, because iteration over flashlistRow children has no guaranteed ordering
				 * (flashlistRow is an object, not an array, therefore action[2] might be returned before action[1] etc.)*/
				int i = Integer.parseInt(fieldName.substring(6, 7));
				positionToValue.put(i, flashlistRow.get(fieldName).asInt());
			}
			
			//adding values from map to array, in action[0]->action[max] ordering
			for (int i=0;i<positionToValue.size();i++){
				actionCounts.add(positionToValue.get(i));
			}
		}
	}

	@Override
	public void clean() {
		
		
	}

}
