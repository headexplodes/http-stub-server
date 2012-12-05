package au.com.sensis.stubby;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

public class MatchResult implements Comparable<MatchResult> {

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
    
    public static class Field {
        
        public FieldType fieldType;
        public String fieldName;
        public MatchType matchType;
        public Object expectedValue; // sometimes will be a Pattern, a JSON object etc.
        public Object actualValue; // could be string, JSON object etc.
        
        public Field(FieldType fieldType, String fieldName, Object expectedValue) {
            this.fieldType = fieldType;
            this.fieldName = fieldName;
            this.expectedValue = expectedValue;
        }
        
        public Field(FieldType fieldType, String fieldName, Object expectedValue, Object actualValue) {
            this.fieldType = fieldType;
            this.fieldName = fieldName;
            this.expectedValue = expectedValue;
            this.actualValue = actualValue;
        }
        
        public Field asMatch(Object actualValue) {
            this.matchType = MatchType.MATCH;
            this.actualValue = actualValue;
            return this;
        }
        
        public Field asNotFound() {
            this.matchType = MatchType.NOT_FOUND;
            return this;
        }
        
        public Field asMatchFailure(Object actualValue) {
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
            try {
                return new ObjectMapper().writeValueAsString(this);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        
    }
    
    public List<Field> fields;
    
    public MatchResult() {
        this.fields = new ArrayList<Field>();
    }
    
    public void add(Field field) {
        fields.add(field);
    }
    
    public boolean matches() {
        for (Field field : fields) {
            if (field.matchType != MatchType.MATCH) {
                return false; // found a failure
            }
        }
        return true; // no failures
    }
    
    public int score() {
        int result = 0;
        for (Field field : fields) {
            result += field.score();
        }
        return result;
    }
    
    @Override
    public int compareTo(MatchResult other) {
        return new Integer(score()).compareTo(other.score()) * -1; // highest score first
    }
    
}
