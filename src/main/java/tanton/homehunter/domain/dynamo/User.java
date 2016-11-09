package tanton.homehunter.domain.dynamo;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import java.util.List;

@DynamoDBTable(tableName = "home_users")
public class User {

    @DynamoDBHashKey
    private String username;
    private List<String> excludedIds;

    public List<String> getExcludedIds() {
        return excludedIds;
    }

    public void setExcludedIds(List<String> excludedIds) {
        this.excludedIds = excludedIds;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
