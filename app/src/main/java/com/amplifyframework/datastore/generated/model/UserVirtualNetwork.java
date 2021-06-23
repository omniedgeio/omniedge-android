package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.annotations.BelongsTo;

import java.util.List;
import java.util.UUID;
import java.util.Objects;

import androidx.core.util.ObjectsCompat;

import com.amplifyframework.core.model.AuthStrategy;
import com.amplifyframework.core.model.Model;
import com.amplifyframework.core.model.ModelOperation;
import com.amplifyframework.core.model.annotations.AuthRule;
import com.amplifyframework.core.model.annotations.Index;
import com.amplifyframework.core.model.annotations.ModelConfig;
import com.amplifyframework.core.model.annotations.ModelField;
import com.amplifyframework.core.model.query.predicate.QueryField;

import static com.amplifyframework.core.model.query.predicate.QueryField.field;

/** This is an auto generated class representing the UserVirtualNetwork type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "UserVirtualNetworks", authRules = {
  @AuthRule(allow = AuthStrategy.GROUPS, groupClaim = "cognito:groups", groups = { "Operator" }, operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
})
@Index(name = "byUser", fields = {"userID","virtualNetworkID"})
@Index(name = "byVirtualNetwork", fields = {"virtualNetworkID","userID"})
public final class UserVirtualNetwork implements Model {
  public static final QueryField ID = field("id");
  public static final QueryField VIRTUAL_NETWORK = field("virtualNetworkID");
  public static final QueryField USER = field("userID");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="VirtualNetwork") @BelongsTo(targetName = "virtualNetworkID", type = VirtualNetwork.class) VirtualNetwork virtualNetwork;
  private final @ModelField(targetType="User") @BelongsTo(targetName = "userID", type = User.class) User user;
  public String getId() {
      return id;
  }
  
  public VirtualNetwork getVirtualNetwork() {
      return virtualNetwork;
  }
  
  public User getUser() {
      return user;
  }
  
  private UserVirtualNetwork(String id, VirtualNetwork virtualNetwork, User user) {
    this.id = id;
    this.virtualNetwork = virtualNetwork;
    this.user = user;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      UserVirtualNetwork userVirtualNetwork = (UserVirtualNetwork) obj;
      return ObjectsCompat.equals(getId(), userVirtualNetwork.getId()) &&
              ObjectsCompat.equals(getVirtualNetwork(), userVirtualNetwork.getVirtualNetwork()) &&
              ObjectsCompat.equals(getUser(), userVirtualNetwork.getUser());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getVirtualNetwork())
      .append(getUser())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("UserVirtualNetwork {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("virtualNetwork=" + String.valueOf(getVirtualNetwork()) + ", ")
      .append("user=" + String.valueOf(getUser()))
      .append("}")
      .toString();
  }
  
  public static BuildStep builder() {
      return new Builder();
  }
  
  /** 
   * WARNING: This method should not be used to build an instance of this object for a CREATE mutation.
   * This is a convenience method to return an instance of the object with only its ID populated
   * to be used in the context of a parameter in a delete mutation or referencing a foreign key
   * in a relationship.
   * @param id the id of the existing item this instance will represent
   * @return an instance of this model with only ID populated
   * @throws IllegalArgumentException Checks that ID is in the proper format
   */
  public static UserVirtualNetwork justId(String id) {
    try {
      UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
    } catch (Exception exception) {
      throw new IllegalArgumentException(
              "Model IDs must be unique in the format of UUID. This method is for creating instances " +
              "of an existing object with only its ID field for sending as a mutation parameter. When " +
              "creating a new object, use the standard builder method and leave the ID field blank."
      );
    }
    return new UserVirtualNetwork(
      id,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      virtualNetwork,
      user);
  }
  public interface BuildStep {
    UserVirtualNetwork build();
    BuildStep id(String id) throws IllegalArgumentException;
    BuildStep virtualNetwork(VirtualNetwork virtualNetwork);
    BuildStep user(User user);
  }
  

  public static class Builder implements BuildStep {
    private String id;
    private VirtualNetwork virtualNetwork;
    private User user;
    @Override
     public UserVirtualNetwork build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new UserVirtualNetwork(
          id,
          virtualNetwork,
          user);
    }
    
    @Override
     public BuildStep virtualNetwork(VirtualNetwork virtualNetwork) {
        this.virtualNetwork = virtualNetwork;
        return this;
    }
    
    @Override
     public BuildStep user(User user) {
        this.user = user;
        return this;
    }
    
    /** 
     * WARNING: Do not set ID when creating a new object. Leave this blank and one will be auto generated for you.
     * This should only be set when referring to an already existing object.
     * @param id id
     * @return Current Builder instance, for fluent method chaining
     * @throws IllegalArgumentException Checks that ID is in the proper format
     */
    public BuildStep id(String id) throws IllegalArgumentException {
        this.id = id;
        
        try {
            UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
        } catch (Exception exception) {
          throw new IllegalArgumentException("Model IDs must be unique in the format of UUID.",
                    exception);
        }
        
        return this;
    }
  }
  

  public final class CopyOfBuilder extends Builder {
    private CopyOfBuilder(String id, VirtualNetwork virtualNetwork, User user) {
      super.id(id);
      super.virtualNetwork(virtualNetwork)
        .user(user);
    }
    
    @Override
     public CopyOfBuilder virtualNetwork(VirtualNetwork virtualNetwork) {
      return (CopyOfBuilder) super.virtualNetwork(virtualNetwork);
    }
    
    @Override
     public CopyOfBuilder user(User user) {
      return (CopyOfBuilder) super.user(user);
    }
  }
  
}
