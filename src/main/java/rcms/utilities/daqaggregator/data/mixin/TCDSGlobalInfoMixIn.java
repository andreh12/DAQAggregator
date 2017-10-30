package rcms.utilities.daqaggregator.data.mixin;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Class configuring json serialization
 *
 * ensure that for this class serialization/deserialization only happens
 *  via getters/setters and not by directly looking for fields.
 *
 *  This allows for backward compatibility due to a schema change
 *  in class TCDSGlobalInfo
 */
@JsonAutoDetect(
				fieldVisibility  = JsonAutoDetect.Visibility.NONE,
				getterVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY,
				setterVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY)
public interface TCDSGlobalInfoMixIn {
}
