package au.com.sensis.stubby.service.model;

import org.apache.commons.lang.builder.EqualsBuilder;

import au.com.sensis.stubby.utils.JsonUtils;

public class MatchField {

    public enum FieldType {
        PATH,
        METHOD,
        QUERY_PARAM,
        HEADER,
        BODY;
    }
    
    public enum MatchType {
        NOT_FOUND,
        MATCH_FAILURE,
        MATCH;
    }
    
    private FieldType fieldType;
    private String fieldName;
    private MatchType matchType;
    private Object expectedValue; // sometimes will be a Pattern, a JSON object etc.
    private Object actualValue; // could be string, JSON object etc.

    public MatchField(FieldType fieldType, String fieldName, Object expectedValue) {
        this.fieldType = fieldType;
        this.fieldName = fieldName;
        this.expectedValue = expectedValue;
    }

    public MatchField(FieldType fieldType, String fieldName, Object expectedValue, Object actualValue) {
        this.fieldType = fieldType;
        this.fieldName = fieldName;
        this.expectedValue = expectedValue;
        this.actualValue = actualValue;
    }

    public MatchField asMatch(Object actualValue) {
        this.matchType = MatchType.MATCH;
        this.actualValue = actualValue;
        return this;
    }

    public MatchField asNotFound() {
        this.matchType = MatchType.NOT_FOUND;
        return this;
    }

    public MatchField asMatchFailure(Object actualValue) {
        this.matchType = MatchType.MATCH_FAILURE;
        this.actualValue = actualValue;
        return this;
    }

    public int score() { // attempt to give some weight to matches so we can guess 'near misses'
        switch (matchType) {
        case NOT_FOUND:
            return 0;
        case MATCH_FAILURE:
            switch (fieldType) {
            case PATH:
            case METHOD:
                return 0; // these guys always exist, so the fact that they're found is unimportant
            case HEADER:
            case QUERY_PARAM:
            case BODY:
                return 1; // if found but didn't match
            default:
                throw new RuntimeException(); // impossible
            }
        case MATCH:
            switch (fieldType) {
            case PATH:
                return 5; // path is most important in the match
            default:
                return 2;
            }
        default:
            throw new RuntimeException(); // impossible
        }
    }

    @Override
    public String toString() {
        return JsonUtils.serialize(this);
    }
    
    @Override
    public boolean equals(Object other) { // only used in tests
        return EqualsBuilder.reflectionEquals(other, this);
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public MatchType getMatchType() {
        return matchType;
    }

    public Object getExpectedValue() {
        return expectedValue;
    }

    public Object getActualValue() {
        return actualValue;
    }

}
